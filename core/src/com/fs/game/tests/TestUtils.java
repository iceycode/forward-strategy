package com.fs.game.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.constants.Constants;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;
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
        String faction1 = GameData.playerFaction; //player 1s faction
        String faction2 = GameData.enemyFaction; //player 2's faction

        Array<UnitInfo> p1UnitInfo = UnitUtils.Setup.getDefaultUnits(faction1);
        Array<UnitInfo> p2UnitInfo = UnitUtils.Setup.getDefaultUnits(faction2);

        Array<Unit> p1Units = UnitUtils.Setup.setupUnits(p1UnitInfo, 1, GameData.playerName, Constants.UNITS_POS_LEFT);
        Array<Unit> p2Units = UnitUtils.Setup.setupUnits(p2UnitInfo, 2, GameData.enemyName, Constants.UNITS_POS_RIGHT);


        stage.addUnits(p1Units );
        stage.addUnits(p2Units );

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


        stage.addUnits(p1Units );
        stage.addUnits(p2Units );

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

        float posX1 = 6 * 32 + Constants.MAP_X;
        float posY1 = 10 * 32 + Constants.MAP_Y;
        float posX2 = 10 * 32 + Constants.MAP_X;
        float posY2 = 10 * 32 + Constants.MAP_Y;

        //NOTE: as of 11/1/14; single digits: 1-6 is small; 7-9 medium; 0 large

        //sets up 1st unit (player 1's unit - Human)
        UnitInfo unitInfo1 = Assets.unitInfoMap.get("Human").get(randUnit1);
        Unit unit1 = new Unit(unitInfo1, posX1, posY1, 1);
        unit1.setOwner(GameData.playerName);
        stage.addActor(unit1);
//        stage.p1Units.add(unit1);        //add to array containing each player's units


        //player 2 is an arthroid unit
        UnitInfo unitInfo = Assets.unitInfoMap.get("Arthroid").get(randUnit2);;
        Unit unit2 = new Unit(unitInfo, posX2, posY2, 2);
        unit2.setOwner(GameData.enemyName);
//        unit2.setLock(true);
        stage.addActor(unit2);
//        stage.p2Units.add(unit2);

//        GameData.unitsInGame.put(1, stage.p1Units);
//        GameData.unitsInGame.put(2, stage.p2Units);
    }


    public static void testAISetup(GameStage stage){

    }


    /** useful class for printing out positions of actors/widgets/tiles/etc
     *
     * @param positions
     * @return
     */
    public static String printPositions(Array<Vector2> positions){
        String posMessage = "";

        for (Vector2 vec : positions){
            String x = Float.toString(vec.x);
            String y = Float.toString(vec.y);
            String screenPos = "{" + x + ", " + y + "}, ";
            posMessage.concat(screenPos);
        }


        return posMessage;
    }





    public static void logTest(String message){

        Gdx.app.log("Test LOG: " , message);

    }
}
