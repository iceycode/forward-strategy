package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.fs.game.MainGame;
import com.fs.game.appwarp.AppWarpAPI;
import com.fs.game.appwarp.WarpController;
import com.fs.game.appwarp.WarpListener;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.units.UnitController;

/** The multiplayer screen implements Screen and  WarpListener
 * - the online multiplayer version of GameScreen
 *
 * Created by Allen on 11/16/14.
 */
public class MultiplayerScreen extends GameScreen implements WarpListener{

    final String LOG = "MultiplayerScreen log: ";

    StartMultiplayerScreen prevScreen; //the previous screen (MainScreen or MenuScreen)

    //player variables
    boolean playerTurn = false;
    public int currPlayer = 1; //current player whose turn it is (1 goes first always)
//    public int playerScore = 0;
    public int enemyScore = 0;
    public int[] unitCounts = {7, 7};

    private int player = 0; //player turn/positioning
    private String playerFaction;
    private int playerID; //playecom.fs.game.com.fs.game.appwarp positions //for com.fs.game.appwarp sdk

    boolean setupDone = false; //tells whether setup is done

    private final String tryingToConnect = Constants.TRY_CONNECT_MSG;
    private final String errorInConnection = Constants.ERROR_CONNECT_MSG;
    String msg = tryingToConnect;
    private String inGameMsg; //in game message
    private String startMsg = "Game Starting for player: "+ GameData.playerName + "\n Game Starting in ";


    public MultiplayerScreen(final MainGame game, StartMultiplayerScreen screen){
        super(game);

        this.prevScreen = screen;
        this.gameState = GameState.RUN;
        this.playerFaction = GameData.playerFaction;

        WarpController.getInstance().setListener(this);
        log(" Multiplayer Screen player: " + WarpController.getInstance().getLocalUser());


        //NOTE: from previous screen, info should have been sent during onGameUpdate
        updateSetup(screen.getUserData());
//        sendSetupData();


    }


    @Override
    public void updateCurrent(float delta){


        stageMap.act(delta); //stage with tiled map & units on it
        stage.act(delta); //stage with other UI elements

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ){
            Gdx.input.setInputProcessor(pauseStage);
            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
            AppWarpAPI.getInstance().sendPauseUpdate();
        }

        if (unitCounts[0]==0 || unitCounts[1]==0)
            gameState = GameState.QUIT;

    }

    public void gameOver(){
        prevScreen.setGameState(GameState.GAME_OVER);
        game.setScreen(prevScreen);
        dispose();
    }


    public void quitCurrent(){
        WarpController.getInstance().handleLeave();

        prevScreen.setGameState(GameState.GAME_OVER);

        game.setScreen(prevScreen);
        dispose();
    }


    public void clearScreen(){
        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    @Override
    public void show() {
//        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1); //sets the color of clear screen
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState == GameState.STARTING){
            clearScreen();
            super.drawGameMsg();
        }
        else{
            super.show();
        }

    }


    @Override
    public void render(float delta) {
//        Gdx.graphics.getGL20().glClearColor(0, 1, 0, 1); //sets the color of clear screen
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

        if (music.getVolume() != currVolumeMusic)
            music.setVolume(currVolumeMusic);
        //music.play();

        //FIXED: now countdown occurs in StartMultiplayerScreen, no longer case STARTING required
        switch (gameState){
            case RUN :
                updateCurrent(delta);
                super.show();
                break;
            case PAUSE :
                pause();
                break;
            case RESUME :
                updateCurrent(delta);
                show();
                break;
            case QUIT :
                quitCurrent();
                dispose();
                break;
            case GAME_OVER:
                gameOver();
                dispose();
                break;
        }

    }

    @Override
    public void pause() {

        pauseStage.act(Gdx.graphics.getDeltaTime());
        pauseStage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Gdx.app.log(LOG, "game is resuming");
            gameState = GameState.RESUME;
            AppWarpAPI.getInstance().sendResumeUpdate(); //send resume update to player
        }
    }

    @Override
    public void resize(int width, int height) {
        //Set the viewport to the whole screen.
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //restore the stages' viewports.
        stage.getViewport().update(width, height, true);
        stageMap.getViewport().update(width, height, true);
        pauseStage.getViewport().update(width, height, true);

        stage.getCamera().update();
        stageMap.getCamera().update();
        pauseStage.getCamera().update();


    }

    /** Returns this player's id in multiplayer game
     *
     * @return : id of player (1 or 2)
     */
    public int getPlayer(){
        return player;
    }

