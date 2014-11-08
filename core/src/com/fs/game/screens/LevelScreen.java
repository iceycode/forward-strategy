package com.fs.game.screens;

//import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.unused_old_classes.GameBoard;
import com.fs.game.utils.*;

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

public class LevelScreen implements Screen {

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
    public int test = 0; //determines which test configuration, if any, is used (0 is default config)
	MapStage stageMap; //all the units, tiles go here
	Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
	Stage pauseStage; //pause menu options

    Music music; //music that plays (during pause & run state)
  	
	//timer variables - load into Label
	final float maxTime = Constants.MAX_TIME; //45 seconds for each player
	float timerCount = 0; //keeps track of hte time
	public boolean letsGo = false; //if true, switch players

    float currVolumeMusic = 1.0f; //initialize to 1.0f (highest volume)
    float currVolumeSound = 1.0f; //initialize to 1.0f

	/*********widgets, skins, textures for stage********
	 * NOTE - get rid of some not being used
	 */
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
     * @param test
	 * @param game
	 */
	public LevelScreen(final MainGame game, int test) {
		
		this.game = game;
        this.test = test;
 
		setupCamera();
		createStages(); //creates a stage for units & map tiles
        createMusic(); //music which is playing

		//all the processors targets created & combined
		in = new InputMultiplexer(); //inputmultiplexer allows for multiple inputs 
		processors = new Array<InputProcessor>();
		processors.add(stage);
		processors.add(stageMap);
 		in.setProcessors(processors);
	}
	
	//method which sets up the camera for this screen
	public void setupCamera(){
		/** the cameras for viewing scene objects**/
		//camera for stage
		camera = new OrthographicCamera(VIEWPORTWIDTH/32, VIEWPORTHEIGHT/32);
		camera.setToOrtho(false, VIEWPORTWIDTH, VIEWPORTHEIGHT);
		camera.update();
	}

	/*************LEVEL STAGE******************
	 * 
	 */
	public void createStages() {
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
        if (test==1){
            stageMap = MapUtils.createMap(11, test);
        }
        else if (test ==2){
            stageMap = MapUtils.createMap(12, test);
        }
        else{
            stageMap = MapUtils.createMap(4, test); //creates the TiledMap with Tiles as actorsOnStage
        }
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )



