package com.fs.game.screens;

import appwarp.WarpController;
import appwarp.WarpListener;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.fs.game.constants.Constants;
import com.fs.game.MainGame;
import com.fs.game.actors.TextActor;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.AudioUtils;
import com.fs.game.utils.PlayerUtils;

/** The multiplayer screen implements Screen and  WarpListener
 * - the online multiplayer version of GameScreen
 *
 * Created by Allen on 11/16/14.
 */
public class MultiplayerScreen extends GameScreen implements WarpListener{

    final String LOG = "MultiplayerScreen log: ";
    private static MultiplayerScreen instance;

    StartMultiplayerScreen prevScreen; //the previous screen (MainScreen or MenuScreen)
    GameState gameState; //current game animState

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;

    //for music & audio TODO: put in some audio for units, movements, finishing, etc
    Music music; //music that plays (during pause & run animState)
    float currVolumeMusic = .5f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

    //timer variables - load into Label
    final float maxTime = Constants.MAX_TIME+.5f;
    float timerCount = 0;

    //player variables
    public int currPlayer = 1; //current player whose turn it is (1 goes first always)
    public int playerScore = 0;
    public int enemyScore = 0;
    public int[] unitCounts = {7, 7};

    private int player = 0; //player turn/positioning
    private String playerFaction;
    private int playerID; //playecom.fs.game.appwarp positions //for appwarp sdk
    private boolean playerTurn; //whose player turn it is

    //stages, widgets & messages
    private GameStage stageMap; //all the units, tiles go here
//    Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
    Stage pauseStage; //pause menu options

    BitmapFont font; //font for in game messages
    private final String tryingToConnect = Constants.TRY_CONNECT_MSG;
    private final String errorInConnection = Constants.ERROR_CONNECT_MSG;
    private String msg = tryingToConnect;
    private String inGameMsg; //in game message
    private String startMsg = "Game Starting for player: "+ GameData.getInstance().playerName + "\n Game Starting in ";
    private String playerTurnMsg = "PLAYER " + currPlayer + " TURN";
    private TextActor textActor;

    Window pauseWindow;
    public TextButton[] uiButtons = new TextButton[3]; //2 side buttons, 1 go button
    Label[] labels = new Label[5]; //2 for info, 2 for scores, 1 for timer
    Texture goButton;
    Rectangle goBounds;

    //cameras, viewports, input processors
    ScalingViewport scalingViewPort;
    OrthographicCamera camera; //main stage cam
    InputMultiplexer in; //handles input events for stage & stageMap
    Array<InputProcessor> processors; //processors on stages


    public MultiplayerScreen(final MainGame game, StartMultiplayerScreen screen){
        super(game);

        this.prevScreen = screen;
        this.gameState = GameState.STARTING;
//        this.font = Assets.uiSkin.getFont("retro1");
        instance = this;

//        setupCamera();
//        setupAudio(); //music which is playing
//        setupStages();
//        setupUI();

        this.playerFaction = GameData.getInstance().playerFaction;

        WarpController.getInstance().setListener(this);
        log(" Multiplayer Screen player: " + WarpController.getInstance().getLocalUser());

        sendSetupData();
    }

    public static MultiplayerScreen getInstance(){
        return instance;
    }

//    //method which sets up the camera for this screen
//    public void setupCamera(){
//        /** the cameras for viewing scene objects**/
//        //camera for stage
//        camera = new OrthographicCamera(VIEWPORTWIDTH/32, VIEWPORTHEIGHT/32);
//        camera.setToOrtho(false, VIEWPORTWIDTH, VIEWPORTHEIGHT);
//        camera.update();
//
//        //viewport.setRotation
//        scalingViewPort = new ScalingViewport(Scaling.fit, VIEWPORTWIDTH, VIEWPORTHEIGHT);
//        scalingViewPort.setCamera(camera);
//    }


    //sets gameplay stages
    public void setupStages() {
//        stage = new Stage(scalingViewPort); //stage : create the stage for UI
//        stageMap = GameMapUtils.getTiledMap(4);
//        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

        //FIXED: moved to AbstractScreen
//        //set up player turn message
//        textActor = new TextActor(font, Constants.TURN_MSG_COORD);
//        textActor.setText(playerTurnMsg);
//        stage.addActor(textActor);
//
//        //all the processors targets created & combined
//        in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs
//        processors = new Array<InputProcessor>();
//        processors.add(stage);
//        processors.add(stageMap);
//        in.setProcessors(processors);
//        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
    }

//    /** creates the info panel
//     *
//     */
//    public void setupUI() {
//        UIUtils.setupUI(uiButtons, labels, stage);
//
//        //an alternative setup for the go button
//        goButton = Assets.uiSkin.get("lets-go-tex", Texture.class);
//        goBounds = new Rectangle(labels[0].getX(), labels[0].getY(), labels[0].getWidth(), labels[0].getHeight());
//
//        setupPauseMenu();
//    }
//
//
//    public void setupPauseMenu(){
//        /** pause stage*/
//        pauseWindow = MenuUtils.PauseMenu.pauseWindow(); //pause window
//        pauseStage = new Stage(scalingViewPort);
//        pauseStage.addActor(pauseWindow);
//    }


