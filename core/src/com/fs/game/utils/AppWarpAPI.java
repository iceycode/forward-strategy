package com.fs.game.utils;

import appwarp.WarpController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;

/** Utility class for creating AppWarp objects for sending/recieving
 *  data related to game players, Units and other (map, time, scores, etc)
 *
 * Created by Allen on 6/1/15.
 */
public class AppWarpAPI {


    private static AppWarpAPI instance;

    int playerID = 0; //playerID to determine whether player 1 or 2, p1 goes first & on left

    public static final String JSON_ERROR = "JsonError";

    //int flags relating to states in decoding/sending data
    public final static int _SETUP = 0;
    public final static int _UNIT_UPDATE = 1;
    public final static int _TURN_CHANGE = 2;
    public final static int _PAUSE = 3;
    public final static int _RESUME = 4;
    public final static int _READY_TO_START = 5;

    public static AppWarpAPI getInstance() {
        if (instance == null)
            instance = new AppWarpAPI();

        return instance;
    }



    public void sendGameSetupUpdate(){
        UserData userData = getSetupUserData();

        String data = AppWarpAPI.getInstance().encodeUserData(userData);

        WarpController.getInstance().sendGameUpdate(data);
    }

    public UserData getSetupUserData(){
        playerID = PlayerUtils.randomLengthPlayerID();

        UserData userData = new UserData();
        userData.setName(GameData.playerName);
        userData.setPlayerID(playerID);
        userData.setFaction(GameData.playerFaction);
        userData.setUpdateState(0);

        return userData;
    }


    //sends that User has recieved setup info & is ready to go
    public void sendReadyUpdate(){
        UserData userData = new UserData();
        userData.setUpdateState(_READY_TO_START);
        userData.setName(GameData.playerName);

        String data = encodeUserData(userData);

        WarpController.getInstance().sendGameUpdate(data);
    }

    public UserData changeTurnData(int score, int player, boolean auto){
        UserData userData = new UserData();
        userData.setScore(score);
        userData.setPlayer(player);
        userData.setPlayerTurn(auto); //sets other player turn as true
        userData.setName(GameData.playerName);
        userData.setUpdateState(_TURN_CHANGE);

        return userData;
    }


    /** Sends Unit update via UnitData nested in a UserData object
     *
     * @param unitData : unitData to send
     */
    public void sendUnitUpdate(UnitData unitData){
        UserData userData = new UserData();
        userData.setUnitData(unitData);
        userData.setUpdateState(1);


        try{
            Json json = new Json();
            String data = json.toJson(userData, UserData.class);

            WarpController.getInstance().sendGameUpdate(data);
        }
        catch(SerializationException e){
            e.printStackTrace();
        }
    }

    /** Encodes UserData into JSON string
     *
     * @param userData : userData to encode
     * @return : a String representing JSON data
     */
    public String encodeUserData(UserData userData){
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

    public UserData decodeUserData(String message){
        UserData data = new UserData();
        try{
            Json json = new Json();
            data = json.fromJson(UserData.class, message);
        }
        catch(SerializationException e){
            e.printStackTrace();
        }

        return data;
    }

    public int getPlayerID(){
        return playerID;
    }

    private void log(String message){
        Gdx.app.log("AppWarp API", message);
    }
}
