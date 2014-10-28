/**
 * 
 */
package com.fs.game.stages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.data.GameData;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.unused_old_classes.GameBoard;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;
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
 	Table gridTable; 	//the table that contains grid board
  	
 	Array<Cell> waterCells;
	Array<Cell> obstacleCells;
	Array<Cell> groundCells;
	
	Map<String, Cell> waterTiles;
	Map<String, Cell> obstaclesInScene;
	Map<String, Cell> groundTiles;
	
	World world; //the box2d world
	Body mapBody; //the body into which actors go
	MapActor[][] mapMatrix; //how maps appear on board in matrix form
	
	public Array<MapActor> mapActorArr; //array of MapActor
	Array<Panel> obstacles;

	Panel[][] panelMatrix; //the grids on game board

	private Array<Panel> panelArray;
	
	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_WIDTH = Constants.GRID_WIDTH;
	final float GRID_X = Constants.GRID_X;
	float GRID_Y = Constants.GRID_Y;
	float scale = 1/32f;
	
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	OrthographicCamera mapCam;
	
	Unit currUnit;	//the selected unit
	SequenceAction moveSequence; //unit move sequence
	
	
	Viewport viewport; // 
	ScreenViewport viewportStage;
	
	/**
	 * instantiated by MapsFactory
	 */
	public MapStage(TiledMap tiledMap) {
//		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));		
//
		//sets up the tiled map & renderer
        this.tiledMap = tiledMap; 
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
		
 		//tiled map lines up well with it
		camera.position.set(GRID_X-16, GRID_Y+50, 0);
        camera.update();	
        
        viewport = new ScreenViewport();
		viewport.setWorldHeight(Constants.SCREENHEIGHT); //sets the camera screen view dimensions
		viewport.setWorldWidth(Constants.SCREENWIDTH);
		viewport.setCamera(camera);
		
 	}
	
	public void setupGridElements(){
 		MapUtils.setupPanels(); //stores data in UnitData
		panelMatrix = GameData.gridMatrix;
		this.setPanelArray(GameData.gamePanels);
		//gridTable = MapUtils.createPanelTable(panelMatrix);
		//addActor(gridTable);
		
		
		for (int x = 0; x < Constants.ROWS; x++)
		{
			for (int y = 0; y < Constants.COLS; y++) {
				addActor(panelMatrix[x][y]);
			}
			
		}
 
  	}
	
	public void createMapActors(){
		//for-each loop thru all layers of map
		for (MapLayer layer : tiledMap.getLayers()) {
		    TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
		    MapUtils.createActorsForLayer(tiledLayer, GameData.gridMatrix, this);
		    //Table tileTable = MapUtils.tableFromLayers(tiledLayer);
		    //addActor(tileTable);
		} //gets all the actors from all the layers
	}
	
 	
	/** initializes & sets units onto stage
	 * creates 7 units on board 
	 *  - gets info from an array in an array
	 *  
	 */
	public void createUnits() {
 		UnitUtils.initializeUnits(getPanelArray(), panelMatrix); 	//initialize units, with GameBoard panelMatrix positions
		UnitUtils.testBoardSetup1(); //test setup b/w humans & reptoids
		
		MapUtils.unitsToStage(UnitUtils.playerUnits, panelMatrix, this);
		
		
		//initialize unit move sequence action
		moveSequence = new SequenceAction();
	}
 
    /**renders the tiled map
     * 
     */
    public void render() {
    	
    	//take these out possibly TODO:
    	//tiledMapRenderer.getSpriteBatch().setProjectionMatrix(camera.combined);
    	//tiledMapRenderer.setView(camera.combined, GRID_X, GRID_Y, 384, 384);

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
     	Array<Unit> allUnits = MapUtils.findAllUnits(getActors());
     	

     	for (Unit u : allUnits){
     		if (u.chosen){
     			this.currUnit = u;
     		}
     	}
 
		
		
     	
     	super.act(delta); 

    }
    
	/**
	 * @return the tiledMap
	 */
	public TiledMap getTiledMap() {
		return tiledMap;
	}

	/**
	 * @param tiledMap the tiledMap to set
	 */
	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	/**
	 * @return the tiledMapRenderer
	 */
	public OrthogonalTiledMapRenderer getTiledMapRenderer() {
		return tiledMapRenderer;
	}

	/**
	 * @param tiledMapRenderer the tiledMapRenderer to set
	 */
	public void setTiledMapRenderer(OrthogonalTiledMapRenderer tiledMapRenderer) {
		this.tiledMapRenderer = tiledMapRenderer;
	}

	/**
	 * @return the camera
	 */
	@Override
	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * @param camera the camera to set
	 */
	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	/**
	 * @return the mapCam
	 */
	public OrthographicCamera getMapCam() {
		return mapCam;
	}

	/**
	 * @param mapCam the mapCam to set
	 */
	public void setMapCam(OrthographicCamera mapCam) {
		this.mapCam = mapCam;
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

	/**
	 * @return the viewportStage
	 */
	public ScreenViewport getViewportStage() {
		return viewportStage;
	}

	/**
	 * @param viewportStage the viewportStage to set
	 */
	public void setViewportStage(ScreenViewport viewportStage) {
		this.viewportStage = viewportStage;
	}

	public Array<Panel> getPanelArray() {
		return panelArray;
	}

	public void setPanelArray(Array<Panel> panelArray) {
		this.panelArray = panelArray;
	}

	public Unit getCurrUnit() {
		return currUnit;
	}

	public void setCurrUnit(Unit currUnit) {
		this.currUnit = currUnit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
 
}