		createHUD(); //the info panels during game
        createPauseMenu();
  	}

	/** creates the info panel
	 * 
	 */
	public void createHUD() {
 
 		createInfoHUD();
 		createSideButtons();
 	}

    public void createPauseMenu(){
        /** pause stage*/
        pauseWindow = MenuUtils.pauseWindow(); //pause window
        pauseStage = new Stage(scalingViewPort);
        pauseStage.addActor(pauseWindow);
    }

    /**
     * TODO: create & add more music
     * ...currently only 1 track
     */
    public void createMusic(){
        music = AudioUtils.createMapMusic(0);
        music.setLooping(true); //loops the track
        GameData.currVolumeMusic = 5f; //initializes volume to 5f
        music.setVolume(GameData.currVolumeMusic);

    }


	/** the main info panel that shows unit information
	 * 
	 */
	public void createInfoHUD(){
		
		//the timer
		timer = HUDUtils.createTimer();
  
		/* setup for ScrollPane panels as individual units within table
		 * 
		 */
		//the main pop-up window & widgets
		unitDetail = HUDUtils.createLabelInfo();
		unitDamageList = HUDUtils.createLabelDamage();  
 
 		//individual ScrollPane for each Label sets widget to Table to display:
		//UnitInfo
		Table infoTable = new Table();
		infoTable.add(unitDetail).width(unitDetail.getWidth()).height(unitDetail.getHeight());
 		
		//unit damageList
		Table damTable = new Table();
		damTable.add(unitDamageList).width(unitDamageList.getWidth()).height(unitDamageList.getHeight());
		
		//the scrollpanes
		ScrollPane infoScroll = HUDUtils.createInfoScroll(infoTable, Constants.INFO_X, Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);
 		ScrollPane damageScroll = HUDUtils.createInfoScroll(damTable, Constants.INFO_X+Constants.INFO_W, Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);
 		
		//scrollTable is the Table which holds the ScrollPane objects
		scrollTable = HUDUtils.createInfoTable(infoScroll, damageScroll);
 
		//adding a Label within ScrollPane within Table to stage
		stage.addActor(scrollTable);
  		stage.addActor(timer);
 		
	}
	
	/** creates the side panels next to the board
 
	 * TODO: - need to figure out what else to put here
	 *   		- perhaps a score board 
	 */
	public void createSideButtons() {
 
		//The side panel buttons indicating whose turn it is
		p1Button = HUDUtils.createSideButton(Constants.BT1_X, Constants.BT_Y);
 		p2Button = HUDUtils.createSideButton(Constants.BT2_X, Constants.BT_Y);
 		p2Button.toggle(); //toggle it as checked state since player one will be going first
 		
 		//for test purposes
 		//TODO: create a better go button for players
 		goButton = HUDUtils.createGoButton();
 		
 		//add a listener to the go button so player turn changes
 		goButton.addListener(new ActorGestureListener(){
 			@Override
			public void touchDown (InputEvent event, float x, float y, int pointer, int button) {
 				letsGo = true;  //player is manually finished turn (timer did not reset)
 				MapUtils.clearBoard(stageMap);	//clears board of selected panels
 				timerCount = 0;
  	        }
 		});
		
 		//add the actors
		stage.addActor(p1Button);
		stage.addActor(p2Button);
		stage.addActor(goButton);
	}
 


	

	
	/** this method checks to see if player time is up or is done
	 * 
	 */
	public void nextPlayerGo() {
		int temp = player; //temp value for player when checking
		
		if (letsGo) {
			//decides based on player which player to lock
			if (temp == 1) {
				MapUtils.lockPlayerUnits(player, stageMap);  //lock these player units
				p1Button.toggle();		//toggle checked stage (button is red)
 				player++;				//next player
				MapUtils.unlockPlayerUnits(player, stageMap); 	//unlock player units
				p2Button.toggle();	//toggle checked state p2
 			}
			else {
 				MapUtils.lockPlayerUnits(player, stageMap);
 				p2Button.toggle();	
 				player--;
                MapUtils.unlockPlayerUnits(player, stageMap); 	//unlock player units
 				p1Button.toggle();
			}
		}
		letsGo = false;
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
            default :
                show();
                break;
		}
 		
			
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		stageMap.getViewport().update(width, height, true);
		
		//Set the viewport to the whole screen.
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//restore the stage's viewport.
		stage.getViewport().update(width, height, true);
		stageMap.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
        Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		Gdx.input.setInputProcessor(in); //set input processor in show method

		timerCount += Gdx.graphics.getDeltaTime();
		nextPlayerGo(); //checks to see if next player will go
		
		//TODO:  currently set to player 1 going first
  		//if the timer reaches max time, lets co set to true
		if (timerCount >= maxTime) {
			letsGo = true; 		//when true, next player goes
			MapUtils.clearBoard(stageMap);	//clears board of selected panels
			timerCount = 0; //reset timer
			Gdx.app.log(LOG, " timer count is " +  timerCount + "\n player is : " + Integer.toString(player));
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
//	  	if (Gdx.input.isTouched()) {
//
//   			Array<Unit> p1Units = MapUtils.findAllUnits(stageMap.getActors());
//			for (Unit u : p1Units) {
//				//show unit info & damage lists
//				if (u.chosen) {
//					//finds enemy units nearby & fights them
//  					unitDetail.setText(UnitUtils.unitDetails(u));
// 					unitDamageList.setText(UnitUtils.unitDamageList(u));
//   				}
//
//				//u.updateUnitDataArrays(stageMap.getActors());
//  			}
// 		}
	  	
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
