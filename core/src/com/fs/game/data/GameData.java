/**
 * 
 */
package com.fs.game.data;

import com.badlogic.gdx.Gdx;
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
	
	 
	//these hold units
	public static Array<Unit> allUnits;
	public static Array<Unit> otherUnits;
	public static Array<Unit> playerUnits;
 	
	public static float score;
 	public static float health; 
 	
 	public static TiledMapTileSet tiledMap;
 	
	//this stores temporarily the unit damage when each unit clicked on
	public static String unitDamage;
 	public static MapActor[][] mapActMatrix; //stores based on grid cell

 	//all these are related to basic board elements (grid tiles 32x32)
 	public static Panel[][] gridMatrix;	//the grid postiions
 	public static Array<Panel> gamePanels; //the gridBoard array of Panels
	public static Array<Panel> unitPanels; //each units panel position	
	public static Table gridTable;  //the table of panels added to stage
	
	//these have to do with tile map actors & map stage
	
	public static String unitName; 
	
	public static Array<MapActor> mapActorArr;
	
	
	/** method which finds all the actors on stage
	 * 
	 * @param stageActors
	 * @return
	 */
	public static Array<Unit> findAllUnits(Array<Actor> stageActors){
		Array<Unit> unitArr = new Array<Unit>();
		
 		for (Actor a : stageActors) {
			if (a.getClass().equals(Unit.class)) {
				Unit u = (Unit)a;
				unitArr.add(u);
			}
		}
 		
		return unitArr;
	}
	
	
	 

}
