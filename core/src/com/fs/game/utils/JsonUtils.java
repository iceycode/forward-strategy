package com.fs.game.utils;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.fs.game.data.UserData;

/** JsonUtils - utility class for creating JSON docs
 *
 * Created by Allen on 6/1/15.
 */
public class JsonUtils {

    public static final String JSON_ERROR = "JsonError";

    /** Encodes UserData into JSON string
     *
     * @param userData : userData to encode
     * @return : a String representing JSON data
     */
    public static String encodeUserData(UserData userData){
        try{
            Json json = new Json();
            json.setIgnoreUnknownFields(true);

            String data = json.toJson(userData, UserData.class);
            return data;
        }
        catch(SerializationException e){
            e.printStackTrace();
            return JSON_ERROR;
        }
    }




}