    /**
     * TODO: create & add more music
     * ...currently only 1 track
     */
    public void setupAudio(){
        music = AudioUtils.createMapMusic(0);
        music.setLooping(true); //loops the track

        GameData.volumes[1] = .5f;
        music.setVolume(GameData.volumes[1]);

    }

//    /** this method checks to see if player turn is done
//     * either by time running out or player hitting go button
//     *
//     */
//    @Override
//    protected void isPlayerDone() {
//
//        //if the timer reaches max time, playerTurn set to true
//        if (timerCount > maxTime || uiButtons[0].isPressed() ||
//                Gdx.input.isKeyJustPressed(Input.Keys.G)) {
//
//            if (playerTurn){
//                playerTurn = false;
//
//                //switch the current player
//                if (player == 1)
//                    currPlayer = 2;
//                else if (player == 2)
//                    currPlayer = 1;
//
//                //toggle button to red (should be green if player's turn)
//                //stageMap.lockPlayerUnits(GameData.getInstance().playerName);  //lock these player units
//                sendPlayerData(); //send data which notifies others screen & allows player to go
//                resetTurn();
//            }
//        }
//
//    }

//    @Override
//    public void resetTurn(){
//        timerCount = 0;
//        uiButtons[player].toggle();
//        if (player == 1)
//            uiButtons[2].toggle(); //since player is either 1 or 2
//        else
//            uiButtons[1].toggle();
//
//        textActor.setText(playerTurnMsg);
//
//    }
//
//    @Override
//    public void updateWidgets(float delta){
//        labels[0].setText("" + (int)timerCount);
//        labels[0].act(delta);
//
//        updatePlayerScores();
//        if (GameData.chosenUnit!=null) {
//            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
//            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
//        }
//    }
//
//    @Override
//    public void updatePlayerScores(){
//        if (GameData.getInstance().player == 1){
//            labels[3].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
//            labels[4].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
//        }
//        else{
//            labels[3].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
//            labels[4].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
//        }
//    }

    @Override
    public void updateCurrent(float delta){

        timerCount += delta;

        if (timerCount < 3.5){
            textActor.setText(playerTurnMsg);
            textActor.toFront();
            textActor.showTurnMsg = true;
        }
        else{
            textActor.showTurnMsg = false;
        }

        stage.updateWidgets(delta); //updates widgets
        stage.isPlayerDone(); //checks to see if next player will go

        stageMap.act(delta); //stage with tiled map & units on it
        stage.act(delta); //stage with other UI elements

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ){
            Gdx.input.setInputProcessor(pauseStage);
            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
            sendPauseData();
        }

        if (unitCounts[0]==0 || unitCounts[1]==1)
            gameState = GameState.QUIT;

    }

//    @Override
//    public void pauseCurrent(float delta){
//        pauseWindow.act(delta);
//        pauseStage.act(delta);
//
//        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
//            Gdx.app.log(LOG, "game is resuming");
//            gameState = GameState.RESUME;
//        }
//    }


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

//
//    public void clearScreen(){
//        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//    }


    //FIXED: moved to abstract screen
//    //should come after stage draw methods
//    public void drawGameMsg(){
//        game.batch.begin();
//        float y = Constants.SCREENHEIGHT/2;
//        float x = Constants.SCREENWIDTH/4;
//        font.drawMultiLine(game.batch, msg, x, y);
//        game.batch.end();
//    }

//FIXED: abstract screen takes care of in show
//    public void draw(){
//        stage.draw();
//        stageMap.draw();
//    }

