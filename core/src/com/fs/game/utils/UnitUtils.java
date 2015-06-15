/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;
import com.fs.game.units.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

// so that Actions do not need reference to Actions
// static class, static import it


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

    /** Returns unit by name & player id
     *
     * @param name : unit name
     * @param player : 1 or 2
     * @return : Unit
     */
    public static Unit getUnitByName(String name, int player){
        return player == 1 ? GameData.p1Units.get(name)
                : GameData.p1Units.get(name);
    }


    public static class Setup {

        /** returns a UnitInfo Array based on faction choice
         *
         * @param faction : faction Units belong to
         * @return
         */
        public static Array<UnitInfo> getDefaultUnits(String faction){

//            Array<UnitInfo> unitInfoArray = Assets.unitInfoMap.get(faction);

            UnitInfo[] unitInfos = Assets.unitInfoMap.get(faction).toArray(UnitInfo.class) ;
            Array<UnitInfo> chosenUnits = new Array<UnitInfo>();

            //adding small units
            for (int i = 0; i < 4; i++){
                chosenUnits.add(unitInfos[i]);
            }

            chosenUnits.add(unitInfos[5]);
            chosenUnits.add(unitInfos[6]);
            chosenUnits.add(unitInfos[8]);

            return chosenUnits;
        }



        /** Places Units on stage based on panel position
         *  Use GameData.panelMatrix[0][i] or [38-39][i] to set positions of Units
         *  Units are positioned depending on size, side and position in array
         *   indices 0-1 (small): y is 0 & 1 respectively
         *   indices 2-3 (small): y is 10 & 11 respectively
         *   indices 4-5 (medium): y is 3 & 8 respectively
         *   indices 6 (large): y is 5
         *
         * @param playerName : name of player
         * @param chosenUnits : chosen unit
         * @param player : player id; player 1 or 2
         * @return : an Array of Units
         */
        public static Array<Unit> setupUnits(Array<UnitInfo> chosenUnits, int player, String playerName){
            Array<Unit> currUnits = new Array<Unit>();

            //seperate by 2 spaces each in Y direction
            for (int i = 0; i < chosenUnits.size; i ++){

                String unitSize = chosenUnits.get(i).getSize(); //for x position

                // Get id of outer array, or where on x-axis panel is & set Unit position
                // if player 2, then is on right side, and Unit's on 2nd to last column (small units on last)
                int idx = player == 1 ? 0 : GameData.cols - 2;
                idx += unitSize.equals(Unit.SMALL) && idx > 0 ? 1 : 0; //if on right side & small unit, move by 1
                //get y id offsets for each
                int yid = Constants.SETUP_Y_OFFSETS[i]; //y coordinate index of panel - varies by unit

                float x = GameData.panelMatrix[idx][yid].getX();
                float y = GameData.panelMatrix[idx][yid].getY();

                Unit unit = new Unit(chosenUnits.get(i), x, y, player, playerName);
                currUnits.add(unit);

                if (player == 1) GameData.p1Units.put(unit.getName(), unit);
                else GameData.p2Units.put(unit.getName(), unit);
            }

//            GameData.unitsInGame.put(player, currUnits);

            return currUnits;
        }


        public static void cloneUnit(Unit unit){
            Unit clone = unit;
            GameStage stage = ((GameStage)unit.getStage());

            float x;
            float y = unit.getY();

            if (unit.player == 2){
                x = Constants.UNIT_POS_X_LEFT;
            }
            else{
                x = Constants.UNIT_POS_X_RIGHT;
            }

            if (unit.getUnitSize().equals("64x64")){
                y += 64f;
            }
            else{
                y += 32f;
            }
            Array<Unit> allUnits = GameMapUtils.findAllUnits(stage.getActors());
            positionClonedUnit(clone, x, y, new Array.ArrayIterator<Unit>(allUnits)); //set the new unit's position

            stage.addActor(clone);

        }


        /** Checks to see whether this is the multiplayer Unit of enemy
         *  and if is, sets it to be untouchable
         *
         * @param unit : unit to check against
         *
         */
//        public static void setMultiEnemy(Unit unit){
//            if (MainScreen.gameState == GameState.MULTIPLAYER || TestScreen.gameState == GameState.MULTIPLAYER &&
//                    GameData.playerName != unit.getOwner()){
//                unit.setTouchable(Touchable.disabled);
//            }
//        }

        /** recursively checks to see whether unit can fit in position
         *  by checking against other unit positions
         *
         * @param unit
         * @param x
         * @param y
         * @param iter
         */
        public static void positionClonedUnit(Unit unit, float x, float y, Array.ArrayIterator<Unit> iter){

            while (iter.hasNext()){
                Unit u = iter.next();
                if (!u.unitBox.contains(x, y) && !u.unitBox.contains(x + unit.getWidth(), y + unit.getHeight())){
                    if (GameMapUtils.isPastTop(y)){
                        y -= y;
                        positionClonedUnit(unit, x, y, iter);
                    }

                    if (GameMapUtils.isPastBottom(y)){
                        y += y;
                        positionClonedUnit(unit, x, y, iter);
                    }
                }
            }

            unit.setPosition(x, y);
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
//        public static Unit spawnUnit(int id, float actorX, float actorY, int player) {
//            Unit unit = new Unit();
//            boolean flip = false; //initialized to false, true if flipped
//
//            //loops through unit info & finds right units
//            for (UnitInfo u: Assets.unitInfoArray) {
//                if (id == u.getId()) {
//
//                   unit = new Unit(u, actorX, actorY, player);
//                }
//            }
//
//            return unit;
//        }


        /** returns units standing still framesheet based on
         * direction facting & info
         *
         * @param info
         * @param flip
         * @return
         */
        public static Texture getUnitStill(UnitInfo info, boolean flip){
            String unitPicPath = info.getTexPaths().get(0);

            //NOTE: all units have a stillLeft.png & stillRight.png file paths
            if (flip){
                unitPicPath = info.getTexPaths().get(1);
                if (Gdx.files.internal(unitPicPath).exists())
                    return Assets.assetManager.get(unitPicPath, Texture.class);
            }

            return Assets.assetManager.get(unitPicPath, Texture.class);
        }


        //-------------------Unit Animation-------------------
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

            Animation anim = new Animation(time, walkFrames); //final moveAnimation

            return anim;
        }

        /** Sets up Unit animations
         *
         * @param unit : unit object whose animations are being set
         * @param aniTime : time b/w each frame in animation
         * @return : an Array of Animation objects (each depicts movement/attacks in various directions)
         */
        public static Array<Animation> setupAnimations(Unit unit, float aniTime){
            Array<Animation> animations = new Array<Animation>(9);

            UnitInfo unitInfo = unit.unitInfo;
            float width = unit.getWidth();
            float height = unit.getHeight();

            // The still animation (from main unit texture)
            // Since player 1 still faces right & player 2 left, subtract by 1 to get still_left or right
            Texture stillTex = Assets.assetManager.get(unitInfo.getTexPaths().get(unit.getPlayer()-1), Texture.class);
            animations.insert(0, createAnimation(aniTime, stillTex, width, height));

            //animations.insert(0, UnitUtils.Setup.createAnimation(aniTime, unit.texture, width, height));
            //gets textures from assetmanager
            for(String fs : unitInfo.getTexPaths()){

                Texture tex = Assets.assetManager.get(fs, Texture.class);

                if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_RIGHT)){
                    animations.insert(1, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_LEFT)){
                    animations.insert(2, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_UP)){
                    animations.insert(3, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_DOWN)){
                    animations.insert(4, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath()+ Constants.UNIT_ATTACK_RIGHT)){
                    animations.insert(5, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_LEFT)){
                    animations.insert(6, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)){
                    animations.insert(7, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)) {
                    animations.insert(8, UnitUtils.Setup.createAnimation(aniTime, tex, width, height));
                }
                //more animations to come...
            }

            //since not all animations have up & down movements
            if (animations.size < 4){
                animations.insert(3, animations.get(0));
                animations.insert(4, animations.get(0));
            }

            return animations;
        }

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

    }


	
  

    /*-------------------Unit Info------------------------
     *
     * methods related to displaying unit information
     *
     */
    public static class Info {

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

        /** Returns String value relating to damage
         *  look through unit damage list & get ones
         *
         * @param uni
         * @return
         */
        public static String unitDamageList(Unit uni) {
            //
            //that relate to enemies on the current board
            Array<Unit> enemies = uni.getPlayer()== 1 ? GameData.enemyUnits : GameData.playerUnits;
            int[] damageList = uni.damageList;

            String unitDamage = "Name : Attack\n";

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



    }


    /* -------------------UNIT POSITIONING------------------------
     *
     * methods for getting unit position
     * - direction for movement & attack
     * FIXED: Unit Panel movement covered by UnitController
     *
     */
    public static class Movement {


        /** Creates move using Unit panelPath array
         *
         * @param panelPath : the Array containing path locations as Vector2 objects
         * @return a {@link SequenceAction} of panel to panel movements for Unit
         */
        public static SequenceAction createMoveAction(Array<Vector2> panelPath){
            SequenceAction moveSequence = new SequenceAction();
            for (Vector2 pos : panelPath) {
                moveSequence.addAction(moveTo(pos.x, pos.y, .8f));
            }

            moveSequence.addAction(run(new Runnable() {
                @Override
                public void run() {
                    log("MoveAction completed!");
                }
            }));

            return moveSequence;
        }



        /** sets unit direction & as a result animation
         *
         * @param uni : unit whose animstate is returned
         * @param destX :
         * @param destY :
         */
        public static AnimState unitDirection(Unit uni, float destX, float destY){
            float oriX = uni.unitBox.getX();
            float oriY = uni.unitBox.getY();

            AnimState state;

            if (isLeft(oriX, oriY, destX, destY)){
                state = AnimState.MOVE_LEFT;
            }
            else if (isRight(oriX, oriY, destX, destY)){
                state = AnimState.MOVE_RIGHT;
            }
            else if (isUp(oriX, oriY, destX, destY)){
                state = AnimState.MOVE_UP;
            }
            else if (isDown(oriX, oriY, destX, destY)){
                state = AnimState.MOVE_DOWN;
            }
            else
                state = AnimState.STILL;

            return state;
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

    }


    /** Unit attack & damage methods
     * NOTE: changed from automatic attack to explicit attack - player/ai has to click on "attackable" unit to attack
     * FIXED: methods dealing with damage changed b/c of change above
     */
    public static class Attack {


        public void findAllUnitEnemies(Array<Unit> playerUnits){
            Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(playerUnits);

            while(unitIter.hasNext()){
                Unit u = unitIter.next();

                //find enemies by unit
                ObjectMap<String, Unit> enemyMap = getEnemyMap(u);

                //attack any enemies in map
                if (enemyMap.size > 0){

                }
            }
        }

        /** When unit finished moving & becomes adjacent to another (or other). The option to attack is possible &
         * the attackable units are put into an Array.
         *
         * @param unit : unit whose turn it is
         * @return Array of Units that can be attacked
         */
        public static ObjectMap<String, Unit> getEnemyMap(Unit unit){
            ObjectMap<String, Unit> enemyMap = new ObjectMap<String, Unit>();
            Array<Unit> allUnits = GameMapUtils.findAllUnits(unit.getStage().getActors());

            Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(allUnits);

            while (unitIter.hasNext()){
                Unit u = unitIter.next();
                if (!u.isPlayerUnit() && unitAdjacent(unit, u)){
                    u.damage = getUnitDamageTo(unit, u);
                    enemyMap.put(u.getName(), u);
                }
            }

            log("Enemies around Unit: " + enemyMap.keys().toArray().toString(", "));

            return enemyMap;
        }

        /** checks whether units are adjacent for when attacking
         *
         * @param uni1 : the unit on right side
         * @param uni2 : the unit on left side
         * @return : true if a unit is adjacent to another one
         */
        public static boolean unitAdjacent(Unit uni1, Unit uni2){

            return (uni1.getX()==uni2.getX() && uni1.getY()+uni1.getHeight()==uni2.getY()) ||  	//check right & up
                    (uni1.getX() + uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY())
                    ||
                    (uni1.getX()==uni2.getX() && uni1.getY()-uni1.getHeight()==uni2.getY()) //check left & down
                    || (uni1.getX()-uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY());

        }

        /** checks if Unit is an enemy
         *
         * @param unit : Unit that is doing the checking
         * @return : true or not
         */
        public static boolean isEnemy(Unit unit){
            if (!unit.getOwner().equals(GameData.playerName)){
                return true;
            }
            return false;
        }


        /** Sets up damage lists specific to Unit relative to enemy IDs & sorts the damages with a counting sort.
         *  Also, it maps out the damages to arrays of unitIDs associated with them
         *
         * @param unit : unit damage coming from
         * @param enemies : enemy Units
         */
        public static void orderedDamageMap(Unit unit, Array<Unit> enemies){
            int unitID = unit.getUnitID();
            int[] damageArr = new int[enemies.size]; //initialize damageArr
            int[] idArr = new int[enemies.size]; //unit id array

            // The (sorted) map damageArr values are added to using keys from idArr
            IntMap<Integer> unitDamageMap = new IntMap<Integer>(enemies.size);

            int max = 0;
            for (int i = 0; i < enemies.size; i++){
                int uid = enemies.get(i).getUnitID();
                damageArr[i] = Math.abs(Assets.damageListArray.get(unitID-1)[uid-1]);
                idArr[i] = uid;
            }

            // Sort using counting sort - get histogra, then calc indices then need to order
            // and also need to put associate unit IDs with each damage

            // Create histogram of numbers in list, of size k, where k is range of damages, 0, 1 ... 4
            int[] count = new int[5]; // 5 total damages, with 4 being highest damage, 0 lowest
            for (int i = 0; i < damageArr.length; i++){
                count[damageArr[i]]++; //eg, if damage[i]=4, increment the count[4] value
            }

            //calculate the indices for damages
            for (int i = 1; i < count.length; i++){
                count[i] += count[i-1]; //is setting where 1st value in a group of same damages goes
            }

            // Ussing calculated indices, copy damageArr values into sorted array
            // Sorts in ascending order, lowest to highest
            int[] sortedDamages = new int[damageArr.length];
            int[] sortedID = new int[idArr.length];
            for (int i = damageArr.length-1; i >=0; i--){
                sortedDamages[count[damageArr[i]]--] = damageArr[i];
                sortedID[count[damageArr[i]]--] = idArr[i];
            }

            //finally, put values all in sorted map
            for (int i = 0; i < sortedDamages.length; i++){
                unitDamageMap.put(sortedID[i], sortedDamages[i]);
            }

            GameData.unitDamageMap.put(unitID, unitDamageMap);
        }


        protected static void logSortedDamageMap(){
            //StringBuilder for displaying result of ordered damages
            StringBuilder builder = new StringBuilder();
        }



        /** Gets Unit damage to another Unit
         *
         * @param attacker : attacking Unit that damages target
         * @param target : target Unit damage being dealt to
         * @return : damage
         */
        public static int getUnitDamageTo(Unit attacker, Unit target){
            return Assets.damageListArray.get(attacker.getUnitID()-1)[target.getUnitID()-1];
        }

//        /** takes in a name also, for AI use
//         *
//         * @param unit
//         * @param name
//         * @return
//         */
//        public static boolean isEnemy(Unit unit, String name){
//            if (!unit.getOwner().equals(name)){
//                return true;
//            }
//            return false;
//        }


    }


    /** Listener Utils for unit
     * some listeners which are or are not being used
     *
     *
     */
    public static class Listeners{

        /** Main input listener for Units. Whenever a Unit is touched/clicked, it becomes selected
         *  and any other Unit becomes deselected.
         *
         * @return
         */
        public static ActorGestureListener unitListener(){
            return new ActorGestureListener() {
                int clickCount = 0;

                @Override
                public void touchDown(InputEvent event, float x, float y, int pointer, int button){
                    Unit currUnit = ((Unit)event.getTarget());

                    if (clickCount < 2 && currUnit.isPlayerUnit()){

                        clickCount++;
//                    currUnit.chosen = true;
                        Gdx.app.log(LOG, currUnit.getName() + " SELECTED (touchDown - ActorGestureListener)");
                        UnitController.getInstance().selectUnit(currUnit);

                    }
                    else if (currUnit.clickCount >= 2 && currUnit.isPlayerUnit()){
//                    currUnit.chosen = false;
                        UnitController.getInstance().deselectUnit(currUnit);
                        currUnit.clickCount = 0; //reset clickCount
                    }

                }

                @Override
                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    Unit currUnit = ((Unit)event.getTarget());

                    if ( clickCount == 2 && currUnit.isPlayerUnit()) {
                        clickCount = 0; //reset clickCount
                        UnitController.getInstance().deselectUnit(currUnit);
                        Gdx.app.log(LOG, currUnit.getName() +" UNSELECTED (touchUp - ActorGestureListener)");
                    }


                }
            };
        }


        /** A ClickListener for Unit
         *
         * @return : a clicklistener for the unit
         */
        public static ClickListener unitClickListener(){
            return new ClickListener(){

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                    currUnit = ((Unit)event.getTarget());

                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Unit unit = ((Unit)event.getTarget());

                    log("Unit " + unit.getName() + " tapcount = " + getTapCount());

                    if (unit.state == UnitState.STANDING && unit.isPlayerUnit()){
//                    currUnit.chosen = true;
                        log(" Selected (touchDown - ClickListener)");
                        UnitController.getInstance().selectUnit(unit);

                    }
                    else if (unit.state == UnitState.CHOSEN && unit.isPlayerUnit()){
                        log(" UNselected (touchDown - ClickListener)");
                        UnitController.getInstance().deselectUnit(unit);
//                        cancel();
                    }

                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public boolean isOver(Actor actor, float x, float y) {
                    Unit unit = (Unit)actor;

                    //NOTE: this is not doing anything...yet
                    log("mouse is over unit " + unit.getName());


                    return true;
                }
            };
        }


        /** An ActorGestureListener for UnitImage actors (menu pics of Units)
         *
         * @return : listener for unit
         */
        public static ActorGestureListener unitImageListener(){
            return new ActorGestureListener() {

                int clickCount = 0;
                Array<String> unitDetailText;

                @Override
                public void touchDown(InputEvent event, float x, float y, int pointer, int button){
                    UnitImage currUnit = ((UnitImage)event.getTarget());
                    clickCount++;
                    unitDetailText = MenuUtils.UnitMenu.updateUnitText(currUnit.unitInfo);

                    if (clickCount < 2){
                        currUnit.selected = true;

                        Gdx.app.log(LOG, " unit selected: " + currUnit.getName() + ", KEY = " + currUnit.key);
                    }

                }

                @Override
                public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                    UnitImage currUnit = ((UnitImage)event.getTarget());

                    if (clickCount == 2) {
                        //returns a confirmation menu, when if Yes clicked, a unit imaged copied to roster
                        MenuUtils.UnitMenu.confirmUnitAdd(currUnit);

                    }
                    else if (clickCount == 3){
                        currUnit.selected = false;
                        Gdx.app.log(LOG, " unit DESELECTED: " + currUnit.getName() + ", KEY = " + currUnit.key);

                        clickCount = 0;
                    }

                }
            };
        }



    }

    private static void log(String message){
        Gdx.app.log(LOG, message);
    }

}

