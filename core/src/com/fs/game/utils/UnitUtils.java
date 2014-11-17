/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Constants;
import com.fs.game.assets.GameManager;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.Panel;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
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
        AssetManager am = GameManager.assetManager;

		//loops through unit info & finds right units
		for (UnitInfo u: GameManager.unitInfoArr) {
			String unitPicPath = u.getUnitPath().concat("still"); //get still pic of unit
 			if (id == u.getId()) {
				Texture tex = am.get(unitPicPath, Texture.class);
				uni = new Unit(tex, actorX, actorY, u);
 			}
		}
		
		return uni;
	}

//


    /**
     *
     * @param faction
     * @param posX
     * @param flip
     * @param player
     * @param stage
     * @return
     */
    public static Array<Unit> setUniPositions16x12(String faction, float posX, boolean flip, int player, GameStage stage){
        Array<Unit> unitsOnBoard = new Array<Unit>(); //array for units per faction
        Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
        GameData.unitsInGame = new Array<Unit>(); //initailize array of units that will be in game
        AssetManager am = GameManager.assetManager;

        //counters to see how many to place on board
        int smallCount = 4;
        int medCount = 2;
        int largeCount = 1;

        float posYB = 100f; //bottom y position
        float posYT = 452f; //top y position

        //set unit actor positions on board
        for (int i = 0; i < unitInfoArr.size; i++) {
            Unit unit;
            UnitInfo uniInfo = unitInfoArr.get(i);
            String size = uniInfo.getSize();
            String uniFaction = uniInfo.getFaction();
            //Gdx.app.log(LOG_PAUSE_MENU, " loading this units assets: " + uniInfo.getUnit());

            if (size.equals("32x32") && uniFaction.equals(faction) && smallCount > 0) {
                Texture tex = getUnitStill(uniInfo, flip);
                if (smallCount >= 3) {
                    unit = new Unit(tex, posX, posYT, uniInfo);
                    posYT-=32;
                }//adds two units to right side of board
                else {
                    //Panel pan = panelMatrix[smallCount-1][posY];
                    unit = new Unit(tex, posX, posYB, uniInfo);
                    posYB+=32;
                }//adds other two units to left side of board
                //unitsOnBoard.add(unit);
                unitsOnBoard.add(unit);
                unit.setPlayer(player);
                stage.addActor(unit);
                smallCount--;
            }//places units on sides of board
            else if (size.equals("64x32") && uniFaction.equals(faction) && medCount > 0) {
                if (flip && medCount==2)
                    posX -=32;

                Texture tex = getUnitStill(uniInfo, flip);

                if (medCount == 2) {
                    unit = new Unit(tex, posX, posYT, uniInfo);
                }
                else {
                    unit = new Unit(tex, posX, posYB, uniInfo);
                }
                unit.setPlayer(player);
                unitsOnBoard.add(unit);
                stage.addActor(unit);
                medCount--;
            }//add medium units to right & left of small ones
            else if (size.equals("64x64") && uniFaction.equals(faction) && largeCount > 0) {
                posYB += 64;
                Texture tex = getUnitStill(uniInfo, flip);
                unit = new Unit(tex, posX, posYB, uniInfo);
                unit.setPlayer(player);
                unitsOnBoard.add(unit);
                stage.addActor(unit);
                largeCount--;
            }//add large unit in middle of board

        }

        return unitsOnBoard; //returns an array with units on board
    }



    public static Texture getUnitStill(UnitInfo info, boolean flip){
		String unitPicPath = info.getTexPaths().get(0);
        AssetManager am = GameManager.assetManager;

		//NOTE: all units have a stillLeft.png file path
		if (flip && Gdx.files.internal(unitPicPath).exists()){
			unitPicPath = info.getTexPaths().get(1);
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
	 * @param width : determines columns to be used
     * @param height : determines the height
	 */
	public static Animation createAnimation(float time, Texture frameSheet, float width, float height) {
		TextureRegion[] walkFrames;
		int cols = (int) (frameSheet.getWidth()/width);
		int rows = (int) (frameSheet.getHeight()/height);
		int numTiles = rows * cols;
        TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);

        walkFrames = new TextureRegion[numTiles]; //creates texture region
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                walkFrames[index++] = temp[i][j];
            }
        }//create the walkFrames textureRegion
//		if (uni.unitInfo.getSize().equals("32x32")) {
//
//
//		}//set frames for animation if small unit
//		else if (uni.unitInfo.getSize().equals("64x32")) {
//			TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);
//			walkFrames = new TextureRegion[numTiles]; //creates texture region
//			int index = 0;
//	        for (int i = 0; i < rows; i++) {
//	            for (int j = 0; j < cols; j++) {
//	                walkFrames[index++] = temp[i][j];
//	            }
//	        }//create the walkFrames textureRegion
//
//
//		}//if size is medium, 64x32
//		else {
//
//			TextureRegion[][] temp = TextureRegion.split(frameSheet, frameSheet.getWidth()/cols, frameSheet.getHeight()/rows);
//			walkFrames = new TextureRegion[numTiles]; //creates texture region
//			int index = 0;
//	        for (int i = 0; i < rows; i++) {
//	            for (int j = 0; j < cols; j++) {
//	                walkFrames[index++] = temp[i][j];
//	            }
//	        }//create the walkFrames textureRegion
//
//		}//if size is large 64x64
		
        Animation anim = new Animation(time, walkFrames); //final moveAnimation
 
        return anim;
	}
	
