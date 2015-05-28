package com.fs.game.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.fs.game.ai.AgentManager;

/** A representation of position (Vector2) and the Unit status (UnitState)
 * Uses singleton design which will not eat up memory
 * Delegated by UnitStateMachine or UnitAgent Entity
 *
 * Created by Allen on 5/6/15.
 */
public enum AgentState implements State<UnitAgent>{

    //global animState - means unit is standing still
    GLOBAL_STATE(){
        @Override
        public void update(UnitAgent unitAgent) {
            if (unitAgent.getUnitsDone() < unitAgent.getAgentUnits().size){
                unitAgent.selectRandomUnit();
                unitAgent.getStateMachine().changeState(DECIDE_ACTION);
            }
        }

        @Override
        public boolean onMessage(UnitAgent unitAgent, Telegram telegram) {

            if (telegram.message == MessageType.CHOSEN_UNIT){
                AgentManager.logAgent(this.getClass(), "Unit randomly selected by agent, changing states...");
            }

            return false;
        }
    },

    //don't need this: covered in GLOBAL_STATE now
//    SELECT_UNIT(){
//        @Override
//        public void enter(UnitAgent unitAgent) {
//            unitAgent.selectRandomUnit();
//        }
//
//        @Override
//        public void update(UnitAgent unitAgent) {
//            if (unitAgent.currUnit.animState == UnitState.CHOSEN){
//                unitAgent.getStateMachine().changeState(DECIDE_ACTION);
//            }
//        }
//
//        @Override
//        public void exit(UnitAgent unitAgent) {
//            AgentManager.logAgent(this.getClass(), "selected unit, deciding what to do...");
//        }
//
//        @Override
//        public boolean onMessage(UnitAgent unitAgent, Telegram telegram) {
//            return false;
//        }
//    },

    //decides what to do
    // the core of AI actions & outcomes occur as a result of this
    // this occurs after unit has been selected
    DECIDE_ACTION(){
        @Override
        public void enter(UnitAgent agent) {

        }

        @Override
        public void update(UnitAgent agent) {

        }

        @Override
        public void exit(UnitAgent agent) {

        }

        @Override
        public boolean onMessage(UnitAgent agent, Telegram telegram) {
            return false;
        }
    },

    MOVE(){
        @Override
        public void enter(UnitAgent unitAgent) {

        }

        @Override
        public void update(UnitAgent unitAgent) {

        }

        @Override
        public void exit(UnitAgent unitAgent) {

        }

        @Override
        public boolean onMessage(UnitAgent entity, Telegram telegram) {
            return false;
        }
    },

    ATTACK_OPPONENT(){
        @Override
        public void enter(UnitAgent entity) {

        }

        @Override
        public void update(UnitAgent entity) {

        }

        @Override
        public void exit(UnitAgent entity) {

        }

        @Override
        public boolean onMessage(UnitAgent entity, Telegram telegram) {
            return false;
        }
    },


    END_TURN(){
        @Override
        public void enter(UnitAgent agent){

        }

        @Override
        public boolean onMessage(UnitAgent entity, Telegram telegram) {
            return false;
        }
    },;

    @Override
    public void enter(UnitAgent entity) {
    }

    @Override
    public void update(UnitAgent entity) {
    }

    @Override
    public void exit(UnitAgent entity) {
    }

    @Override
    public boolean onMessage(UnitAgent entity, Telegram telegram) {
        return false;
    }
//
//    private void logAgentState(String message){
//        Gdx.app.log("AgentState LOG:", message);
//    }
}
