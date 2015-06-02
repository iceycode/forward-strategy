package com.fs.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.fs.game.MainGame;
import com.fs.game.ai.AgentManager;
import com.fs.game.ai.fsm.UnitAgent;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.map.Locations;
import com.fs.game.stages.GameStage;
import com.fs.game.stages.InfoStage;
import com.fs.game.stages.PauseStage;
import com.fs.game.tests.TestScreen;
import com.fs.game.tests.TestUtils;
import com.fs.game.utils.AudioUtils;

/** Abstract Screen class
 * - all other Screens should implement this for simplicity
 *
 *
 * NOTES/IDEAS:
 *   - need to update other screens
 *  Textures needed:
 *   - need to create progress down splash screen
 *  STAGES: need to call viewport before each stage, since both use there own
 *
 * Created by Allen on 5/5/15.
 */
public class GameScreen implements Screen{

    final MainGame game;
    final String LOG = "GameScreen log: ";

    public GameState gameState; //starts out running
    float countDown = 0; //countdown to game start

    public String msg = ""; //Game start/end message
    private String startMsg = "Game Starting for player: "+ GameData.playerName + "\n Game Starting in ";

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;

    float currVolumeMusic = .01f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

    //Stages holding information about Game, main game map & characters & pause menu
    InfoStage stage; //main stage - shows info about gameplay, not game actors
    GameStage stageMap; //gameplay stage - all the units, tiles go here
    PauseStage pauseStage; //pause menu options

    //Game font, text image (TextActor) font & Messages
    BitmapFont font;

    //TODO: put in some audio for units, movements, finishing, etc
    Music music; //music that plays (during pause & run animState)
    Array<Sound> sounds; //sounds TODO: find & add sounds to game

    //For AI - State Machine
    UnitAgent agent; //agent making decisions
    AgentManager aiManager; //manages AI turn - updates during stage act() method

    //cameras & viewport
    ScalingViewport scalingViewPort;
    OrthographicCamera camera; //main stage cam
    public InputMultiplexer in; //handles input events for stage & stageMap


    public GameScreen(final MainGame game){
        this.game = game;
        this.gameState = GameState.STARTING;
        this.font = Assets.uiSkin.getFont("retro1");

        if (GameData.isTest)
            GameData.mapChoice = GameData.testType;

//        setupView(); //sets camera & viewport

        setStages(GameData.mapChoice); //sets the stages

        setInputProcessors();
        setupAudio();
    }

    //method which sets up the viewport & camera for screen
//    public void setupView(){
////        /** the cameras for viewing scene objects**/
////        //camera for stage
////        camera = new OrthographicCamera(VIEWPORTWIDTH , VIEWPORTHEIGHT );
////        camera.setToOrtho(false, VIEWPORTWIDTH, VIEWPORTHEIGHT);
////        camera.update();
//
//        scalingViewPort = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);
////        scalingViewPort.setCamera(camera);
//    }


    /** stage for GamePlay - for units/tiled map
     *
     * @param mapChoice : user or random map choice
     */
    protected void setStages(int mapChoice){
        //sets HUD stage - shows unit damage & info, score, time
        stage = new InfoStage(); //has its own batch

        stageMap = new GameStage(mapChoice);
        // Add Units - for single player, units don't need to wait to be added.
        // For multiplayer, both players need to be linked up first.
        if (MainScreen.gameState == GameState.SINGLEPLAYER || TestScreen.gameState == GameState.SINGLEPLAYER) {
            addUnits();
            //initialize Locations singleton PanelGraph/Node/Connection data
            // this is done after Panels AND Units are done setting up
            Locations.getLocations().initLocations();
        }


        stage.setupMiniMap(stageMap);

        stage.setGameStageListener(stageMap);
        stageMap.setInfoStageListener(stage);

        pauseStage = new PauseStage(this); //set PauseStage
    }

    /** initializes & sets units onto stage
     * creates 7 units on board
     *  - gets info from an array in an array
     *
     */
    public void addUnits() {

        if (GameData.testType==1) {
            TestUtils.test2Units(stageMap);
        }
        else if (GameData.testType == 2){
            TestUtils.testBoardSetup3(stageMap);
        }
        else if (GameData.testType == 4){
            TestUtils.testBoardSetup4(stageMap);
        }
    }



    //sets the input processors for the stage
    protected void setInputProcessors(){
        //all the processors targets created & combined
        in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs

        Array<InputProcessor> processors = new Array<InputProcessor>();
        processors.add(stage);
        processors.add(stageMap);
//        stageMap.addCameraInputProcessor(processors); //add tiled map camera processor
        in.setProcessors(processors);
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
    }


