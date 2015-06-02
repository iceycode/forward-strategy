package com.fs.game.units;

/** Enum for Unit control state, or what player is doing with it
 *  Unit can be moving, done_moving, standing, finished/done, chosen/selected, attacking,
 *
 * NOTE: int values are for AI Telegraph messages
 *
 * Created by Allen on 5/21/15.
 */
public enum UnitState {

    STANDING, //either done attacking

    CHOSEN, //is chosen, can then attack

    MOVING, //is moving
    ATTACKING, //can attack


    DONE_MOVING,

    UNDER_ATTACK,

    //Unit is at border, so a new Unit is added
    AT_ENEMY_BORDER,

    DONE,//finished all possible actions for turn

    DEAD, //Unit is dead
}
