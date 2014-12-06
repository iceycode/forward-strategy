package com.fs.game.screens;

import appwarp.WarpController;
import appwarp.WarpListener;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.stages.GameStage;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.utils.*;

/** The multiplayer screen extends GameScreen, implements WarpListener
 * - the online multiplayer version of GameScreen
 *
 * Created by Allen on 11/16/14.
 */
public class MultiplayerScreen implements Screen, WarpListener{

    final MainGame game;
    final String LOG = "MultiplayerScreen log: ";

    MainScreen prevScreen; //the previous screen (MainScreen or MenuScreen)
    GameState gameState; //current game state

    UserData userData; //user data for game (a serialized JSON file)

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;

    boolean switchPlayer;

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

    private final String[] tryingToConnect = Constants.TRY_CONNECT_MSGS;
    private final String[] waitForOtherUser = Constants.WAIT_MSGS;
    private final String[] errorInConnection = Constants.ERROR_CONNECT_MSGS;
    private final String[] game_win = Constants.GAME_WIN_MSGS;
    private final String[] game_loose = Constants.GAME_LOSE_MSGS;
    private final String[] enemy_left = Constants.PLAYER_LEFT_MSGS;

    private String[] msg = tryingToConnect;

    Window pauseWindow;

    TextButton[] uiButtons = new TextButton[3];
//    TextButton goButton;
//    TextButton p1Button; //to show which player's turn it is
//    TextButton p2Button;

    Label[] labels = new Label[5];
//    Label timer; //the timer, background set to timer.png
//    Label unitDetail;
//    Label unitDamageList;
//    Label scoreLabel1;    //for scores
//    Label scoreLabel2;


    //cameras, viewports, input processors
    ScalingViewport scalingViewPort;
    OrthographicCamera camera; //main stage cam
    InputMultiplexer in; //handles input events for stage & stageMap
    Array<InputProcessor> processors; //processors on stages
    ScreenViewport viewport;

    Array<Unit> playerUnits;
    Array<Unit> enemyUnits;
    int currPlayer = 0; //current player
    int playerScore = 0;
    int enemyScore = 0;

    int[] playerScores = {0, 0}; //2nd is enemies score

    String playerFaction;
    String enemyFaction;
    String enemyName;
    String userName;

    UserData playerData;
    UserData enemyData;

    public MultiplayerScreen(final MainGame game, MainScreen screen){
        this.game = game;
        this.prevScreen = screen;
        this.switchPlayer = false;
        this.gameState = GameState.WAITING;
        this.userName = GameData.playerName;

        setupCamera();
        setupAudio(); //music which is playing
        setupStages();


        WarpController.getInstance().setListener(this);
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

        /** stage : create the stage for HUD */
        stage = new Stage(scalingViewPort); //has its own batch
        /** stageMap : contains tile map & actors associated with it ****/
        //either create test stage or normal stage
        stageMap = GameUtils.Map.createMap(4); //creates the TiledMap with Tiles as actorsOnStage
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

        setupUI(); //the info panels during game

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
        GameUtils.Screen.setupUI(uiButtons, labels, stage, stageMap);
        setupPauseMenu();

    }

//    /** the main info panel that shows unit information
//     *
//     */
//    public void setupLabels(){
//
//        //create the timer & add it to stage
//        timer = UIUtils.createTimer();
//        //----setup for ScrollPane panels as individual units within table----
//        //the main pop-up window & widgets
//        unitDetail = UIUtils.createLabelInfo();
//        unitDamageList = UIUtils.createLabelDamage();
//
//        //scrollTable is the Table which holds the ScrollPane objects
//        Table scrollTable = UIUtils.createUnitScrollTable(unitDetail, unitDamageList);
//
//        //initalize scores
//        GameData.scoreP1 = 0;
//        GameData.scoreP2 = 0;
//        //score labels
//        scoreLabel1 = UIUtils.scoreBoard(0, 8f, Constants.SCREENHEIGHT - 40);
//        scoreLabel2 = UIUtils.scoreBoard(0, Constants.SCREENWIDTH - 72, Constants.SCREENHEIGHT - 40);
//        stage.addActor(scoreLabel1);
//        stage.addActor(scoreLabel2);
//
//
//        //adding a Label within ScrollPane within Table to stage
//        stage.addActor(scrollTable);
//        stage.addActor(timer);
//    }
//
//    /** creates the side panels next to the board
//     *
//     *
//     */
//    public void createSideButtons() {
//
//        //The side panel buttons indicating whose turn it is
//        p1Button = UIUtils.createSideButton("P1", Constants.BT1_X, Constants.BT_Y);
//        p2Button = UIUtils.createSideButton("P2", Constants.BT2_X, Constants.BT_Y);
//
//        //for test purposes
//        goButton = UIUtils.createGoButton(stageMap);
//
//        //add the actors
//        stage.addActor(p1Button);
//        stage.addActor(p2Button);
//        stage.addActor(goButton);
//
//        //locks those player units whose turn it is not
//        GameData.playerTurn = false;
//        GameData.currPlayer = GameUtils.Player.randPlayer();
//        GameUtils.Player.nextPlayer(GameData.currPlayer, p1Button, p2Button, stageMap);
//    }


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

/** this method checks to see if player time is up or is done
     *
     */
    public void changePlayer() {
//		int temp = player; //temp value for player when checking

        if (GameData.playerTurn == true) {
            GameUtils.Player.nextPlayer(currPlayer, uiButtons[1], uiButtons[2], stageMap);

            GameData.playerTurn = false;
            timerCount = 0;
        }

        //if the timer reaches max time, lets co set to true
        if (timerCount >= maxTime) {
            GameData.playerTurn = true;
            GameUtils.Map.clearBoard(stageMap);	//clears board of selected panels
            timerCount = 0; //reset timer
        }

    }



