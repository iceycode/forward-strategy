/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.data.GameData;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;

/** MapStage.java
 * 
 * - creates the maps as a stage (MapStage extends Stage)
 * - uses IDs to create new maps
 * 
 * @author Allen Jagoda
 *
 */
public class MapUtils {
	
	//TextureRegion[][] tilesets;
	//variables related to stage/screen placements
	final static private String LOG = "MapUtils log: ";

	static float SCREENWIDTH = Constants.SCREENWIDTH;
	static float SCREENHEIGHT = Constants.SCREENHEIGHT;
	static float GRID_ORI_X = Constants.GRID_X;
	static float GRID_ORI_Y = Constants.GRID_Y;
	
	private static TiledMap tiledMap;
	private static MapActor mapActor;
	protected static MapStage stage;
	private static Table table; //creates a the table that stores layers

	static Array<MapActor> mapActorsArr;	
	public static MapActor[][] mapMatrix; //how maps appear on board in matrix form

	
	int id = 0; //id for the map
	private static ClickListener clickListener;

 
	public static MapStage createMap(int id ) {
		//select the .tmx map to load
		if (id == 1)
			tiledMap = new TmxMapLoader().load("maps/justGrass.tmx");
		else if (id == 2)
			tiledMap = new TmxMapLoader().load("maps/map2.tmx");
		else if (id == 3)
			tiledMap = new TmxMapLoader().load("maps/map3.tmx");
		else if (id == 4)
			tiledMap = new TmxMapLoader().load(Constants.MAP_3A);
		
		tiledMap.getProperties();
		
		//creates a  stage
		MapStage stage = new MapStage(tiledMap); //set the stage

		return stage;
	}

	/**
	 * creates a table out of actors on stage
	 * @return
	 */
	public static Table createTable() {
		Table table = new Table();
	    table.setFillParent(true);

		for (MapLayer layer : tiledMap.getLayers()) {
			TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
			Table tableLayer = tableFromLayers(tiledLayer);//obtain table for layer
			table.add(tableLayer).width(384).height(384); //add to root table
			table.addActor(tableLayer);
		} //gets all the tiles as actors in table format 
		
		return table;
	}

	public static Table tableFromLayers(TiledMapTileLayer tiledLayer) {
		Table layerTable = new Table();
		layerTable.setFillParent(true);

		for (int x = 0; x < tiledLayer.getWidth(); x++) {
			for (int y = 0; y < tiledLayer.getHeight(); y++) {
				TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
				
				MapActor mapActor = new MapActor(tiledMap, tiledLayer, cell);
 				
				mapActor.setBounds(x * tiledLayer.getTileWidth(), y * tiledLayer.getTileHeight(), tiledLayer.getTileWidth(),
						tiledLayer.getTileHeight());
				mapActor.setPosition(GRID_ORI_X+x*mapActor.getWidth(), GRID_ORI_Y+y*mapActor.getHeight());
 				mapActor.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gdx.app.log("log: ", " tilemap actor clicked at " + x + y);
					}
				});
 				
				//add to table
				layerTable.add(mapActor).width(32).height(32);
				layerTable.addActor(mapActor);
			}//get all the columns
			
			layerTable.row();
		}//get all the rows
		
		return layerTable;
	}

	public static void addClickListener() {

		clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println(((MapActor)event.getTarget()).getCell() + " has been clicked.");
			}
		};

	}

	/** creates actors from the TiledMapTileSet
	 * 
	 * @param tileset
	 */
	public static void createActorsFromTileSets(TiledMapTileSet tileset, MapStage stage) {
		for (TiledMapTile tile : tileset) {
			Object property = tile.getProperties().get("Water");
			
			if (property != null) {
				MapActor mapActor = new MapActor(tile, GRID_ORI_X, GRID_ORI_X);
 				stage.addActor(mapActor);
			}
		}
	}

	/** creates actors on the layers
	 * also sets the unit data
	 * 
	 * @param tiledLayer
	 * @param panelMatrix
	 * @param stage
	 */
    public static void createActorsForLayer(TiledMapTileLayer tiledLayer, Panel[][] panelMatrix, MapStage stage) {
        mapMatrix = new MapActor[11*3][11*3]; //11 by 11 tiles * 5 layers 
        GameData.mapActorArr = new Array<MapActor>();
        GameData.gamePanels = new Array<Panel>();
        
		String terrainType = tiledLayer.getName();

        int rows = tiledLayer.getWidth();
        int cols = tiledLayer.getHeight();
 
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
                     
                MapActor mapActor = new MapActor(tiledMap, tiledLayer, cell);
                
                float posX = GRID_ORI_X + x*32;
                float posY = GRID_ORI_Y + y*32;
                 
                mapActor.setBounds(posX, posY, 32, 32);
                mapActor.setPosition(posX, posY);
                Gdx.app.log(LOG, "position of mapactor when created is (" + mapActor.getX() + ", " + mapActor.getY() + ")");

				Gdx.app.log(LOG, " layer's name is "+ tiledLayer.getName());
				//Gdx.app.log(LOG, " layer property value is " + mapActor.property.get("Terrain"));

				if (cell!= null) {
	 				mapMatrix[x][y] = mapActor;
	 				
	 				stage.addActor(mapActor);
 	                stage.addActor(panelMatrix[x][y]); //also adds the panel
	 				
	                GameData.gridMatrix[x][y].setTerrainType(terrainType); //set panel to terrain type of tile
 	                GameData.gamePanels.add(GameData.gridMatrix[x][y]); //add to panel array
 	                GameData.mapActorArr.add(mapActor);	//add to a mapActor array TODO: create animation within tiled map
 				}
 
            }
        }
    }
    
    
