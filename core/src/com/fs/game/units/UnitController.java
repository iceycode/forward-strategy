package com.fs.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.fs.game.MainGame;
import com.fs.game.ai.pf.PanelPathfinder;
import com.fs.game.appwarp.AppWarpAPI;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.fs.game.map.Locations;
import com.fs.game.map.Panel;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.UnitUtils;
import com.fs.game.utils.pathfinder.PathFinder;
import com.fs.game.utils.pathfinder.PathGenerator;

/** Controls unit actions in relation to player input
 *  Sends Unit actions and changes its internal data
 *  Works with Panels to move Unit to locations by implementing PathGenerator & PathFinder
 *  classes and adding a SequenceAction to Unit.
 *
 * NOTE 1: for now, attack only 1 enemy, even if several are around
 * NOTE 2: UnitController now only assigns currUnit when selecting Unit, so to not allow multiple selections
 *
 * @author Allen
 * Created by Allen on 5/23/15.
 */
public class UnitController implements Panel.UnitUpdater, Telegraph{

    private static UnitController instance;

    Unit currUnit; //current unit selected
    UnitData unitData; //current Unit multiplayer data
    int damageTo; //damage to enemy (for attack)

//    int damageMod = 15; //damage modifier (multiplier) //FIXED: modified in Assets

    Array<Unit> enemies; //all enemy units near currUnit
    Array<String> enemyNames; //those enemy names (for multiplayer update)
    ObjectMap<String, Unit> enemyMap; //map of enemy Units with names as key

    PathGenerator pathGen; //generates Unit paths
    PathFinder pathFinder; //finds best path to selected Panel using A*
    PanelPathfinder pf; //alt panel pathfinder Utility that uses gdx-ai Indexed A*


    //Game Modes
    boolean forceMoveFinish = false; //if true, sequence interrupted by turn change
    boolean attackAtEnd = false; //if true, then all Units attack at the end



    public UnitController(){
//        pathGen = new PathGenerator();
//        pathFinder = new PathFinder();
    }


    public static UnitController getInstance() {
        if (instance == null)
            instance = new UnitController();

        return instance;
    }



    /** Updates all Units on player start of turn. Mainly just sets state from DONE to STANDING.
     *
     * @param unit : unit that is being updated
     * @param player : next player, whose units are being "unlocked" by changing state
     */
    public void onTurnChange(Unit unit, int player) {
        if (player == unit.getPlayer()){
            unit.state = UnitState.STANDING;
        }
        else{
            findEnemiesToAttack(unit);

            if (unit.state != UnitState.MOVING)
                onTurnFinish(unit); //FIXED: was in attackUnits method
        }

        hideUnitMoves(); //hides any current unit's moves
    }

    /** Selects a Unit when actor experiences a touchdown
     * Deselects previous unit even if player did not click on it to deselect
     *
     * @param unit : unit selected
     */
    public void selectUnit(Unit unit) {
        if (currUnit!=null)
            deselectUnit(currUnit);

        //check if unit is not done & currUnit is equal to null (upon finishing, currUnit set to null)
        if (unit.state != UnitState.DONE){
            currUnit = unit;
            currUnit.state = UnitState.CHOSEN;

            log("SELECTED Unit, unit, " + currUnit.getName() + ", is " + currUnit.state.toString());

//            GameMapUtils.setPanelsInRange(currUnit); //show moves NOTE: temporary change

            //dispatches a message to PanelPathFinder TODO: test out new pathfinding algorithm
            MessageManager.getInstance().dispatchMessage(this, PanelPathfinder.getInstance(), PanelPathfinder
                    .SHOW_ALL_PATHS, unit);

            updateUnitInfoStage(); //show info about Unit

            //only sends it out if player owns this unit
            if (MainGame.isMultiGame() && currUnit.isPlayerUnit()){
                unitData = getUnitData(currUnit); //sets up UnitData that will be used to send
                AppWarpAPI.getInstance().sendUnitUpdate(currUnit, unitData); //updates multiplayer via AppWarp
            }
        }
    }

    //deselects current Unit
    public void deselectUnit(Unit unit){
//        GameMapUtils.resetPanelsInRange(unit); //NOTE: temporary change

        MessageManager.getInstance().dispatchMessage(this, PanelPathfinder.getInstance(), PanelPathfinder.HIDE_PATHS);

        // If this Unit is not MOVING, then change state to STANDING
        // This is in the case that player switched units but did not move one.
        if (unit.state != UnitState.MOVING){
            log("UNSELECTED Unit, unit, " + unit.getName() + ", is IS_STANDING");
            unit.state = UnitState.STANDING;

            if (MainGame.isMultiGame() && unit.isPlayerUnit()){
                updateCurrUnitData(unit);
            }
        }

        //UnitController currUnit is set to null, if any unit was assigned to it
        if (currUnit != null)
            currUnit = null;

    }


