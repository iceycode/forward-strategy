package com.fs.game.ai.fsm;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitController;

/** AgentUtils contains methods which help UnitAgent decide where
 *  to move Units and sets up information about Units in game for Agent to use.
 *

 *
 * Created by Allen on 5/12/15.
 */
public class AgentUtils {

    private static AgentUtils instance;

    //flags for damage types - get factored into UnitAgent decision
    public final static int KILL_DAMAGE = 4;
    public final static int HIGH_DAMAGE = 3;
    public final static int MEDIUM_DAMAGE = 2;
    public final static int LOW_DAMAGE = 1;
    public final static int NO_DAMAGE = 0;

    //contains arrays of each unit's list of damage
    public static IntMap<Array<int[]>> damageMap = new IntMap<Array<int[]>>();

    //contains health animState
    public static IntMap<Array<Integer>> healthMap = new IntMap<Array<Integer>>();





    /** sets up healthMap
     * NOTE: unitsCount is ALL units in game
     *
     * @param unitsCount : number of units on board TOTAL
     */
    public void initHealthMap(int unitsCount){
        Integer[] healths = new Integer[unitsCount]; //initalize array

        for (int i = 0; i < unitsCount; i++){
            healths[i] = 4;
        }

        Array<Integer> healthArray1 = new Array<Integer>(healths);
        Array<Integer> healthArray2 = new Array<Integer>(healths);

        healthMap.put(0, healthArray1);
        healthMap.put(1, healthArray2);
    }


    public void initDamageMap(Array<Unit> agentUnits, Array<Unit> playerUnits){
        Array<int[]> aUnitDamages = new Array<int[]>();
        for (Unit unit : agentUnits){
            aUnitDamages.add(unit.getUnitInfo().getDamageList());
        }

        Array<int[]> pUnitDmgs = new Array<int[]>();
        for (Unit unit : playerUnits){
            pUnitDmgs.add(unit.getUnitInfo().getDamageList());
        }

        damageMap.put(0, aUnitDamages);
        damageMap.put(1, pUnitDmgs);
    }


    /** Checks factors which affect where Unit will move
     *  Finds health of other units, damages and other information such
     *  as distance to and damage dealt to currUnit.
     *
     * @param currUnit : current Units
     */
    public void checkMoveFactors(Unit currUnit, Array<Unit> enemies){

        for (Unit u : enemies){
            UnitController.getInstance().getEnemyDamage(u);
        }
    }

    /** Assesses damage unit can do to all other units
     *
     * @param unitIndex : unit index
     * @param healths :
     */
    public static void assessDamageTo(int unitIndex, Array<int[]> healths){

    }

    public static void assessDamageFrom(Unit unit, Array<int[]> damages){

    }


}
