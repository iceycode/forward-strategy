/**
 * 
 */
package com.fs.game.assets;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.fs.game.units.Unit;

import java.util.HashMap;
import java.util.Map;

/** contains units used by each player
 * 
 * @author Allen Jagoda
 *
 */
public class PlayerPrefs {

	Preferences pref;
	
	String playerFaction;
	int playerScore;
	String nameMapPlayedLast;
	
	

	public static HashMap<String, Map> hm = new HashMap<String, Map>();

	/** keeps record of player units
	 *  - tracks health, moves, distance
	 *  - compares unit stats
	 * 
	 */
	public static Array<Unit> player1Units() {
		Array<Unit> currentUnits = new Array<Unit>();
		
		return currentUnits;
	}
	
	public static void player2Units() {
		
	}
	
	private static class PlayerData {
		public PlayerData(String name) {
			
		}
		
	}

}