    /** Moves unit to target panel
     *
     */
    public void moveUnit(){
        log("Moving Unit");

//        currUnit.setTargetPan(panel); //set targetPanel
//        pathFinder.findBestPath(currUnit, currUnit.targetPan); //find best path NOTE: old setup with old pathfinder
//        currUnit.panelPath = pathFinder.getUnitMovePath(); //set the path
//        GameMapUtils.resetPanelsInRange(currUnit); //hide moves
        Locations.getLocations().updateUnitNodePosition(currUnit.getPosData(), currUnit);

        currUnit.state = UnitState.MOVING; //set state to IS_MOVING

        if (MainGame.isMultiGame() && currUnit.isPlayerUnit()){
            updateCurrUnitData(currUnit);
        }

        currUnit.addAction(UnitUtils.Movement.createMoveAction(currUnit.panelPath));

    }


    //unit has finished moving
    public void onMoveFinish(Unit unit){
        log("Unit finished moving");

        // TODO: figure out best attack sequence for game design/mechanics
//        Unit enemy = UnitUtils.Attack.findBestEnemy(currUnit); //NOTE: alt attack - only attacks best enemy

        //NOTE: this is an alternate sequence, where Unit attacks at end of movement if enemies nearby
//        ObjectMap<String, Unit> enemyMap = UnitUtils.Attack.getEnemyMap(unit);
//
//        if (enemyMap.size > 0){
//            attackUnits(unit, enemyMap); //attack any nearby Units, if any
//        }
//        else{
//            onTurnFinish(unit);
//        }

        onTurnFinish(unit); //finish up unit turn
    }


    public void findEnemiesToAttack(Unit unit){
        ObjectMap<String, Unit> enemyMap = UnitUtils.Attack.getEnemyMap(unit);
        if (enemyMap.size > 0){
            attackUnits(unit, enemyMap); //attack units
        }
    }

    /** Attack any nearby units after moving, if enemyMap size > 0
     *  Enemies are in enemyMap and in multiplayer, names are sent in UnitData
     *  NOTE: currently attackUnits is automatically done at END OF PLAYER TURN
     */
    protected void attackUnits(Unit unit, ObjectMap<String, Unit> enemyMap){
        unit.state = UnitState.ATTACKING; //set state to attacking

        for (String key : enemyMap.keys()){
            enemyMap.get(key).damageUnit(); //damage enemy in map
        }

//        onTurnFinish(unit);
    }

    /** Updates Unit death and triggers consequences that result eg. updates GameStage score
     *  Player whose unit died SENDS multi update & does not get score updated.
     *  Opposite player gets score incremented by points based on size.
     *   TODO: in future, add extra specials here for Unit that killed this one.
     *
     * @param unit : unit that has died
     */
    public void unitDeathUpdate(Unit unit){
        int player = unit.player == 1 ? 2 : 1;

        //set score based on unit size
        int points = unit.getUnitSize() == Unit.SMALL ? 10
                : unit.getUnitSize() == Unit.MEDIUM ? 20
                : 30;

        ((GameStage)unit.getStage()).updateScore(player, points);

        unit.remove(); //remove unit from stage

        //only send out if units player whose score was incremented, is not this player
        if (MainGame.isMultiGame() && player != ((GameStage)unit.getStage()).getPlayer()){
            updateCurrUnitData(unit);
        }
    }

    /** Method called after Unit finished either
     * 1) Moving, if no enemies are found adjacent to them
     * 2) Attacking, if the Unit is not at border.
     */
    public void onTurnFinish(Unit unit){
        log("Unit finishing turn");

//        isAtEnemyBorder(); //TODO: finish this

        unit.state = UnitState.DONE;
    }

    //TODO: create an in-game selection menu for new Units
//    public void isAtEnemyBorder(){
//        if (currUnit.getX() == Constants.MAP_TOP_RIGHT[0] && currUnit.player == 1 ||
//                (currUnit.getX() == Constants.MAP_BTM_LEFT[0] && currUnit.player == 2)){
//            UnitUtils.Setup.cloneUnit(currUnit);
//        }
//    }



    /** Updates GameData for InfoStage to display Unit information
     *  which includes terrain type, range, size & damage to opponent units
     *
     */
    protected void updateUnitInfoStage(){
        //if currUnit is not null
        if (currUnit.state != null) {
            String info = UnitUtils.Info.unitDetails(currUnit);
            String damages = UnitUtils.Info.unitDamageList(currUnit);
            GameData.unitDetails = new String[]{info, damages}; //set GameData.unitDetails
        }
    }


