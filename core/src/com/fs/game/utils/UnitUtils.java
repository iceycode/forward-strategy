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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.fs.game.actors.*;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.map.Panel;
import com.fs.game.stages.GameStage;

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

    public static int getUnitIndex(Unit unit){
        int index;
        if (unit.getFaction().equals("Arthroid")){
            index = unit.getUnitID() - 21;

        }
        else if (unit.getFaction().equals("Reptoid")){
            index = unit.getUnitID() - 11;
        }
        else{
            index = unit.getUnitID() -1;
        }

        return index;
    }


    public static class Setup {

        /** returns a UnitInfo Array based on faction choice
         *
         * @param faction : faction Units belong to
         * @return
         */
        public static Array<UnitInfo> getDefaultUnits(String faction){
            Array<UnitInfo> unitInfoArray = Assets.unitInfoMap.get(faction);
            Array<UnitInfo> chosenUnits = new Array<UnitInfo>();

            //adding small units
            for (int i = 0; i < 4; i++){
                chosenUnits.add(unitInfoArray.get(i));
            }

            chosenUnits.add(unitInfoArray.get(5));
            chosenUnits.add(unitInfoArray.get(6));
            chosenUnits.add(unitInfoArray.get(8));

            return chosenUnits;
        }

        /** adds into an array all the units with positions
         * adds info to units: name & player number
         *
         * @param chosenUnits
         * @param player
         * @param playerName
         * @param positions
         * @return
         */
        public static Array<Unit> setupUnits(Array<UnitInfo> chosenUnits, int player, String playerName, float[][] positions) {
            Array<Unit> unitArray = new Array<Unit>();

            for (int i = 0; i < chosenUnits.size; i++) {
                float x = positions[i][0];
                float y = positions[i][1];
                Unit unit = new Unit(chosenUnits.get(i), x, y, player);

                if (!GameData.getInstance().playerName.equals(playerName))
                    unit.setTouchable(Touchable.disabled);

                unit.setOwner(playerName);
                unitArray.add(unit);

                if (player == 2)
                    unit.setLock(true);
            }

            return unitArray;
        }


        /** Places Units on stage based on panel position
         *
         * @param playerName : name of player
         * @param chosenUnits : chosen unit
         * @param player : player id; player 1 or 2
         * @param panels : panels representing spaces Units move to
         * @return : an Array of Units
         */
        public static Array<Unit> setupUnits(Array<UnitInfo> chosenUnits, int player, String playerName, Panel[] panels){
            Array<Unit> currUnits = new Array<Unit>();

            for (int i = 0; i < chosenUnits.size; i++){
                float x = panels[i].getX();
                float y = panels[i].getY();
                Unit unit = new Unit(chosenUnits.get(i), x, y, player);
                unit.setOwner(playerName);

//                UnitController.getInstance().initUnitInMap(unit);

                currUnits.add(unit);
            }

            if (player == 1) {
                GameData.unitsInGame.put(1, currUnits);
            }
            else{
                GameData.unitsInGame.put(2, currUnits);
            }

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
        public static Unit spawnUnit(int id, float actorX, float actorY, int player) {
            Unit unit = new Unit();
            boolean flip = false; //initialized to false, true if flipped

            //loops through unit info & finds right units
            for (UnitInfo u: Assets.unitInfoArray) {
                if (id == u.getId()) {

                   unit = new Unit(u, actorX, actorY, player);
                }
            }

            return unit;
        }


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

            //the still animation (from main unit texture)
            //since player 1 still faces right & player 2 left, subtract by 1 to get still_left or right
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

        /** gets unit names from unit info array & returns as string array
        *
        * @param unitInfoArray
        * @return
        */
        public static Array<String> getUnitNames(Array<UnitInfo> unitInfoArray){
            Array<String> unitNames = new Array<String>();
            for (UnitInfo unitInfo : unitInfoArray){
                unitNames.add(unitInfo.getUnit());
            }

            return unitNames;
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

        /**
         * sets duration based on moves actually taken
         * TODO: tweak times when all animation put in
         *
         * @param maxMoves
         * @param actualMoves
         * @return
         */
        public static float setDuration(int maxMoves, int actualMoves) {
            float duration = 4f;
            int moves = maxMoves - actualMoves;

            //based on number of moves unit takes
            switch (moves) {
                case 2 : duration = 2.5f; break;
                case 3 : duration = 2f; break;
                case 4 : duration = 1f; break;
                case 5 : duration = .5f; break;
                default : duration = 2f; break; //1 moves
            }

            return duration;
        }

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


        /** Creates move action for Unit
         *
         * @param moveSequence : the sequence of moves to selected panel
         * @param unit : the unit that is moving
         * @return a {@link SequenceAction} of panel to panel movements for Unit
         */
        public static SequenceAction createMoveAction(SequenceAction moveSequence, Unit unit){
            if (moveSequence.getActions().size != unit.panelPath.size) {
                for (Vector2 pos : unit.panelPath) {
                    MoveToAction moveAction = moveTo(pos.x, pos.y, 5f);
                    moveSequence.addAction(moveAction);
                }
            }
            //add a runnable action at end
            moveSequence.addAction(run(new Runnable() {
                @Override
                public void run() {
                    log("MoveAction completed!");
                }
            }));

            return moveSequence;
        }

        /** Creates move action for Unit
         *
         * @param unit : the unit that is moving
         * @return a {@link SequenceAction} of panel to panel movements for Unit
         */
        public static SequenceAction createMoveAction(Unit unit){
            SequenceAction moveSequence = new SequenceAction();
            if (moveSequence.getActions().size != unit.panelPath.size) {
                for (Vector2 pos : unit.panelPath) {
                    MoveToAction moveAction = moveTo(pos.x, pos.y, 5f);
                    moveSequence.addAction( moveTo(pos.x, pos.y, 5f));
                }
            }


            return moveSequence;
        }

        /** Creates move using Unit panelPath array
         *
         * @param panelPath : the Array containing path locations as Vector2 objects
         * @return a {@link SequenceAction} of panel to panel movements for Unit
         */
        public static SequenceAction createMoveAction(Array<Vector2> panelPath){
            SequenceAction moveSequence = new SequenceAction();
            for (Vector2 pos : panelPath) {
                moveSequence.addAction(moveTo(pos.x, pos.y, 1.2f));
            }

            moveSequence.addAction(run(new Runnable() {
                @Override
                public void run() {
                    log("MoveAction completed!");
                }
            }));

            return moveSequence;
        }

        public static void addUnitAction(Unit unit, Array<Vector2> path){
            switch(unit.state){
                case MOVING:
                    unit.addAction(createMoveAction(path));
                    break;
            }
        }


        /** sets unit direction & as a result animation
         *
         * @param uni
         * @param destX destination of target
         * @param destY
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

        /** when unit finished moving & becomes adjacent to another (or other)
         *  the option to attack is possible & the attackable unts
         *  are put into an Array.
         *
         * @param unit : unit whose turn it is
         * @return Array of Units that can be attacked
         */
//        public static Array<Unit> enemiesInRange(Unit unit, GameStage stage){
//            Array<Unit> allUnits = GameMapUtils.findAllUnits(stage.getActors());
//            Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(allUnits);
//
//            Array<Unit> unitsInRange = new Array<Unit>(); //units that can be attacked
//
//            log("unit " + unit.getName() + " trying to find nearby units to attack");
//
//            while (unitIter.hasNext()){
//                Unit u = unitIter.next();
//                if (UnitUtils.Attack.isEnemy(u) && UnitUtils.Attack.unitAdjacent(unit, u) && !u.underattack){
//
//                    int damage = Assets.damageListArray.get(unit.getUnitID()-1)[u.getUnitID()-1];
//                    u.isAttackable = true;
//                    unitsInRange.add(u);
//                    u.damage = damage;
//                }
//            }
//
//            return unitsInRange;
//        }


        public static Unit findBestEnemy(Unit unit){
            GameStage stage = ((GameStage) unit.getStage()); //get stage
            Unit enemy = null; //set as new empty Unit for now
            int hv = 0; //highest damage that can be done

            Array<Unit> allUnits = GameMapUtils.findAllUnits(stage.getActors());
            Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(allUnits);

            log("unit " + unit.getName() + " trying to find nearby units to attack");

            while (unitIter.hasNext()){
                Unit u = unitIter.next();
                if (UnitUtils.Attack.isEnemy(u) && UnitUtils.Attack.unitAdjacent(unit, u) && !u.underattack){

                    int damage = Assets.damageListArray.get(unit.getUnitID()-1)[u.getUnitID()-1];
                    if (hv < damage){
                        hv = damage;
                        enemy = u;
                        u.damage = damage;
                    }

                }
            }

            return enemy; //returns enemy that Unit deals highest damage to
        }


        /** checks whether panel is next to unit
         *  for movement of AI
         *
         * @param panel : the panel
         * @param  unit: the unit
         * @return
         */
        public static boolean panelAdjacent(Panel panel, Unit unit){

            return (unit.getX()==panel.getX() && unit.getY()+unit.getHeight()==panel.getY()) ||  	//check right & up
                    (unit.getX() + unit.getWidth()==panel.getX() && unit.getY()==panel.getY())
                    ||
                    (unit.getX()==panel.getX() && unit.getY()-unit.getHeight()==panel.getY()) //check left & down
                    || (unit.getX()-unit.getWidth()==panel.getX() && unit.getY()==panel.getY());

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
            if (!unit.getOwner().equals(GameData.getInstance().playerName)){
                return true;
            }
            return false;
        }

        /** takes in a name also, for AI use
         *
         * @param unit
         * @param name
         * @return
         */
        public static boolean isEnemy(Unit unit, String name){
            if (!unit.getOwner().equals(name)){
                return true;
            }
            return false;
        }


    }


    /** Listener Utils for unit
     * some listeners which are or are not being used
     *
     *
     */
    public static class Listeners{

        public static ActorGestureListener actorGestureListener = new ActorGestureListener() {

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button){
                Unit currUnit = ((Unit)event.getTarget());

                if (currUnit.clickCount < 2 && !currUnit.lock){

                    currUnit.clickCount++;
//                    currUnit.chosen = true;
                    Gdx.app.log(LOG, currUnit.getName() + " SELECTED (touchDown - ActorGestureListener)");
                    UnitController.getInstance().selectUnit(currUnit);

                }
                else if (currUnit.clickCount >= 2 && !currUnit.lock){
//                    currUnit.chosen = false;
                    UnitController.getInstance().deselectUnit();
                    currUnit.clickCount = 0; //reset clickCount
                }

            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Unit currUnit = ((Unit)event.getTarget());

                if (currUnit.clickCount == 2 && !currUnit.lock) {
//                    currUnit.chosen = false;
                    currUnit.clickCount = 0; //reset clickCount
                    UnitController.getInstance().deselectUnit();
                    Gdx.app.log(LOG, currUnit.getName() +" UNSELECTED (touchUp - ActorGestureListener)");
                }


            }
        };


        public static ChangeListener unitChangeListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Unit unit = ((Unit)actor);


            }
        };



        public static ActorGestureListener unitImageActorListener = new ActorGestureListener() {

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

    private static void log(String message){
        Gdx.app.log(LOG, message);
    }

}