/*-------------------Unit Info------------------------
 * 
 * methods related to displaying unit information
 * 
 * 
 */

    /** since size is always ##x##, index of x is always 2
     *
     * @param size
     * @return
     */
    public static float[] convertStringSizeToFloat(String size){
        float posX = (float) Integer.parseInt(size.substring(0, 2));
        float posY = (float) Integer.parseInt(size.substring(3));
        float dimensions[] = {posX, posY}; //will be returning x & y value

        return dimensions;
    }

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
					String damage = Integer.toString(Math.abs(damageList[i])); //gets damage
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
	 * @param uni
 	 * @return pathMoves
	 */
	public static Array<Vector2> getMovePath(Unit uni, Panel target) throws NullPointerException{
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


 
	/** sets unit direction & as a result animation
	 * 
	 * @param uni
	 * @param destX destination of target
	 * @param destY
	 */
	public static void unitDirection(Unit uni, float destX, float destY){
		float oriX = uni.unitBox.getX();
		float oriY = uni.unitBox.getY();
		
		if (isLeft(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_LEFT;
		}
		else if (isRight(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_RIGHT;
		}
		else if (isUp(oriX, oriY, destX, destY)){
			uni.state = UnitState.MOVE_UP;
		}
		else if (isDown(oriX, oriY, destX, destY)){
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
	public static boolean isRight(float oriX, float oriY, float destX, float destY){
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
	public static boolean isLeft(float oriX, float oriY, float destX, float destY){
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
	public static boolean isUp(float oriX, float oriY, float destX, float destY){
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
	public static boolean isDown(float oriX, float oriY, float destX, float destY){
		return oriX == destX && oriY > destY;
	}
	

/*-----------------Unit DAMAGE Info--------------------
 * 
 * 
 * 
 * 
 */
	/** returns the damage unit inflicts (or takes, if negative)
	 * 
	 * @param unit
 	 * @return
	 */
	public static float getUnitDamage(Unit unit){
		float damage = 0;
		//float damageTest = -1; //for testing attack mechanics of units
		int[] damageList = unit.unitInfo.getDamageList();

        for (int i = 0; i < damageList.length; i++) {
            //find unit which is being fought
            if (unit.getUnitID() == i + 1) {
                damage = damageList[i];
                Gdx.app.log(LOG, "damage is " + damage);
                break;
            }

        }

		return damage;
	}
	
/*---------------Getting Units from Stage---------------
 * 
 * 
 * 
 * 
 * 	 
 */

	
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
	
	

 	
/* -----------Act On Stage----------------
 * 
 * methods used while units on stage 
 * 
 * 
 * 
 * 	
 */

//	/**
//	 * @param lock : boolean
//	 */
//	public static void lockUnit(Unit uni, boolean lock){
//		//if locked, cannot be touched
//		if (lock)
//			uni.setTouchable(Touchable.disabled);
//		else
//			uni.setTouchable(Touchable.enabled);
//	}

	
	/** unselects other units
	 * - 2 units cannot be selected at once...yet
	 * 
	 * @param otherUnits
	 */
	public static void deselectUnits(Array<Unit> otherUnits){
		for (Unit u : otherUnits){
			if (u.chosen ){
				u.chosen = false;
				u.hideMoves();
				u.clickCount = 0;
			}
		}
	}


    public static Label createDamageLabel(Unit unit, String damage){

        float x = unit.getX()+unit.getWidth()-10;
        float y = unit.getY()+unit.getHeight()-10;
        Label.LabelStyle styleDamage = new Label.LabelStyle();
        styleDamage.background = GameManager.gameSkin.getDrawable("dmgTex");
        styleDamage.font = GameManager.gameSkin.getFont("damageFont");
        styleDamage.fontColor = Color.RED;
        Label label = new Label(damage, styleDamage);
        label.setX(x);
        label.setY(y);

        return label;
    }

    /** checks whether units are adjacent
     *
     * @param uni1 : the unit on right side
     * @param uni2 : the unit on left side
     * @return
     */
	public static boolean unitAdjacent(Unit uni1, Unit uni2){
//        return uni1.unitBox.getX() == uni2.unitBox.getX() + uni2.unitBox.getWidth() &&
//                uni1.unitBox.getX() + uni1.unitBox.getWidth() == uni2.unitBox.getX() &&
//                uni1.unitBox.getY() == uni2.unitBox.getY() + uni2.unitBox.getHeight() &&
//                uni1.unitBox.getY() + uni1.unitBox.getHeight() == uni2.unitBox.getY();

        return (uni1.getX()==uni2.getX() && uni1.getY()+uni1.getHeight()==uni2.getY()) ||  	//check right & up
                (uni1.getX() + uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY())
                ||
                (uni1.getX()==uni2.getX() && uni1.getY()-uni1.getHeight()==uni2.getY()) //check left & down
                || (uni1.getX()-uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY());

    }


}
/** gets the units positions on stage
 //	 * - uses flip as way to determine whether actor needs to be rotated
 //	 * before being placed on board
 //	 *
 //	 * @param faction : The String designating faction
 //	 * @param posX
 //	 * @param flip
 //	 * @param player
 //	 *
 //	 * @return
 //	 */
//	public static Array<Unit> setUniPositions (String faction, int posX, boolean flip, int player){
//		Array<Unit> unitsOnBoard = new Array<Unit>(); //array for units per faction
//		Array<UnitInfo> unitInfoArr = GameManager.unitInfoArr;
//        am = GameManager.assetManager;
//
// 		//counters to see how many to place on board
//		int smallCount = 4;
//		int medCount = 2;
//		int largeCount = 1;
//
//		//float posX = 208f; //original x position
//		float posY = 100f; //original y position
//
// 		//set unit actor positions on board
//		for (int i = 0; i < unitInfoArr.size; i++) {
//			UnitInfo uniInfo = unitInfoArr.get(i);
//			//Gdx.app.log(LOG_PAUSE_MENU, " loading this units assets: " + uniInfo.getUnit());
//
//			if (uniInfo.getSize().equals("32x32") && uniInfo.getFaction().equals(faction) &&
//					smallCount > 0) {
//				String unitPicPath = uniInfo.getTexPaths().get(0);
//
//				//NOTE: all units have a stillLeft.png file path
//				boolean exists = Gdx.files.internal(unitPicPath).exists();
//
//				if (flip && exists){
//					unitPicPath = uniInfo.getTexPaths().get(1);
//				}
//
//				Texture tex = am.get(unitPicPath, Texture.class);
//				if (smallCount >= 3) {
//					Panel pan = panelMatrix[posX][smallCount+7];
//					Unit uni = new Unit(tex, posX*32, posX*32, uniInfo);
//					uni.setArrayPosition(posX, smallCount+7 ); //position within the grid (ie x3y4)
// 					uni.setPlayer(player); //sets the player
//
//					unitsOnBoard.add(uni);
//				}//adds two units to right side of board
//				else {
//					//Panel pan = panelMatrix[smallCount-1][posY];
//					Panel pan = panelMatrix[posX][smallCount-1];
//					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
//					uni.setArrayPosition(posX, smallCount-1);
// 					uni.setPlayer(player);
//
//					unitsOnBoard.add(uni);
//				}//adds other two units to left side of board
//
//				smallCount--;
//			}//places units on sides of board
//			else if (uniInfo.getSize().equals("64x32") && uniInfo.getFaction().equals(faction) &&
//					medCount > 0) {
//				int adjPosX = posX;
//				String unitPicPath = uniInfo.getTexPaths().get(0);
//
//				//NOTE: all units have a stillLeft.png file path
//				boolean exists = Gdx.files.internal(unitPicPath).exists();
//
//				if (flip && exists){
//					unitPicPath = uniInfo.getTexPaths().get(1);
//					adjPosX = posX - 1;
//				}
//
//				Texture tex = am.get(unitPicPath, Texture.class);
//
//				if (medCount == 2) {
//					Panel pan = panelMatrix[adjPosX][medCount+6];
//					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
//					uni.setArrayPosition(adjPosX, medCount+6);
// 					uni.setPlayer(player);
//					unitsOnBoard.add(uni);
//				}
//				else {
//					Panel pan = panelMatrix[adjPosX][medCount+1];
//					Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
//					uni.setArrayPosition(adjPosX, medCount+1);
//					uni.setPlayer(player);
// 					unitsOnBoard.add(uni);
//				}
//				medCount--;
//			}//add medium units to right & left of small ones
//			else if (uniInfo.getSize().equals("64x64") && uniInfo.getFaction().equals(faction) &&
//					largeCount > 0) {
//				if (flip) {
//					posX -= 1;
//				}//decrease y grid pos position to compensate for size
//				String unitPicPath = uniInfo.getTexPaths().get(0);
//
//				//NOTE: all units have a stillLeft.png file path
//				boolean exists = Gdx.files.internal(unitPicPath).exists();
//
//				if (flip && exists){
//					unitPicPath = uniInfo.getTexPaths().get(1);
//				}
//
//				Texture tex = am.get(unitPicPath, Texture.class);
//				Panel pan = panelMatrix[posX][largeCount+4];
//				Unit uni = new Unit(tex, pan.getX(), pan.getY(), uniInfo);
//				uni.setArrayPosition(posX, largeCount+4);
// 				uni.setPlayer(player);
//				unitsOnBoard.add(uni);
//
//				largeCount--;
//			}//add large unit in middle of board
//
//		}
//
//		return unitsOnBoard; //returns an array containing 2 arrays of units
//	}