//OUTDATED methods
// ----------Movement -----------------
//        /** Get all possible unit movements on board
//         * uses the max moves to show player movements
//         * adds the panels into an array
//         * find relative position of panels to all possible moves
//         * highlights all possible panels on board if unit is selected
//         */
//        public static void showMoves(Unit unit) {
//            if (unit.panelArray!=null){
//                for (Panel p : unit.panelArray) {
//                    if (!p.moveableTo){
//                        p.moveableTo = true;
//                    }
//                }
//            }
//
//        }//sets the associated panels with unit max moves

//        /**
//         * hides the panels highlighted by showMoves()
//         */
//        public static void hideMoves(Unit unit){
//            if (unit.panelArray!=null){
//                for (Panel p : unit.panelArray) {
//                    p.selected = false; //in case p was selected
//                    p.moveableTo = false;
//                }
//            }
//            unit.panelsFound = false;
//        }

//        /** finds the targetPanel that the user selected
//         * 	unit to go to
//         *
//         */
//        public static void checkTargetPanel(Unit unit) {
//            for (Panel p : unit.panelArray) {
//                if ((p.selected && p.moveableTo)) {
//                    unit.setTargetPan(p);
//                    //movePath = pathGen.findBestPath(targetPan);
//
//                    unit.moving = true;
//                    unit.standing = false;
//                    unit.chosen = false;
//                    hideMoves(unit); //moves not shown now
//
//                    //sets unit to new position
//                    unit.destX = p.getX();
//                    unit.destY = p.getY();
//
//                    unit.panelPath = getMovePath(unit, p, new PathFinder());
//
//                    break;
//
//                }
//            }
//        }

