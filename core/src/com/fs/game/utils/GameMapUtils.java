package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitController;
import com.fs.game.utils.pathfinder.PathGenerator;

/** Map Utility methods
 * - helps with creating TiledMap, Panel actors & setting them up on stages
 * - methods for helping find & manipulate actors (units) on GameStage
 *
 * Created by Allen on 5/7/15.
 */ //----------------------GAME MAP TOOLS--------------------------//
public class GameMapUtils {

    //for logging
    public static boolean logEnabled = false;

    /**
     * Gets the map based on id
     * configuration based on test (default 0; regular test/map)
     *
     * FIXED: now just returns a tiled map, everything else done in GameScreen
     * @param id : ID of map
     * @return GameStage
     */
    public static TiledMap getTiledMap(int id) {
        TiledMap tiledMap;

        //TODO: CREATE MORE TILED MAPS
        //create map based on ID - only 2 of size 16x12 at the moment
        if (id == 1)
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);
        else if (id==2)
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);
        else if (id == 3)
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);
        else if (id == 4){
            tiledMap = new TmxMapLoader().load(Constants.TEST_MAP_40x30_1);
        }
        else{
            tiledMap = new TmxMapLoader().load(Constants.MAP_3A);
        }

        //tiledMap.getProperties();

        return tiledMap; //create & return new GameStage
    }


    //TODO: fuse panel actors with tiledmap actors
    public static void setupGridElements(GameStage stage) {
        //1st kind of setup - 16x12
        if (GameData.testType != 4){
            setupPanels(16, 12, 32, 32);
            createMapActors(stage);
//            stage.setPanelArray(GameData.gamePanels);
        }
        else if (GameData.testType == 4){
            //new setup 40x30, 32x32
            setupPanels(40, 30, 32, 32);
            createMapActors(stage);
//            stage.setPanelArray(GameData.gamePanels);
        }

        //TODO: create seup with 64x64 grid size
    }


    /** Creates TiledMapTileLayer.Cell actors for interaction purposes
     *  Allows them to be touchable or animated more easily
     *
     * @param stage : GameStage
     */
    public static void createMapActors(GameStage stage) {
        //for-each loop thru all layers of map
        for (MapLayer layer : stage.tiledMap.getLayers()) {
            TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;
            setupTiledMapTerrain(tiledLayer, stage);
        } //gets all the actors from all the layers
    }

    /** sets up terrain from TiledMap tiles for Panel
     *
     * FIXED: instead of using a MapActor, now Panel takes a terrain from TiledMapTileLayer.Cell Texture
     *
     * @param tiledLayer : tiledLayer being extracted
     * @param stage : GameStage actors are added to
     */
    public static void setupTiledMapTerrain(TiledMapTileLayer tiledLayer, GameStage stage) {
        int cols = tiledLayer.getWidth(); //layers width IN TILES
        int rows = tiledLayer.getHeight();

//        MapActor[][] mapMatrix = new MapActor[rows ][cols];
        String tileLayer = tiledLayer.getName();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {

                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);

                // some cells with a terrain type do not exist on certain layers
                // also, some cells are "panels" or "grids", but they are not added
                // NOTE: do NOT add "grid" or "panel" tiles to new TiledMaps, only terrain tiles
                if (cell != null ) {//&& (!terrainName.equals("panels") && !terrainName.equals("grid"))
                    GameData.panelMatrix[x][y].setTerrain(tileLayer, cell); //set panel to terrain type of tile
                }
            }
        }

        setGamePanels(GameData.panelMatrix, stage);
    }


    /** Sets up GameData.Panels for PathGenerator/PathFinder stack
     *  Also, adds Panels to stage
     *
     * @param panelMap : the GameData.panelMatrix object, now fully set up
     * @param stage : stage to add panels to
     */
    public static void setGamePanels(Panel[][] panelMap, GameStage stage){
        for (int x = 0; x < panelMap.length; x++){
            for (int y = 0; y < panelMap[y].length; y++){
                GameData.gamePanels.add(panelMap[x][y]);
                stage.addActor(panelMap[x][y]);
            }
        }

    }


    /** Shows panels in range
     *
     * @param currUnit : current unit that is selected
     */
    public static void setPanelsInRange(Unit currUnit){
        PathGenerator pg = PathGenerator.getPG();
        pg.update(currUnit, currUnit.getX(), currUnit.getY());
        Array<Panel> unitMoves = pg.findPaths();

        if (unitMoves!=null){
            for (Panel p : unitMoves) {
                p.setPanelState(PanelState.MOVEABLE);
                currUnit.panelArray.add(p); //add directly to Unit
            }
        }

    }

    //

    /** Resets state of Panels Unit can move to
     *
     * @param unit : unit that moved or was deselected
     */
    public static void resetPanelsInRange(Unit unit){
        Array<Panel> unitMoves = unit.panelArray;
        if (unitMoves!=null){
            for (Panel p : unitMoves) {
                p.setPanelState(PanelState.NONE);
            }
        }

        unit.panelArray.clear();
    }


    //-----setup for Panels and MapActors for TiledMap

    /** Sets up Panels basd on TiledMap size and size of tiles
     *
     * @param rows : rows of tiles in map
     * @param columns : columns of tiles in map
     * @param width : width of tiles
     * @param height : height of tiles
     */
    public static void setupPanels(int columns, int rows, int width, int height ){
        Panel[][] panelMatrix = new Panel[columns][rows];
        UnitController controller = UnitController.getInstance(); //create controller instance

        Array<String[]> positions = new Array<String[]>(); //array of positions NOTE: for printing purpsoses

        for (int x = 0; x < columns; x++) {
            String panelName = "x" + x;
            for (int y = 0; y < rows; y++) {
                float screenX = x * width + Constants.MAP_X;
                float screenY = y * height + Constants.MAP_Y;

                positions.add(new String[]{Float.toString(screenX), Float.toString(screenY)}); //NOTE: for printing purposes

                Panel panelActor = new Panel(screenX, screenY);
                panelActor.setName(panelName.concat("y" + y)); //used for id
                panelActor.setGraphPosition(x, y);


                panelActor.setUnitUpdater(controller); //set interface for unit controller

                //panelActor.toFront();
                panelMatrix[x][y] = panelActor; //store in position matrix

//                    panelsOnStage.add(panelActor);
            }
        }

        //setup the game board = stored in constants for pathfinding
        //set the elements which will be used on MapStage by Units
        GameData.panelMatrix = panelMatrix;
        GameData.cols = columns;
        GameData.rows = rows;

//        logPanelPositions(positions, rows, columns); //NOTE: don't delete, may be useful in future
    }


    /** Prints out all panel positions in screen coordinate system
     *
     * @param positions : positions as String values
     * @param rows : rows in grid (aka game map)
     * @param cols : columns in grid (aka game map)
     */
    private static void logPanelPositions(Array<String[]> positions, int rows, int cols){

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < positions.size; i++){
            builder.append("{" + positions.get(i)[0] + ", " + positions.get(i)[1] + "}, ");
            if (i != 0 && i%12==0){
                builder.append("\n");
            }
        }
        builder.trimToSize();
        log("Panel Coordinates on Grid Size (units in Tiles) of " + Integer.toString(rows) + "x" +
                Integer.toString(cols) + ":\n" + builder.toString());
    }



    //--------------------FIND STAGE UNIT METHODS--------------------------
    /**
     * finds all units on the stage
     *
     * @return
     */
    public static Array<Unit> findAllUnits(Array<Actor> actorsOnStage) {
        Array<Unit> unitsOnStage = new Array<Unit>();

        for (Actor a : actorsOnStage) {
            if (a instanceof Unit) {
                Unit uni = (Unit) a;
                unitsOnStage.add(uni);
            }
        }

        return unitsOnStage;
    }

    public static Array<Unit> findOtherUnits(Array<Unit> allUnits, Unit u) {
        Array<Unit> tempArr = allUnits; //the array to be created each iteration
        tempArr.removeValue(u, false); //remove it from temporary array

        return tempArr;
    }


    /** Update Unit states based on player turn.
     *  Any current player Unit's are DONE, new players Units are set to IS_STANDING
     *
     * @param player : next player, whose units are being locked
     */
    public static void togglePlayerUnits(int player, GameStage stage) {
        Array<Unit> allUnits = findAllUnits(stage.getActors());

        //look through all units to see if certain ones locked or not
        for (int i = 0; i < allUnits.size; i++) {
            Unit u = allUnits.get(i);
            UnitController.getInstance().onTurnChange(u, player);
        }
    }

