package com.fs.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

/**
 * Created by Allen on 11/2/14.
 */


public class AudioUtils {

    public static AssetManager am = GameManager.getAm();

    /** returns a music track for map (during gameplay)
     *
     * @param track
     * @return
     */
    public static Music createMapMusic(int track){

        //Default music track (0)
        Music gameMusic = null;
        if (am.isLoaded(Constants.music1))
            gameMusic = am.get(Constants.music1, Music.class);

        if (track == 1){
            
        }

        return gameMusic;
    }

}