package com.fs.game.utils;

import appwarp.WarpController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;

import java.util.Random;

/** Multiplayer Utility class
 *  helps with interaction of App42 API & GameData during setup & game play
 *
 * Created by Allen on 11/26/14.
 */
public class MultiUtils {

    private final static String LOG = "MultiUtils LOG: ";


    //Initializes App42 API/AppWarp cloud services
    public static void initApp42Services(){
        //ServiceAPI spJava = new ServiceAPI(Constants.App42.API_KEY, Constants.App42.SECRET_KEY);
        WarpClient.initialize(Constants.App42.API_KEY, Constants.App42.SECRET_KEY);
        WarpController.getInstance().startApp(GameData.playerName); //starts appwarp

    }



    public static String setupUsername(){
        Random rand = new Random();
        String numID = Integer.toString(rand.nextInt(1000)+1);

        return "playerTester" + numID;
    }



    /** Checks to see if users have conflicting player assignments
     * 1st player always gets switched if there is a collision
     *
     * @param player
     * @param otherPlayer
     * @return returns an updated (or not) player value
     */
    public static int matchPlayerVals(int player, int otherPlayer){
        if (player == 1 && otherPlayer == 1)
            player = 2;
        else if (player == 2 && otherPlayer == 2)
            player = 1;

        return player;
    }


//    public static class PlayerData{
//        private int player;
//        private String faction;
//
//        public PlayerData(int player, String faction){
//            this.player = player;
//            this.faction = faction;
//        }
//
//        public int getPlayer() {
//            return player;
//        }
//
//        public String getFaction() {
//            return faction;
//        }
//
//    }


    public static void sendStartData(int player, String faction){
        try{
//            PlayerData playerData = new PlayerData(player, faction);
            UserData userData = new UserData(player, 0, faction);

            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            String data = json.toJson(userData);

            Gdx.app.log(LOG, "start Json file for player "+ player + ":" + data); //for logging purposes

            WarpController.getInstance().sendGameUpdate(data);

        }
        catch(Exception e){
            System.out.println("Start data Json writing error!");
            e.printStackTrace();
        }
    }

    /** writes & sends a JSONObject with updated unit states
     *
     * @param player : player
     * @param score : player's score
     * @param name : player's name (unique ID)
     * @param units : all of the players current Units (for updating units)
     *
     */
    public static void sendPlayerData(int player, int score, String name, Array<UnitData> units){

        try {
            UserData userData = new UserData(player, score, name);
            userData.setUnitList(units);

            Json json = new Json();
            String data = json.prettyPrint(userData);


            System.out.println("UserData Json as string: " + data);

            WarpController.getInstance().sendGameUpdate(data);

        } catch (Exception e) {
            // exception in sendPlayerData
            System.out.println("Error writing json! ");
            e.printStackTrace();
        }
    }








}