    /** TODO: create & add more music
     * ...currently only 1 track
     */
    public void setupAudio(){
        music = AudioUtils.createMapMusic(0);
        music.setLooping(true); //loops the track

        GameData.volumes[1] = .5f;
        music.setVolume(GameData.volumes[1]);
    }


    //draws stages
    protected void drawStages(){

        stageMap.draw();

        stage.draw();

    }

    /** Draws status messages for players
     *
     */
    public void drawGameMsg(){
        game.batch.begin();
        float y = Constants.SCREENHEIGHT/2;
        float x = Constants.SCREENWIDTH/4;
        game.font.drawMultiLine(game.batch, msg, x, y);
        game.batch.end();
    }


    /** Updates what is currently going on in render
     *
     * @param delta : current time from Gdx
     */
    protected void updateCurrent(float delta){
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

//        stage.updateWidgets(delta);
//        stage.isPlayerDone(); //checks to see if next player will go

        stage.act(delta); //stage with other UI elements
        stageMap.act(delta); //stage with tiled map & units on it

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ){
            Gdx.input.setInputProcessor(pauseStage);
            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
        }
    }


    @Override
    public void show() {
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawStages();
    }

    @Override
    public void render(float delta) {
        currVolumeMusic = GameData.volumes[1];
//        music.play(); //FIXME: enable music eventually

        switch (gameState){
            case STARTING:
                if (MainGame.isTest)
                    gameState = GameState.RUN;
                else {
                    countDown += delta;
                    msg = startMsg + Integer.toString(3 - (int) countDown);
                    drawGameMsg();
                    if (countDown > 3.5) {
                        gameState = GameState.RUN;
                        countDown = 0;
                    }
                }
                break;
            case RUN :
                updateCurrent(delta);
                show();
                break;
            case PAUSE :
                pause();
                break;
            case RESUME :
                show();
                break;
            case QUIT :
                game.setScreen(game.getMainScreen());
                dispose();
                break;
            default :
                show();
                break;
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

    @Override
    public void pause() {
        Gdx.graphics.getGL20().glClearColor(0, 1, 0, 1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pauseStage.act(Gdx.graphics.getDeltaTime());
        pauseStage.draw();
    }

    @Override
    public void resume() {
//        Gdx.input.setInputProcessor(in); //for when returning from pause animState
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        music.dispose();
        stage.dispose();
        stageMap.dispose();
        pauseStage.dispose();
    }



    //sets game animState
    public void setGameState(GameState state){
        this.gameState = state;
    }

    //sets input to InputMultiplexer
    public void setGameInput(){
        Gdx.input.setInputProcessor(in);
    }

    private void log(String message){
        Gdx.app.log(LOG, message);
    }
}

//    protected void updateWidgets(float delta){
//        timerCount += delta;
//        labels[0].setText("" + (int) timerCount);
////        labels[0].act(delta);
//
//        updatePlayerScores();
//        if (GameData.chosenUnit!=null) {
//            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
//            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
//        }
//    }
//
//
//    protected void updatePlayerScores(){
//        if (GameData.getInstance().player == 1){
//            labels[3].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
//            labels[4].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
//        }
//        else{
//            labels[3].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
//            labels[4].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
//        }
//    }
//
//    protected void isPlayerDone() {
//
//        //if the timer reached max time, button or "G" pressed, then player is done
//        if (timerCount > MAX_TIME || uiButtons[0].isPressed() || Gdx.input.isKeyJustPressed(Input.Keys.G)) {
//
//            if (playerTurn){
//                playerTurn = false;
//
//                //switch the current player
//                if (player == 1)
//                    currPlayer = 2;
//                else
//                    currPlayer = 1;
//
//                //toggle button to red (should be green if player's turn)
//                //stageMap.lockPlayerUnits(GameData.getInstance().playerName);  //lock these player units
//                resetTurn();
//            }
//        }
//
//    }
//
//    //resets players turn time & displays message alert
//    protected void resetTurn(){
//        timerCount = 0;
//        uiButtons[player].toggle();
//        if (player == 1)
//            uiButtons[2].toggle(); //since player is either 1 or 2
//        else
//            uiButtons[1].toggle();
//
//        textActor.setText(playerTurnMsg);
//        textActor.act(Gdx.graphics.getDeltaTime());
//    }


//    //sets up textActor & adds to HUD stage, stage
//    protected void setTextActor(){
//        textActor = new TextActor(font, Constants.TURN_MSG_COORD);
//        stage.addActor(textActor);
//    }

//    public void setupPauseMenu(){
//        /** pause stage*/
//        pauseWindow = MenuUtils.PauseMenu.pauseWindow(); //pause window
//        pauseStage = new Stage(scalingViewPort);
//        pauseStage.addActor(pauseWindow);
//    }
