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

//	static float SCREENWIDTH = Constants.SCREENWIDTH;
//	static float SCREENHEIGHT = Constants.SCREENHEIGHT;
	static float GRID_ORI_X = Constants.GAMEBOARD_X;
	static float GRID_ORI_Y = Constants.GAMEBOARD_Y;
	
	private static TiledMap tiledMap;
	private static MapActor mapActor;
	protected static MapStage stage;
	private static Table table; //creates a the table that stores layers
//	static Array<MapActor> mapActorsArr;
	public static MapActor[][] mapMatrix; //how maps appear on board in matrix form

	
	int id = 0; //id for the map

    /** creates the map based on id
     *  configuration based on test (default 0; regular test/map)
     *
     * @param id
     * @param test
     * @return
     */
	public static MapStage createMap(int id, int test) {
		//select the .tmx map to load
		if (id == 1)
			tiledMap = new TmxMapLoader().load("maps/justGrass.tmx");
		else if (id == 2)
			tiledMap = new TmxMapLoader().load("maps/map2.tmx");
		else if (id == 3)
			tiledMap = new TmxMapLoader().load("maps/map3.tmx");
		else if (id == 4)
			tiledMap = new TmxMapLoader().load(Constants.MAP_3B);
        //if test
        else if (id==11)
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);
        else if (id == 12)
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);

		//tiledMap.getProperties();
		
		//creates a  stage
		MapStage stage = new MapStage(tiledMap, test); //set the stage

		return stage;
	}
 


	/** creates actors on the layers
	 * also sets the unit data
	 * 
	 * @param tiledLayer
	 * @param panelMatrix
	 * @param stage
	 */
    public static void createActorsForLayer(TiledMapTileLayer tiledLayer, Panel[][] panelMatrix, MapStage stage) {
        mapMatrix = new MapActor[15*3][11*3]; //16x12 tiles  
        GameData.mapActorArr = new Array<MapActor>();
        GameData.gamePanels = new Array<Panel>();
        
		String terrainType = tiledLayer.getName();

        int rows = tiledLayer.getWidth(); //layers width IN TILES
        int cols = tiledLayer.getHeight();
 
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
            	
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);
               
                MapActor mapActor = new MapActor(tiledMap, tiledLayer, cell);
                
                float posX = GRID_ORI_X + x*32;
                float posY = GRID_ORI_Y + y*32;
                 
                mapActor.setBounds(posX, posY, 32, 32);
                mapActor.setPosition(posX, posY);
//                Gdx.app.log(LOG, "position of mapactor when created is (" + mapActor.getX() + ", " + mapActor.getY() + ")");
//				Gdx.app.log(LOG, " layer's name is "+ tiledLayer.getName());
				

				if (cell!= null && (!terrainType.equals("panels") && !terrainType.equals("grid"))) {
					//Gdx.app.log(LOG, " layer property value is " + mapActor.property.get("Terrain"));
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
    
    

	
	
	
    /*****Sets all the panels positions & actors in matrix
	 * - sets all game board actors as arrays
	 */
	public static void setupPanels16x12() {
		
		int rows = 16;
		int columns = 12;
		float width = 32;
		float height = 32;
		
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
				
				//panelActor.toFront();
				panelMatrix[x][y] = panelActor; //store in position matrix
				panelsOnStage.add(panelActor);
			}
		}
		
		//setup the game board = stored in constants for pathfinding 
		//set the elements which will be used on MapStage by Units
		GameData.gamePanels = panelsOnStage;
		GameData.gridMatrix = panelMatrix;
 	}
	
	

 
/*--------------Units on stage-----------------
 * 
 * 
 * 
 * 
 */
//	/**
//	 *
//	 * @param p1Units
//	 * @param stage
//	 */
//	public static void unitsToStage(Array<Unit> p1Units, Stage stage){
//		//get each player's units from a encapsulated array
//		Array<Unit> unitsOnStage = new Array<Unit>();
//
//		/* Player 1 units created
//		 */
//		//get & add player 1 units to board
//		for (Unit u : unitsOnStage) {
//  			if (u.player == 2) {
//                u.setLock(true);
//            }
//
//			unitsOnStage.add(u);
//  		}
//
//		/* Player 2 units on board
//		 *
//		 */
//		//get & add player 2 units
//		for (Unit u : p2Units) {
//  			u.setPlayer(2);
// 			u.setLock(true);
//  			u.setEnemyUnits(p1Units);
//			unitsOnStage.add(u);
//  		}
//
//		//sets other units that placed on stage
//		for (Unit u : unitsOnStage) {
//			//sets 1st player's enemy units
//			if (u.getPlayer() == 1)
//				u.setEnemyUnits(p2Units);
//
//			stage.addActor(u);	//add the actor
// 		}
//	}
	
	
	/** finds all units on the stage
	 * 
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
     * @param unitArr
	 * @param player
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

    /** finds the enemy units on board
     * - this needs to be reset every time units change positions
     *
     */
    public static Array<Unit> findEnemyUnits(Unit unit, Stage stage){
        Array<Unit> enemyUnits = new Array<Unit>();
        Array<Unit> otherUnits = otherUnits(findAllUnits(stage.getActors()), unit);

        //look through other units
        // if does not equal to this player, then it is enemy
        for (Unit u : otherUnits) {
            if (u.player != unit.player) {
                enemyUnits.add(u);
            }
        }

        return enemyUnits;

    }



    /** update player turn
     * - int value to determine which player's units to lock
     *
     *
     * @param player
     */
    public static void lockPlayerUnits(int player, MapStage stage) {
        Unit u = new Unit(); //initialize constructor
        Array<Unit> allUnits = MapUtils.findAllUnits(stage.getActors());

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
    public static void unlockPlayerUnits(int player, MapStage stage) {
        Unit u = new Unit(); //initialize constructor
        Array<Unit> allUnits = MapUtils.findAllUnits(stage.getActors());

        //look through all units to see if certain ones locked or not
        for (int i = 0; i < allUnits.size; i++) {
            u = allUnits.get(i);
            //unlock if this is not player being locked
            if (u.isLock() && u.player == player) {
                if (u.underattack)
                    u.underattack = false;
                u.lock = false;
                u.done = false;
                u.standing = true;
            }
        }
    }


    /** clears the stage of any active (selected) panels
     *
     */
    public static void clearBoard(MapStage stage){
        for (Panel p : stage.getPanelArray()){
            if (p.selected || p.moveableTo){
                p.selected = false;
                p.moveableTo = false;
                p.viewing = false;
            }
        }
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
	 * @param panel
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


