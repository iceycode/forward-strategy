package com.fs.game.screens;

//import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
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
	
	GameState gameState = GameState.RUN; //starts out running
	GameData gd;
 
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



    Array<Unit> playerUnits;
    Array<Unit> player1Units;
    Array<Unit> player2Units;
    int userPlayer = 0; //this player (for multiplayer)
    int currPlayer = 0; //current player
    int playerScore = 0;
    int[] playerScores = {0, 0};
    String playerName;
    boolean playerSet; //true if player is set up

	/** the main screen for game play
	 * test determines whether a test stage will pop up
     *
	 * @param game
	 */
	public GameScreen(final MainGame game) {
		this.game = game;
//        this.testChoice = GameData.testType;
 
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
        currPlayer = GameUtils.Player.randPlayer();
        GameUtils.Screen.setupUI(uiButtons, labels, stage, stageMap, currPlayer);
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
    /** this method checks to see if player time is up or is done
	 * 
	 */
	public void changePlayer() {
        GameData.currPlayer = currPlayer;
        if (GameData.playerTurn == true) {
            GameUtils.Player.nextPlayer(currPlayer, uiButtons[1], uiButtons[2], stageMap);

            GameData.playerTurn = false;
            timerCount = 0;
        }

        //if the timer reaches max time, lets co set to true
        if (timerCount >= maxTime) {
            GameData.playerTurn = true;
            GameUtils.StageUtils.clearBoard(stageMap);	//clears board of selected panels
            timerCount = 0; //reset timer
        }
    }


    public void updateCurrent(float delta){
        Gdx.input.setInputProcessor(in); //in order to be called when new input arrives


        updateWidgets(delta);
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

        pauseStage.act(delta);
        //shows the main pause window
        pauseWindow.act(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
            Gdx.app.log(LOG, "game is resuming");
            gameState = GameState.RESUME;
        }
    }

	
	/** updates the stages' batch
	 * 
	 */
	@Override
	public void render(float delta) {

        currVolumeMusic = GameData.volumes[1];
        music.play();
		
		switch (gameState){
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

        //float delta = Gdx.graphics.getDeltaTime();

     	stageMap.draw();
		stage.draw();

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

    }


}
