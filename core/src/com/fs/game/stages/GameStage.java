/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.maps.Panel;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.UnitUtils;

import java.util.HashMap;


/** the stage which contains the tiled map
 * 
 * @author Allen
 *
 */
public class GameStage extends Stage {
	
	final String LOG = "MapStage log: ";

	public TiledMap tiledMap; 	//creates the actual map
	private Array<Panel> panelArray;
	
	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_WIDTH = Constants.GRID_WIDTH;
	final float GRID_HEIGHT = Constants.GRID_HEIGHT;
	final float GRID_X = Constants.GAMEBOARD_X;
	float GRID_Y = Constants.GAMEBOARD_Y;
	float scale = 1/32f;
	
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	OrthographicCamera mapCam;

	Viewport viewport;
	ScreenViewport viewportStage;

    Unit currUnit;
    boolean unitSelected = false; //whether or nmot a unit on board chosen
    HashMap<Integer, Array<String>> damageTextMap; //hashmap containing damage list text

	/**
	 * instantiated by MapsFactory
	 */
	public GameStage(TiledMap tiledMap) {
//		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));		
//
		//sets up the tiled map & renderer
        this.tiledMap = tiledMap;
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        setupCamera();//sets up the camera
        setupGridElements(); //get Table & Actors from GameBoard class
		addUnits();

    }
	
	//sets up camera
	public void setupCamera(){
		camera = new OrthographicCamera(Constants.SCREENHEIGHT, Constants.SCREENWIDTH);
		camera.setToOrtho(false, 800, 500);
		
 		//tiled map lines up well with this setup
		//camera.position.set(GRID_X-16, GRID_Y+50, 0);
        camera.position.set(GRID_X + 112, GRID_Y +50, 0);
		camera.update();	
        
        viewport = new ScreenViewport();
		viewport.setWorldHeight(SCREENHEIGHT); //sets the camera screen view dimensions
		viewport.setWorldWidth(SCREENWIDTH);
		viewport.setCamera(camera);
		
 	}

    //TODO: fuse panel actors with tiledmap actors
	public void setupGridElements(){
// 		MapUtils.setupPanels12x12(); //stores data in UnitData
//		panelMatrix = GameData.panelMatrix;
//		this.setPanelArray(GameData.gamePanels);
		
		//2nd kind of setup
		GameUtils.setupPanels16x12();
		GameUtils.createMapActors(this);

  	}


	
 	
	/** initializes & sets units onto stage
	 * creates 7 units on board 
	 *  - gets info from an array in an array
	 *  
	 */
	public void addUnits() {

        if (GameData.testType==1) {
            TestUtils.test2Units(this);
        }
		else if (GameData.testType == 2){
            TestUtils.testBoardSetup2_16x12(this); //test setup b/w humans & reptoids
        }
        else{
            TestUtils.testBoardSetup2_16x12(this); //test setup b/w humans & reptoids
        }

        damageTextMap = new HashMap<Integer, Array<String>>();
        putIntoMap(GameData.p1Units, GameData.p2Units); //adds 1st players unit's damage to 2nd player
        putIntoMap(GameData.p2Units, GameData.p1Units); //adds 2nd players unit's damage to 1st player

	}
 
    /**renders the tiled map
     * 
     */
    public void render() {

    	tiledMapRenderer.getSpriteBatch().setProjectionMatrix(camera.combined);
    	tiledMapRenderer.setView(camera.combined, GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);
    	camera.update();
    	tiledMapRenderer.setView(camera);
     	tiledMapRenderer.render();

    }


    public void putIntoMap(Array<Unit> playerUnits, Array<Unit> enemyUnits){


        for (Unit unit : playerUnits) {
            Array<String> damageTexts = new Array<String>();
            for (Unit enemy : enemyUnits){
                int indexEnemy = enemy.getUnitID()-1;
                String damageText = Integer.toString(unit.damageList[indexEnemy]);
                damageTexts.add(damageText);
            }
            damageTextMap.put(unit.getUnitID(), damageTexts);
        }

    }

    public void updateDamageLabels(){
        for (Unit u : GameData.enemyUnits){
            for (String damage : damageTextMap.get(u.getUnitID())){
                Label label = UnitUtils.createDamageLabel(u, damage);
                addActor(label);
            }
        }

    }
   
    /* stage maps draw method */
    @Override
    public void draw() {
    	render(); //renders the tiled map & damageLabelGroup

    	super.draw();

    }


    
    /**
     *  map stage act method 
     *  */
    @Override
    public void act(float delta) {
        if (GameData.unitIsChosen) {
            updateDamageLabels(); //update damage labels if unit is selected
        }

     	super.act(delta);
    }

	/**
	 * @return the camera
	 */
	@Override
	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * @return the viewport
	 */
	@Override
	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * @param viewport the viewport to set
	 */
	@Override
	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}


	public Array<Panel> getPanelArray() {
		return panelArray;
	}

	public void setPanelArray(Array<Panel> panelArray) {
		this.panelArray = panelArray;
	}


}
