/**
 * 
 */
package com.fs.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.fs.game.maps.MapActor;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.utils.Constants;

/** stores data that is relevant to current Units on the grid board
 *  - keeps track of current score
 *  - current units & player turn
 * 
 * @author Allen Jagoda
 *
 */
public class GameData {
	


	public static Array<Unit> otherUnits;
	public static Array<Array<Unit>> playerUnits;

    public static Array<Integer> unitDamageList;
 	
	public static float score;
 	public static float health;
 	public static TiledMapTileSet tiledMap;



	//----------for Unit data---------
    public static Array<Unit> unitsInGame;
    public static Array<Unit> p1Units;
    public static Array<Unit> p2Units;
    public static Array<String> unitDetails;
	public static String unitDamage;
    public static String unitName;



    //------for Stage/TiledMap data------
    //all these are related to basic board elements (grid tiles 32x32)
 	public static Panel[][] gridMatrix;	//the grid postiions
 	public static Array<Panel> gamePanels; //the gridBoard array of Panels
	public static Array<Panel> unitPanels; //each units panel position	
	public static Table gridTable;  //the table of panels added to stage
    public static MapActor[][] mapActMatrix; //stores based on grid cell


    //these have to do with tile map actors & map stage
	public static Array<MapActor> mapActorArr;



	//----Audio data: music, sound, volumes----//
	public static Music currMusic;
    public static Sound currSound;
    public static float currVolumeMusic;
    public static float currVolumeSounds;
	 

}
