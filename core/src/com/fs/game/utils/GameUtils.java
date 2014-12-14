/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;

import java.util.Random;

/** MapStage.java
 * 
 * - creates the maps as a stage (MapStage extends StageUtils)
 * - uses IDs to create new maps
 * 
 * @author Allen Jagoda
 *
 */
public class GameUtils {

	final static private String LOG = "MapUtils log: ";


    //----------------------GAME MAP TOOLS--------------------------//
    public static class Map {

        /** creates the map based on id
         *  configuration based on test (default 0; regular test/map)
         *
         * @param id
         * @return GameStage
         */
        public static GameStage createMap(int id) {
            TiledMap tiledMap;
            GameData.mapChoice = id;

            //select the .tmx map to load
            if (id == 0)
                tiledMap = new TmxMapLoader().load(Constants.MAP_1);
            else if (id == 1)
                tiledMap = new TmxMapLoader().load(Constants.MAP_2);
            else if (id == 2)
                tiledMap = new TmxMapLoader().load(Constants.MAP_3);
            else if (id == 3)
                tiledMap = new TmxMapLoader().load(Constants.MAP_3A);
            else if (id == 4)
                tiledMap = new TmxMapLoader().load(Constants.MAP_3B);
            //if test
            else if (id==11)
                tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);
            else
                tiledMap = new TmxMapLoader().load(Constants.TEST_MAP1);

            //tiledMap.getProperties();

            //creates a  stage
            GameStage stage = new GameStage(tiledMap); //set the stage

            return stage;
        }


        //TODO: fuse panel actors with tiledmap actors
        public static void setupGridElements(GameStage stage){
// 		MapUtils.setupPanels12x12(); //stores data in UnitData
//		panelMatrix = GameData.panelMatrix;
//		this.setPanelArray(GameData.gamePanels);

            //2nd kind of setup
            setupPanels16x12();
            createMapActors(stage);
            stage.setPanelArray(GameData.gamePanels);
        }



        /** these map actors are touchable tiled map cells
         *
         */
        public static void createMapActors(GameStage stage){
            //for-each loop thru all layers of map
            for (MapLayer layer : stage.tiledMap.getLayers()) {
                TiledMapTileLayer tiledLayer = (TiledMapTileLayer)layer;
                createActorsForLayer(tiledLayer, GameData.panelMatrix, stage);
            } //gets all the actors from all the layers
        }

        /** creates actors on the layers
         * also sets the unit data
         *
         * @param tiledLayer
         * @param panelMatrix
         * @param stage
         */
        public static void createActorsForLayer(TiledMapTileLayer tiledLayer, Panel[][] panelMatrix, GameStage stage) {
            MapActor[][] mapMatrix = new MapActor[15*3][11*3]; //16x12 tiles

            String terrainType = tiledLayer.getName();

            int rows = tiledLayer.getWidth(); //layers width IN TILES
            int cols = tiledLayer.getHeight();

            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {

                    TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);

                    MapActor mapActor = new MapActor(stage.tiledMap, tiledLayer, cell);

                    float posX = Constants.GAMEBOARD_X + x*32;
                    float posY = Constants.GAMEBOARD_Y + y*32;

                    mapActor.setBounds(posX, posY, 32, 32);
                    mapActor.setPosition(posX, posY);

                    if (cell!= null && (!terrainType.equals("panels") && !terrainType.equals("grid"))) {
                        //Gdx.app.log(LOG_PAUSE_MENU, " layer property value is " + mapActor.property.get("Terrain"));
                        mapMatrix[x][y] = mapActor;

                        stage.addActor(mapActor);
                        stage.addActor(panelMatrix[x][y]); //also adds the panel

                        //TODO: get rid of redundencies
                        GameData.panelMatrix[x][y].setTerrainType(terrainType); //set panel to terrain type of tile
                        GameData.gamePanels.add(GameData.panelMatrix[x][y]); //add to panel array
                        GameData.mapActorArr.add(mapActor);	//add to a mapActor array
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

//            Array<Panel> panelsOnStage = new Array<Panel>(rows*columns);
            Panel[][] panelMatrix = new Panel[rows][columns];


            for (int x = 0; x < rows; x ++) 	{
                String panelName = "x"+x;
                if (x%2==0){
                    System.out.println();
                }

                for (int y = 0; y < columns; y++) 	 {
                    float stagePosX = x*width + Constants.GAMEBOARD_X;
                    float stagePosY = y*height + Constants.GAMEBOARD_Y;

                    Panel panelActor = new Panel(stagePosX, stagePosY);
                    panelActor.setName(panelName.concat("y"+y)); //used for id
                    panelActor.setMatrixPosX(x);
                    panelActor.setMatrixPosY(y);

                    //panelActor.toFront();
                    panelMatrix[x][y] = panelActor; //store in position matrix
//                    panelsOnStage.add(panelActor);
                }
            }

            //setup the game board = stored in constants for pathfinding
            //set the elements which will be used on MapStage by Units
            GameData.panelMatrix = panelMatrix;
        }


    }