//        /**
//         * sets duration based on moves actually taken
//         * TODO: tweak times when all animation put in
//         *
//         * @param maxMoves
//         * @param actualMoves
//         * @return
//         */
//        public static float setDuration(int maxMoves, int actualMoves) {
//            float duration = 4f;
//            int moves = maxMoves - actualMoves;
//
//            //based on number of moves unit takes
//            switch (moves) {
//                case 2 : duration = 2.5f; break;
//                case 3 : duration = 2f; break;
//                case 4 : duration = 1f; break;
//                case 5 : duration = .5f; break;
//                default : duration = 2f; break; //1 moves
//            }
//
//            return duration;
//        }

//        /** finds all areas unit will move to
//         *  returns in a Vecto2 array with screen coordinates
//         *
//         * @param uni
//          * @return pathMoves
//         */
//        public static Array<Vector2> getMovePath(Unit uni, Panel target, PathFinder pathFinder) throws NullPointerException{
//            Array<Vector2> gridPaths = new Array<Vector2>();
//
//            pathFinder.findBestPath(uni, target); //finds the best path
//
//            try{
//                gridPaths = pathFinder.getUnitMovePath();
//                Gdx.app.log(LOG, "paths found by PathFinder: " + gridPaths.toString(", "));
//            }catch(NullPointerException e){
//                Gdx.app.log(LOG, " no path found, unit CANNOT MOVE!");
//            }
//
//            return gridPaths;
//        }