//    /**
//     * Unlocks player units whose turn it is & locks those whose turn it is not.
//     *
//     * @param player : player whose units will be UNLOCKED
//     */
//    public static void togglePlayerUnits(int player, GameStage stage) {
//        Unit u = new Unit(); //initialize constructor
//        Array<Unit> allUnits = findAllUnits(stage.getActors());
//
//        //look through all units to see if certain ones locked or not
//        for (int i = 0; i < allUnits.size; i++) {
//            u = allUnits.get(i);
//            u.state = u.player == player ? UnitState.IS_STANDING : UnitState.DONE;
//        }
//    }

    /** Returns position in x/y coordinates, like in a game board grid
     *  eg bottom left would be 0, 0; top left would be 0, 0 + height
     *
     * @param x : x position
     * @param y : y position
     * @return : an array holding positions as int values
     */
    public static int[] getGridPosition(float x, float y){
        int gridX = (int)(x - Constants.MAP_X)/32;
        int gridY = (int)(y - Constants.MAP_Y)/32;

        return new int[]{gridX, gridY};
    }

    /** Checks to see whether x & y coordinates (screen coordinates) is within map boundaries on stage
     *
     * @param x : x position
     * @param y : y position
     * @return : true if is in bounds, false if otherwise
     */
    public static boolean isInMapBounds(float x, float y){
        return !isPastLeft(x) && !isPastRight(x) && !isPastBottom(y) && !isPastTop(y);
    }


    public static boolean isPastTop(float y) {
        if (y >= Constants.MAP_TOP_RIGHT[1])
            return true;

        return false;
    }

    public static boolean isPastBottom(float y) {
        if (y <= Constants.MAP_BTM_LEFT[1])
            return true;
        return false;
    }

    public static boolean isPastRight(float x) {
        if (x >= Constants.MAP_TOP_RIGHT[0])
            return true;
        return false;
    }

    public static boolean isPastLeft(float x) {
        if (x <= Constants.MAP_BTM_LEFT[0]) {
            return true;
        }
        return false;
    }

    private static void log(String message){
        Gdx.app.log("GameMapUtils LOG: ", message);
    }

}

