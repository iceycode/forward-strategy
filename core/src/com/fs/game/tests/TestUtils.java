package com.fs.game.tests;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
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
        Array<Unit> playerUnits;
        Array<UnitInfo> unitInfoArray = UnitUtils.Setup.getDefaultUnits(GameData.playerFaction);

        if (player == 1){
            playerUnits = UnitUtils.Setup.setupUnits(unitInfoArray, player, playerName, Constants.UNITS_POS_LEFT);

            GameData.unitsInGame.put(1, playerUnits);
        }
        else{
            playerUnits = UnitUtils.Setup.setupUnits(unitInfoArray, player, playerName, Constants.UNITS_POS_RIGHT );
            GameData.unitsInGame.put(2, playerUnits);
        }

        return playerUnits;
    }

    public static Array<Unit> randomMultiplayerSetup1(int player, String name, float[][] positions, String faction, GameStage stage){
        Array<Unit> playerUnits;
        Array<UnitInfo> unitInfoArray = UnitUtils.Setup.getDefaultUnits(faction);

        playerUnits = UnitUtils.Setup.setupUnits(name, unitInfoArray, player, positions, stage);

        GameData.unitsInGame.put(player, playerUnits);
        stage.p1Units = playerUnits;

        return playerUnits;
    }


    public static void testJsonFile(int player, int score, String name, Array<Unit> units, GameStage stage){
        Json json = new Json();

    }



//    public static Array<Integer> randIndices(int[] indices){
//        Random rand = new Random();
//        ArrayList listIndices = new ArrayList(Arrays.asList(indices));
//        Array<Integer> randIndices = new Array<Integer>();
//
//        while (listIndices.size()> 0){
//
//            int in = rand.nextInt(listIndices.toArray().length);
//
//            if (listIndices.contains(in)){
//                listIndices.remove(in);
//                randIndices.add(in);
//            }
//
//        }
//
//        return randIndices;
//    }

	/** testSetup1
	 * Humans vs Arthroids
	 * 
	 */
	public static void testBoardSetup2_16x12(GameStage stage) {
 		String faction1 = "Human"; //player 1s faction
		String faction2 = "Arthroid"; //player 2's faction


        Array<UnitInfo> p1UnitInfo = UnitUtils.Setup.getDefaultUnits(faction1);
        Array<UnitInfo> p2UnitInfo = UnitUtils.Setup.getDefaultUnits(faction2);

        stage.p1Units = UnitUtils.Setup.setupUnits(p1UnitInfo, 1, Constants.UNITS_POS_LEFT, stage);
        stage.p2Units = UnitUtils.Setup.setupUnits(p2UnitInfo, 2, Constants.UNITS_POS_RIGHT, stage);

        //adds to all units array in game data
        GameData.unitsInGame.put(1, stage.p1Units);
        GameData.unitsInGame.put(2, stage.p2Units);
	}


    /** returns an array containing exactly 2 units
     * used on TestStage
     * mainly for testing out unit interactions & actions
     *
     * @return
     */
    public static void test2Units(GameStage stage){


        Random rand = new Random();
        int randUnit1 = rand.nextInt((4-1)+1)+1; //human units
        int randUnit2 = rand.nextInt((24-21)+21)+1; //arthroid units

        float posX1 = 6 * 32 + Constants.GRID_X;
        float posY1 = 10 * 32 + Constants.GRID_Y;
        float posX2 = 10 * 32 + Constants.GRID_X;
        float posY2 = 10 * 32 + Constants.GRID_Y;

        Array<UnitInfo> unitInfoArr = Assets.unitInfoArray;
        Array<Unit> unitsOnBoard1 = new Array<Unit>(); //array for units per faction
        Array<Unit> unitsOnBoard2 = new Array<Unit>(); //array for units per faction

        AssetManager am = Assets.assetManager;
        //NOTE: as of 11/1/14; single digits: 1-6 is small; 7-9 medium; 0 large


        //sets up 1st unit (player 1's unit - Human)
        UnitInfo unitInfo1 = unitInfoArr.get(randUnit1);
        Unit unit1 = new Unit(unitInfo1, posX1, posY1, 1);
        stage.addActor(unit1);
        stage.p1Units.add(unit1);        //add to array containing each player's units


        //player 2 is a reptoid unit
        UnitInfo unitInfo = unitInfoArr.get(randUnit2);
        Unit unit2 = new Unit(unitInfo, posX2, posY2, 2);
        unit2.setLock(true);
        stage.addActor(unit2);
        stage.p2Units.add(unit2);

        GameData.unitsInGame.put(1, stage.p1Units);
        GameData.unitsInGame.put(2, stage.p2Units);
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
}
