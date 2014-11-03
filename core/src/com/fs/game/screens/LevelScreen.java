package com.fs.game.screens;

//import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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
import com.fs.game.main.MainGame;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.tests.TestStage;
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
	
	int gameState = Constants.GAME_RUNNING; //starts out running
	
 
	final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
	final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_WIDTH = Constants.GRID_WIDTH_B;
	final float GRID_HEIGHT = Constants.GRID_HEIGHT_B;
	
	final float GRID_ORI_X = Constants.GAMEBOARD_X;
	final float GRID_ORI_Y = Constants.GAMEBOARD_Y;
	final String LOG = "LevelScreen log: ";
	
	//utilities for unit's user interface
	UnitUtils uf;
	Unit currUnit;
 	Array<Unit> p1Units;
	Array<Unit> p2Units;
	Array<Panel> panelArray;
	int player = 1; //starts with player 1 going first
	
	/** the stages & their utilities/components******
	 * for units & maps
	 */
	InputMultiplexer in; //handles input events for stage
 
  	//The stages
    public boolean test ; //determines whether test stage is used
	MapStage stageMap; //all the units, tiles go here
    TestStage testStage; //a stage for testing
	Stage stage; 	//this shows unit info, timer, player turn, etc. (HUD)
	Stage pauseStage; //pause menu options

    Music music; //music that plays during screen

	Unit[][] unitArr; //stores units as actors
	Panel[][] panelMatrix;//stores positions of panels as matrix
  	
	//timer variables - load into Label
	final float maxTime = Constants.MAX_TIME; //45 seconds for each player
	float timerCount = 0; //keeps track of hte time
	public boolean letsGo = false; //if true, switch players


	/*********widgets, skins, textures for stage********
	 * NOTE - get rid of some not being used
	 */
	Skin skin; //skin used for timer
	Table infoTable;
	Label timer; //the timer, background set to timer.png 
	Label unitDetail;
	Label unitDamageList;
	TextButton goButton;
	Button p1Button;
	Button p2Button;
	ScrollPane scrollPaneInfo;
	Table scrollTable;
	Window popUpInfo;
	//TextureRegion class describes a rectangle inside a texture
	// and is useful for drawing only a portion of the texture.
	TextureRegion[] gridRegions;
	TextureRegion[][] gridMatrix;
 
	//cameras 
	OrthographicCamera camera; //main stage cam
 
	//protected Vector2 screenCoord;

	//Arrays  for actors, panels, units, processors
	Array<InputProcessor> processors;
	Array<Unit> unitsOnStage;
	Array<Panel> panelsOnStage; //all panels
	Array<Actor> actorsOnStage; //all actors
	Array<Array<Unit>> playerUnits; //get this from previous screen
	
	protected ScreenViewport viewport;
 	//protected ActorGestureListener unitGestureListener;
	protected InputListener stageInputListener;

	//the actor listeners on stage
	protected InputListener stagePanelListener;
	protected InputListener stageUnitListener;
 
	/** the main screen for game play
	 * test determines whether a test stage will pop up
     *
     * @param test
	 * @param game
	 */
	public LevelScreen(final MainGame game, boolean test) {
		
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
		ScalingViewport scalingViewPort = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);
						
		/** stage : create the stage for HUD */
		stage = new Stage(scalingViewPort); //has its own batch
		//stage.setViewport(viewport);


        //either create test stage or normal stage
        if (test){
            stageMap = MapUtils.createMap(11, test);
        }
        else{
            /** stageMap : contains tile map & actors associated with it ****/
            stageMap = MapUtils.createMap(4, test); //creates the TiledMap with Tiles as actorsOnStage
        }
        stageMap.setViewport(scalingViewPort); //sets viewport (renderer must have same )

		/** pause stage*/
		pauseStage = new Stage(scalingViewPort);
		Window pauseWin = MenuUtils.pauseWindow();
		pauseStage.addActor(pauseWin);

		createHUD(); //the info panels during game

  	}

	/** creates the info panel
	 * 
	 */
	public void createHUD() {
 
 		createInfoHUD();
 		createSideButtons();
 	}

    /**
     * TODO: create & add more music
     * ...currently only 1 track
     */
    public void createMusic(){
        music = AudioUtils.createMapMusic(0);
        music.setLooping(true); //loops the track
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
 				clearBoard();	//clears board of selected panels
 				timerCount = 0;
  	        		}
 		});
		
 		//add the actors
		stage.addActor(p1Button);
		stage.addActor(p2Button);
		stage.addActor(goButton);
	}
 
	 
	/** clears the stage of any active (selected) panels
	 * 
	 */
	public void clearBoard(){
		for (Panel p : stageMap.getPanelArray()){
			if (p.selected || p.moveableTo){
				p.selected = false;
				p.moveableTo = false;
				p.viewing = false;
			}
		}
	}
	

	
	/** update player turn
	 * - int value to determine which player's units to lock
	 * 
	 *
	 * @param player
	 */
	public void lockPlayerUnits(int player) {
 		Unit u = new Unit(); //initialize constructor
 		Array<Unit> allUnits = MapUtils.findAllUnits(stageMap.getActors());
 		
		//look through all units to see if certain ones locked or not
		for (int i = 0; i < allUnits.size; i++) {
			u = allUnits.get(i);

			//lock all units of this player
			if (!u.isLock() && u.player == player) {
				u.lock = true;
				u.done = true;
				u.chosen = false;
			}
		}
 	}
	
	/**
	 * 
	 * @param player
	 */
	public void unlockPlayerUnits(int player) {
 		Unit u = new Unit(); //initialize constructor
 		Array<Unit> allUnits = MapUtils.findAllUnits(stageMap.getActors());
 
		//look through all units to see if certain ones locked or not
		for (int i = 0; i < allUnits.size; i++) {
			u = allUnits.get(i);
			//unlock if this is not player being locked
			if (u.isLock() && u.player == player) {
				u.lock = false;
				u.done = false;
				u.standing = true;
 			}
		}
	}
	
	/** this method checks to see if player time is up or is done
	 * 
	 */
	public void nextPlayerGo() {
		int temp = player; //temp value for player when checking
		
		if (letsGo) {
			//decides based on player which player to lock
			if (temp == 1) {
				lockPlayerUnits(player);  //lock these player units
				p1Button.toggle();		//toggle checked stage (button is red)
 				player++;				//next player
				unlockPlayerUnits(player); 	//unlock player units
				p2Button.toggle();	//toggle checked state p2
 			}
			else {
 				lockPlayerUnits(player);
 				p2Button.toggle();	
 				player--;
 				unlockPlayerUnits(player);
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
		Gdx.graphics.getGL20().glClearColor(0,0,0,1); //sets the color of clear screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		
		switch (gameState){
			case Constants.GAME_RUNNING : show();
				break;
			case Constants.GAME_PAUSED : pause();
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
        music.play();
		Gdx.input.setInputProcessor(in); //set input processor in show method

		timerCount += Gdx.graphics.getDeltaTime();
		nextPlayerGo(); //checks to see if next player will go
		
		//TODO: make 1st player random ... currently set to player 1 going first 		
  		//if the timer reaches max time, lets co set to true
		if (timerCount >= maxTime) {
			letsGo = true; 		//when true, next player goes
			clearBoard();	//clears board of selected panels
			timerCount = 0; //reset timer
			Gdx.app.log(LOG, " timer count is " +  timerCount + "\n player is : " + Integer.toString(player));
		}

        //*****Render the stage(s)*******
     	//stage with tiled map on it
		float delta = Gdx.graphics.getDeltaTime();
		stageMap.act(delta);
     	stageMap.draw();

     	//stage with the unit actors & UI on it
		stage.act(delta);
		stage.draw();	
		// removed in libgdx 1.4.1 -->Table.drawDebug(stage);                                                                       
        
		timer.setText("" + (int)timerCount);
		timer.act(delta); 
		
		Gdx.input.setInputProcessor(in); //in order to be called when new input arrives
		
	  	if (Gdx.input.isTouched()) {
	  		
   			Array<Unit> allUnits = MapUtils.findAllUnits(stageMap.getActors());
			for (Unit u : allUnits) {
				//show unit info & damage lists				
				if (u.chosen) {
					//finds enemy units nearby & fights them 
  					unitDetail.setText(UnitUtils.unitDetails(u));
 					unitDamageList.setText(UnitUtils.unitDamageList(u)); 
   				}
				
				//u.updateUnitDataArrays(stageMap.getActors());
  			}	
 		}
	  	
	  	if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ){
	  		if(Gdx.input.isKeyPressed(Keys.P)){
	  			Gdx.app.log(LOG, "gameState is paused");
	  			gameState = Constants.GAME_PAUSED;
	  		}
  		}
	  	
 	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {
		Gdx.input.setInputProcessor(pauseStage);
		
		pauseStage.act();
		pauseStage.draw();
		
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ){
	  		if(Gdx.input.isKeyPressed(Keys.P)){
	  			gameState = 1; //game runs again
	  		}
		}
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
        music.dispose();
        stage.dispose();
        stageMap.dispose();
	}
 

}