//    float countDown = 0; //FIXED: in GameScreen
    @Override
    public void render(float delta) {
//        clearScreen();
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

        if (music.getVolume() != currVolumeMusic)
            music.setVolume(currVolumeMusic);
        //music.play();

        switch (gameState){
            case STARTING:
                countDown += delta;
                msg = startMsg + Integer.toString(3 - (int)countDown);
                drawGameMsg();
                if (countDown > 3.5) {
                    gameState = GameState.RUN;
                    countDown = 0;
                }
                break;
            case RUN :
                updateCurrent(delta);
                show();
                break;
            case PAUSE :
                //in GameScreen method
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



    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }


// FIXED: no need for these methods since in GameScreen
//    @Override
//    public void show() {
//
//    }
//    /**
//     */
//    @Override
//    public void pause() {
//        clearScreen();
//        pauseStage.draw();
//    }

//    /**
//     */
//    @Override
//    public void resume() {
//
//    }

//    @Override
//    public void dispose() {
//        music.dispose();
//        stage.dispose();
//        stageMap.dispose();
//        pauseStage.dispose();
//    }

//    @Override
//    public void resize(int width, int height) {
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//
//        //restore the stage's viewport.
//        scalingViewPort.update(width, height);
//        stage.getViewport().update(width, height, true);
//        stageMap.getViewport().update(width, height, true);
//        pauseStage.getViewport().update(width, height, true);
//
//        camera.update();
//        stage.getCamera().update();
//        stageMap.getCamera().update();
//        pauseStage.getCamera().update();
//    }


    @Override
    public void onWaitingStarted(String message) {
		
    }

    @Override
    public void onError(String message) {
        gameState = GameState.QUIT;
        this.msg = errorInConnection;
    }


    @Override
    public void onGameStarted(String message) {

    }

    @Override
    public void onGameFinished(int code, boolean isRemote) {
        if(isRemote){
            prevScreen.onGameFinished(code, true);
        }else{
            if(code==WarpController.GAME_WIN){
                gameState = GameState.GAME_OVER;
            }else if(code==WarpController.GAME_LOST){
                gameState = GameState.GAME_OVER;
            }
        }
    }

    /** TODO: Fix issues with mutliplayer
     * - changeplayer properly
     * Observations: NEED to compensate for time players get into room
     *   - either player can get into room slower or faster (rarely at same time exactly)
     * CLEAR CACHE occasionally
     */
    @Override
    public void onGameUpdateReceived(String message) {
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            UserData data = json.fromJson(UserData.class, message);

            //updateState: 0 = setup units; 1 = update unit; 2 = update player
            int updateState = data.getUpdateState();
            log("Message being received: " + message);
            log("Update animState : "+ updateState);

            switch (updateState){
                case 0: //initiate setup
                    updateSetup(data);
                    break;
                case 1:
                    updateUnit(data);
                    break;
                case 2:
                    updatePlayer(data);
                    break;
                case 3:
                    gameState = GameState.PAUSE; //game is paused
                    break;
                case 4:
                    gameState = GameState.RUN;
                    break;
                default:
                    //do nothing
                    break;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendSetupData(){
        timerCount = 0;
        try{
            playerID = PlayerUtils.randomLengthPlayerID();

            UserData userData = new UserData();
            userData.setName(GameData.getInstance().playerName);
            userData.setPlayerID(playerID);
            userData.setFaction(playerFaction);
            userData.setUpdateState(0);

            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            String data = json.toJson(userData);

            WarpController.getInstance().sendGameStartUpdate(data);
        }
        catch(Exception e){
            System.out.println("exception while writing to json & sending setupData");
            e.printStackTrace();
        }
    }


    //sends data about player (score, turn, other)
    private void sendPlayerData(){
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);

            UserData userData = new UserData();
            userData.setScore(playerScore);
            userData.setPlayerTurn(true); //sets other player turn as true
            userData.setName(GameData.getInstance().playerName);
            userData.setUpdateState(2);


            String data = json.toJson(userData, UserData.class);
            WarpController.getInstance().sendGameUpdate(data);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendPauseData(){
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);

            UserData userData = new UserData();
            userData.setName(GameData.getInstance().playerName);
            userData.setUpdateState(3);

            String data = json.toJson(userData, UserData.class);
            WarpController.getInstance().sendGameUpdate(data);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    //updates the initial setup data
    private void updateSetup(UserData data){
        log("Player receiving data: " + GameData.getInstance().playerName);
        float enemyID = data.getPlayerID();
        String enemyFaction = data.getFaction();
        GameData.getInstance().enemyName = data.getName();

        if (playerID > enemyID) {
            playerTurn = true;
            player = 1;
            stageMap.setupUnits(player, 2, GameData.getInstance().enemyName, playerFaction, enemyFaction);

        } else if (playerID < enemyID) {
            playerTurn = false;
            player = 2;
            stageMap.setupUnits(player, 1, GameData.getInstance().enemyName, playerFaction, enemyFaction);
            //stageMap.lockPlayerUnits(GameData.getInstance().playerName);
        }

        uiButtons[2].toggle();
    }

    //updates a single unit on stage
    private void updateUnit(UserData data){
        if (data.getUnitData().getHealth() <= 0){
            enemyScore += 10;
            unitCounts[data.getPlayer()-1]--;
        }

        stageMap.updateUnit(data.getUnitData());
    }

    //updates the screen mainly (who goes & score)
    private void updatePlayer(UserData data){

        this.enemyScore = data.getScore();
        this.playerTurn = data.isPlayerTurn();

        System.out.println("unlocking player " + currPlayer + " units");
        //stageMap.unlockPlayerUnits(GameData.getInstance().playerName);
        this.currPlayer = player;
        stage.resetTurn();
    }

    //shows message about what is going on
    private void log(String message){
        Gdx.app.log(LOG, message);
    }

}
