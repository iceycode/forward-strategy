package com.fs.game.ai;

/** Contains message types for MessageDispatcher Telegrams
 *  Currently Telegrams are used by UnitAgent, as well as UnitController
 *
 * Created by Allen on 5/27/15.
 */
public class MessageType {

    public static final int PF_PLAYER_REQUEST = 0;

    public static final int PF_PLAYER_RESPONSE = 1;



    //for path finding messages
    public static final int PF_FIND_PATHS = 2;
    public static final int PF_FIND_BEST_PATH = 3;




}
