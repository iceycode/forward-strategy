package com.fs.game.ai.fsm;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitController;
import com.fs.game.ai.pf.PanelPathfinder;
import com.fs.game.map.Locations;

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
 * Example of enter/update/exit method
 * / Let Elsa know I'm home
 MessageManager.getInstance().dispatchMessage( //
     0.0f, // time delay
     bob, // ID of sender
     bob.elsa, // ID of recipient
     MessageType.HI_HONEY_I_M_HOME, // the message
     null);
 *
 * Created by Allen on 3/14/15.
 */
public class UnitAgent implements Telegraph{

    final int difficulty;
    public int unitsDone = 0; //number of units that have finished action

    Locations locations;
    RiskFactors riskFactors;

//    //data that determines how UnitAgent decides Unit action
//    DecisionUtils decisionUtils;

    float currTime = 0; //current time
    PanelPathfinder pathFinder ;

    StateMachine<UnitAgent> stateMachine; //instance of animState machine class
    BehaviorTree<UnitAgent> agentTree; //behavior tree helps UnitAgent decisions

    Unit currUnit; //the unit currently chosen
    int unitAction; //the current unit action taken

    Array<Unit> agentUnits;
    Array<Unit> opponents; //opponents of unit
    Array<Integer> unitsWaiting = new Array<Integer>(new Integer[]{0,1,2,3,4,5,6}); //indices of units not selected

    public UnitAgent(Array<Unit> allies, Array<Unit> enemies, int difficulty){
        this.difficulty = difficulty;
        this.agentUnits = allies;
        this.opponents = enemies;

        unitsDone = 0;

        //initialize animState machine
        stateMachine = new DefaultStateMachine<UnitAgent>(this, AgentState.GLOBAL_STATE);
        pathFinder = PanelPathfinder.getInstance(); //initialize pathfinder

        riskFactors = RiskFactors.getInstance();
        locations = Locations.getLocations();
//        decisionUtils.init(allies, enemies);  //initialize decisionUtils
    }

//    public UnitAgent(Unit unit, Array<Unit> opponents){
//        this.currUnit = unit;
//        this.opponents = opponents;
//        stateMachine = new DefaultStateMachine<UnitAgent>(this, AgentState.GLOBAL_STATE);
//    }

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

        unitAction = CurrentState.CHOSEN;
    }


    //moves unit
    public void moveUnit(){
        switch(difficulty){
            case Difficulty.VERY_EASY:
                randomMove();
                break;
            default:
                randomMove();
                break;
        }
    }

    //decides what unit should do
    protected void decideUnitAction(){

    }

    //moves Unit to random position
    // TODO: finish up random move
    protected void randomMove(){
        int in = ThreadLocalRandom.current().nextInt(0, currUnit.panelArray.size);
//        currUnit.panelArray.get(in).setSelected(true);
        unitAction = CurrentState.MOVE_ACTION;
    }

    public StateMachine<UnitAgent> getStateMachine(){
        return stateMachine;
    }

    public int getUnitAction(){
        return unitAction;
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

    //current unit animState of UnitAgent's currUnit
    public static class CurrentState {

        public final static int NONE = -1;
        //current unit actions being taken
        public static int CHOSEN = 0; //unit is chosen
        public final static int MOVE_ACTION = 1;
        public final static int ATTACK_ACTION = 2;

        public final static int MOVE_ATTACK = 4;
        public final static int ATTACK_MOVE = 5;
    }
}
