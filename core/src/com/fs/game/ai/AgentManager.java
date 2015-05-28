package com.fs.game.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.utils.StreamUtils;
import com.fs.game.constants.Constants;
import com.fs.game.ai.tasks.AdjustTask;
import com.fs.game.ai.tasks.DamageTask;
import com.fs.game.ai.tasks.PositionTask;
import com.fs.game.map.Locations;
import com.fs.game.ai.fsm.RiskFactors;
import com.fs.game.ai.fsm.UnitAgent;
import com.fs.game.ai.pf.PanelPathfinder;
import com.fs.game.data.GameData;

import java.io.Reader;

/** Utility class for managing AI agents as Units
 * - Takes care of the bulk of gdxAI process, including:
 *   - Message dispatching
 *   - Scheduling
 * For the ForwardStrategy AI, multiple Units need to move autonomously AND need to
 *   cooperate by using a strategy. They need to be aware of each other, the player Units and
 *   the overall strategy being implemented.
 *   To solve this problem, 2 AI methods are used: Finite State Machines & Decision Trees
 *
 * Finite State Machine
 * - UnitAgent
 *  Each Unit is controlled by an autonomous UnitAgent. UnitAgents communicate states to each other
 *  These states are all handled by the StateMachine & Telegraph interface along with the MessageManager
 *   instance. All UnitAgents are interconnected with the AgentManager and
 *   the AgentManager controls the overall decisions using a BehaviorTree.
 * - BehaviorTree
 *  The Blackboard object, which the behaviorTree writes to, is
 *  in this case, the AgentManager.
 *  The AgentManager
 *
 * Created by Allen on 5/6/15.
 *
 * TODO:
 *
 * FIXME: implement Behavior Tree in higher difficulty
 *
 */
public class AgentManager {

    private static AgentManager aiManager;



    //animState of unit in terms of movement capability
    public static final int LAND_UNIT = 0;
    public static final int WATER_UNIT = 1;
    public static final int AIR_UNIT = 2;

    //size of unit - also effects movement
    public static final int SIZE_SMALL = 0;
    public static final int SIZE_MED = 1;
    public static final int SIZE_LARGE = 2;

    int[] unitState; //current unit animState

    MessageManager manager; //singleton which creates, manages & dispathes telegrams
    PanelPathfinder pathFinder;
    BehaviorTree<UnitAgent> btree;

    AdjustTask adjustTask;
    DamageTask damTask;
    PositionTask posTask;

    UnitAgent unitAgent;

    int currAgentIndex = -1; //index of agent which is currently "deciding"



    float timeElapsed = 0; //total time elapsed

    public AgentManager(){
        manager = MessageManager.getInstance();
        pathFinder = PanelPathfinder.getInstance();

        //initialize UnitAgent (StateMachine)
        unitAgent = new UnitAgent(GameData.enemyUnits, GameData.playerUnits, GameData.difficulty);
        //setBehaviorTree(); //initialize BehaviorTree
    }


    protected void setBehaviorTree(){

        Reader reader = null;

        try{
            reader = Gdx.files.internal(Constants.BEHAVIOR_TREE).reader();
            BehaviorTreeParser<UnitAgent> parser = new BehaviorTreeParser<UnitAgent>(BehaviorTreeParser.DEBUG_HIGH);
            btree = parser.parse(reader, unitAgent);
        }
        finally{
            StreamUtils.closeQuietly(reader);
        }
    }


    public void updateManager(){
        timeElapsed += Gdx.graphics.getRawDeltaTime();

//        btree.step(); //
        unitAgent.update(timeElapsed);
    }


    public static void logAgent(Class type, String message){
        Gdx.app.log("AgentManager LOG [Class " + type.getSimpleName() + "]", message);
    }

    /** Stores information about UnitAgent behavior
     *  relative to Player behavior, and Player behaviors as they
     *  relate to UnitAgent behaviors
     *
     *  Also, can be seen as a dynamic blackboard
     *
     *  BehaviorTree writes data to this class
     *
     * Created by Allen on 5/12/15.
     */
    public static class AIData {

        RiskFactors agentRisks; //perceieved risks by UnitAgent
        Locations unitLocations; //all unit locations


    }
}
