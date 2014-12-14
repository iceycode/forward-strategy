/**
 * 
 */
package com.fs.game.data;

import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

/** contains units used by each player
 * 
 * @author Allen Jagoda
 *
 */
public class PlayerPrefs implements Preferences{

	Preferences pref;
	
	String playerFaction;
	int playerScore;
	String nameMapPlayedLast;
	
	

	public static HashMap<String, Map> hm = new HashMap<String, Map>();

    @Override
    public Preferences putBoolean(String key, boolean val) {
        return null;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        return null;
    }

    @Override
    public Preferences putLong(String key, long val) {
        return null;
    }

    @Override
    public Preferences putFloat(String key, float val) {
        return null;
    }

    @Override
    public Preferences putString(String key, String val) {
        return null;
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        return null;
    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public int getInteger(String key) {
        return 0;
    }

    @Override
    public long getLong(String key) {
        return 0;
    }

    @Override
    public float getFloat(String key) {
        return 0;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public int getInteger(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public String getString(String key, String defValue) {
        return null;
    }

    /**
     * Returns a read only Map<String, Object> with all the key, objects of the preferences.
     */
    @Override
    public Map<String, ?> get() {
        return null;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void remove(String key) {

    }

    /**
     * Makes sure the preferences are persisted.
     */
    @Override
    public void flush() {

    }


    private static class PlayerData {
		public PlayerData(String name) {
			
		}
		
	}

}