//        /** Creates move action for Unit
//         *
//         * @param moveSequence : the sequence of moves to selected panel
//         * @param unit : the unit that is moving
//         * @return a {@link SequenceAction} of panel to panel movements for Unit
//         */
//        public static SequenceAction createMoveAction(SequenceAction moveSequence, Unit unit){
//            if (moveSequence.getActions().size != unit.panelPath.size) {
//                for (Vector2 pos : unit.panelPath) {
//                    MoveToAction moveAction = moveTo(pos.x, pos.y, 5f);
//                    moveSequence.addAction(moveAction);
//                }
//            }
//            //add a runnable action at end
//            moveSequence.addAction(run(new Runnable() {
//                @Override
//                public void run() {
//                    log("MoveAction completed!");
//                }
//            }));
//
//            return moveSequence;
//        }

//        /** Creates move action for Unit
//         *
//         * @param unit : the unit that is moving
//         * @return a {@link SequenceAction} of panel to panel movements for Unit
//         */
//        public static SequenceAction createMoveAction(Unit unit){
//            SequenceAction moveSequence = new SequenceAction();
//            if (moveSequence.getActions().size != unit.panelPath.size) {
//                for (Vector2 pos : unit.panelPath) {
//                    MoveToAction moveAction = moveTo(pos.x, pos.y, 5f);
//                    moveSequence.addAction( moveTo(pos.x, pos.y, 5f));
//                }
//            }
//
//
//            return moveSequence;
//        }

