package com.fs.game.data;

/** Game Mode dicates what game mechanics are used througout game
 *  Contains variables that represent certain logic and sequence orders followed by
 *  UnitController.
 *
 *  For example, if attackAtEnd is true, all Units first move to places, then attack
 *  If false, then Units attack after moving.
 *
 * @author Allen
 *         Created on 6/6/15.
 */
public class GameMode {

    public boolean attackAtEnd = false;

}
