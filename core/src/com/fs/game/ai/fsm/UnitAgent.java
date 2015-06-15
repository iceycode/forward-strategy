package com.fs.game.ai.fsm;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.fs.game.ai.pf.PanelPathfinder;
import com.fs.game.map.Locations;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitController;

import java.util.concurrent.ThreadLocalRandom;

/**{@code UnitAgent}
 * UnitAgent, that controls Unit & implements Telegraph
 * - telegraph handles messages about animState of individual Units & all Units
 *<p>
 *
 * NOTE: detailed info links
 * StackStateMachine: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#stackstatemachine"}
 * DefaultStateMachine: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#defaultstatemachine"}
 * Simple Example: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#a-simple-example"}
 * Example with Messaging: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#a-complete-example-with-messaging"}
 *
 * NOTES on design:
 * - Need to make agents aware of each other in the grid
 * - using messages, each agent also has to be aware of what other is doing, so they all do not move together in same
 *      place
 * - this is where BehaviorTree implementation may come in handy
 *      - each StateMachine is controlled by BehaviorTree tasks
 *      - Tree composite tasks are in turn influenced by the messages UnitAgents are handling (there actions)
 *      - so Tree Task --> Agent updated --> stateMachine State changed --> State sends message -->
 *              UnitAgent acts --> depending on action, tree does Task which alters UnitAgent act -->
 *              --> act updates StateMachine State --> gets success or failure
 *              --> update UnitAgent action
 *
 *
 *
 * Example of enter/update/exit method
 * / Let Elsa know I'm home
 MessageManager.getInstance().dispatchMessage( //
     0.0f, // time delay
     bob, // ID of sender
     bob.elsa, // ID of recipient
     MessageType.HI_HONEY_I_M_HOME, // the message
     null);

 ----UnitAgent Decisions----
 UnitAgent Heuristics are based on a several variables:
 *      1) Damage - dealt TO Units
 *      2) Damage - taken FROM Units
 *      3) Distance - distance to enemies,
 *      4) Health - Current health of Unit chosen
 *      5) Unit Count - number of units on board (if less then opponents, will preserve)
 *      6) Unit Count Opponent - number of units opponent has on board
 *
 * Created by Allen on 3/14/15.
 */
public class UnitAgent implements Telegraph{

    final int difficulty; //diffculty determines movement preferences

    Array<Unit> agentUnits;
    Array<Unit> opponents; //opponents of unit
    Array<Integer> unitsWaiting = new Array<Integer>(new Integer[]{0,1,2,3,4,5,6}); //indices of units not selected

    public int unitsDone = 0; //number of units that have finished action
    int[] target; //current Unit's target panel position
    int[] currPos; //current Units position

    Unit currUnit; //the unit currently chosen
    UnitController uc = UnitController.getInstance();; //unit controller
    Locations locations = Locations.getLocations();

    OrderedMap<String, Array<Float>> distanceMap; //distances to enemies
    OrderedMap<String, int[]> damageMap; //damages to enemies
    OrderedMap<String, Integer> healthMap; //health of enemies
    Array<Integer> moveRanges; //how far enemies can move

    float currTime = 0; //current time
    PanelPathfinder pathFinder;

    StateMachine<UnitAgent> stateMachine; //instance of animState machine class
    BehaviorTree<UnitAgent> agentTree; //behavior tree helps UnitAgent decisions


    public UnitAgent(Array<Unit> allies, Array<Unit> enemies, int difficulty){
        this.difficulty = difficulty;
        this.agentUnits = allies;
        this.opponents = enemies;

        unitsDone = 0;

        //initialize animState machine
        stateMachine = new DefaultStateMachine<UnitAgent>(this, AgentState.GLOBAL_STATE);
        pathFinder = PanelPathfinder.getInstance(); //initialize pathfinder



    }

    /** Sets up the percept maps with format: Unit name key, percept of enemy
     *  Percept of enemy includes: damageTo/From, distance b/w, current health, move range
     *  Used in conjunction with damage map to make movement decision
     */
    protected void setEnemyPercepts(){
        distanceMap = new OrderedMap<String, Array<Float>>();
        damageMap = new OrderedMap<String, int[]>();
//        healthMap = new OrderedMap<String, Integer>();
//        moveRanges = new Array<Integer>(); //move ranges
        for (Unit unit : agentUnits){
            int[] damArr = new int[opponents.size];
            Array<Float> distances = new Array<Float>();
            for (int i = 0; i < opponents.size; i++){
                Unit enem = opponents.get(i);
                float dist = locations.getManhattanDistance(unit.getGraphOrigin(), enem.getGraphOrigin());
                distances.add(dist);
                int damage = uc.getEnemyDamage(enem);
                damArr[i] = damage;
            }

            distanceMap.put(unit.getName(), distances);
            damageMap.put(unit.getName(), damArr);
        }

    }




    public void update(float delta){
        currTime += delta;

        stateMachine.update();
    }


    public int getUnitsDone(){
        return unitsDone;
    }

    public Array<Unit> getAgentUnits(){
        return agentUnits;
    }

    //selects random agent unit
    public void selectRandomUnit(){
        //get random unit
        int randIndex = unitsWaiting.removeIndex(ThreadLocalRandom.current().nextInt(unitsWaiting.size));

        currUnit = agentUnits.get(randIndex); //set current unit
        UnitController.getInstance().selectUnit(currUnit); //chose unit
    }


    //moves unit
    public void moveUnit(){
        switch(difficulty){
            case Difficulty.EASY:
                randomMove();
                break;
            case Difficulty.NORMAL:
                decideUnitMove();
                break;
            default:
                randomMove();
                break;
        }
    }

    //decides what unit should do
    public void decideUnitMove(){


    }

    //moves Unit to random position
    // TODO: finish up random move
    protected void randomMove(){
        int in = ThreadLocalRandom.current().nextInt(0, currUnit.panelArray.size);
//        currUnit.panelArray.get(in).setSelected(true);
    }

    public StateMachine<UnitAgent> getStateMachine(){
        return stateMachine;
    }


    public boolean isAtTarget(){
        return currPos[0] == target[0] && currPos[1]==target[1];
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }


    //AI difficulty states
    public static class Difficulty{
        public static final int VERY_EASY = 0; //NOTE: this is only for basic setup, not for realy game play
        public static final int EASY = 1;
        public static final int NORMAL = 2;
        public static final int HARD = 3;
    }

}