    /** Hides unit moves if one is chosen and turn changes. Normally, this is done after panel for unit to move to has
     *  been chosen & path is found.
     */
    public void hideUnitMoves(){
        if (currUnit != null){
            if (currUnit.state == UnitState.CHOSEN){
                MessageManager.getInstance().dispatchMessage(this, PanelPathfinder.getInstance(), PanelPathfinder
                        .HIDE_PATHS);
            }
        }
    }


    /** Creates a new UnitData object for currUnit,
     *  adding basic info for it, to be updated during each step
     *  after it is chosen.
     */
    public UnitData getUnitData(Unit unit){
        UnitData unitData = new UnitData(); //create new object
        unitData.setName(unit.getName());
        unitData.setHealth(unit.health);
        unitData.setUnitState(unit.state);
//        unitData.setDamage(currUnit.damage);

        return unitData;
    }

    /** Updates data that was set above
     *  Used in {@link UnitController#deselectUnit(Unit unit)}, {@link UnitController#moveUnit()},
     *  and perhaps more...TODO...add any new Unit control to Multi update
     */
    protected void updateCurrUnitData(Unit unit){
        UnitData unitData = getUnitData(unit);

        if (unit.state == UnitState.MOVING)
            unitData.setTarget(unit.targetPan.getGraphPosition());


        //send out data
        AppWarpAPI.getInstance().sendUnitUpdate(unit, unitData);
    }



    /** Implemented from MultiplayerScreen
     *  Updates a multiplayer Unit based on UnitData in UserData
     *
     * @param userData : data other player sent that was decoded by AppWarpAPI
     */
    public void updateUnitMulti(UserData userData){
        UnitData unitData = userData.getUnitData();
        Unit unit = UnitUtils.getUnitByName(unitData.getName(), userData.getPlayer());

        switch(unitData.getUnitState()){
            case CHOSEN:
                //obtain current Unit being controlled by player
                selectUnit(unit); //set the currUnit
                break;
            case MOVING:
                int[] loc = unitData.getTarget(); //selected Panel of opponent currUnit
                Panel p = GameData.panelMatrix[loc[0]][loc[1]];
                setSelectedPanel(p); //moves unit
                break;
            case STANDING:
                deselectUnit(unit);
                break;
            case UNDER_ATTACK:
                unit.damageUnit();
                break;
            case DEAD:
                unitDeathUpdate(unit);
                break;
//            case ATTACKING:
//                //damage those units
//                for (String name : unitData.getEnemies()){
//                    Unit u = UnitUtils.getUnitByName(name, userData.getPlayer() == 1 ? 2 : 1);
//                    u.damageUnit(); //since damage sould already be set in Unit
//                }
//                break;
        }
    }


    /** Gets damage done from currUnit to Unit parameter
     *
     * @param unit : unit damage is done it
     * @return : int damage
     */
    public int getEnemyDamage(Unit unit){
        return Assets.damageListArray.get(currUnit.getUnitID()-1)[unit.getUnitID()-1];
    }


    /** Returns an array of Unit healths
     *
     * @param player : player whose units healths are put int array
     * @return : an int array
     */
    public int[] getUnitHealths(int player){
        Array<Unit> units = player == 1 ? GameData.playerUnits : GameData.enemyUnits;

        int[] healths = new int[units.size];
        for (int i = 0; i < units.size; i++){
            healths[i] = units.get(i).health;
        }

        return healths;
    }


    @Override
    public void setSelectedPanel(Panel panel) {
//        moveUnit(panel); //NOTE: Changed
        currUnit.setTargetPan(panel);
        MessageManager.getInstance().dispatchMessage(this, PanelPathfinder.getInstance(), PanelPathfinder
                .FIND_PATH, panel.graphPos);
    }


    @Override
    public boolean handleMessage(Telegram msg) {

        switch(msg.message){
            case PanelPathfinder.FOUND_ALL_PATHS:
                Array<Panel> unitMoves = (Array<Panel>)msg.extraInfo;
                currUnit.panelArray = unitMoves;
                break;
            case PanelPathfinder.FOUND_PATH:
                log("A path was found...set path, then move unit");
                currUnit.panelPath = (Array<Vector2>) msg.extraInfo;
                moveUnit();
                break;
            case PanelPathfinder.NO_PATH_FOUND:
                log("ERROR: no path to target panel found!");
                break;
        }

        return false;
    }


    public Unit getCurrUnit(){
        return currUnit;
    }


    private void log(String message){
        Gdx.app.log("UnitController LOG: ", message);
    }

}
