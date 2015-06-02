package com.fs.game.ai.fsm;

/**MessageType class
 * - contains int value, MessageType, used in MessageDispatcher
 * - tells any MessageDispatcher listeners the current animState
 *
 * Created by Allen on 5/10/15.
 */
public class MessageType {

    public static final int READY = 0; //ready to go

    public static final int CHOSEN_UNIT = 1; //unit is chosen

    public static final int FINISHED_UNIT = 2; //unit finished move/attack

    public static final int FINISHED = 3; //finished turn

}
