/**
 * @author Allen Jagoda
 */
package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.fs.game.units.Unit;

/**
 * @author Allen
 *
 */
public class Constants { 
	//stage constants
	public static final float SCREENHEIGHT = 500;
	public static final float SCREENWIDTH = 800;
	
	
	//this text is used for debugging and log entries
	public static final String LOG_MAIN = "LOG LevelScreen : ";
	public static final String EXCEPTION = " exception : ";
	public static final String LOG_UNIDAT = "UnitData log: ";
	public static String LEVELSCREEN_LOG = "LevelScreen LOG : ";
	public static String LOG_UNIT_UTILS = "Unit Utils LOG: ";
	
/*-------------GAME STATES-----------
 * paused, running or over
 * 
 * 
 */
	public final static int GAME_PAUSED = 0;
	public final static int GAME_RUNNING = 1;
	public final static int GAME_OVER = 2;


/*-------------MAPS----------------
 * 
 */
	public final static String MAP_3A = "maps/mapTemplate3a.tmx";
	public final static String MAP_3B = "maps/mapTemplate3b.tmx";
	
/*---------------Image pathways in assets folder------
 * assets folder in android/assets
 * 
 * 
 */
	public static final String TITLE_PATH = "title/forwardStrategyTitle1.png";
	
	public static final String UNIT_STAT_JSON_PATH = "units/unitStatsV3.json";
	public static final String UNIT_DAMAGE_JSON_PATH = "units/damageListV1.json";
	public static final String GRID_DOWN_PATH = "maps/tiles/gridDown.png";
	public static final String GRID_PATH = "maps/tiles/grid.png";
	public static final String GRID_VIEW = "maps/tiles/gridView.png";
	
//	public static final String UNIT_MOVE_PATH = "move/move.png";
//	public static final String UNIT_STILL_PATH = "still_pic.png";
	
	
/*---------------UNIT TEXTURE PATHS------------------------
 * 
 * 
 */
	public static final String UNIT_STILL_LEFT = "stillLeft.png";
	public static final String UNIT_STILL_RIGHT = "stillRight.png";
	
	public static final String UNIT_MOVE_RIGHT = "move/moveRight.png";
	public static final String UNIT_MOVE_LEFT = "move/moveLeft.png";
	public static final String UNIT_MOVE_UP = "move/moveUp.png";
	public static final String UNIT_MOVE_DOWN = "move/moveDown.png";
	
	public static final String UNIT_ATTACK_RIGHT = "attack/attackRight.png";
	public static final String UNIT_ATTACK_LEFT = "attack/attackLeft.png";
	public static final String UNIT_ATTACK_UP = "attack/attackUp.png";
	public static final String UNIT_ATTACK_DOWN = "attack/attackDown.png";

	//an array of the paths of unit textures TODO: add in the attack movements
	public static final String[] UNIT_TEX_PATHS = 
		{UNIT_STILL_RIGHT, UNIT_STILL_LEFT, UNIT_MOVE_RIGHT, UNIT_MOVE_LEFT, UNIT_MOVE_UP, UNIT_MOVE_DOWN};
	
	
	
/*--------------GAME BOARD INFO--------------------
 * 
 */
	//for the game grid (board where units go on) 
	public static final float GRIDSIDE = 384;
	public static final float GRID_WIDTH = 384;
	public static final float GRID_HEIGHT = 384;
	public static final float GRID_X = 800/2 - 384/2;
	public static final float GRID_Y = 100;
	public static final float GRID_TILE_WIDTH = 32;
	public static final float GRID_TILE_HEIGHT = 32;
	public static final int COLS = 12;		//columns
	public static final int ROWS = 12;		//rows
	
	public static final String HUMAN = "Human";
	public static final String REPTOID = "Reptoid";
	public static final String ARTHRO = "Arthropodan";

	/*unit information
	 * 
	 */
	//health
	public static final float HEALTH = 4f; //health is out of 4
	public static final int HLTH_W = 12; //this is the width in pixels of the health bar when full
	public static final int HLTH_H = 4; 		//height of health bar
 	
	//dimensions of units
	public static final float SMALL_W = 32; //width of small unit
	public final static float SMALL_H = 32; //height of small
	public static final float MED_W = 64;
	public static final float MED_H = 32;
	public static final float LARGE_W = 64;
	public static final float LARGE_H = 64;
	
	//related to game timer and info panel
	public static final float TIMER_WIDTH = 64; //timer width in pixels
	public static final float TIMER_HEIGHT = 100; //timer height
	public static final float MAX_TIME = 30f; //max game time
	
	//for the info panel below game board
	public static final float INFO_X = GRID_X + TIMER_WIDTH; //
	public static final float INFO_Y = 0;
	public static final float INFO_W = 160; //<--(384-64)/2 
	public static final float INFO_H = 100; //<--height of infopanel
 	
	//for the side panel objects TODO: make these buttons BIGGER
	public static final float SIDE_BUTTON_RADIUS = 50;
	
	public static final float BT1_X = 30f;
	public static final float BT2_X = 800 - (SIDE_BUTTON_RADIUS + 30f); 
	public static final float BT_Y = 250; //screen height/2 (both buttons same height)
	//the go button (might make it bigger)
	public static final float GO_X = 120;
	public static final float GO_Y = 100;
	
