package com.fs.game.enums;

/** GameState enum
 * - used for determining current state of game
 *
 * Created by Allen on 11/7/14.
 */
public enum GameState {

    //during player faction, unit, map selection
    START_SCREEN,
    MAIN_MENU,
    SETTINGS_GLOBAL,
    FACTION_SELECT,
    UNIT_SELECT,
    MAP_SELECT,

    //Game state during game play (cannot go into selection or main menus)
    PLAYER_IDLE, //when a player is idle (has not moved in >2 turns
    RUN,    //when actual game is running
    PAUSE,  //when game is paused
    RESUME, //when game is resumed
    GAME_OVER, //when game is over
    QUIT,   //when game is quit by player


}
