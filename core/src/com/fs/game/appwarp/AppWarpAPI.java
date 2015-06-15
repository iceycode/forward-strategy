package com.fs.game.appwarp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.fs.game.units.Unit;
import com.fs.game.utils.PlayerUtils;

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
    public final static int READY_TO_START = 6; //ready to start game, both players are setup
    public final static int SETUP_UPDATE = 0;
    public final static int UNIT_UPDATE = 1;
    public final static int TURN_UPDATE = 2;
    public final static int GAME_END_UPDATE = 3; //when game ended via win/lose
    public final static int PAUSE_UPDATE = 4;
    public final static int RESUME_UPDATE = 5;


    public static AppWarpAPI getInstance() {
        if (instance == null)
            instance = new AppWarpAPI();

        return instance;
    }



    public void sendGameSetupUpdate(){
        UserData userData = getSetupUserData();

        String data = AppWarpAPI.getInstance().encodeUserData(userData);

        WarpController.getInstance().sendGameUpdate(data);
        log("SENT setup update");
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
        userData.setUpdateState(READY_TO_START);
        userData.setName(GameData.playerName);

        String data = encodeUserData(userData);

        WarpController.getInstance().sendGameUpdate(data);
    }

//    public UserData changeTurnData(int score, int player, boolean auto){
//        UserData userData = new UserData();
//        userData.setScore(score);
//        userData.setPlayer(player);
//        userData.setPlayerTurn(auto); //sets other player turn as true
//        userData.setName(GameData.playerName);
//        userData.setUpdateState(TURN_UPDATE);
//
//        return userData;
//    }


    /** Sends multiplayer update for the currUnit
     *
     */
    public void sendUnitUpdate(Unit unit, UnitData unitData){
        //add to a UserData object
        UserData userData = new UserData();
        userData.setPlayer(unit.getPlayer()); //set userData player
        userData.setUpdateState(UNIT_UPDATE);
        userData.setUnitData(unitData);

        sendUserUpdate(userData);
    }


    /** Sends UserData containing updateState for game to
     *  enter PAUSE GameState
     *
     */
    public void sendPauseUpdate(){
        UserData userData = new UserData();
        userData.setName(GameData.playerName);
        userData.setUpdateState(PAUSE_UPDATE); //pause game

        sendUserUpdate(userData);
    }


    /** Sends UserData containing updateState for game to
     *  enter RESUME GameState
     *
     */
    public void sendResumeUpdate(){
        UserData userData = new UserData();
        userData.setName(GameData.playerName);
        userData.setUpdateState(RESUME_UPDATE); //pause game

        sendUserUpdate(userData);
    }


    /** Sends update for player who won the game to the
     *  player that lost, indicating that game is over.
     *
     *  TODO: in future add more functionality to this
     *      ideas: Leaderboards, Log (history of players won/lost against)
     */
    public void sendGameWinUpdate(){
        UserData userData = new UserData();
        userData.setUpdateState(GAME_END_UPDATE); //score update

        sendUserUpdate(userData);
    }


    /** Sends a user update with UserData
     *  This is the main method that communicates with other player
     *
     * @param userData : userData containing information relavent to game play
     */
    public void sendUserUpdate(UserData userData){
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