	//future possible use
	//public static String[] paths = {};
 	/*
 	 *  Strings related to log/debug
 	 */
	public static final String UNIT_CHOSE = "unit has been chosen";
	public static final String GRID_RESET= "panels have been reset";
	public static final String UNIT_DESELECT = "unit now not chosen"; 
	
	
	/* these are all the 32x32 board positions 
	 * starting from bottom to right, then move up and repeat
	 * - NOTE: these can possibly be used as coordinates units can move to
	 *  
	 */
	public static final double[][] GRID_SCREEN_VECTORS = 																				//column
		{{208.0, 100.0}, {208.0, 132.0}, {208.0, 164.0}, {208.0, 196.0}, {208.0, 228.0}, {208.0, 260.0}, {208.0, 292.0}, {208.0, 324.0},  //1 
		{208.0, 356.0}, {208.0, 388.0},  {208.0, 420.0}, {208.0, 452.0}, 
		{240.0, 100.0}, {240.0, 132.0}, {240.0, 164.0}, {240.0, 196.0}, {240.0, 228.0}, {240.0, 260.0}, {240.0, 292.0}, {240.0, 324.0},  //2
		{240.0, 356.0}, {240.0, 388.0}, {240.0, 420.0}, {240.0, 452.0}, 
		{272.0, 100.0}, {272.0, 132.0}, {272.0, 164.0}, {272.0, 196.0}, {272.0, 228.0}, {272.0, 260.0}, {272.0, 292.0}, {272.0, 324.0}, //3
		{272.0, 356.0}, {272.0, 388.0}, {272.0, 420.0}, {272.0, 452.0},
		{304.0, 100.0}, {304.0, 132.0}, {304.0, 164.0}, {304.0, 196.0}, {304.0, 228.0}, {304.0, 260.0}, {304.0, 292.0}, {304.0, 324.0}, //4
		{304.0, 356.0}, {304.0, 388.0}, {304.0, 420.0}, {304.0, 452.0}, 
		{336.0, 100.0}, {336.0, 132.0}, {336.0, 164.0}, {336.0, 196.0}, {336.0, 228.0}, {336.0, 260.0}, {336.0, 292.0}, {336.0, 324.0}, //5
		{336.0, 356.0}, {336.0, 388.0}, {336.0, 420.0}, {336.0, 452.0}, 
		{368.0, 100.0}, {368.0, 132.0}, {368.0, 164.0}, {368.0, 196.0}, {368.0, 228.0}, {368.0, 260.0}, {368.0, 292.0}, {368.0, 324.0}, //6
		{368.0, 356.0}, {368.0, 388.0}, {368.0, 420.0}, {368.0, 452.0}, 
		{400.0, 100.0}, {400.0, 132.0}, {400.0, 164.0}, {400.0, 196.0}, {400.0, 228.0}, {400.0, 260.0}, {400.0, 292.0}, {400.0, 324.0}, //7
		{400.0, 356.0}, {400.0, 388.0}, {400.0, 420.0}, {400.0, 452.0}, 
		{432.0, 100.0}, {432.0, 132.0}, {432.0, 164.0}, {432.0, 196.0}, {432.0, 228.0}, {432.0, 260.0}, {432.0, 292.0}, {432.0, 324.0}, //8
		{432.0, 356.0}, {432.0, 388.0}, {432.0, 420.0}, {432.0, 452.0}, 
		{464.0, 100.0}, {464.0, 132.0}, {464.0, 164.0}, {464.0, 196.0}, {464.0, 228.0}, {464.0, 260.0}, {464.0, 292.0}, {464.0, 324.0}, //9
		{464.0, 356.0}, {464.0, 388.0}, {464.0, 420.0}, {464.0, 452.0}, 
		{496.0, 100.0}, {496.0, 132.0},{496.0, 164.0}, {496.0, 196.0}, {496.0, 228.0}, {496.0, 260.0}, {496.0, 292.0}, {496.0, 324.0},  //10
		{496.0, 356.0}, {496.0, 388.0}, {496.0, 420.0}, {496.0, 452.0},
		{528.0, 100.0}, {528.0, 132.0}, {528.0, 164.0}, {528.0, 196.0}, {528.0, 228.0}, {528.0, 260.0}, {528.0, 292.0}, {528.0, 324.0},  //11
		{528.0, 356.0}, {528.0, 388.0}, {528.0, 420.0}, {528.0, 452.0}, 
		{560.0, 100.0}, {560.0, 132.0}, {560.0, 164.0}, {560.0, 196.0}, {560.0, 228.0}, {560.0, 260.0}, {560.0, 292.0}, {560.0, 324.0}, //12
		{560.0, 356.0}, {560.0, 388.0}, {560.0, 420.0}, {560.0, 452.0}};
	
	
 
	/*------------Unit movement offsets when on grid------------
	 * 
	 * the offsets represent how much unit can move in certain direction
	 * NOTE: 
	 *   x negatives first, then positives, in order of offset size
	 * 
	 * 
	 * 
	 */
	//if unit can move 1 move
 	public static final int[][] offset1 = {
 		//negative offsets
 		{-1, 0}, 
        {0, -1},

        //positive offsets
        {0, +1},
        {+1, 0}, 

 	};
	
 	public static final int[][] offset2 = {
 		{-2, 0},
 		{-1, 0}, 		
 
  		{-2, -1},
  		{-1, -2},
  		{-1, -1},
 		
 		{0, -2},
 		{+1, -2},
 		{+1, 0},

   		//positive offsets
 		
  		{+2, +1},
 		{+1, +2},
 		{+1, +1},
 		{+2, 0},
 
 	};
 	
 	
 }
