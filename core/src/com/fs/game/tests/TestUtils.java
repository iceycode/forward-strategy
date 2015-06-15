package com.fs.game.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.PlayerUtils;
import com.fs.game.utils.UnitUtils;

import java.util.Random;

public class TestUtils {

    /** A method for testing multiplayer setup
     * Sets up player's units using defualt setup
     *
     * @param player : determines the player's side
     * @param faction : chosen or random
     */
    public static Array<Unit> randomMultiplayerSetup(int player, String playerName, String faction){
        Array<UnitInfo> unitInfoArray = UnitUtils.Setup.getDefaultUnits(faction);

        Array<Unit> playerUnits = UnitUtils.Setup.setupUnits(unitInfoArray, player, playerName);
        return playerUnits;
    }


    public static void testBoardSetup3 (GameStage stage){
        GameData.playerName = PlayerUtils.setupUsername();
        GameData.enemyName = "testAI";

        String faction1 = GameData.playerFaction; //player 1s faction
        String faction2 = GameData.enemyFaction; //player 2's faction

        Array<UnitInfo> p1UnitInfo = UnitUtils.Setup.getDefaultUnits(faction1);
        Array<UnitInfo> p2UnitInfo = UnitUtils.Setup.getDefaultUnits(faction2);

        Array<Unit> p1Units = UnitUtils.Setup.setupUnits(p1UnitInfo, 1, GameData.playerName);
        Array<Unit> p2Units = UnitUtils.Setup.setupUnits(p2UnitInfo, 2, GameData.enemyName);


        stage.addUnits(p1Units);
        stage.addUnits(p2Units);

        GameData.playerUnits = p1Units;
        GameData.enemyUnits = p2Units;

        //adds to all units array in game data
        GameData.unitsInGame.put(1, p1Units);
        GameData.unitsInGame.put(2, p2Units);
    }





    /** Test setup for TEST 4- Larger Grid
     *  Sets up Units on a grid of size 40x30
     *
     * @param stage: stage to put units on
     */
    public static void testBoardSetup4(GameStage stage){
        String faction1 = GameData.playerFaction; //player 1s faction
        String faction2 = GameData.enemyFaction; //player 2's faction

        Array<UnitInfo> p1UnitInfo = UnitUtils.Setup.getDefaultUnits(faction1);
        Array<UnitInfo> p2UnitInfo = UnitUtils.Setup.getDefaultUnits(faction2);

        Array<Unit> p1Units = UnitUtils.Setup.setupUnits(p1UnitInfo, 1, GameData.playerName );
        Array<Unit> p2Units = UnitUtils.Setup.setupUnits(p2UnitInfo, 2, GameData.enemyName );


        stage.addUnits(p1Units);
        stage.addUnits(p2Units);

        //adds to all units array in game data
        GameData.unitsInGame.put(1, p1Units);
        GameData.unitsInGame.put(2, p2Units);

        GameData.playerUnits = p1Units;
        GameData.enemyUnits = p2Units;
    }

    /** returns an array containing exactly 2 units
     * used on TestStage
     * mainly for testing out unit interactions & actions
     *
     * @return
     */
    public static void test2Units(GameStage stage){
        Random rand = new Random();
        int randUnit1 = rand.nextInt(4); //human units
        int randUnit2 = rand.nextInt(4); //arthroid units

        //custom positions to place units at after original setup
        float posX1 = GameData.panelMatrix[6][10].getX();
        float posY1 = GameData.panelMatrix[6][10].getY();
        float posX2 = GameData.panelMatrix[10][10].getX();
        float posY2 = GameData.panelMatrix[10][10].getY();

        //NOTE: as of 11/1/14; single digits: 1-6 is small; 7-9 medium; 0 large

        //sets up 1st unit (player 1's unit - Human)
        GameData.playerName = "tester1_H";
        UnitInfo unitInfo1 = Assets.unitInfoMap.get("Human").get(randUnit1);
        GameData.playerUnits = UnitUtils.Setup.setupUnits(new Array<UnitInfo>(new UnitInfo[]{unitInfo1}), 1, GameData
                .playerName);
        GameData.playerUnits.first().setPosition(posX1, posY1);
        GameData.playerUnits.first().setOrigin(posX1, posY1);
//        GameData.playerUnits.first().setGridPos(new int[]{6, 10});

        stage.addUnits(GameData.playerUnits);

        //player 2 is an arthroid unit
        GameData.enemyName = "tester2_A";
        UnitInfo unitInfo2 = Assets.unitInfoMap.get("Arthroid").get(randUnit2);
        GameData.enemyUnits = UnitUtils.Setup.setupUnits(new Array<UnitInfo>(new UnitInfo[]{unitInfo2}), 2, GameData
                .enemyName);
        GameData.enemyUnits.first().setPosition(posX2, posY2);
        GameData.enemyUnits.first().setOrigin(posX1, posY1);
//        GameData.enemyUnits.first().setGridPos(new int[]{10, 10});
        stage.addUnits(GameData.enemyUnits);
    }





//    /** useful class for printing out positions of actors/widgets/tiles/etc
//     *
//     * @param positions
//     * @return
//     */
//    public static String printPositions(Array<Vector2> positions){
//        String posMessage = "";
//
//        for (Vector2 vec : positions){
//            String x = Float.toString(vec.x);
//            String y = Float.toString(vec.y);
//            String screenPos = "{" + x + ", " + y + "}, ";
//            posMessage.concat(screenPos);
//        }
//
//
//        return posMessage;
//    }

    public static void logTest(String message){

        Gdx.app.log("Test LOG: " , message);

    }
}