/*--------------------Grid Panels on Board----------------
 * methods to create grid panels which show unit moves
 * 
 * 
 */
    /*****Sets all the panels positions & actors in matrix
	 * - sets all game board actors as arrays
	 */
	public static void setupPanels() {
		
		int rows = Constants.ROWS;
		int columns = Constants.COLS;
		float width = Constants.GRID_TILE_WIDTH;
		float height = Constants.GRID_TILE_HEIGHT;
		
		Array<Panel> panelsOnStage = new Array<Panel>(rows*columns); //<----not using now  
		Panel[][] panelMatrix = new Panel[rows][columns];
		
		
		for (int x = 0; x < rows; x ++) 	{
			String panelName = "x"+x;
	 		if (x%2==0){
	 			System.out.println(); 
	 		}
			for (int y = 0; y < columns; y++) 	 {
				float stagePosX = x*width + GRID_ORI_X;
				float stagePosY = y*height + GRID_ORI_Y;

				//String gridPos = "{" + x + ", " + y + "}, ";
				String screenPos = "{" + stagePosX + ", " + stagePosY+ "}, ";
 
		 		System.out.print(screenPos);
		 		//System.out.print(gridPos);


				Panel panelActor = new Panel(stagePosX, stagePosY);
				panelActor.setName(panelName.concat("y"+y)); //used for id
				panelActor.setMatrixPosX(x);
				panelActor.setMatrixPosY(y);
				
				
				//this simply gets the map terrain & sets terraintype field in
				//panel to this terrain property 
//				MapActor ma = mapActMatrix[x][y]; // <----these are seen in MapStage's TiledMap
// 				panelActor.setTerrainType(ma.terrainType);
				
				//panelActor.toFront();
				panelMatrix[x][y] = panelActor; //store in position matrix
				panelsOnStage.add(panelActor);
			}
		}
		
		//setup the game board = stored in constants for pathfinding
		
		
		//set the elements which will be used on stage & by Unit actors
		GameData.gamePanels = panelsOnStage;
		GameData.gridMatrix = panelMatrix;
 	}
	
	/*************TABLE GRID******w************
	 *  
	 *  makes a Table which can be added to stage
	 */
	public static Table createPanelTable(Panel[][] gridMatrix) {
		Table table = new Table();	
		table.setFillParent(false);
		
		for(int x = 0; x < Constants.ROWS; x++)  {
			for (int y = 0; y < Constants.COLS; y++ ) {
				// final Panel p = new Panel(stage, tiles, x, y);
				Panel panelActor = gridMatrix[x][y];
				//panelActor.addListener(MapUtils.createPanelListener(panelActor));
				table.add(panelActor).width(panelActor.getWidth()).height(panelActor.getHeight());
				table.addActor(panelActor);
			}
			table.row(); //creates a row out of the actors
		}	
		
		return table;
 	}
 
