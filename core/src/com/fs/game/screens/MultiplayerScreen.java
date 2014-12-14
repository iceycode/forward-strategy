package com.fs.game.screens;

import appwarp.WarpController;
import appwarp.WarpListener;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.stages.GameStage;
import com.fs.game.tests.TestUtils;
import com.fs.game.utils.AudioUtils;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.MenuUtils;
import com.fs.game.utils.UnitUtils;

import java.util.Random;

/** The multiplayer screen extends GameScreen, implements WarpListener
 * - the online multiplayer version of GameScreen
 *
 * Created by Allen on 11/16/14.
 */
public class MultiplayerScreen implements Screen, WarpListener{

    final MainGame game;
    final String LOG = "MultiplayerScreen log: ";

    StartMultiplayerScreen prevScreen; //the previous screen (MainScreen or MenuScreen)
    GameState gameState; //current game state

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;


    //for music & audio TODO: put in some audio for units, movements, finishing, etc
    Music music; //music that plays (during pause & run state)
    float currVolumeMusic = .5f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

    //timer variables - load into Label
    final float maxTime = Constants.MAX_TIME+1;
    float timerCount = 0;

    //stages, widgets & messages
    GameStage stageMap; //all the units, tiles go here
    Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
    Stage pauseStage; //pause menu options

    private final String tryingToConnect = Constants.TRY_CONNECT_MSG;
    private final String errorInConnection = Constants.ERROR_CONNECT_MSG;
    private String msg = tryingToConnect;


    Window pauseWindow;
    TextButton[] uiButtons = new TextButton[3];
    Label[] labels = new Label[5];
    Texture goButton;
    Rectangle goBounds;


    //cameras, viewports, input processors
    ScalingViewport scalingViewPort;
    OrthographicCamera camera; //main stage cam
    InputMultiplexer in; //handles input events for stage & stageMap
    Array<InputProcessor> processors; //processors on stages
    ScreenViewport viewport;

    Vector3 touchPoint;

    int currPlayer = 0; //current player whose turn it is
    int playerScore = 0;
    int enemyScore = 0;
    int playerID; //playerID for positions

    public MultiplayerScreen(final MainGame game, StartMultiplayerScreen screen){
        this.game = game;
        this.prevScreen = screen;
        this.gameState = GameState.STARTING;
        this.touchPoint = new Vector3();

        setupCamera();
        setupAudio(); //music which is playing
        setupStages();
        setupUI();

        WarpController.getInstance().setListener(this);
        //setupInitialPositions(); //sets up initial positions (based on name length)
        sendSetupData();
    }

    //method which sets up the camera for this screen
    public void setupCamera(){
        /** the cameras for viewing scene objects**/
        //camera for stage
        camera = new OrthographicCamera(VIEWPORTWIDTH/32, VIEWPORTHEIGHT/32);
        camera.setToOrtho(false, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        camera.update();

    }


    //creates stages
    public void setupStages() {
        viewport = new ScreenViewport();
        viewport.setWorldHeight(VIEWPORTWIDTH); //sets the camera screen view dimensions
        viewport.setWorldWidth(VIEWPORTHEIGHT);
        viewport.setCamera(camera);
        //viewport.setRotation
        scalingViewPort = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);

        stage = new Stage(scalingViewPort); //stage : create the stage for UI
        stageMap = GameUtils.Map.createMap(4);
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

        //all the processors targets created & combined
        in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs
        processors = new Array<InputProcessor>();
        processors.add(stage);
        processors.add(stageMap);
        in.setProcessors(processors);
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
    }

    /** creates the info panel
     *
     */
    public void setupUI() {
        GameUtils.Screen.setupUI(uiButtons, labels, stage, stageMap, currPlayer);
        goButton = Assets.uiSkin.get("lets-go-tex", Texture.class);
        goBounds = new Rectangle(labels[0].getX(), labels[0].getY(), labels[0].getWidth(), labels[0].getHeight());

        setupPauseMenu();

    }


    public void setupPauseMenu( ){
        /** pause stage*/
        pauseWindow = MenuUtils.PauseMenu.pauseWindow(); //pause window
        pauseStage = new Stage(scalingViewPort);
        pauseStage.addActor(pauseWindow);
    }


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


    boolean setupDone = false; //so that sendSetupData does not happen twice
    public void sendSetupData(){
        try{
            if (!setupDone){
                playerID = randomLengthPlayerID(10000);

                UserData userData = new UserData();
                userData.setName(GameData.playerName);
                userData.setPlayerID(playerID);
                userData.setFaction(GameData.playerFaction);
                userData.setUpdateState(0);

                Json json = new Json();
                json.setIgnoreUnknownFields(true);
                String data = json.toJson(userData);
                setupDone = true;

                WarpController.getInstance().sendGameUpdate(data);
            }
        }
        catch(Exception e){
            System.out.println("an exception occured while writing to json on StartMultiplayerScreen");
            e.printStackTrace();
        }
    }

    //sends information about unit details & damage
    private void sendScoreData(){

    }

    private void setupPlayers(int player, int enemyPlayer, String enemyFaction){

        GameData.playerUnits = TestUtils.randomMultiplayerSetup(player, GameData.playerName, GameData.playerFaction);
        stageMap.addUnits(GameData.playerUnits, GameData.playerName);

        GameData.enemyUnits = TestUtils.randomMultiplayerSetup(enemyPlayer, GameData.enemyName, enemyFaction);
        stageMap.addUnits(GameData.enemyUnits, GameData.enemyName);
    }

