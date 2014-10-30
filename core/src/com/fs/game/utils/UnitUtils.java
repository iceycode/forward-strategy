/**
 * 
 */
package com.fs.game.utils;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.unused_old_classes.GameBoard;
import com.fs.game.utils.pathfinder.PathFinder;


/** Unit Utils
 * Methods which units use in conjunction with stage:
 * CREATION/SETUP
 * - obtains player choices of units/factions
 * - generates units & positions
 * - uses an AssetManager to load/read textures
 * - generates a test situation
 * 
 * ANIMATIONS
 * - creates the unit still, move., attack animations
 * 
 * ACTIONS
 * - unit death actions
 * - unit move actions
 *
 * 
 * 
 * MOVEMENTS
 * - left, right, up, down
 * - finds movement range on board
 * - 
 * - returns the shortest path to target panel
 * 
 * @author Allen Jagoda
 *
 */
public class UnitUtils  {
	final static String LOG = Constants.LOG_UNIT_UTILS;
	
	//vars for JSON conversion
	protected Json json;
	protected FileHandle handle;
	protected String jsonPath; 
	protected String jsonAsString;
	protected static AssetManager am;
	public static Panel[][] panelMatrix;
 	
	//arrays of newly created unit-related objs
	public static Array<Unit> arrayUnits;
	public static Array<Array<Unit>> playerUnits;
	static Array<Panel> panelArr;
	public static Array<UnitInfo> arrayUnitInfo;
	public static UnitInfo uniInfo;
  
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

 
	/** createUnit method
	 * - creates units to be placed on grid
	 * - takes id for unit identification & also initial positions
	 * 
	 * @param id
	 * @param actorX
	 * @param actorY
	 * @return Unit
	 */
	public static Unit createUnit(int id, float actorX, float actorY) {
		Unit uni = new Unit();

		//loops through unit info & finds right units
		for (UnitInfo u: arrayUnitInfo) {
			String unitPicPath = u.getUnitPath().concat("still"); //get still pic of unit
 			if (id == u.getId()) {
				Texture tex = am.get(unitPicPath, Texture.class);
				uni = new Unit(tex, actorX, actorY, u);
 			}
		}
		
		return uni;
	}
	
	/** testSetup1
	 * Humans vs Reptoids
	 * 
	 */
	public static void testBoardSetup1() {
 		String faction1 = "Human";
		String faction2 = "Reptoid";
		String faction3 = "Arthroid";
		
		Array<Unit> humanUni = setUniPositions(faction1, 0, false, 1);
		Array<Unit> reptoidUni = setUniPositions(faction3, 11, true, 2);
		
		playerUnits.add(humanUni);
		playerUnits.add(reptoidUni);
		
	}
	
	/** testSetup1
	 * Humans vs Reptoids
	 * 
	 */
	public static void testBoardSetup2() {
 		String faction1 = "Human";
		String faction2 = "Reptoid";
		String faction3 = "Arthroid";
		
		Array<Unit> humanUni = setUniPositions(faction1, 0, false, 1);
		Array<Unit> reptoidUni = setUniPositions(faction3, 11, true, 2);
		
		playerUnits.add(humanUni);
		playerUnits.add(reptoidUni);
		
	}

