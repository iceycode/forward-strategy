package com.fs.game.utils;

import appwarp.WarpController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.fs.game.assets.Constants;
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









    public static boolean positionOnLeft(){
        Random rand = new Random();

        boolean leftPos = false;
        int pos1 = rand.nextInt(1000);
        int pos2 = rand.nextInt(1000);

        if (pos1 > pos2){
            leftPos = true;
        }
        else if (pos1 == pos2){
            leftPos = positionOnLeft();
        }

        return leftPos;
    }



    public static class PlayerData{
        private String player;
        private String faction;

        public PlayerData(){

        }

        public String getName() {
            return player;
        }

        public String getFaction() {
            return faction;
        }

        public void setPlayer(String player) {
            this.player = player;
        }

        public void setFaction(String faction) {
            this.faction = faction;
        }
    }

    public static void sendSetupData(int playerID, String playerName, String faction){
        try{
            UserData userData = new UserData();
            userData.setName(playerName);
            userData.setFaction(faction);
            userData.setPlayerID(playerID);

            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            String data = json.toJson(userData);

            WarpController.getInstance().sendGameUpdate(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void sendStartData(int player, String name, String faction, Array<UnitData> unitDataArray){
        try{
//            PlayerData playerData = new PlayerData(player, faction);
            UserData userData = new UserData();

            userData.setPlayer(player);
            userData.setName(name);
            userData.setFaction(faction);
            userData.setUnitList(unitDataArray);

            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            String data = json.toJson(userData);

//            Gdx.app.log(LOG, "start Json file for player "+ player + ":" + data); //for logging purposes

            WarpController.getInstance().sendGameUpdate(data);

        }
        catch(Exception e){
            System.out.println("Start data Json writing error!");
            e.printStackTrace();
        }
    }



    //Initializes App42 API/AppWarp cloud services
    public static void initApp42Services(String playerName){
        //ServiceAPI spJava = new ServiceAPI(Constants.App42.API_KEY, Constants.App42.SECRET_KEY);
        WarpClient.initialize(Constants.App42.API_KEY, Constants.App42.SECRET_KEY);
        WarpController.getInstance().startApp(playerName); //starts appwarp

    }



    public static String setupUsername(){
        Random rand = new Random();
        int idLength = rand.nextInt(15)+1;

        String uniqueID = getRandomHexString(idLength);

        return "tester" + uniqueID;
    }

    private static String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }


}
