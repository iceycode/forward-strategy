package com.fs.game.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;
import com.fs.game.utils.UnitUtils;

import java.util.Random;

public class TestUtils {
	
	public static AssetManager am = GameManager.assetManager;
	public static Array<Unit> p1Units;
	public static Array<UnitInfo> arrayUnitInfo = GameManager.unitInfoArr;

	/** initializes unit creation, taking into account game board info
	 * 
	 * @param panelsOnStage
     * @param gridMatrix
	 */
	public static void initializeUnits(Array<Panel> panelsOnStage, Panel[][] gridMatrix) {
		am = GameManager.assetManager;
		
		am.getAssetNames(); //an array containing file name info
		
 
		GameData.gamePanels = panelsOnStage;
		GameData.gridMatrix = gridMatrix;
 		//UnitUtils.panelMatrix = gridMatrix;
 
		p1Units = new Array<Unit>(); //holds both players arrays of units

		//get the array unit info from unit textures

 	}

	/** testSetup1
	 * Humans vs Reptoids
	 * 
	 */
	public static void testBoardSetup2_16x12(MapStage stage) {
 		String faction1 = "Human";
		String faction2 = "Reptoid"; //not being used at the moment
		String faction3 = "Arthroid";
		
		float posXBL = Constants.GAMEBOARD_X; //x coordinate bottom left
		float posXBR = Constants.GAMEBOARD_X + 32*15; //(208f + 32f*15 ) x coordinate bottom right

        GameData.p1Units = UnitUtils.setUniPositions16x12(faction1, posXBL, false, 1, stage);
        GameData.p2Units = UnitUtils.setUniPositions16x12(faction3, posXBR, true, 2, stage);
        GameData.unitsInGame = p1Units;
		
	}


    /** returns an array containing exactly 2 units
     * used on TestStage
     * mainly for testing out unit interactions & actions
     *
     * @return
     */
    public static void test2Units(MapStage stage){
        Random rand = new Random();
        int randUnit1 = rand.nextInt((4-1)+1)+1; //human units
        int randUnit2 = rand.nextInt((24-21)+21)+1; //arthroid units

        float posX1 = 6 * 32 + Constants.GRID_X;
        float posY1 = 6 * 32 + Constants.GRID_Y;
        float posX2 = 10 * 32 + Constants.GRID_X;
        float posY2 = 10 * 32 + Constants.GRID_Y;

        Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
        Array<Unit> unitsOnBoard1 = new Array<Unit>(); //array for units per faction
        Array<Unit> unitsOnBoard2 = new Array<Unit>(); //array for units per faction

        am = GameManager.assetManager;
        //NOTE: as of 11/1/14; single digits: 1-6 is small; 7-9 medium; 0 large


        //sets up 1st unit (player 1's unit - Human)
        UnitInfo unitInfo1 = unitInfoArr.get(randUnit1);
        String unitPicPath = unitInfo1.getTexPaths().get(0);
        Texture tex1 = am.get(unitPicPath, Texture.class);
        Unit unit1 = new Unit(tex1, posX1, posY1, unitInfo1);
        unit1.setPlayer(1); //sets the player
        stage.addActor(unit1);
        p1Units.add(unit1);        //add to array containing each player's units


        //player 2 is a reptoid unit
        UnitInfo unitInfo = unitInfoArr.get(randUnit2);
        unitPicPath = unitInfo.getTexPaths().get(1);
        boolean exists = Gdx.files.internal(unitPicPath).exists();
        if (!exists){
            unitInfo.getTexPaths().get(0);
        }
        Texture tex2 = am.get(unitPicPath, Texture.class);
        Unit unit2 = new Unit(tex2, posX2, posY2, unitInfo);
        unit2.setPlayer(2);
        stage.addActor(unit2);
        p1Units.add(unit2);


    }



}