	/** gets the units positions on stage
	 * - uses flip as way to determine whether actor needs to be rotated
	 * before being placed on board
	 * 
	 * @param faction : The String designating faction
	 * @param posY
	 * @param flip
	 * @param player
	 * 	
	 * @return
	 */
	public static Array<Unit> setUniPositions (String faction, int posX, boolean flip, int player){
		Array<Unit> unitsOnBoard = new Array<Unit>(); //array for units per faction
		Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
		
 		//counters to see how many to place on board
		int smallCount = 4;
		int medCount = 2;
		int largeCount = 1;
		
		//float posX = 208f; //original x position
		float posY = 100f; //original y position
 
 		//set unit actor positions on board
		for (int i = 0; i < unitInfoArr.size; i++) {
			UnitInfo uniInfo = unitInfoArr.get(i);
			//Gdx.app.log(LOG, " loading this units assets: " + uniInfo.getUnit());
 			
			if (uniInfo.getSize().equals("32x32") && uniInfo.getFaction().equals(faction) &&
					smallCount > 0) {
				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
				}
			 
				Texture tex = am.get(unitPicPath, Texture.class);
				if (smallCount >= 3) {
					Panel pan = panelMatrix[posX][smallCount+7];
					Unit uni = new Unit(tex, posX*32, posX*32, uniInfo);
					uni.setArrayPosition(posX, smallCount+7 ); //position within the grid (ie x3y4)
 					uni.setPlayer(player); //sets the player
					
					unitsOnBoard.add(uni);
				}//adds two units to right side of board
				else {
					//Panel pan = panelMatrix[smallCount-1][posY]; 
					Panel pan = panelMatrix[posX][smallCount-1];
					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
					uni.setArrayPosition(posX, smallCount-1);
 					uni.setPlayer(player);   
					
					unitsOnBoard.add(uni);
				}//adds other two units to left side of board
				
				smallCount--;
			}//places units on sides of board
			else if (uniInfo.getSize().equals("64x32") && uniInfo.getFaction().equals(faction) &&
					medCount > 0) {
				int adjPosX = posX;
				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
					adjPosX = posX - 1; 
				}
				
				Texture tex = am.get(unitPicPath, Texture.class);
 
				if (medCount == 2) {
					Panel pan = panelMatrix[adjPosX][medCount+6];
					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
					uni.setArrayPosition(adjPosX, medCount+6);
 					uni.setPlayer(player); 
					unitsOnBoard.add(uni);
				}
				else {
					Panel pan = panelMatrix[adjPosX][medCount+1];
					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
					uni.setArrayPosition(adjPosX, medCount+1);
					uni.setPlayer(player);
 					unitsOnBoard.add(uni);
				}
				medCount--;
			}//add medium units to right & left of small ones
			else if (uniInfo.getSize().equals("64x64") && uniInfo.getFaction().equals(faction) && 
					largeCount > 0) {
				if (flip) {
					posX -= 1; 
				}//decrease y grid pos position to compensate for size			
				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
				}
			 
				Texture tex = am.get(unitPicPath, Texture.class);				
				Panel pan = panelMatrix[posX][largeCount+4];
				Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
				uni.setArrayPosition(posX, largeCount+4);
 				uni.setPlayer(player);
				unitsOnBoard.add(uni);
				
				largeCount--;
			}//add large unit in middle of board
 
		}
		
		return unitsOnBoard; //returns an array containing 2 arrays of units
	}
	
	public static Texture getUnitStill(UnitInfo info, boolean flip){
		String unitPicPath = uniInfo.getTexPaths().get(0);
		
		//NOTE: all units have a stillLeft.png file path
		boolean exists = Gdx.files.internal(unitPicPath.concat(Constants.UNIT_STILL_RIGHT)).exists();
		
		if (flip && exists){
			unitPicPath = uniInfo.getTexPaths().get(1);
		}
	 
		
		Texture tex = am.get(unitPicPath, Texture.class);
		
		
		return tex;
	}
	

	
  
	