    public void updateWidgets(float delta){
        timerCount += delta;

        labels[0].setText("" + (int)timerCount);
        labels[0].act(delta);

        labels[3].setText(Integer.toString(playerScores[0]));
        labels[4].setText(Integer.toString(playerScores[1]));

        if (GameData.chosenUnit!=null) {

            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
        }

    }


    public void updateWaiting(){
        game.batch.begin();

        float y = 230;
        for (int i = msg.length-1; i >= 0; i--) {
            float width = game.font.getBounds(msg[i]).width;
            game.font.draw(game.batch, msg[i] + " current player: " + Integer.toString(currPlayer), 160-width/2, y);
            y += game.font.getLineHeight();
        }

        game.batch.end();
    }


    public void updateCurrent(float delta){
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

        //continually updates stageMap based on current player
        MultiUtils.sendPlayerData(GameData.currPlayer, playerScore, userName, stageMap.unitsData);

        updateWidgets(delta); //updates widgets
        changePlayer(); //checks to see if next player will go


        //MultiUtils.sendPlayerData(currPlayer, playerScore, playerName, playerUnits);

        stageMap.act(delta); //stage with tiled map & units on it
        stage.act(delta); //stage with other UI elements
        stageMap.draw();
        stage.draw();

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


    public void quitCurrent(){
        WarpController.getInstance().handleLeave();

        game.setScreen(prevScreen);
        dispose();
    }


    public void clearScreen(){
        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    /** updates the stages' batch
     *
     */
    @Override
    public void render(float delta) {
        //currVolumeMusic = GameData.currVolumeMusic;
        //music.play();

        switch (gameState){
            case WAITING:
                updateWaiting();
                break;
            case RUN :
                updateCurrent(delta);
                show();
                break;
            case PAUSE :
                pauseCurrent(delta);
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
                quitCurrent();
                dispose();
                break;
        }


    }


    @Override
    public void show() {
        clearScreen();

        stage.draw();
        stageMap.draw();
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
        gameState = GameState.WAITING;
        this.msg = tryingToConnect;

        //first player joins first
        //this.player1Units = TestUtils.randomMultiplayerSetup(currPlayer, stageMap);
    }

    @Override
    public void onError(String message) {
        gameState = GameState.WAITING;
        this.msg = errorInConnection;
    }


    @Override
    public void onGameStarted(String message) {
        currPlayer = GameUtils.Player.randPlayer();
        playerFaction = GameUtils.Player.randomFaction();


        MultiUtils.sendStartData(currPlayer, playerFaction);

//        Gdx.app.postRunnable(new Runnable() {
//            @Override
//            public void run () {
//
//            }
//        });
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




    //TODO: figure out if GameData is necessary or keep unit data vars in screen
    //TODO: use player name instead of player side
    @Override
    public void onGameUpdateReceived(String message) {

        try {
            Json json = new Json();

            if (gameState ==GameState.WAITING){

                enemyData = json.fromJson(UserData.class, message);

                enemyName = enemyData.getName();

                if (enemyData.getPlayer() == 1 && currPlayer == 1)
                    currPlayer = 2;

                this.playerUnits = TestUtils.randomMultiplayerSetup(currPlayer, enemyFaction, stageMap);
                this.enemyUnits = TestUtils.randomMultiplayerSetup(enemyData.getPlayer(), playerFaction, stageMap);

                gameState = GameState.RUN;
            }
            else{
                UserData userData = json.fromJson(UserData.class, message);
                GameData.playerName = userData.getName();

                playerScore = userData.getScore();
                GameData.currPlayer = userData.getPlayer();
                currPlayer = userData.getPlayer();
                playerScores[currPlayer - 1] = userData.getScore();



            //stageMap.updateStage(playerUnits, currPlayer);

////            MultiUtils.sendPlayerData(currPlayer, playerScore, playerName, player1Units, stageMap);
//            if (currPlayer == 1){
//                this.player1Units = unitData.getUnitList();
//                playerScores[0] += playerScore;
//                MultiUtils.sendPlayerData(currPlayer, playerScore, userName, player1Units, stageMap);
//            }
//            else{
//                this.player2Units = unitData.getUnitList();
//                playerScores[1] += playerScore;
//                MultiUtils.sendPlayerData(currPlayer, playerScore, playerName, player2Units, stageMap);
//            }
////
            }
        } catch (Exception e) {
            System.out.print("An exception occured while sending data: \n" + e.toString());
        }
    }
}