//        /** Adds a
//         *
//         * @param unit
//         * @param path
//         */
//        public static void addUnitAction(Unit unit, Array<Vector2> path){
//            switch(unit.state){
//                case MOVING:
//                    unit.addAction(createMoveAction(path));
//                    break;
//            }
//        }

//    /** adds into an array all the units with positions
//     * adds info to units: name & player number
//     *
//     * @param chosenUnits : chosen units
//     * @param player : player ID
//     * @param playerName : player name
//     * @param positions : custom positions on gridmap
//     * @return
//     */
//    public static Array<Unit> setupUnits(Array<UnitInfo> chosenUnits, int player, String playerName, int[][]
//            positions) {
//        Array<Unit> unitArray = new Array<Unit>();
//
//        int idx = player == 1 ? 0 : GameData.cols - 2;
//
//        for (int i = 0; i < chosenUnits.size; i++) {
//            String unitSize = chosenUnits.get(i).getSize(); //for x position
//            idx += unitSize == Unit.SMALL && idx > 0 ? 1 : 0; //if on right side & small unit, move by 1 to right
//
//            float x = GameData.panelMatrix[idx][i].getX();
//            float y = GameData.panelMatrix[idx][i].getY();
//            Unit unit = new Unit(chosenUnits.get(i), x, y, player);
//
//            unit.setOwner(playerName);
//            unitArray.add(unit);
//        }
//
//        return unitArray;
//    }