// TODO: get rid of these methods at some point
//    /** returns positions in grid based on actors screen coordinates
//	 *
//	 * @param x
//	 * @param y
//	 * @return Vector2 instance containing x, y positions
//	 */
//	public static Vector2 screenToGridPosition(float x, float y){
//		float gridX = (x+1-Constants.MAP_X)/Constants.MAP_VIEW_WIDTH;
//		float gridY = (y+1-Constants.MAP_Y)/Constants.MAP_VIEW_HEIGHT;
//
//		return new Vector2(gridX, gridY);
//
//	}
//
//	/** creates and returns a clickListener for panel
//	 *
//	 * @param panel
//	 * @return
//	 */
//	public static InputListener createPanelListener(final Panel panel){
//		InputListener inputListener = new InputListener(){
//			@Override
//			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//				panel.clickCount++;
//
//				if (panel.clickCount == 1) {
//					Gdx.app.log(LOG, "panel " + panel.getName() + " is now being viewed");
//
//					panel.selected = true;
// 				}
//
//				return true;
//			}
//
//			@Override
//			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//				if (panel.clickCount == 2) {
//					panel.selected = false;
// 					panel.clickCount = 0;
//				}
//			}
//
// 		};
//
// 		return inputListener;
// 	}
//    /**
//     * unselects other units
//     * - 2 units cannot be selected at once...yet
//     *
//     * @param otherUnits
//     */
//    public static void deselectUnits(Array<Unit> otherUnits) {
//        for (Unit u : otherUnits) {
//            if (u.chosen) {
//                u.chosen = false;
//                UnitUtils.Movement.hideMoves(u);
//                u.clickCount = 0;
//            }
//        }
//    }
//
//    /**
//     * clears the stage of any active (selected) panels
//     */
//    public static void clearBoard(GameStage stage) {
//        for (Panel p : stage.getPanelArray()) {
//            if (p.selected || p.moveableTo) {
//                p.selected = false;
//                p.moveableTo = false;
//                //p.viewing = false;
//            }
//        }
//    }

//    public static boolean isWithinBorder(float x, float y) {
//        if (!isPastBottom(y) && !isPastTop(y) && !isPastLeft(x) && !isPastRight(x))
//            return true;
//        return false;
//    }
//    /**
//     * returns a unit iterator from stage actors
//     *
//     * @param actors : actors on stage
//     * @return
//     */
//    public static Array.ArrayIterator<Unit> unitIterator(Array<Actor> actors) {
//        Array.ArrayIterator<Actor> actorIterator = new Array.ArrayIterator<Actor>(actors);
//        Array<Unit> units = new Array<Unit>();
//
//        while (actorIterator.hasNext()) {
//            Actor a = actorIterator.next();
//            if (a instanceof Unit) {
//                units.add((Unit) a);
//            }
//        }
//
//        Array.ArrayIterator<Unit> iterator = new Array.ArrayIterator<Unit>(units);
//
//        return iterator;
//    }

//    /**
//     * ArrayIterator method for finding all units on stage
//     * NOTE: this allows for multithreaded & nested iteration
//     *
//     * @param actors
//     * @return
//     */
//    public static Array.ArrayIterator<Unit> playerUnitIterator(Array<Actor> actors, String name) {
//        Array<Unit> units = new Array<Unit>();
//        Array.ArrayIterator<Actor> iter = new Array.ArrayIterator<Actor>(actors);
//
//        while (iter.hasNext()) {
//            Actor a = iter.next();
//            if (a instanceof Unit) {
//                Unit u = (Unit) a;
//                if (u.getOwner().equals(name))
//                    units.add(u);
//            }
//        }
//
//        return new Array.ArrayIterator<Unit>(units);
//    }
//
//    public static void checkIfUnitClose(Unit unit, Array<Actor> actors) {
//        Array.ArrayIterator<Actor> iter = new Array.ArrayIterator<Actor>(actors);
//
//        while (iter.hasNext()) {
//            Actor a = iter.next();
//            if (a instanceof Unit) {
//                Unit u = (Unit) a;
//                if (UnitUtils.Attack.unitAdjacent(unit, u) && unit.player != u.player) {
//
//                }
//            }
//        }
//
//    }