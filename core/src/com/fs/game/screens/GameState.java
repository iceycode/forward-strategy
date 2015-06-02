package com.fs.game.screens;

/** GameState enum
 * - used for determining current animState of game
 *
 * Created by Allen on 11/7/14.
 */
public enum GameState {

    TEST_SCREEN, //test screen

    //during player faction, unit, map selection
    START_SCREEN,
    MAIN_MENU,
    GAME_RULES,
    SETTINGS_GLOBAL,
    FACTION_SELECT,
    UNIT_SELECT,
    MAP_SELECT,

    //whether single or multiplayer game screen
    MULTIPLAYER, //when a multiplayer game is start
    SINGLEPLAYER, // single player game

    //Game animState during game play (cannot go into selection or main menus)
    WAITING, //when a player is waiting
    STARTING, //starting the game
    STARTING_MULTI, //starting multiplayer game...means players connected & waiting for update from each other
    PLAYER_DONE, //when player is done
    PLAYER_TURN, //when player has turn


    RUN,    //when actual game is running
    PAUSE,  //when game is paused
    RESUME, //when game is resumed


    GAME_OVER, //when game is over
    QUIT,   //when game is quit by player


}