    private int randomLengthPlayerID(int max){
        Random rand = new Random();
        int id = rand.nextInt(max);

        return id;
    }



    /** this method checks to see if player turn is done
     * either by time running out or player hitting go button
     *
     */
    public void isPlayerDone() {
//		int temp = player; //temp value for player when checking

        if (Gdx.input.isTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (goBounds.contains(touchPoint.x, touchPoint.y)){
                GameData.playerTurn = false;
                GameUtils.StageUtils.clearBoard(stageMap);
            }
        }

//        if (GameData.playerTurn = false) {
//            currPlayer = GameUtils.Player.nextPlayer(currPlayer, uiButtons[1], uiButtons[2], stageMap);
//            sendPlayerData();
//            timerCount = 0;
//        }

        //if the timer reaches max time, playerTurn set to true
        if (timerCount >= maxTime) {
            GameData.playerTurn = true;
            GameUtils.StageUtils.clearBoard(stageMap);	//clears board of selected panels
            timerCount = 0; //reset timer
        }

    }

    private void sendPlayerData(){
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);

            UserData userData = new UserData();
            userData.setPlayerID(currPlayer);
            userData.setScore(playerScore);
            userData.setName(GameData.playerName);


            String data = json.toJson(userData, UserData.class);
            WarpController.getInstance().sendGameUpdate(data);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateWidgets(float delta){

        labels[0].setText("" + (int)timerCount);
        labels[0].act(delta);

        updatePlayerScores();
        if (GameData.chosenUnit!=null) {

            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
        }


        drawGoButton();

    }

    public void drawGoButton(){
        game.batch.begin();
        game.batch.draw(goButton, Constants.GO_X, Constants.GO_Y);
        game.font.draw(game.batch, "GO", Constants.GO_X+4f, Constants.GO_Y+4f);
        game.batch.end();
    }


    public void updatePlayerScores(){
        if (GameData.currPlayer == 1){
            labels[3].setText(Integer.toString(playerScore));
            labels[4].setText(Integer.toString(enemyScore));
        }
        else{
            labels[3].setText(Integer.toString(enemyScore));
            labels[4].setText(Integer.toString(playerScore));
        }
    }


    public void updateCurrent(float delta){

        timerCount += delta;

        updateWidgets(delta); //updates widgets
        isPlayerDone(); //checks to see if next player will go



        stageMap.act(delta); //stage with tiled map & units on it
        stage.act(delta); //stage with other UI elements

//        stageMap.sendPlayerData(GameData.currPlayer, playerScore);

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ){
            Gdx.input.setInputProcessor(pauseStage);
            //handleLeaveGame();
            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
        }

    }

    public void pauseCurrent(float delta){
        pauseWindow.act(delta);
        pauseStage.act(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Gdx.app.log(LOG, "game is resuming");
            gameState = GameState.RESUME;
        }
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


    public void draw(){
        stage.draw();
        stageMap.draw();
    }

    /** updates the stages' batch
     *
     */
    @Override
    public void render(float delta) {
        clearScreen();
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
        //currVolumeMusic = GameData.currVolumeMusic;
        //music.play();

        switch (gameState){
            case STARTING:
                sendSetupData();
                break;
            case RUN :
                //only updates if it is this players turn
                updateCurrent(delta);
                draw();
                break;
            case PAUSE :
                pauseCurrent(delta);
                pause();
                break;
            case RESUME :
                updateCurrent(delta);
                draw();
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
    public void show() {

    }


    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }


    /**
     */
    @Override
    public void pause() {
        clearScreen();
        pauseStage.draw();
    }

    /**
     */
    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        music.dispose();
        stage.dispose();
        stageMap.dispose();
        pauseStage.dispose();
    }

    //resize should be managed by MainGame
    @Override
    public void resize(int width, int height) {

    }


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

        //gameState = GameState.RUN; //run the game
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


    int updateState = 0;
    @Override
    public void onGameUpdateReceived(String message) {
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            UserData data = json.fromJson(UserData.class, message);

            int updateState = data.getUpdateState();

            //updateState: 0 = setup units; 1 = update unit; 2 = update player
            switch (updateState){
                case 0: //initiate setup
                    updateSetup(data);
                    if (setupDone)
                        gameState = GameState.RUN; //run the game
                    break;
                case 1:
                    updateUnit(data);
                    break;
                case 2:
                    updatePlayer(data);
                    break;
                default: //any value other than 0-2, does not update anything in game
                    //do nothing; no updates
                    break;
            }
            System.out.println("Update state : "+ updateState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //updates the initial setup data
    private void updateSetup(UserData data){

        int enemyID = data.getPlayerID();
        GameData.enemyFaction = data.getFaction();
        GameData.enemyName = data.getName();

        if (playerID > enemyID) {

            GameData.playerTurn = true;
            GameData.currPlayer = 1;
            setupPlayers(1, 2, data.getFaction());
        } else if (playerID < enemyID) {

            GameData.playerTurn = false;
            GameData.currPlayer = 2;
            setupPlayers(2, 1, data.getFaction());
        } else {
            setupDone = false;
            sendSetupData();
        }



    }

    //updates a single unit on stage
    private void updateUnit(UserData data){
        stageMap.updateUnit(data.getUnitData());
    }

    //updates the screen mainly (who goes & score)
    private void updatePlayer(UserData data){
        enemyScore = data.getScore();
        currPlayer = data.getPlayer();
    }




}