    public static class Screen{

        /** sets up the stage UI & who goes first
         *
         * @param buttons
         * @param labels
         * @param stage
         * @param stageMap
         * @param firstPlayer
         */
        public static void setupUI(TextButton[] buttons, Label[] labels, Stage stage, GameStage stageMap, int firstPlayer) {

            //The side panel buttons indicating whose turn it is
            buttons[1] = UIUtils.createSideButton("P1", Constants.BT1_X, Constants.BT_Y);
            buttons[2] = UIUtils.createSideButton("P2", Constants.BT2_X, Constants.BT_Y);


            //for test purposes
            buttons[0] = UIUtils.createGoButton(stageMap);

            //add the actors
            for (TextButton tb : buttons){
                stage.addActor(tb);
            }

            labels[0] = UIUtils.createTimer();
            //----setup for ScrollPane panels as individual units within table----
            //the main pop-up window & widgets
            labels[1] = UIUtils.createLabelInfo();
            labels[2] = UIUtils.createLabelDamage();
            //score labels
            labels[3] = UIUtils.scoreBoard(0, 8f, Constants.SCREENHEIGHT - 40);
            labels[4] = UIUtils.scoreBoard(0, Constants.SCREENWIDTH - 72, Constants.SCREENHEIGHT - 40);

            for (Label label : labels){
                stage.addActor(label);
            }

            //adding labels within ScrollPane within Table to stage
            //scrollTable is the Table which holds the ScrollPane objects
            Table scrollTable = UIUtils.createUnitScrollTable(labels[1], labels[2]);
            stage.addActor(scrollTable);


            //locks those player units whose turn it is not
            GameData.playerTurn = false;

            int player = GameUtils.Player.randPlayer();
            buttons[player].toggle();

            GameUtils.Player.nextPlayer(firstPlayer, buttons[1], buttons[2], stageMap);

        }

    }


    //------------------PLAYER INFORMATION-------------------------
    public static class Player {

        //sets random player bw 1 & 2
        public static int randPlayer(){
            Random rand = new Random();
            return rand.nextInt(2)+1;
        }

        /** returns a random Faction
         *
         */
        public static String randomFaction(){
            Random rand = new Random();
            int factInt = rand.nextInt(3); //0-1 (only 1 working factions ATM)
            if (factInt == 0)
                return Constants.HUMAN;

            return Constants.ARTHROID;
        }


        /** returns timercount of 0
         *
         * @param p1Button
         * @param p2Button
         * @param stage
         * @return
         */
        public static float changePlayer(int player, Button p1Button, Button p2Button, GameStage stage) {
//		int temp = player; //temp value for player when checking

            if (GameData.playerTurn) {
                //decides based on player which player to lock
                nextPlayer(player, p1Button, p2Button, stage);
                GameData.playerTurn = false;
            }

            return 0;
        }



