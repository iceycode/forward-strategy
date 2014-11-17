package com.fs.game.screens;

//import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.AudioUtils;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.MenuUtils;
import com.fs.game.utils.UIUtils;

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
	
 
	final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
	final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_ORI_X = Constants.GAMEBOARD_X;
	final float GRID_ORI_Y = Constants.GAMEBOARD_Y;
	final String LOG = "LevelScreen log: ";
	
	//utilities for unit's user interface
//	UnitUtils uf;
//	Unit currUnit;
// 	Array<Unit> p1Units;
//	Array<Unit> p2Units;
//	Array<Panel> panelArray;
	int player = 1; //starts with player 1 going first
	
	/** the stages & their utilities/components******
	 * for units & maps
	 */
	InputMultiplexer in; //handles input events for stage
 
  	//The stages
    public int mapChoice; //the map chosen during this game
    public int testChoice; //determines which test configuration, if any, is used (0 is default config)

	GameStage stageMap; //all the units, tiles go here
	Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
	Stage pauseStage; //pause menu options

    Music music; //music that plays (during pause & run state)
  	
	//timer variables - load into Label
	final float maxTime = Constants.MAX_TIME+1;
    float timerCount = 0;
    float currVolumeMusic = .5f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

	//*********widgets, skins, textures for stage********
	Label timer; //the timer, background set to timer.png 
	Label unitDetail;
	Label unitDamageList;

    TextButton goButton;
    Button p1Button;
	Button p2Button;

	Table scrollTable;

    Window pauseWindow;

    ScalingViewport scalingViewPort;

	//cameras 
	OrthographicCamera camera; //main stage cam
	Array<InputProcessor> processors; //processors on stages
	
	protected ScreenViewport viewport;
	protected InputListener stageInputListener;

	/** the main screen for game play
	 * test determines whether a test stage will pop up
     *
	 * @param game
	 */
	public GameScreen(final MainGame game) {
		this.game = game;
//        this.testChoice = GameData.testType;
 
		setupCamera();
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
        if (GameData.testType==1){
            GameData.mapChoice = 11;
            stageMap = GameUtils.createMap(11);
        }
        else if (GameData.testType ==2){
            GameData.mapChoice = 12;
            stageMap = GameUtils.createMap(12);
        }
        else{
            stageMap = GameUtils.createMap(4); //creates the TiledMap with Tiles as actorsOnStage
        }
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

		setupUI(); //the info panels during game

        //all the processors targets created & combined
        in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs
        processors = new Array<InputProcessor>();
        processors.add(stage);
        processors.add(stageMap);
        in.setProcessors(processors);
  	}

	/** creates the info panel
	 * 
	 */
	public void setupUI() {

 		createInfoUI();
 		createSideButtons();
        createPauseMenu();

 	}

	/** the main info panel that shows unit information
	 * 
	 */
	public void createInfoUI(){
		
		//create the timer & add it to stage
		timer = UIUtils.createTimer();
        stage.addActor(timer);


		//----setup for ScrollPane panels as individual units within table----
		//the main pop-up window & widgets
		unitDetail = UIUtils.createLabelInfo();
		unitDamageList = UIUtils.createLabelDamage();

		//scrollTable is the Table which holds the ScrollPane objects
		scrollTable = UIUtils.createUnitScrollTable(unitDetail, unitDamageList);

		//adding a Label within ScrollPane within Table to stage
		stage.addActor(scrollTable);

	}
	
	/** creates the side panels next to the board
 
	 * TODO: - put scoreboards here
	 *
	 */
	public void createSideButtons() {
 
		//The side panel buttons indicating whose turn it is
		p1Button = UIUtils.createSideButton(Constants.BT1_X, Constants.BT_Y);
 		p2Button = UIUtils.createSideButton(Constants.BT2_X, Constants.BT_Y);
 		p2Button.toggle(); //toggle it as checked state since player one will be going first
 		
 		//for test purposes
 		//TODO: create a better go button for players
 		goButton = UIUtils.createGoButton(stageMap);
		
 		//add the actors
		stage.addActor(p1Button);
		stage.addActor(p2Button);
		stage.addActor(goButton);
	}




    public void createPauseMenu(){
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
        GameData.currVolumeMusic = .5f; //initializes volume to .5f
        music.setVolume(GameData.currVolumeMusic);

    }




    /** this method checks to see if player time is up or is done
	 * 
	 */
	public void nextPlayerGo() {
//		int temp = player; //temp value for player when checking

		if (GameData.finishTurn) {
			//decides based on player which player to lock
            GameUtils.nextPlayer(p1Button, p2Button, stageMap);
		}
        GameData.finishTurn = false;
        timerCount = 0;

    }
	
	
	/** updates the stages' batch
	 * 
	 */
	@Override
	public void render(float delta) {
		//glClearColor takes 3 RGB float values & alpha
        currVolumeMusic = GameData.currVolumeMusic;
        music.play();
		
		switch (gameState){
			case RUN : show();
				break;
			case PAUSE : pause();
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
	public void resize(int width, int height) {
//		stage.getViewport().update(width, height, true);
//		stageMap.getViewport().update(width, height, true);
		
		//Set the viewport to the whole screen.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//restore the stage's viewport.
		stage.getViewport().update(width, height, true);
		stageMap.getViewport().update(width, height, true);
        pauseStage.getViewport().update(width, height, true);


	}

	@Override
	public void show() {
        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.input.setInputProcessor(in); //set input processor in show method

		timerCount += Gdx.graphics.getDeltaTime();
		nextPlayerGo(); //checks to see if next player will go

		//TODO: randomize who goes first
  		//if the timer reaches max time, lets co set to true
		if (timerCount >= maxTime) {
            GameData.finishTurn = true; 		//when true, next player goes
			GameUtils.clearBoard(stageMap);	//clears board of selected panels
			timerCount = 0; //reset timer
			Gdx.app.log(LOG,
                    " timer count is " +  timerCount + "\n player is : " + Integer.toString(GameData.currPlayer));
		}

        //*****Render the stage(s)*******
     	//stage with tiled map on it
		float delta = Gdx.graphics.getDeltaTime();
		stageMap.act(delta);
     	stageMap.draw();

     	//stage with UI on it
		stage.act(delta);
		stage.draw();	
		// removed in libgdx 1.4.1 -->Table.drawDebug(stage);
        
		timer.setText("" + (int)timerCount);
		timer.act(delta); 
		
		Gdx.input.setInputProcessor(in); //in order to be called when new input arrives

        if (GameData.unitDetails!=null) {
            unitDetail.setText(GameData.unitDetails.get(0));
            unitDamageList.setText(GameData.unitDetails.get(1));
        }
	  	
	  	if (Gdx.input.isKeyPressed(Input.Keys.P) ){

            Gdx.app.log(LOG, "gameState is paused");
            gameState = GameState.PAUSE;
  		}
	  	
 	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
        //Gdx.graphics.getGL20().glClearColor(0,1,0,1); //sets the color of clear screen
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.input.setInputProcessor(pauseStage);
        pauseStage.act();
		pauseStage.draw();

        //shows the main pause window
        pauseWindow.act(Gdx.graphics.getDeltaTime());

        if (!Gdx.input.isKeyJustPressed(Keys.R)) {
            Gdx.app.log(LOG, "game is resuming");
            gameState = GameState.RESUME;
        }
	}

	@Override
	public void resume() {
        show(); //game runs again
	}

	@Override
	public void dispose() {
        music.dispose();
        stage.dispose();
        stageMap.dispose();
	}
 

}
