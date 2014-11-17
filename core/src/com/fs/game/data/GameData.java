/**
 * 
 */
package com.fs.game.data;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitImage;
import com.fs.game.units.UnitInfo;

import java.util.HashMap;

/** stores data that is relevant to current Units on the grid board
 *  - keeps track of current score
 *  - current units & player turn
 * 
 * @author Allen Jagoda
 *
 */
public class GameData {

    //-------Game play info--------
    public static Array<UnitInfo> unitInfoArray;
    public static Array<int[]> damageListArray;
    public static HashMap<String, Integer> playerScores;
    public static float scoreP1;
    public static float scoreP2;
    public static Unit chosenUnit; //current unit chosen
    public static Array<Unit> enemyUnits;
    public static boolean unitIsChosen; //whether a unit is chosen
    public static boolean finishTurn;
    public static Array<String> unitDetails; //chosen unit's attributes
	public static String unitDamage; //unit damageList as String
    public static String unitName; //unit name
    public static float timerCount; //keeps track of time during game play


    //------data to/from menu screens (or tests)----
    public static int currPlayer;
    public static String currFaction;
    public static int currMapChoice;
    public static String p1Name;
    public static String p2Name;
    public static String p1Faction;
    public static String p2Faction;
    public static Array<UnitImage> factUnitImages;
    public static Array<Unit> p1Units; //player 1 units
    public static Array<Unit> p2Units; //player 2 units
    public static Array<Unit> unitsInGame; //all units
    public static int mapChoice; //level choice (for tiled map)


    //------for Stage/TiledMap data------
    //all these are related to basic board elements (grid tiles 32x32)
 	public static Panel[][] panelMatrix;	//the grid postiions
 	public static Array<Panel> gamePanels; //the gridBoard array of Panels
    //these have to do with tile map actors & map stage
	public static Array<MapActor> mapActorArr;



	//----Audio data: music, sound, volumes----//
	public static Music currMusic;
    public static Sound currSound;
    public static float currVolumeMusic;
    public static float currVolumeSounds;






    //------TESTING PARAMS-------
    public static int testType; //type of test that will be implemented




}