//===================WarpListener Interface Methods=========================//
    @Override
    public void onWaitingStarted(String message) {
        /** DO NOTHING HERE
         *  Everything is done in {@link StartMultiplayerScreen}
         */
    }

    @Override
    public void onError(String message) {
        gameState = GameState.QUIT;
        this.msg = errorInConnection;
    }


    @Override
    public void onGameStarted(String message) {
        /** DO NOTHING HERE
         *  Everything is done in {@link StartMultiplayerScreen}
         */
    }

    @Override
    public void onGameFinished(int code, boolean isRemote) {
        if(isRemote){
            prevScreen.onGameFinished(code, true);
        }
        else {
            if(code==WarpController.GAME_WIN || code==WarpController.GAME_LOST){
                gameState = GameState.GAME_OVER;
            }
        }
    }

    /**
     * Observations: NEED to compensate for time players get into room
     *   - either player can get into room slower or faster (rarely at same time exactly)
     * CLEAR CACHE occasionally
     */
    @Override
    public void onGameUpdateReceived(String message) {
        UserData data = AppWarpAPI.getInstance().decodeUserData(message);

        //updateState: 0 = setup units; 1 = update unit; 2 = update player
        int updateState = data.getUpdateState();
        log("Message being received: " + message);
        log("Update state : "+ updateState);

        switch (updateState){
            case AppWarpAPI.SETUP_UPDATE: //initiate setup
                updateSetup(data);
                setupDone = true;
                break;
            case AppWarpAPI.UNIT_UPDATE:
                updateUnit(data);
                break;
            case AppWarpAPI.TURN_UPDATE:
                updatePlayer(data);
                break;
            case AppWarpAPI.PAUSE_UPDATE:
                gameState = GameState.PAUSE; //game is now paused
                break;
            case AppWarpAPI.RESUME_UPDATE:
                gameState = GameState.RUN;
                break;
            case AppWarpAPI.GAME_END_UPDATE:
                gameState = GameState.GAME_OVER;
                break;
            default:
                //do nothing
                break;
        }
    }


    /**
     *
     * @param data
     */
    public void updateSetup(UserData data){
        playerID = AppWarpAPI.getInstance().getPlayerID(); //player id for comparison

        log("Player updating multiplayer setup: " + GameData.playerName);
        float enemyID = data.getPlayerID();
        String enemyFaction = data.getFaction();
        GameData.enemyName = data.getName();

        //set player
        player = playerID > enemyID ? 1 : 2;
//        playerTurn = player == 1 ? true : false;

        stage.setPlayer(player);
        stageMap.setupUnitsMulti(player, playerFaction, enemyFaction);
    }

    //updates a single unit on stage
    private void updateUnit(UserData data){
        if (data.getUnitData().getHealth() <= 0){

            unitCounts[data.getPlayer()-1]--;
        }
        else{
//            stageMap.updateUnit(data.getPlayer(), data.getUnitData());
            UnitController.getInstance().updateUnitMulti(data);
        }
    }

    //updates the screen mainly (who goes & score)
    private void updatePlayer(UserData data){

        this.enemyScore = data.getScore();
        this.playerTurn = data.isPlayerTurn();

        System.out.println("unlocking player " + currPlayer + " units");
        stage.resetTurn();
        //stageMap.unlockPlayerUnits(GameData.getInstance().playerName);
//        this.currPlayer = player;
//
//        //reset if false, since does not know it is this players turn
//        if (!playerTurn)
//            stage.resetTurn();
    }

    protected void updatePlayerScore(UserData data){



    }

    //shows message about what is going on
    private void log(String message){
        Gdx.app.log(LOG, message);
    }

}

// NOTE: this is now done in StartMultiplayerScreen
//    public void sendSetupData(){
//        playerID = PlayerUtils.randomLengthPlayerID();
//
//        UserData userData = new UserData();
//        userData.setName(GameData.playerName);
//        userData.setPlayerID(playerID);
//        userData.setFaction(playerFaction);
//        userData.setUpdateState(0);
//
//        String data = AppWarpAPI.getInstance().encodeUserData(userData);
//
//        WarpController.getInstance().sendGameUpdate(data);
//    }



//Methods found in GameScreen used in initialization
//GLOBAL variables in GameScreen

//    final MainGame game;
//    public GameState gameState; //starts out running
//    float countDown = 0; //countdown to game start
//    GameState gameState; //current game animState

//    float currVolumeMusic = .01f; //initialize to 1.0f (highest volume)
//    float currVolumeSound = 1.0f; //initialize to 1.0f
//    Music music; //music that plays (during pause & run animState)
//    Array<Sound> sounds; //sounds TODO: find & add sounds to game

//Stages holding information about Game, main game map & characters & pause menu
//    InfoStage stage; //main stage - shows info about gameplay, not game actors
//    GameStage stageMap; //gameplay stage - all the units, tiles go here
//    PauseStage pauseStage; //pause menu options
//
//    //Game font, text image (TextActor) font & Messages
//    BitmapFont font;

//timer variables - load into Label
//    final float maxTime = Constants.MAX_TIME+.5f;


// SETUP In Constructor
//        this.game = game;
//        this.gameState = GameState.STARTING;
//        this.font = Assets.uiSkin.getFont("retro1");
//
//        if (GameData.isTest)
//            GameData.mapChoice = GameData.testType;
//
//        scalingViewPort = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);; //sets camera & viewport
//
//        setStages(GameData.mapChoice); //sets the stages
//
//        setInputProcessors();
//        setupAudio();
//
//    /** stage for GamePlay - for units/tiled map
//     *
//     * @param mapChoice : user or random map choice
//     */
//    protected void setStages(int mapChoice){
//        //sets HUD stage - shows unit damage & info, score, time
//        stage = new InfoStage(); //has its own batch
//
//        stageMap = new GameStage(mapChoice);
//        stage.setupMiniMap(stageMap);
//
//        stage.setGameStageListener(stageMap);
//        stageMap.setInfoStageListener(stage);
//
//        pauseStage = new PauseStage(this, scalingViewPort); //set PauseStage
//    }
//
//
//    //sets the input processors for the stage
//    protected void setInputProcessors(){
//        //all the processors targets created & combined
//        in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs
//
//        Array<InputProcessor> processors = new Array<InputProcessor>();
//        processors.add(stage);
//        processors.add(stageMap);
////        stageMap.addCameraInputProcessor(processors); //add tiled map camera processor
//        in.setProcessors(processors);
//        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
//    }
//
//
//    /** TODO: create & add more music
//     * ...currently only 1 track
//     */
//    public void setupAudio(){
//        music = AudioUtils.createMapMusic(0);
//        music.setLooping(true); //loops the track
//
//        GameData.volumes[1] = .5f;
//        music.setVolume(GameData.volumes[1]);
//    }

