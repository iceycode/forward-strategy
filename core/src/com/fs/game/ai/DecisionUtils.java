package com.fs.game.ai;

import com.badlogic.gdx.utils.Array;
import com.fs.game.units.Unit;
import com.fs.game.ai.fsm.RiskFactors;

/** Utility class all about decisions (contains all static methods)
 *
 * Need to define optimal move for Unit in each animState.
 * State = {(position, distTo{E_Units}), (damTo{E_Units}, damFrom{E_Units})}
 *  distTo{UnitsE}: distance to each ENEMY Unit based on position of Unit at each
 *  damTo{UnitsE}: damage to each Unit
 *
 * TODO: create a constraint factor
 * Created by Allen on 5/12/15.
 */
public class DecisionUtils {

    public static int distanceFrom; //distance from one unit to another

    /** Assesses damage unit can do to all other units
     *
     * @param unitIndex : unit index
     * @param healths :
     */
    public static void assessDamageTo(int unitIndex, Array<int[]> healths){

    }

    public static void assessDamageFrom(Unit unit, Array<int[]> damages){

    }


    public static void decideMove(){

    }


    public static void setRandomMove(Unit currUnit){

    }


    /** Obtains current unit health
     *
     * @param unitIndex : index of unit being looked at
     * @param playerID : whether player is human (0)
     * @return
     */
    public static int getUnitHealth(int unitIndex, int playerID){
        return RiskFactors.healthMap.get(playerID).get(unitIndex);
    }

}