/*-------------------Unit Animation-------------------
 * method to animate unit
 * 
 * 
 * 
 * 
 */

	/** method for animating units
	 * - based on unit size returns info about animation
	 * - time determined based on how far unit moves
	 * 		- slower if closer, faster if further
	 * 
	 * @param time : for time split between frames
	 * @param frameSheet : Texture to be split for animation
	 * @param unitInfo : info mainly for unit size 
	 */
	public static Animation animateUnit(float time, Texture frameSheet, Unit uni) {
		TextureRegion[] walkFrames;
		int cols = (int) (frameSheet.getWidth()/uni.getWidth()); 
		int rows = (int) (frameSheet.getHeight()/uni.getHeight());
		int numTiles = rows * cols;
		
		if (uni.unitInfo.getSize().equals("32x32")) {
 			TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);   
 			
			walkFrames = new TextureRegion[numTiles]; //creates texture region
			int index = 0; 
	        for (int i = 0; i < rows; i++) {
	            for (int j = 0; j < cols; j++) {
	                walkFrames[index++] = temp[i][j];
	            }
	        }//create the walkFrames textureRegion
	        
		}//set frames for animation if small unit
		else if (uni.unitInfo.getSize().equals("64x32")) {
			TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);   
			walkFrames = new TextureRegion[numTiles]; //creates texture region
			int index = 0;
	        for (int i = 0; i < rows; i++) {
	            for (int j = 0; j < cols; j++) {
	                walkFrames[index++] = temp[i][j];
	            }
	        }//create the walkFrames textureRegion
	        
	        
		}//if size is medium, 64x32
		else {
 
			TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);   
			walkFrames = new TextureRegion[numTiles]; //creates texture region
			int index = 0;
	        for (int i = 0; i < rows; i++) {
	            for (int j = 0; j < cols; j++) {
	                walkFrames[index++] = temp[i][j];
	            }
	        }//create the walkFrames textureRegion
	        
		}//if size is large 64x64
		
        Animation anim = new Animation(time, walkFrames); //final moveAnimation
 
        return anim;
	}
	
/*-------------------Unit Info------------------------
 * 
 * methods related to displaying unit information
 * 
 * 
 */
	/** returns String value related to unit details
	 * used in both LevelScreen & UnitScreen
	 * 
	 * @param uni
	 * @return 
	 */
	public static String unitDetails(Unit uni) {
		
		String unitDetails = "Name: " + uni.unitInfo.getUnit() + 
				"\nHealth: " + uni.health + "/4"+
				"\nFaction: " + uni.unitInfo.getFaction() + 
				"\nTerrain: " + uni.unitInfo.getType() + 
				"\nAttacks:  " + uni.unitInfo.getUnitAnti()  + 
				"\nType: " +	uni.unitInfo.getType() +
				"\nCrosses:\n * water? " + uni.unitInfo.isCrossWater() +
				"\n *land obstacle? "+ uni.unitInfo.isCrossLandObst() ;
		
		return unitDetails;
	}
	
	
	/** returns String value relating to damage
	 * 
	 * @param uni
	 * @return
	 */
	public static String unitDamageList(Unit uni) {
		//look through unit damage list & get ones 
		//that relate to enemies on the current board
		Array<Unit> enemies = uni.enemyUnits;
		int[] damageList = uni.damageList;
		
		String unitDamage = "Name : Damage\n";
		
		//checks to see if units on board
		for (Unit u : enemies){
 
			for (int i = 0; i < damageList.length; i++) {
				int id = i+1; //since unit id assign start is 1
				 
				//make sure that only damage to unit enemies on board returned
				if (u.unitInfo.getId() == id) {
					String name =  u.unitInfo.getUnit(); //gets enemy name
					String damage = Integer.toString(damageList[i]); //gets damage
	 				unitDamage += name + " : " + damage + "\n";
				}
			}
			
		}
 
		return unitDamage;
	}
	
	
