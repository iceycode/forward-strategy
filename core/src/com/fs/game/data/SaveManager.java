package com.fs.game.data;

import com.badlogic.gdx.utils.ObjectMap;

/** Stores player save data
 *
 * Created by Allen on 12/18/14.
 */
public class SaveManager {

    private boolean encoded; //encrypts data if true

    public SaveManager(boolean encoded){
        this.encoded = encoded;
    }

    //class that actually stores the data in object map
    public static class Save{
        public ObjectMap<String, Object> data = new ObjectMap<String, Object>();

    }

}