        public static int nextPlayer(int player, Button p1Button, Button p2Button, GameStage stageMap){
            if (player == 1) {
                StageUtils.lockPlayerUnits(player, stageMap);  //lock these player units

                if (!p1Button.isChecked())
                    p1Button.toggle(); //toggle

                player = 2; //next player
                StageUtils.unlockPlayerUnits(player, stageMap); 	//unlock player units
                p2Button.toggle();	//toggle checked state p2
            } //player 2 goes
            else {
                StageUtils.lockPlayerUnits(player, stageMap);

                if (!p2Button.isChecked())
                    p2Button.toggle(); //if it is not checked

                player = 1; //next player
                StageUtils.unlockPlayerUnits(player, stageMap); 	//unlock player units
                p1Button.toggle();
            } //player 1 goes


            return player;
        }




        /** updates current game score
         *
         * @param currScore
         * @param unit
         * @return
         */
        public static int updateScore(int currScore, Unit unit){

            if (unit.unitInfo.getSize().equals("32x32")){
                currScore += 10;
            }
            else if (unit.unitInfo.getSize().equals("64x32")){
                currScore+= 20;
            }
            else
                currScore += 30;


            return currScore;
        }
    }


    public static class StageUtils {

        //--------------------FIND STAGE UNIT METHODS--------------------------
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

        public static Array<Unit> findOtherUnits(Array<Unit> allUnits, Unit u){
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

        /** finds all units of certain player
         * - finds all units of a certain player
         *
         * @param playerName: player id as String (players name)
         * @param stage : GameStage on which units exist
         */
        public static Array<Unit> findPlayerUnits(String playerName, GameStage stage){
            Array<Actor> actors = stage.getActors();
            Array<Unit> playerUnits = new Array<Unit>();

            for (int i = 0; i< actors.size; i++) {
                Actor a = actors.get(i);
                if (a instanceof Unit) {
                    Unit uni = (Unit)a;
                    if (uni.getOwner() == playerName)
                        playerUnits.add(uni);

                }
            }

            return playerUnits;
        }

        /** finds the enemy units on board
         * - this needs to be reset every time units change positions
         *
         */
        public static Array<Unit> findEnemyUnits(Unit unit, com.badlogic.gdx.scenes.scene2d.Stage stage){
            Array<Unit> enemyUnits = new Array<Unit>();
            Array<Unit> otherUnits = findOtherUnits(findAllUnits(stage.getActors()), unit);

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
        public static void lockPlayerUnits(int player, GameStage stage) {
            Unit u = new Unit(); //initialize constructor
            Array<Unit> allUnits = findAllUnits(stage.getActors());

            //look through all units to see if certain ones locked or not
            for (int i = 0; i < allUnits.size; i++) {
                u = allUnits.get(i);

                //lock all units of this player
                if (!u.isLock() && u.player == player) {
                    u.lock = true;
                    u.done = true;
                    u.chosen = false;
                    u.standing = true;
                }
            }
        }

        /** unlocks player units
         *
         * @param player
         */
        public static void unlockPlayerUnits(int player, GameStage stage) {
            Unit u = new Unit(); //initialize constructor
            Array<Unit> allUnits = findAllUnits(stage.getActors());

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

        /** unselects other units
         * - 2 units cannot be selected at once...yet
         *
         * @param otherUnits
         */
        public static void deselectUnits(Array<Unit> otherUnits){
            for (Unit u : otherUnits){
                if (u.chosen ){
                    u.chosen = false;
                    UnitUtils.Movement.hideMoves(u);
                    u.clickCount = 0;
                }
            }
        }


        public static void updateUnit(Unit unit, int state){

        }

        /** clears the stage of any active (selected) panels
         *
         */
        public static void clearBoard(GameStage stage){
            for (Panel p : stage.getPanelArray()){
                if (p.selected || p.moveableTo){
                    p.selected = false;
                    p.moveableTo = false;
                    //p.viewing = false;
                }
            }
        }
    }





//    /** returns positions in grid based on actors screen coordinates
//	 *
//	 * @param x
//	 * @param y
//	 * @return Vector2 instance containing x, y positions
//	 */
//	public static Vector2 screenToGridPosition(float x, float y){
//		float gridX = (x+1-Constants.GRID_X)/Constants.GRID_WIDTH;
//		float gridY = (y+1-Constants.GRID_Y)/Constants.GRID_HEIGHT;
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
//


}


