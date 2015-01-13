package com.fs.game.screens;

//import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.actors.TextActor;
import com.fs.game.ai.GameAI;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.AudioUtils;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.MenuUtils;
import com.fs.game.utils.UnitUtils;

/** Holds the string representation of levelScreen
 *  - also holds the background
 *  
 *  NOTES/IDEAS: 
 *   - need to update other screens
 *  Textures needed:
 *   - need to create progress down splash screen
 *   
 * 
 *  
 * @author Allen Jagoda
 *
 */

public class GameScreen implements Screen {

	final MainGame game;
	
	GameState gameState; //starts out running
	GameData gd;
    GameAI ai;

	final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
	final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_ORI_X = Constants.GAMEBOARD_X;
	final float GRID_ORI_Y = Constants.GAMEBOARD_Y;
	final String LOG = "LevelScreen log: ";

	/** the stages & their utilities/components******
	 * for units & maps
	 */


	GameStage stageMap; //all the units, tiles go here
	Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
	Stage pauseStage; //pause menu options
    InputMultiplexer in; //handles input events for stage

    //cameras & viewport
    ScalingViewport scalingViewPort;
    OrthographicCamera camera; //main stage cam
    Array<InputProcessor> processors; //processors on stages
    ScreenViewport viewport;



    Music music; //music that plays (during pause & run state)
  	
	//timer variables - load into Label
	final float maxTime = Constants.MAX_TIME+1;
    float timerCount = 0;
    float currVolumeMusic = .5f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

    //widgets
    TextButton[] uiButtons = new TextButton[3]; //go button, p1 & p2 side button,
    Label[] labels = new Label[5]; //timer, unit detail, unit damages, score p1, score p2
    Window pauseWindow;

    boolean playerTurn = false; //this player (for multiplayer)
    int currPlayer = 1; //current player's turn
    int player = 1; //the main player (AI is always 2)
    int playerScore = 0;
    int enemyScore = 0;
    int[] playerScores = {0, 0};
    String playerName;

    float countDown = 0;
    BitmapFont font;
    TextActor textActor;
    private String msg;
    private String startMsg = "Game Starting for player: "+ GameData.getInstance().playerName + "\n Game Starting in ";
    private String playerTurnMsg = "PLAYER " + currPlayer + " TURN";

	/** the main screen for game play
	 * test determines whether a test stage will pop up
     *
	 * @param game
	 */
	public GameScreen(final MainGame game) {
		this.game = game;
        this.gameState = GameState.STARTING;
        this.font = Assets.uiSkin.getFont("retro1");

		setupCamera();
        setupViewport();
		setupStages(); //creates a stage for units & map tiles
        setupAudio(); //music which is playing

	}
	
	//method which sets up the camera for this screen
	public void setupCamera(){
		/** the cameras for viewing scene objects**/
		//camera for stage
		camera = new OrthographicCamera(VIEWPORTWIDTH/32, VIEWPORTHEIGHT/32);
		camera.setToOrtho(false, VIEWPORTWIDTH, VIEWPORTHEIGHT);
		camera.update();
	}

    public void setupViewport(){
        viewport = new ScreenViewport();
        viewport.setWorldHeight(VIEWPORTWIDTH); //sets the camera screen view dimensions
        viewport.setWorldWidth(VIEWPORTHEIGHT);
        viewport.setCamera(camera);
        //viewport.setRotation
        scalingViewPort = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);
    }


	//creates stages
	public void setupStages() {

						
		/** stage : create the stage for HUD */
		stage = new Stage(scalingViewPort); //has its own batch
        textActor = new TextActor(font, Constants.TURN_MSG_COORD);

        /** stageMap : contains tile map & actors associated with it ****/
        //either create test stage or normal stage
        if (GameData.testType==1){
            stageMap = GameUtils.Map.createMap(11);
        }
        else if (GameData.testType ==2){
            stageMap = GameUtils.Map.createMap(12);
        }
        else if (GameData.testType == 3){
            stageMap = GameUtils.Map.createMap(4); //creates the TiledMap with Tiles as actorsOnStage
        }
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

        setupInputProcessors();
        setupUI(); //the info panels during game

    }

    public void setupInputProcessors(){
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
        GameUtils.Screen.setupUI(uiButtons, labels, stage);
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



    public void updateWidgets(float delta){
        labels[0].setText("" + (int)timerCount);
        labels[0].act(delta);

        updatePlayerScores();
        if (GameData.chosenUnit!=null) {
            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
        }
    }


    public void updatePlayerScores(){
        if (GameData.getInstance().player == 1){
            labels[3].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
            labels[4].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
        }
        else{
            labels[3].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
            labels[4].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
        }
    }

    private void isPlayerDone() {

        //if the timer reaches max time, playerTurn set to true
        if (timerCount > maxTime || uiButtons[0].isPressed() ||
                Gdx.input.isKeyJustPressed(Input.Keys.G)) {

            if (playerTurn){
                playerTurn = false;

                //switch the current player
                if (player == 1)
                    currPlayer = 2;
                else if (player == 2)
                    currPlayer = 1;

                //toggle button to red (should be green if player's turn)
                //stageMap.lockPlayerUnits(GameData.getInstance().playerName);  //lock these player units
                resetTurn();
            }
        }

    }

    public void resetTurn(){
        timerCount = 0;
        uiButtons[player].toggle();
        if (player == 1)
            uiButtons[2].toggle(); //since player is either 1 or 2
        else
            uiButtons[1].toggle();

        textActor.setText(playerTurnMsg);

    }



    public void updateCurrent(float delta){
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

        updateWidgets(delta);
        isPlayerDone(); //checks to see if next player will go

        stageMap.act(delta); //stage with tiled map & units on it
        stage.act(delta); //stage with other UI elements


        if (Gdx.input.isKeyJustPressed(Input.Keys.P) ){
            Gdx.input.setInputProcessor(pauseStage);
            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
        }
    }

    public void pauseCurrent(float delta){

        pauseStage.act(delta);
        //shows the main pause window
        pauseWindow.act(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
            Gdx.app.log(LOG, "game is resuming");
            gameState = GameState.RESUME;
        }
    }

    public void drawGameMsg(){
        game.batch.begin();
        float y = Constants.SCREENHEIGHT/2;
        float x = Constants.SCREENWIDTH/4;
        font.drawMultiLine(game.batch, msg, x, y);
        game.batch.end();
    }

    public void draw(){
        stageMap.draw();
        stage.draw();
    }
	
	/** updates the stages' batch
	 * 
	 */
	@Override
	public void render(float delta) {

        currVolumeMusic = GameData.volumes[1];
        music.play();
		
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
                pauseCurrent(delta);
                pause();
				break;
            case RESUME : show();
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
	public void show() {
        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //<---managed by Game interface
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

     	draw();

 	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
        Gdx.graphics.getGL20().glClearColor(0,1,0,1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		pauseStage.draw();

	}

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

    @Override
    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
//        stageMap.getViewport().update(width, height, true);
//        pauseStage.getViewport().update(width, height, true);

        //Set the viewport to the whole screen.
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //restore the stage's viewport.
        stage.getViewport().update(width, height, true);
        stageMap.getViewport().update(width, height, true);
        pauseStage.getViewport().update(width, height, true);

        camera.update();
        stage.getCamera().update();
        stageMap.getCamera().update();

    }


}
