/**
 * 
 */
package com.fs.game.stages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.utils.Constants;
import com.fs.game.utils.MapUtils;
import com.fs.game.utils.UnitUtils;


/** the stage which contains the tiled map
 * 
 * @author Allen
 *
 */
public class MapStage extends Stage implements ActionListener{
	
	final String LOG = "MapStage log: ";

	TiledMap tiledMap; 	//creates the actual map
    int test; //determines use of test methods (alternative ATM)

	Panel[][] panelMatrix; //the grids on game board
	private Array<Panel> panelArray;
	
	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_WIDTH = Constants.GRID_WIDTH_B;
	final float GRID_HEIGHT = Constants.GRID_HEIGHT_B;
	final float GRID_X = Constants.GAMEBOARD_X;
	float GRID_Y = Constants.GAMEBOARD_Y;
	float scale = 1/32f;
	
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	OrthographicCamera mapCam;

	Viewport viewport; // 
	ScreenViewport viewportStage;
	
	/**
	 * instantiated by MapsFactory
	 */
	public MapStage(TiledMap tiledMap, int test) {
//		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));		
//
		//sets up the tiled map & renderer
        this.tiledMap = tiledMap;
        this.test = test;
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        setupCamera();//sets up the camera
        setupGridElements(); //get Table & Actors from GameBoard class
        createMapActors(); //creates & adds these actors to map
		createUnits();  
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
	
	public void setupGridElements(){
// 		MapUtils.setupPanels12x12(); //stores data in UnitData
//		panelMatrix = GameData.gridMatrix;
//		this.setPanelArray(GameData.gamePanels);
		
		//2nd kind of setup
		MapUtils.setupPanels16x12();
		panelMatrix = GameData.gridMatrix;
		this.setPanelArray(GameData.gamePanels);
		

 
  	}

    /** these map actors are touchable tiled map cells
     *
     */
	public void createMapActors(){
		//for-each loop thru all layers of map
		for (MapLayer layer : tiledMap.getLayers()) {
		    TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
		    MapUtils.createActorsForLayer(tiledLayer, GameData.gridMatrix, this);

		} //gets all the actors from all the layers
	}
	
 	
	/** initializes & sets units onto stage
	 * creates 7 units on board 
	 *  - gets info from an array in an array
	 *  
	 */
	public void createUnits() {
//		TestUtils.testBoardSetup1_12x12(); //test setup b/w humans & reptoids

		//alternative setup - 16x16 board
		//TestUtils.initializeUnits(getPanelArray(), panelMatrix); 	//initialize units, with GameBoard panelMatrix positions

        if (test==1) {
            TestUtils.test2Units(this);
        }
		else if (test == 2){
            TestUtils.testBoardSetup2_16x12(this); //test setup b/w humans & reptoids
        }
        else{
            TestUtils.testBoardSetup2_16x12(this); //test setup b/w humans & reptoids
        }

	}
 
    /**renders the tiled map
     * 
     */
    public void render() {
    	
    	//take these out possibly TODO:
    	tiledMapRenderer.getSpriteBatch().setProjectionMatrix(camera.combined);
    	tiledMapRenderer.setView(camera.combined, GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);

    	camera.update();
 
    	tiledMapRenderer.setView(camera);
     	tiledMapRenderer.render();
 
    }
    
   
    /* stage maps draw method */
    @Override
    public void draw() {
    	render(); //renders the tiled map

    	super.draw();    	    	

    }
    
    /**
     *  map stage act method 
     *  */
    @Override
    public void act(float delta) {

        for (Unit u : UnitUtils.findAllUnits(getActors())){
            if (u.state== UnitState.DEAD)
                u.remove();
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


	@Override
	public void actionPerformed(ActionEvent e) {

	}
 
}
