package com.fs.game.tests;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;
import com.fs.game.utils.UnitUtils;

public class TestBoard {
	
	public static AssetManager am;
	public static Array<Unit> arrayUnits;
	public static Array<Array<Unit>> playerUnits;
	public static Array<UnitInfo> arrayUnitInfo;

	/** initializes unit creation, taking into account game board info
	 * 
	 * @param gb
	 */
	public static void initializeUnits(Array<Panel> panelsOnStage, Panel[][] gridMatrix) {
		am = GameManager.am;
		
		am.getAssetNames(); //an array containing file name info
		
 
		GameData.gamePanels = panelsOnStage;
		GameData.gridMatrix = gridMatrix;
 		UnitUtils.panelMatrix = gridMatrix;
 
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
		
		Array<Unit> humanUni = UnitUtils.setUniPositions16x16(faction1, posXBL, false, 1);
		Array<Unit> reptoidUni = UnitUtils.setUniPositions16x16(faction3, posXBR, true, 2);
		
		playerUnits.add(humanUni);
		playerUnits.add(reptoidUni);
		
	}
}