/* -------------------UNIT MOVEMENTS------------------------
 * 
 * methods for getting unit move paths
 * 
 * 
 * 
 * 
 */
 
	/** finds all areas unit will move to 
	 *  returns in a Vecto2 array with screen coordinates
	 * 
	 * @param panelArray
	 * @param uni
	 * @param stage
	 * @return pathMoves
	 */
	public static Array<Vector2> getMovePath(Array<Panel> panelArray, Unit uni, Panel target) throws NullPointerException{
		Array<Vector2> gridPaths = new Array<Vector2>();
		
		PathFinder pathFinder = new PathFinder(uni, target); //gets the shortest path
		
 		try{
 			gridPaths = pathFinder.getUnitMovePath();
 			Gdx.app.log(LOG, "paths found by PathFinder: " + gridPaths.toString(", "));
 		}catch(NullPointerException e){
 			Gdx.app.log(LOG, " no path found, unit CANNOT MOVE!");
 		}
 
 		
 		
		return gridPaths;
	}
	
	
	/** returns all possible moves 
	 * - gets the offset based on the max moves & unit position
	 * 
	 * @param maxmoves
	 */
	public static Array<Panel> getMoveRange(Unit uni, Panel[][] panelPos) {
 		int maxMoves = uni.getMaxMoves(); //need to get from unit (constructor adjusts for larger units)
		int gridPosX = uni.gridPosX;
		int gridPosY = uni.gridPosY;
		
		Array<Panel> panelArray = new Array<Panel>();
		int[][] offsets;
    
		//offset#, where number is max moves
		if (maxMoves == 2) {
			int[][] offsets2 = {
				{gridPosX-maxMoves, gridPosY},
		        {gridPosX+maxMoves, gridPosY},
		        {gridPosX+1, gridPosY},
		        {gridPosX+1, gridPosY+1},
		        {gridPosX+1, gridPosY-1},
		        {gridPosX-1, gridPosY},
		        {gridPosX-1, gridPosY+1},
		        {gridPosX-1, gridPosY-1},
		        {gridPosX, gridPosY+maxMoves},
		        {gridPosX, gridPosY+1},
		        {gridPosX, gridPosY-1},
		        {gridPosX, gridPosY-maxMoves}
			};
			offsets = offsets2;
			
		}
		else if (maxMoves == 3) {
			int[][] offsets3 = {
		        {gridPosX-maxMoves, gridPosY},
		        {gridPosX-1, gridPosY},
		        {gridPosX-1, gridPosY+1},
		        {gridPosX-1, gridPosY-1},
		        {gridPosX-1, gridPosY+2},
		        {gridPosX-1, gridPosY-2},
		        {gridPosX-2, gridPosY},
		        {gridPosX-2, gridPosY+1},
		        {gridPosX-2, gridPosY-1},
		        
		        {gridPosX+1, gridPosY},
		        {gridPosX+1, gridPosY+1},
		        {gridPosX+1, gridPosY-1},
		        {gridPosX+1, gridPosY+2},
		        {gridPosX+1, gridPosY-2},
		        {gridPosX+2, gridPosY},
		        {gridPosX+2, gridPosY+1},
		        {gridPosX+2, gridPosY-1},
		        
		        {gridPosX, gridPosY+maxMoves},
		        {gridPosX, gridPosY+2},
		        {gridPosX, gridPosY+1},
		        {gridPosX, gridPosY-1},
		        {gridPosX, gridPosY-2},
		        {gridPosX+maxMoves, gridPosY}

			};//gets all offsets for 3 moves
			offsets = offsets3;
		}
		else if (maxMoves == 4) {
			int[][] offsets4 = {
		        {gridPosX-maxMoves, gridPosY},
		        {gridPosX+maxMoves, gridPosY},

		        {gridPosX+1, gridPosY},
		        {gridPosX+1, gridPosY+1},
		        {gridPosX+1, gridPosY-1},
		        {gridPosX+1, gridPosY+2},
		        {gridPosX+1, gridPosY-2},
		        {gridPosX+1, gridPosY+3},
		        {gridPosX+1, gridPosY-3},
		        
		        {gridPosX+2, gridPosY},
		        {gridPosX+2, gridPosY+1},
		        {gridPosX+2, gridPosY-1},
		        {gridPosX+2, gridPosY+2},
		        {gridPosX+2, gridPosY-2},
		        
		        {gridPosX-2, gridPosY},
		        {gridPosX-2, gridPosY+1},
		        {gridPosX-2, gridPosY-1},
		        {gridPosX-2, gridPosY+2},
		        {gridPosX-2, gridPosY-2},
		        
		        {gridPosX+3, gridPosY},
		        {gridPosX+3, gridPosY+1},
		        {gridPosX+3, gridPosY-1},
		        
		        {gridPosX-3, gridPosY},
		        {gridPosX-3, gridPosY+1},
		        {gridPosX-3, gridPosY-1},
		        
		        {gridPosX-1, gridPosY},
		        {gridPosX-1, gridPosY+1},
		        {gridPosX-1, gridPosY-1},
		        {gridPosX-1, gridPosY+2},
		        {gridPosX-1, gridPosY-2},
		        {gridPosX-1, gridPosY+3},
		        {gridPosX-1, gridPosY-3},

		        {gridPosX, gridPosY+maxMoves},
		        {gridPosX, gridPosY+3},
		        {gridPosX, gridPosY+2},
		        {gridPosX, gridPosY+1},
		        {gridPosX, gridPosY-1},
		        {gridPosX, gridPosY-2},
		        {gridPosX, gridPosY-3},
		        {gridPosX, gridPosY-maxMoves}
			};//gets all offsets for 3 moves
			offsets = offsets4;
		}
		else {
			int[][] offsets1 = {
				{gridPosX-maxMoves, gridPosY},
		        {gridPosX+maxMoves, gridPosY},
		        {gridPosX, gridPosY+maxMoves},
		        {gridPosX, gridPosY-maxMoves}
			};
			offsets = offsets1;
		}
		
		//places correct panels based on offset into array
		for (int[] o : offsets) {
			//makes sure units are not out of bounds
			if ((o[0] >= 0 && o[1] >= 0) && 
					(o[0]<= 11 && o[1] <= 11)) {
				panelArray.add(panelPos[o[0]][o[1]]);
			}
		}
 
		return panelArray;
   	}
	
	/* ----------Method that checks for obstacles & other units in the way------------------
	 * TODO: Figure out what to do about the panels which are neighbors of 
	 * 
	 */
	public static Array<Panel> checkForCollisions(Unit uni, Array<Panel> panelArray){
		MapStage stage = (MapStage)uni.getStage();
		Array<Unit> allUnits = findAllUnits(stage.getActors());
		
		String w = uni.unitInfo.isCrossWater(); 
		String o = uni.unitInfo.isCrossLandObst();
		
		//Gdx.app.log(LOG, " panel coordinates:/n" );
		//check to see if there is anything blocking
 		for (Panel p : stage.getPanelArray()){
			if( ((w.equals("No") && p.terrainType.equals("water")) ||
					(o.equals("No") && p.terrainType.equals("obstacles")))){
				p.blocked = true;
				//panelArray.removeValue(p, false);
 			}
 
 			//checks for any untis overlapping panels
			for (Unit u : allUnits){
 				//check to see that another unit is not occupying space
				if (u.unitBox.overlaps(p.panelBox)){
					
					p.blocked = true;
					//p.moveableTo = false;
					//panelArray.removeValue(p, false);
				}
  			}
  		}
 		
 		return panelArray;
	}
 
	/** sets unit direction & as a result animation
	 * 
	 * @param uni
	 * @param destX
	 * @param destY
	 */
	public static void unitDirection(Unit uni, float destX, float destY){
		float oriX = uni.getOriginX();
		float oriY = uni.getOriginY();
		
		if (movingLeft(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_LEFT;
		}
		else if (movingRight(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_RIGHT;
		}
		else if (movingUp(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_UP;
		}
		else if (movingDown(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_DOWN;
		}
		else
			uni.state = UnitState.STILL;
 	}
	
	
	/** unit is moving left
	 * 
	 * @param oriX
	 * @param oriY
	 * @param destX
	 * @param destY
	 * @return
	 */
	public static boolean movingRight(float oriX, float oriY, float destX, float destY){
		return oriX < destX && oriY == destY;
	}
	
	/** unit is moving left
	 * 
	 * @param oriX
	 * @param oriY
	 * @param destX
	 * @param destY
	 * @return
	 */
	public static boolean movingLeft(float oriX, float oriY, float destX, float destY){
		return oriX > destX && oriY == destY;
	}
	
	/** unit is moving up
	 *  
	 * @param oriX
	 * @param oriY
	 * @param destX
	 * @param destY
	 * @return
	 */
	public static boolean movingUp(float oriX, float oriY, float destX, float destY){
		return oriX == destX && oriY < destY;
	}
	
	
	/** unit is moving down
	 *  
	 * @param oriX
	 * @param oriY
	 * @param destX
	 * @param destY
	 * @return
	 */
	public static boolean movingDown(float oriX, float oriY, float destX, float destY){
		return oriX == destX && oriY > destY;
	}
	
	
/* -------------------UNIT ACTIONS------------------------
 * 
 * methods for creating actions
 * 
 * 
 * 
 * 
 */
	/** creates a sequence of actions for use by the units
	 * 
	 */
	public static MoveToAction createMoveAction(Unit uni, float posX, float posY, float duration) {
		Gdx.app.log(LOG + "before completing action...", "actorX: "  + uni.getX() + "actorY: " + uni.getY());
		
		MoveToAction moveAction = Actions.moveTo(posX, posY, 1);
		
		
		moveAction.setPool(uni.actionPool);
		return moveAction;
	}
	
	/** creates a series of move sequences
	 * - uses units panel path (from pathfinder)
	 * 
	 * @param uni
	 * @param x
	 * @param y
	 * @param duration
	 * @return
	 */
	public static SequenceAction createMoveSequence(final Unit uni, float x, float y, float duration){
		
		SequenceAction seq = new SequenceAction();	//a sequence of actions
		Array<Vector2> paths = uni.panelPath; //the paths unit will move to
		
		//add a move action for every panel on unit move path
		for (Vector2 vec : paths){
			MoveToAction move = Actions.moveTo(vec.x, vec.y, 1);
 			seq.addAction(move);
		}
		
		Gdx.app.log(LOG, "action completed \n" + " name: " + uni.getName() + "actorX = " + uni.getX() + 
				"\n actorY = " + uni.getY() + "\n gridPosX = " + uni.gridPosX + ", gridPosY = " +uni.gridPosY); 
		
		//set the pool into which actions are recylced
		seq.setPool(uni.actionPool);
		
		return seq;
	}
	
	/** the attack action
	 * 
	 * @param uni1
	 * @param posX
	 * @param posY
	 * @param duration
	 * @return
	 */
	public static SequenceAction createAttackAction(Unit uni1, Unit uni2, float posX, float posY, float duration){
 
		SequenceAction unitAttack = Actions.sequence(Actions.delay(duration));
				 
		return unitAttack;
	}
	
	
	/** death action sequence for unit
	 * 
	 * @param duration
	 * @return
	 */
	public static SequenceAction unitDeathAction(Unit uni, float duration){
		SequenceAction unitDeath = Actions.sequence(Actions.fadeOut(duration), 
			Actions.delay(duration, Actions.removeActor()));
		
		//unitDeath.setPool(uni.actionPool); //<---for recycling (if unit were to come back to life)
		
		return unitDeath;
	}
	
	/** method for when units meet
	 * 
	 * @param uni1
	 * @param uni2
	 */
	public static void unitAttacks(Unit uni1, Unit uni2){
 
		
		if ((uni1.getX()==uni2.getX() && uni1.getY()+uni1.getHeight()==uni2.getY()) ||  	//check right & up
				(uni1.getX() + uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY()) 
				||
				(uni1.getX()==uni2.getX() && uni1.getY()-uni1.getHeight()==uni2.getY()) //check left & down
				|| (uni1.getX()-uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY())) 
			//TODO: figure out new attack alternatives
		{
			uni2.damage = getUnitDamage(uni1);
			uni1.damage = getUnitDamage(uni2);
			
			uni2.attacking = true;
			uni1.attacking = true;
  		}
	
 	}
	
	
	
/*-----------------Unit Info--------------------
 * 
 * 
 * 
 * 
 */
	/** returns the damage unit inflicts (or takes, if negative)
	 * 
	 * @param unit
	 * @param damageList
	 * @return
	 */
	public static float getUnitDamage(Unit unit){
		float damage = 0;
		float damageTest = -1; //for testing damage of units (don't have current damage list)
		int[] damageList = unit.unitInfo.getDamageList();
		
		for (int i = 0; i < damageList.length; i++){
			//find unit which is being fought
			if (unit.getUnitID() == i+1){
				damage = -damageList[i]; 				
 				Gdx.app.log(Constants.LOG_UNIT_UTILS, "Unit " + unit.getName() + " health is at " + unit.health);
 
 			}
			
		}
		
		return damage; //TODO: get actual damage list
	}
	
/*---------------Getting Units from Stage---------------
 * 
 * 
 * 
 * 
 * 	 
 */
	
	/** finds all units on the stage
	 * 
	 * @param stageUnits
	 * @return
	 */
	public static Array<Unit> findAllUnits(Array<Actor> actorsOnStage){
		Array<Unit> unitsOnStage = new Array<Unit>();
		
		for (Actor a : actorsOnStage) {
			if (a instanceof Unit) {
				Unit uni = (Unit)a;
				unitsOnStage.add(uni);
			}
		}
 		
		return unitsOnStage;
	}

	/** finds all units of certain player
	 * - finds all units of a certain player
	 * 
	 * @param int player
	 */
	public static Array<Unit> findPlayerUnits(int player, Array<Unit> allUnits){
		Array<Unit> playerUnits = new Array<Unit>();
 		
		for (Unit u : allUnits) {
			if (u.getPlayer() == player) {
				playerUnits.add(u);
			}
		}
		
		return playerUnits;
	}
	
	/** finds the other units on the stage
	 * 
	 * @param stageUnits
	 * @return
	 */
	public static Array<Unit> findOtherUnits(Array<Actor> stageUnits, Unit unit){
		Array<Unit> otherUnits = new Array<Unit>();
 
		for (Actor a : stageUnits) {
			if (a.getClass().equals(Unit.class)) {
				Unit uni = (Unit)a;
				otherUnits.add(uni);
			}
		}
		
		otherUnits.removeValue(unit, false); 
		
		return otherUnits;
	}
	
	
	/** finds the enemy units on board 
	 * - this needs to be reset every time units change positions
	 * 
	 */
	public static Array<Unit> findEnemyUnits(Unit unit, MapStage stage){
		Array<Unit> enemyUnits = new Array<Unit>();
		Array<Unit> otherUnits = findOtherUnits(stage.getActors(), unit);
		
		//look through other units
		// if does not equal to this player, then it is enemy
		for (Unit u : otherUnits) {
			if (u.player != unit.player) {
				enemyUnits.add(u);
			}
		}
		
		return enemyUnits;
	
	}
	
	/** checks board to see if other units are selected or around
	 * 
	 * @param uni
	 */
	public static void checkBoard(Unit uni){
		//check to see if other units are chosen, if so reset them   
		for (Unit u : UnitUtils.findOtherUnits(uni.getStage().getActors(), uni)) {
	    	if (u.chosen ) {
	    		u.setChosen(false); 
	    		u.hideMoves(); //need this as other actors not hit
		    	u.clickCount = 0;
	    	}

	    }
	}
 	
/* -----------Act On Stage----------------
 * 
 * methods used while units on stage 
 * 
 * 
 * 
 * 	
 */

	/**
	 * @param lock : boolean 
	 */
	public static void lockUnit(Unit uni, boolean lock){
		//if locked, cannot be touched
		if (lock) 
			uni.setTouchable(Touchable.disabled);
		else 
			uni.setTouchable(Touchable.enabled);
	}
	
	
	/** unselects other units
	 * - 2 units cannot be selected at once...yet
	 * 
	 * @param otherUnits
	 */
	public static void deselectUnits(Array<Unit> otherUnits){
		for (Unit u : otherUnits){
			if (u.chosen){
				u.chosen = false;
				u.hideMoves();
				u.clickCount = 0;
			}
		}
	}
	
	
	
	public static Pixmap createPixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);

		// pixmap.drawRectangle(200, 200, width, height);
		pixmap.setColor(color);
		pixmap.fill(); // fill with the color

		return pixmap;
	}

	
	
	
	
	
	public static Array<Unit> setUniPositions16x16 (String faction, float posX, boolean flip, int player){
		Array<Unit> unitsOnBoard = new Array<Unit>(); //array for units per faction
		Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
		
 		//counters to see how many to place on board
		int smallCount = 4;
		int medCount = 2;
		int largeCount = 1;
		
		float posYB = 100f; //bottom y position
		float posYT = 452f; //top y position
 
 		//set unit actor positions on board
		for (int i = 0; i < unitInfoArr.size; i++) {
			UnitInfo uniInfo = unitInfoArr.get(i);
			//Gdx.app.log(LOG, " loading this units assets: " + uniInfo.getUnit());
 			
			if (uniInfo.getSize().equals("32x32") && uniInfo.getFaction().equals(faction) &&
					smallCount > 0) {
				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
				}
			 
				Texture tex = am.get(unitPicPath, Texture.class);
				if (smallCount >= 3) {
					
					Unit uni = new Unit(tex, posX, posYT, uniInfo);
 					uni.setPlayer(player); //sets the player				
					unitsOnBoard.add(uni);
					posYT-=32;
				}//adds two units to right side of board
				else {
					//Panel pan = panelMatrix[smallCount-1][posY]; 
					Unit uni = new Unit(tex, posX, posYB, uniInfo);
 					uni.setPlayer(player);   
					unitsOnBoard.add(uni);
					posYB+=32;
				}//adds other two units to left side of board
				
				smallCount--;
			}//places units on sides of board
			else if (uniInfo.getSize().equals("64x32") && uniInfo.getFaction().equals(faction) &&
					medCount > 0) {
 				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
					posX = posX - 32; //since the unit's position is actually 1 panel left of end
				}
				
				Texture tex = am.get(unitPicPath, Texture.class);
 
				if (medCount == 2) {
					Unit uni = new Unit(tex, posX, posYT, uniInfo);
 					uni.setPlayer(player); 
					unitsOnBoard.add(uni);
				}
				else {
					Unit uni = new Unit(tex, posX, posYB, uniInfo);
					uni.setPlayer(player);
 					unitsOnBoard.add(uni);
				}
				medCount--;
			}//add medium units to right & left of small ones
			else if (uniInfo.getSize().equals("64x64") && uniInfo.getFaction().equals(faction) && 
					largeCount > 0) {
				
				String unitPicPath = uniInfo.getTexPaths().get(0);
				
				//NOTE: all units have a stillLeft.png file path
				boolean exists = Gdx.files.internal(unitPicPath).exists();
				if (flip && exists){
					unitPicPath = uniInfo.getTexPaths().get(1);
				}
			 
				posYB += 64;
				Texture tex = am.get(unitPicPath, Texture.class);				
				Unit uni = new Unit(tex, posX, posYB, uniInfo);
 				uni.setPlayer(player);
				unitsOnBoard.add(uni);
				
				largeCount--;
			}//add large unit in middle of board
 
		}
		
		return unitsOnBoard; //returns an array containing 2 arrays of units
	}
	
	
	
	
	
	
	
	
	
	
 
}
