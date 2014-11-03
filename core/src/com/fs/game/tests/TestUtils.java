package com.fs.game.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;
import com.fs.game.utils.UnitUtils;

public class TestUtils {
	
	public static AssetManager am;
	public static Array<Unit> arrayUnits;
	public static Array<Array<Unit>> playerUnits;
	public static Array<UnitInfo> arrayUnitInfo;

	/** initializes unit creation, taking into account game board info
	 * 
	 * @param panelsOnStage
     * @param gridMatrix
	 */
	public static void initializeUnits(Array<Panel> panelsOnStage, Panel[][] gridMatrix) {
		am = GameManager.am;
		
		am.getAssetNames(); //an array containing file name info
		
 
		GameData.gamePanels = panelsOnStage;
		GameData.gridMatrix = gridMatrix;
 		//UnitUtils.panelMatrix = gridMatrix;
 
		playerUnits = new Array<Array<Unit>>(2); //holds both players arrays of units
 		arrayUnits = new Array<Unit>(30); //array that stores all units
		
		//get the array unit info from unit textures
 		arrayUnitInfo = GameManager.unitInfoArr;
 	}
	
	/** testSetup1
	 * Humans vs Reptoids
	 * 
	 */
	public static void testBoardSetup1_12x12() {
 		String faction1 = "Human";
		String faction2 = "Reptoid";
		String faction3 = "Arthroid";
		
		Array<Unit> humanUni = UnitUtils.setUniPositions(faction1, 0, false, 1);
		Array<Unit> reptoidUni = UnitUtils.setUniPositions(faction3, 11, true, 2);
		
		playerUnits.add(humanUni);
		playerUnits.add(reptoidUni);
		
	}
	
	/** testSetup1
	 * Humans vs Reptoids
	 * 
	 */
	public static void testBoardSetup2_16x12() {
 		String faction1 = "Human";
		//String faction2 = "Reptoid";
		String faction3 = "Arthroid";
		
		float posXBL = Constants.GAMEBOARD_X; //x coordinate bottom left
		float posXBR = Constants.GAMEBOARD_X + 32*15; //(208f + 32f*15 ) x coordinate bottom right
		
		Array<Unit> humanUni = UnitUtils.setUniPositions16x12(faction1, posXBL, false, 1);
		Array<Unit> reptoidUni = UnitUtils.setUniPositions16x12(faction3, posXBR, true, 2);
		
		playerUnits.add(humanUni);
		playerUnits.add(reptoidUni);
		
	}


    /** returns an array containing exactly 2 units
     * used on TestStage
     * mainly for testing out unit interactions & actions
     *
     * @return
     */
    public static void testTwoUnits(){

        float posX1 = 4 * 32 + Constants.GRID_X;
        float posY1 = 4 * 32 + Constants.GRID_Y;
        float posX2 = 10 * 32 + Constants.GRID_X;
        float posY2 = 10 * 32 + Constants.GRID_Y;

        Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
        Array<Unit> unitsOnBoard1 = new Array<Unit>(); //array for units per faction
        Array<Unit> unitsOnBoard2 = new Array<Unit>(); //array for units per faction

        am = GameManager.am;
        //NOTE: as of 11/1/14; single digits: 1-6 is small; 7-9 medium; 0 large


        //sets up 1st unit (player 1's unit - Human)
        UnitInfo unitInfo = unitInfoArr.get(2);
        String unitPicPath = unitInfo.getTexPaths().get(0);
        Texture tex1 = am.get(unitPicPath, Texture.class);
        Unit unit1 = new Unit(tex1, posX1, posY1, unitInfo);
        unit1.setPlayer(1); //sets the player
        unitsOnBoard1.add(unit1);
        playerUnits.add(unitsOnBoard1);        //add to array containing each player's units


        //player 2 is reptoids
        unitInfo = unitInfoArr.get(23);
        unitPicPath = unitInfo.getTexPaths().get(1);
        boolean exists = Gdx.files.internal(unitPicPath).exists();
        if (!exists){
            unitInfo.getTexPaths().get(0);
        }
        Texture tex2 = am.get(unitPicPath, Texture.class);
        Unit unit2 = new Unit(tex2, posX2, posY2, unitInfo);
        unit2.setPlayer(2);
        unitsOnBoard2.add(unit2);
        playerUnits.add(unitsOnBoard2);


    }



}
