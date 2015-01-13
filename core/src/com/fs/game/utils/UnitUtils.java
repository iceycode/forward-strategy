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
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.actors.Panel;
import com.fs.game.screens.MultiplayerScreen;
import com.fs.game.stages.GameStage;
import com.fs.game.actors.Unit;
import com.fs.game.actors.UnitImage;
import com.fs.game.actors.UnitInfo;
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
         * @param faction
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



        public static Array<Unit> setupUnits(String playerName, Array<UnitInfo> chosenUnits, int player, float[][] positions,  GameStage stage){
            Array<Unit> currUnits = new Array<Unit>();

            for (int i = 0; i < chosenUnits.size; i++){
                float x = positions[i][0];
                float y = positions[i][1];
                Unit unit = new Unit(chosenUnits.get(i), x, y, player);
                unit.setOwner(playerName);
                stage.addActor(unit);
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
            Array<Unit> allUnits = GameUtils.StageUtils.findAllUnits(stage.getActors());
            Array.ArrayIterator<Unit> iter = new Array.ArrayIterator<Unit>(allUnits);
            positionClonedUnit(clone, x, y, iter);

            stage.addActor(clone);

        }

        /** recursively checks to see whether unit can fit in position
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
                    if (GameUtils.StageUtils.isPastTop(y)){
                        y -= y;
                        positionClonedUnit(unit, x, y, iter);
                    }

                    if (GameUtils.StageUtils.isPastBottom(y)){
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
     *
     *
     */
    public static class Movement {

        /** Get all possible unit movements on board
         * uses the max moves to show player movements
         * adds the panels into an array
         * find relative position of panels to all possible moves
         * highlights all possible panels on board if unit is selected
         */
        public static void showMoves(Unit unit) {
            if (unit.panelArray!=null){
                for (Panel p : unit.panelArray) {
                    if (!p.moveableTo){
                        p.moveableTo = true;
                    }
                }
            }

        }//sets the associated panels with unit max moves

        /**
         * hides the panels highlighted by showMoves()
         */
        public static void hideMoves(Unit unit){
            if (unit.panelArray!=null){
                for (Panel p : unit.panelArray) {
//                    if ((p.moveableTo || p.selected) ) {
//
//                    }
                    p.selected = false; //in case p was selected
                    p.moveableTo = false;
                }
            }
            unit.panelsFound = false;
        }

        /** finds the targetPanel that the user selected
         * 	unit to go to
         *
         */
        public static void checkTargetPanel(Unit unit) {
            for (Panel p : unit.panelArray) {
                if ((p.selected && p.moveableTo)) {
                    unit.setTargetPan(p);
                    //movePath = pathGen.findBestPath(targetPan);

                    unit.moving = true;
                    unit.standing = false;
                    unit.chosen = false;
                    hideMoves(unit); //moves not shown now

                    //sets unit to new position
                    unit.destX = p.getX();
                    unit.destY = p.getY();

                    unit.panelPath = getMovePath(unit, p);

                    break;

                }

            }
        }

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


        /**
         *
         * @param moveSequence
         * @param unit
         */
        public static SequenceAction createMoveAction(SequenceAction moveSequence, Unit unit){
            if (moveSequence.getActions().size != unit.panelPath.size) {
                for (Vector2 pos : unit.panelPath) {
                    MoveToAction moveAction = Actions.moveTo(pos.x, pos.y, 5f);
                    moveSequence.addAction(moveAction);
                }
            }
            return moveSequence;
        }



        /** sets unit direction & as a result animation
         *
         * @param uni
         * @param destX destination of target
         * @param destY
         */
        public static UnitState unitDirection(Unit uni, float destX, float destY){
            float oriX = uni.unitBox.getX();
            float oriY = uni.unitBox.getY();

            UnitState state;

            if (isLeft(oriX, oriY, destX, destY)){
                state = UnitState.MOVE_LEFT;
            }
            else if (isRight(oriX, oriY, destX, destY)){
                state = UnitState.MOVE_RIGHT;
            }
            else if (isUp(oriX, oriY, destX, destY)){
                state = UnitState.MOVE_UP;
            }
            else if (isDown(oriX, oriY, destX, destY)){
                state = UnitState.MOVE_DOWN;
            }
            else
                state = UnitState.STILL;

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
     *
     */
    public static class Attack {

        /** when unit finished moving & becomes adjacent to another
         *
         * @param unit
         * @param stage
         */
        public static void attackNearbyUnits(Unit unit, GameStage stage){
            Array<Unit> allUnits = GameUtils.StageUtils.findAllUnits(stage.getActors());
            Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(allUnits);
//        OrderedMap<Integer, Unit> unitsInRange = new OrderedMap<Integer, Unit>();
//
//            int highDamage = 1; //the highest damage dealt; set at 1 since they are negative
//            Unit unitToAttack = unitIter.next(); //unit that will be attacked

            log("unit " + unit.getName() + " trying to find nearby units to attack");

            while (unitIter.hasNext() && !unit.isAttacking){
                Unit u = unitIter.next();
                if (UnitUtils.Attack.isEnemy(u) && UnitUtils.Attack.unitAdjacent(unit, u) && !u.underattack){
                    int damage = Assets.damageListArray.get(unit.getUnitID()-1)[u.getUnitID()-1];
                    u.isAttackable = true;
                    u.damage = damage;
                }

                if (!unitIter.hasNext())
                    unit.isAttacking = true;
            }
        }

        /** another (possibly outdated) unit attack method
         *
         * @param unit
         */
        public static void unitAttacked(Unit unit){
            Array<Unit> allUnits = GameUtils.StageUtils.findAllUnits(unit.getStage().getActors());
            Array.ArrayIterator<Unit> iterator = new Array.ArrayIterator<Unit>(allUnits);

            while (iterator.hasNext()){
                Unit u = iterator.next();
                if (isEnemy(u) && unitAdjacent(unit, u)){
                    unit.damage = Assets.damageListArray.get(u.getUnitID()-1)[unit.getUnitID()-1];
                    unit.health += unit.damage;
                    unit.underattack = true;

                    if (unit.health <= 0){
                        MultiplayerScreen.getInstance().playerScore += 10;
                        MultiplayerScreen.getInstance().unitCounts[unit.getPlayer()-1]--;
                    }
                }
                else{
                    unit.underattack = false;
                }

            }
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
         * @return
         */
        public static boolean unitAdjacent(Unit uni1, Unit uni2){

            return (uni1.getX()==uni2.getX() && uni1.getY()+uni1.getHeight()==uni2.getY()) ||  	//check right & up
                    (uni1.getX() + uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY())
                    ||
                    (uni1.getX()==uni2.getX() && uni1.getY()-uni1.getHeight()==uni2.getY()) //check left & down
                    || (uni1.getX()-uni1.getWidth()==uni2.getX() && uni1.getY()==uni2.getY());

        }

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

        /** unit is dealt damage
         *
         * @param damage
         * @param u
         */
        public static void damageUnit(int damage, Unit u){
            u.unitDamaged(damage);
            u.underattack = true;

            if (u.health <= 0){
                MultiplayerScreen.getInstance().playerScore += 10;
                MultiplayerScreen.getInstance().unitCounts[u.getPlayer()-1]--;
            }

            Gdx.app.log(LOG, "unit " + u.getName() + " damage inflicted = " + damage
                    + " \n health = " + u.health);

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

                if (currUnit.clickCount < 2 && !currUnit.lock && !currUnit.done){
                    if (currUnit.otherUnits!=null)
                        GameUtils.StageUtils.deselectUnits(currUnit.otherUnits);

                    currUnit.clickCount++;
                    currUnit.chosen = true;
                    ((GameStage)currUnit.getStage()).setChosenUnit(currUnit);
                    Gdx.app.log(LOG, currUnit.getName() + " selected (touchDown - ActorGestureListener)");
                }
                else if (currUnit.clickCount >= 2 && !currUnit.lock && !currUnit.done){
                    currUnit.chosen = false;
                    currUnit.clickCount = 0; //reset clickCount
                }


                if (currUnit.isAttackable){
                    currUnit.underattack = true;
                    currUnit.isAttackable = false;
                }

            }

            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Unit currUnit = ((Unit)event.getTarget());

                if (currUnit.clickCount == 2 && !currUnit.lock && !currUnit.done) {
                    currUnit.chosen = false;
                    currUnit.clickCount = 0; //reset clickCount
                    UnitUtils.Movement.hideMoves(currUnit);
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