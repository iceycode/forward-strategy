package com.fs.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.ai.pf.PanelPathfinder;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.map.Panel;
import com.fs.game.utils.GameMapUtils;
import com.fs.game.utils.UnitUtils;
import com.fs.game.utils.pathfinder.PathFinder;
import com.fs.game.utils.pathfinder.PathGenerator;

/** Controls unit actions in relation to player input
 *  Sends Unit actions and changes its internal data
 *  Works with Panels to move Unit to locations by implementing PathGenerator & PathFinder
 *  classes and adding a SequenceAction to Unit.
 *
 * Created by Allen on 5/23/15.
 */
public class UnitController implements Panel.UnitUpdater, Telegraph{

    private static UnitController instance;

    Unit currUnit; //current unit selected
    boolean isPlayer; //tells whether this is the player or opponent

    Array<Panel> unitMoves; //current Unit moves
    PathGenerator pathGen; //generates Unit paths
    PathFinder pathFinder; //finds best path to selected Panel using A*
    PanelPathfinder pf; //alt panel pathfinder Utility that uses gdx-ai Indexed A*


    public UnitController(){
        pathGen = new PathGenerator();
        pathFinder = new PathFinder();

    }


    public static UnitController getInstance() {
        if (instance == null)
            instance = new UnitController();

        return instance;
    }


    /** Selects a Unit when actor experiences a touchdown
     * Deselects previous unit even if player did not click on it to deselect
     *
     * @param unit : unit selected
     */
    public void selectUnit(Unit unit) {
        //deselect any current unit if any
        if (currUnit!=null)
            deselectUnit();

        //check if unit is not done
        if (unit.state != UnitState.DONE){
            currUnit = unit;
            currUnit.state = UnitState.CHOSEN;
//            this.isPlayer = currUnit.isPlayerUnit(); //set this as a Player Unit or Opponent Unit


            log("SELECTED Unit, unit, " + currUnit.getName() + ", is " + currUnit.state.toString());

            GameMapUtils.setPanelsInRange(currUnit); //hide moves

//            pf.setPanelsInRange(currUnit); //set moveable Panels with pathfinder (Indexed A* version)
            updateStage(); //show info about Unit
        }
    }

    //deselects current Unit
    public void deselectUnit(){
        GameMapUtils.resetPanelsInRange(currUnit);

        // If this Unit is not DONE, then it is just STANDING
        // Player switched Units to move
        if (currUnit.state != UnitState.DONE){
            log("UNSELECTED Unit, unit, " + currUnit.getName() + ", is STANDING");
            currUnit.state = UnitState.STANDING;
        }

        //UnitController currUnit is set to null afterwards
        currUnit = null;
    }


    /** Moves unit to target panel
     *
     * @param panel : target panel to move to
     */
    public void moveUnit(Panel panel){
        log("Moving Unit");

        currUnit.setTargetPan(panel); //set targetPanel
        pathFinder.findBestPath(currUnit, currUnit.targetPan); //find best path

        currUnit.panelPath = pathFinder.getUnitMovePath(); //set the path

        GameMapUtils.resetPanelsInRange(currUnit); //hide moves

        currUnit.state = UnitState.MOVING; //set state to MOVING
        currUnit.addAction(UnitUtils.Movement.createMoveAction(pathFinder.getUnitMovePath()));
    }


    //unit has finished moving
    public void onMoveFinish(){
        log("Unit finished moving");

        //check for any enemy
        Unit enemy = UnitUtils.Attack.findBestEnemy(currUnit);

//        mapManager.updatePanelActive(currUnit);

        if (enemy!= null){
//            mapManager.updatePanelActive(enemy);
            currUnit.enemyUnit = enemy; //set the enemy Unit
            currUnit.state = UnitState.ATTACKING;
        }
        else{
//            currUnit.state = UnitState.DONE_MOVING;
            onTurnFinish();
        }
    }




    /** Method called after Unit finished either
     * 1) Moving, if no enemies are found adjacent to them
     * 2) Attacking, if the Unit is not at border.
     *
     */
    public void onTurnFinish(){
        log("Unit finishing turn");

        if (currUnit.enemyUnit!=null && currUnit.enemyUnit.state==UnitState.DEAD){
            currUnit.enemyUnit.remove();
        }

//        isAtEnemyBorder(); //TODO: create an in-game selection menu for new Units

        currUnit.state = UnitState.DONE;
        deselectUnit();
    }

//    public void isAtEnemyBorder(){
//        if (currUnit.getX() == Constants.MAP_TOP_RIGHT[0] && currUnit.player == 1 ||
//                (currUnit.getX() == Constants.MAP_BTM_LEFT[0] && currUnit.player == 2)){
//            UnitUtils.Setup.cloneUnit(currUnit);
//        }
//    }

    public int getEnemyDamage(Unit unit){
        return Assets.damageListArray.get(currUnit.getUnitID()-1)[unit.getUnitID()-1];
    }


    protected void updateStage(){
//        ((GameStage)currUnit.getStage()).setChosenUnit(currUnit);

        if (currUnit.state == UnitState.CHOSEN || currUnit.state == UnitState.MOVING) {
            String[] unitDetails = {UnitUtils.Info.unitDetails(currUnit), UnitUtils.Info.unitDamageList(currUnit)};
            GameData.unitDetails = unitDetails;
            GameData.chosenUnit = currUnit;
            GameData.isChosen = true;
        }
        else{
            GameData.isChosen = false;
        }
    }

    @Override
    public void setSelectedPanel(Panel panel) {
        moveUnit(panel);
    }


    @Override
    public boolean handleMessage(Telegram msg) {

        switch(msg.message){
            case PanelPathfinder.PF_FIND_PATH:
                unitMoves = (Array<Panel>)msg.extraInfo;
                break;
            case PanelPathfinder.PF_SHOW_MOVES:
                currUnit.panelPath = (Array<Vector2>)msg.extraInfo;
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
