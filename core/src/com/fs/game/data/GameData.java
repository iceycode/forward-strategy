/**
 *
 */
package com.fs.game.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitImage;
import com.fs.game.units.UnitInfo;

/**
 * stores data that is relevant to current Units on the grid board
 * - keeps track of current score
 * - current units & player turn
 *
 * @author Allen Jagoda
 */
public class GameData {


    //-------Game play info--------
    public static Unit chosenUnit; //current unit chosen
    public static Array<Unit> enemyUnits;
    public static String[] unitDetails; //chosen unit's attributes
    public static int currScore; //current players score
    public static int scoreP1; //tracks player 1 score
    public static int scoreP2;

    public static Array<Unit> playerUnits;
    public static OrderedMap<Integer, Array<Unit>> unitsInGame; //all units, p1Units, p2Units
    public static String playerName; //player name
    public static String enemyName;  //enemy's name
    public static int playerPosition; //1 : left, 2 : right
    public static int enemyPosition;
    public static int updateState;

    public static int currPlayer; //current player (1 or 2)
    public static boolean isChosen; //whether a unit is chosen
    public static boolean playerTurn; //if true, next player goes

    //------data to/from menu screens (or tests)----
    public static String[] factions;
    public static String playerFaction;
    public static String enemyFaction; //could be obtained from multiplayer json
    public static Array<UnitImage> factUnitImages;
    public static Array<UnitInfo> playerUnitChoices;
    public static int mapChoice; //level choice (for tiled map)
    public static int testType; //type of test that will be implemented


    //------for Map (TiledMap & Panel) data------
    //TODO: need to fix redudencies in these
    //all these are related to basic board elements (grid tiles 32x32)
    public static Panel[][] panelMatrix;    //the grid postiions
    public static Array<Panel> gamePanels; //the gridBoard array of Panels
    public static Array<MapActor> mapActorArr; //tiled map actors

    //for audio
    public static float[] volumes;


    //initialize all of the fields here
    public static void initGameData(){
        chosenUnit = null;
        unitsInGame = new OrderedMap<Integer, Array<Unit>>();

        factUnitImages = new Array<UnitImage>();
        playerUnitChoices = new Array<UnitInfo>();

        gamePanels = new Array<Panel>();
        mapActorArr = new Array<MapActor>();

        float[] vols = {.5f, .5f}; //temporary, TODO: get these from preferences
        volumes = vols;

        //initalize scores
        scoreP1 = 0;
        scoreP2 = 0;
    }


}