/*--------------Units on stage-----------------
 * 
 * 
 * 
 * 
 */
	/**
	 * 
	 * @param allUnits
	 * @param stage
	 */
	public static void unitsToStage(Array<Array<Unit>> allUnits,  Panel[][] panelMatrix, Stage stage){
		//get each player's units from a encapsulated array
		Array<Unit> p1Units = allUnits.get(0);
		Array<Unit> p2Units = allUnits.get(1);
		Array<Unit> unitsOnStage = new Array<Unit>();
 
		/* Player 1 units created
		 */
		//get & add player 1 units to board
		for (Unit u : p1Units) {
  			u.setPlayer(1); 
		//	stage.addActor(u);	//add the actor
			unitsOnStage.add(u);
  		}
		
		/* Player 2 units on board
		 * 
		 */
		//get & add player 2 units
		for (Unit u : p2Units) {
  			u.setPlayer(2);
 			u.setLock(true);
  			u.setEnemyUnits(p1Units);
   		//	stage.addActor(u);	//add the actor
			unitsOnStage.add(u);
  		}
 
		//sets other units that placed on stage
		for (Unit u : unitsOnStage) {
			//sets 1st player's enemy units
			if (u.getPlayer() == 1) 
				u.setEnemyUnits(p2Units);
 
			stage.addActor(u);	//add the actor
 		}
	}
	
	
	/** finds all units on the stage
	 * 
	 * @param stageUnits
	 * @return
	 */
	public static Array<Unit> findAllUnits(Array<Actor> actorsOnStage){
		Array<Unit> unitsOnStage = new Array<Unit>();
		
		for (Actor a : actorsOnStage) {
			if (a instanceof Unit) {
				Unit uni = (Unit)a;
				unitsOnStage.add(uni);
			}
		}
 		
		return unitsOnStage;
	}
 
	

	public static Array<Unit> otherUnits(Array<Unit> allUnits, Unit u){
		Array<Unit> tempArr = allUnits; //the array to be created each iteration
 		tempArr.removeValue(u, false); //remove it from temporary array
 		
		return tempArr;
	}
	
	/** finds all units of certain player
	 * - finds all units of a certain player
	 * 
	 * @param int player
	 */
	public static Array<Unit> findPlayerUnits(Array<Unit> unitArr, int player){
		Array<Unit> playerUnits = new Array<Unit>();
 		
		for (Unit u : unitArr) {
			if (u.getPlayer() == player) {
				playerUnits.add(u);
			}
		}
		
		return playerUnits;
	}
	
	
	/** returns positions in grid based on actors screen coordinates
	 *  
	 * @param x
	 * @param y
	 * @return
	 */
	public static Vector2 screenToGridPosition(float x, float y){
		float gridX = (x+1-Constants.GRID_X)/Constants.GRID_WIDTH;
		float gridY = (y+1-Constants.GRID_Y)/Constants.GRID_HEIGHT;
		
		return new Vector2(gridX, gridY);

	}
	
	/** creates and returns a clickListener for panel
	 * 
	 * @param pan
	 * @return
	 */
	public static InputListener createPanelListener(final Panel panel){
		InputListener inputListener = new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				panel.clickCount++;

				if (panel.clickCount == 1) {
					Gdx.app.log(LOG, "panel " + panel.getName() + " is now being viewed");
 
					panel.selected = true;
 				}
				
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (panel.clickCount == 2) {
					panel.selected = false;
 					panel.clickCount = 0;
				}

			}
			  
 		};
 		
 		return inputListener;
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
	 * @return the mapActor
	 */
	public MapActor getMapActor() {
		return mapActor;
	}

	/**
	 * @param mapActor the mapActor to set
	 */
	public void setMapActor(MapActor mapActor) {
		this.mapActor = mapActor;
	}

	/**
	 * @return the stage
	 */
	public MapStage getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(MapStage stage) {
		this.stage = stage;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * @return the mapMatrix
	 */
	public static MapActor[][] getMapMatrix() {
		return mapMatrix;
	}

	/**
	 * @param mapMatrix the mapMatrix to set
	 */
	public void setMapMatrix(MapActor[][] mapMatrix) {
		this.mapMatrix = mapMatrix;
	}

}


/*  *//** creates a camera for the map
 * TODO: figure out whether this needs to be used eventually (mainly for zooming)
 *//*
public static void createMapCam() {
	*//*****camera for tiled map*****//*
	float width = 12 * (screenWidth/screenHeight); //width aspect ratio corrected for
	float height = 12; //the height, fills to top
	
	camera = new OrthographicCamera();
	camera.setToOrtho(false, 800, 500 ); //sets scale of units for rendered
	
	//set screen viewport
	viewport = new ScreenViewport();
	viewport.setWorldWidth(screenWidth);
	viewport.setWorldHeight(screenHeight); 
	viewport.setCamera(camera);

    mapCam = new OrthographicCamera(width, height);
		mapCam.setToOrtho(false, 800, 500);
	
	vecPos = new Vector3(gridOriX, gridOriY, 1);
	camera.project(vecPos); //project the stage camera
	mapCam.unproject(vecPos); //unproject to return position of map camera

	vecPos.x = gridOriX; 
	vecPos.y = gridOriY;
		
	mapCam.position.set(vecPos);
	camera.position.set(gridOriX, gridOriY, 0);
    
	viewport.setCamera(camera);

	setViewport(viewport);//sets this stage viewport

}*/