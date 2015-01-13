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
    GAME_RULES,
    SETTINGS_GLOBAL,
    FACTION_SELECT,
    UNIT_SELECT,
    MAP_SELECT,
    MULTIPLAYER, //when a multiplayer game is start
    SINGLEPLAYER, // single player game

    //Game state during game play (cannot go into selection or main menus)
    WAITING, //when a player is waiting
    STARTING, //starting the game
    STARTING_MULTI,
    PLAYER_DONE, //when player is done
    PLAYER_TURN, //when player has turn


    RUN,    //when actual game is running
    PAUSE,  //when game is paused
    RESUME, //when game is resumed


    GAME_OVER, //when game is over
    QUIT,   //when game is quit by player


}
