/**
 * @author Allen Jagoda
 */
package com.fs.game.assets;

/**
 * @author Allen
 *
 */
public class Constants { 
	//stage constants
	public static final float SCREENHEIGHT = 500;
	public static final float SCREENWIDTH = 800;
	
	
	public static final int ROWS = 12;
	public static final int COLS = 16;
	public static final float GRID_WIDTH = 512;
	public static final float GRID_HEIGHT = 384;
    public static final float GRID_X = 800/2 - 512/2;
    public static final float GRID_Y = 100;
	public static final float GAMEBOARD_X = 144f;
	public static final float GAMEBOARD_Y = 100f;
    //public static final float GRID_WIDTH = 384;
    //public static final float GRID_HEIGHT = 384;
//    public static final float GRID_TILE_WIDTH = 32;
//    public static final float GRID_TILE_HEIGHT = 32;
	
	//this text is used for debugging and log entries
	public static final String LOG_MAIN = "LOG LevelScreen : ";
	public static String LOG_UNIT_UTILS = "Unit Utils LOG: ";
    public static final String UNIT_CHOSEN = "unit has been chosen";
    public static final String UNIT_DESELECT = "unit now not chosen";
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
    public final static String MAP_1 = "maps/justGrass.tmx";
    public final static String MAP_2 = "maps/map2.tmx";
    public final static String MAP_3 = "maps/map3.tmx";
    public final static String MAP_3A = "maps/mapTemplate3a.tmx";
	public final static String MAP_3B = "maps/mapTemplate3b.tmx";

    //test maps
    public final static String TEST_MAP1 = "maps/testMap1.tmx";
	
/*---------------Image pathways in assets folder------
 * assets folder in android/assets
 * 
 * 
 */
	public static final String TITLE_PATH = "title/forwardStrategyTitle1.png";
	
	public static final String UNIT_STAT_JSON_PATH = "units/unitStatsV3.json";
	public static final String UNIT_DAMAGE_JSON_PATH = "units/damageColArr.json";
	public static final String GRID_DOWN_PATH = "maps/tiles/gridDown.png";
	public static final String GRID_PATH = "maps/tiles/grid.png";
	public static final String GRID_VIEW = "maps/tiles/gridView.png";

    public static final String DMG_LABL_TEX = "units/dmgTextBkgrnd.png";
	
//	public static final String UNIT_MOVE_PATH = "move/move.png";
//	public static final String UNIT_STILL_PATH = "still_pic.png";
    public static final String FONT_DEFAULT1 = "fonts/default1.fnt";
    public static final String FONT_BATTLENET = "fonts/battlenet.ttf";
    public static final String FONT_MEGAMAN = "fonts/MEGAMAN10.ttf";


    public static final String[] FACTION_LIST = {"Human", "Arthroid", "Reptoid"};
	
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

	//an array of the paths of unit textures
	public static final String[] UNIT_TEX_PATHS = 
		{UNIT_STILL_RIGHT, UNIT_STILL_LEFT, UNIT_MOVE_RIGHT, UNIT_MOVE_LEFT, UNIT_MOVE_UP, UNIT_MOVE_DOWN, UNIT_ATTACK_RIGHT,
        UNIT_ATTACK_LEFT, UNIT_ATTACK_UP, UNIT_ATTACK_DOWN};



/*------------MENU parameters/attributes--------------
 */

    public static final String[] UNIT_IMAGE_LABELS = {"Small Units","Medium Units", "Large Units"};
    public static final String UNIT_IMAGE_LABELSTYLE = "unitText";

    //UNIT selection menu
    //some calculations
    public static final float UNITS_TABLE_X = Constants.SCREENWIDTH/2 - 5*32+25/2;
    public static final float UNITS_TABLE_Y = Constants.SCREENHEIGHT - 50f;

	
/*--------------GAME HUD INFO--------------------
 * 
 */


	/*unit information
	 * 
	 */
	//health
	public static final float HEALTH = 4f; //health is out of 4
	public static final int HLTH_W = 12; //this is the width in pixels of the health bar when full
	public static final int HLTH_H = 4;  //height of health bar

	//related to game timer and info panel
	public static final float TIMER_WIDTH = 64; //timer width in pixels
	public static final float TIMER_HEIGHT = 100; //timer height
	public static final float MAX_TIME = 30f; //max game time
	
	//for the info panel below game board
	public static final float INFO_X = GAMEBOARD_X + TIMER_WIDTH; //
	public static final float INFO_Y = 0;
	public static final float INFO_W = 224f; //<--(512-64)/2 
	public static final float INFO_H = 100f; //<--height of infopanel
 	
	//for the side panel objects TODO: make these buttons BIGGER
	public static final float SIDE_BUTTON_RADIUS = 50;
	
	public static final float BT1_X = 15f;
	public static final float BT2_X = 800 - (SIDE_BUTTON_RADIUS + 15f); 
	public static final float BT_Y = 250; //screen height/2 (both buttons same height)
	//the go button (might make it bigger)
	public static final float GO_X = 120;
    public static final float GO_Y = 100;
	

//------------PAUSE MENU DATA---------------
    public static final float PAUSE_X = 200f;
    public static final float PAUSE_Y = 100f;
    public static final float PAUSE_WIDTH = 384f;
    public static final float PAUSE_HEIGHT = 256f;
	
	/* these are all the 32x32 board positions 
	 * starting from bottom to right, then move up and repeat
	 * - NOTE: these can possibly be used as coordinates units can move to
	 *  
	 */
	public static final double[][] GRID_SCREEN_VECTORS = {{}};
	
	

 	


/*--------------AUDIO File Paths------------
 */
    public static final String music1 = "audio/music/FS-music1.mp3";





/*----------APP WARP Game Info (Multiplayer)----------*/
    public static final String API_KEY = "ae7493bafe3ef380323eb41c1032f62b5ddd0b940017648c62a7ea183471c408";
    public static final String SECRET_KEY = "55e85dd41157782e533246712d06b7913c3d56c2eec0d615f19c6ec08f942e66";
    public static final String ROOM_A = "506734404"; //ADMIN room

 }
