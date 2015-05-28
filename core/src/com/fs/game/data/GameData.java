/**
 *
 */
package com.fs.game.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.fs.game.actors.UnitImage;
import com.fs.game.map.MapActor;
import com.fs.game.map.Panel;
import com.fs.game.actors.Unit;
import com.fs.game.actors.UnitInfo;

/**
 * stores data that is relevant to current Units on the grid board
 * - keeps track of current score
 * - current units & player turn
 *
 * @author Allen Jagoda
 */
public class GameData {
	private static GameData instance;

    //-------Game play info--------
    public static Unit chosenUnit = null; //current unit chosen
    public static Array<Unit> enemyUnits;
    public static String[] unitDetails; //chosen unit's attributes
    public static int scoreP1 = 0; //tracks player 1 score
    public static int scoreP2 = 0; //tracks player 2 score

    public static Array<Unit> playerUnits;
    public static OrderedMap<Integer, Array<Unit>> unitsInGame  = new OrderedMap<Integer, Array<Unit>>(); //all units, p1Units, p2Units

    public static String enemyName;  //enemy's name
    public static String playerName; //player name
    public static int player; //current player (1 or 2)
    public static boolean playerTurn; //if true, next player goes
    public static String playerFaction;
    public static String enemyFaction;

    public static int difficulty = 0; //AI difficulty, default is 0

    public static boolean isChosen; //whether a unit is chosen

    //------data to/from menu screens (or tests)----
    public static String[] factions;

    public static Array<UnitImage> factUnitImages = new Array<UnitImage>();
    public static Array<UnitInfo> playerUnitChoices = new Array<UnitInfo>();
    public static int mapChoice; //level choice (for tiled map)
    public static int testType; //type of test that will be implemented

    //------for Map (TiledMap & Panel) data------
    //TODO: need to fix redudencies in Panel list/array data
    //all these are related to basic board elements (grid tiles 32x32)
    public static Panel[][] panelMatrix;    //the grid postiions in double array
    public static Array<Panel> gamePanels = new Array<Panel>();; //the gridBoard Array of Panels
    public static Array<MapActor> mapActorArr = new Array<MapActor>();; //tiled map actors

    //for audio
    public static float[] volumes = {.5f, .5f};

    //boolean value for game being in test mode
    public static boolean isTest = true;

    
    public static GameData getInstance(){
    	if (instance == null){
    		instance = new GameData();
    	}
    	return instance;
    }





}
