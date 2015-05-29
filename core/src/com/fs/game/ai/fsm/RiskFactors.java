package com.fs.game.ai.fsm;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.fs.game.units.Unit;
import com.fs.game.data.GameData;

/** RiskState contains information about
 *  Agent Units' & Player Units' health & damageList
 * Created by Allen on 5/12/15.
 */
public class RiskFactors {

    private static RiskFactors instance;

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


    public RiskFactors(){
        initHealthMap(GameData.enemyUnits.size + GameData.playerUnits.size);
        initDamageMap(GameData.enemyUnits, GameData.playerUnits);
    }

    public static RiskFactors getInstance() {
        if (instance == null)
            instance = new RiskFactors();

        return instance;
    }



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


//    //scores relating to decision UnitAgent makes
//    public int[] initDecisionScores(){
//
//    }

//
//    public int[] getFactorScores(){
//
//    }

}
