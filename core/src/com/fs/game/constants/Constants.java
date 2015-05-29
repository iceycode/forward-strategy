/**
 * @author Allen Jagoda
 */
package com.fs.game.constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * @author Allen
 *
 */
public class Constants {
//------------GAME/SCREEN-----------
	public static final float SCREENHEIGHT = 500;
	public static final float SCREENWIDTH = 800;



//-----------FILE PATHS--------------
    public static final String SAVE = "saves/save.json";
    public static final String SAVE_ENCODED = "saves/SAVE_ENCODED.json";
    //names of settings (aka Preferences in LibGDX)
    public static final String SETTINGS_Desktop = "settings1";
    public static final String SETTINGS_Android = "settings2";
    public static final String SETTINGS_Web = "settings3";
    public static final String SETTINGS_iOS = "settings4";

    //----DIRECTORIES for assets----
    //absolute directory
    public static final String ABSOLUTE_ASSETS_DIR = "/Users/Allen/MEGA/Workspaces/game workspace/forwardstrategy/android/assets/";
    public static final String TEXTURE_DIR = "textures/"; //texture output directory
    public static final String DEFAULT_TEX_DIR = TEXTURE_DIR + "default/"; //default textures go in here
    public static final String TEST_ASSET_DIR = "test_assets/";

    public static final String PANELS_32X32 = "maps/tilesets/Tileset-Panels_32x32.png";

    //----SKINS----
    public static final String TEST_SKIN_GDX = TEST_ASSET_DIR + "data/uiskin.json";
    public static final String DARK_SKIN = "skins/dark/darkSkin.json"; //dark skin - taken from my EJuice Toolkit App

//---------------Skin screen dimensions, positions, messages-----------------//

    public static final String BACK_TO_PREVIOUS = "BACK";
    public static final float[] BACK_TEX_POS = {5f, 5f};
    public static final float[] BACK_SIZE = {70f, 50f};

    //-----------start screen-----------------//
    public static final String WELCOME = "Forward Strategy \n click anywhere to begin";

    //==============Test Names & Messages===========
    //names of tests
    public static final String[] TEST_NAMES = {"Select Test", "2 Units on Board", "Humans vs Arthroid Setup",
            "Multiplayer Setup", "40x30 Grid"};

    //==============Messages for MainScreen===========
    public static final String[] MAIN_MSGS = {"Click here to get run Tests", "UNDER CONSTRUCTION...",
            "UNDER CONSTRUCTION...", "UNDER CONSTRUCTION..."};


    /**===========Game Rules==========
     * Text, positions, sizes
     */
    public static final String TO_RULES = "Game Rules";

//    public static final float TEST_TEX_WIDTH = 325f;
//    public static final float TEST_TEX_HEIGHT = 60f;
    public static final float[][] MAIN_TEX_POSITIONS = {{15f, 220f}, {460f, 220f},
            {15f, 75f}, {460f, 75f }}; //x padding from side of screen = 15f
    public static final float[] RULE_TEX_POS = {350f, 150f};


    //----------Rules -----------------//
    public static final float[] RULES_POS = {100f, 100f};
    public static final String RULES = "GAME RULES\n" +
            "-----------" +
            "\nGAMEPLAY\n " +
            " Each player moves units on their side of the board.\n" +
            " - the circles on the left & right side represent players\n" +
            " - if it is green, it is your turn\n" +
            " Score boards keep track of score & are on side of player\n" +
            " - every time a unit is killed, player gets 10 points (this will probably change)\n" +
            "\nMOVING:\n" +
            " - clicking on unit will highlight panels it can move to on board\n" +
            " - clicking on a highlighted panel will move chosen unit to it \n" +
            "\nATTACKING:\n" +
            " - enemy units will have a number over them indicating damage\n " +
            "that the chosen unit can inflict damage\n" +
            " - whenever a unit is adjacent to an enemy unit, it attacks\n" +
            " - each unit can only attack after they move & only once a turn\n";
    public static final float[] RULES_SIZE = {800f, 900f};
    public static final float[] RULES_SCROLL_SIZE = {600f, 400f};


    /** =============Pause Game message==============
     *
     */
    public static final String PAUSE_GAME = "PAUSE";
    public static final String RESUMING_GAME = "RESUMING GAME IN "; //resume game message


    //-----------multiplayer connection/game status---------------//
    public static final String TRY_CONNECT_MSG = "Connecting\n to AppWarp";
    public static final String WAIT_MSG = "Waiting for\n other user";
    public static final String ERROR_CONNECT_MSG = "Error in \n Connection \n Go Back";
    public static final String GAME_WIN_MSG = "Congrats You Win!\nEnemy Defeated";
    public static final String GAME_LOSE_MSG = "You Lose!\nEnemy Won";
    public static final String PLAYER_LEFT_MSG = "Congrats You Win!\nEnemy Left the Game\nLeaving in ";



	//this text is used for debugging and log entries
	public static final String LOG_MAIN = "LOG LevelScreen : ";
	public static String LOG_UNIT_UTILS = "Unit Utils LOG: ";
    public static final String UNIT_CHOSEN = "unit has been chosen";
    public static final String UNIT_DESELECT = "unit now not chosen";



/*-------------STAGE/MAPS----------------
 * 
 */
    public static final int GRID_ROWS = 12;
    public static final int GRID_COLS = 16;

    public static final int MAP_ROWS_L = 30;
    public static final int MAP_COLS_L = 40;

    public static final int[] TILE_SIZE_S = {32, 32};
    public static final int[] TILE_SIZE_M = {64, 64};
    public static final float MAP_VIEW_WIDTH = 512;
    public static final float MAP_VIEW_HEIGHT = 384;
    public static final float MAP_X = SCREENWIDTH/2 - MAP_VIEW_WIDTH/2; //NOTE: change these around to fit MiniMap
    public static final float MAP_Y = 100f;
    public static final float[] MAP_BTM_LEFT = {MAP_X, MAP_Y};
    public static final float[] MAP_TOP_RIGHT = {MAP_X + MAP_VIEW_WIDTH, MAP_Y + MAP_VIEW_HEIGHT};

    //----Minimap
    public static final float MM_X = MAP_X + MAP_VIEW_WIDTH + 1; //1 px offset
    public static final float MM_Y = MAP_Y;
    public static final float MM_WIDTH = MAP_VIEW_WIDTH/10;
    public static final float MM_HEIGHT = MAP_VIEW_HEIGHT/10;




    //---------Map Paths--------

    public final static String MAP_1 = "maps/justGrass.tmx";
    public final static String MAP_2 = "maps/map2.tmx";
    public final static String MAP_3 = "maps/map3.tmx";
    public final static String MAP_3A = "maps/mapTemplate3a.tmx"; //<------16x12-------
	public final static String MAP_3B = "maps/mapTemplate3b.tmx";
    public final static String TEST_MAP1 = "maps/testMap1.tmx";

    //------40x30, 32x32-------
    public final static String TEST_MAP_40x30_1 = "maps/Basic1_40x30.tmx";


	
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

    public static final String DMG_LABL_TEX = "units/dmgTextBkgrnd.png";

    //------------------FONTS-----------------------
    public static final String FONT_DEFAULT1 = "fonts/default1.fnt";
    public static final String FONT_BATTLENET = "fonts/battlenet.ttf";
    public static final String FONT_MEGAMAN = "fonts/MEGAMAN10.ttf";
    public static final String FONT_BATTLENET_FNT1 = "fonts/battlenet_FNT1.fnt";
    public static final String FONT_MEGAMAN_FNT1 = "fonts/megaman10_FNT1.fnt";


    /**BEHAVIOR TREE PATH - used by {@link com.fs.game.ai.AgentManager}
     *  for helping adjust UnitAgent decisions during game
     */
    public static final String BEHAVIOR_TREE = "ai/unitAgent.tree"; //text format of behavior
    public static final String AI_MEMORY_JSON = "ai/memory.json"; //stores ai "memories"

    //------MENU SKIN------
    public static final String MENU_SKIN = "menu/menuSkin.json";


    public static final String[] FACTION_LIST = {"Human", "Arthroid", "Reptoid"};
	
/*---------------UNIT SETUP INFO------------------------
 * 
 * 
 */

    //-------------FACTIONS-------------
    public static final String ARTHROID = "Arthroid";
    public static final String HUMAN = "Human";
    public static final String REPTOID = "Reptoid";

    //----------UNIT IMAGE PATH MODIFIERS--------
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

    public static final String ATTACK_FRAME_SMALL = "units/misc/attack_frame_32x32.png";
    public static final String ATTACK_FRAME_MED = "units/misc/attack_frame_64x32.png";
    public static final String ATTACK_FRAME_LARGE = "units/misc/attack_frame_64x64.png";

	//an array of the paths of unit textures
	public static final String[] UNIT_TEX_PATHS = 
		{UNIT_STILL_RIGHT, UNIT_STILL_LEFT, UNIT_MOVE_RIGHT, UNIT_MOVE_LEFT, UNIT_MOVE_UP, UNIT_MOVE_DOWN, UNIT_ATTACK_RIGHT,
        UNIT_ATTACK_LEFT, UNIT_ATTACK_UP, UNIT_ATTACK_DOWN};

    //positions for small, medium, large units IN ORDER of appearance in unit info list

    public static final float UNIT_POS_X_LEFT = 144f;
    public static final float UNIT_POS_X_RIGHT = 624f;
    public static final float[][] UNITS_POS_LEFT = {{144f, 452f}, {144f, 420f}, {144f, 100f}, {144f, 132f}, {144f, 388f},
            {144f, 164f}, {144f, 228f}};
    public static final float[][] UNITS_POS_RIGHT = {{624f, 452f}, {624f, 420f}, {624f, 100f}, {624f, 132f}, {592f, 388f},
            {592f, 164f}, {592f, 228f}};


    //IDENTIFIERS for small, med & large untis in arrays (NOT UNIT IDs)
    //add 10 to get Reptoids, 20 to get Arthroid
    public static Integer[] I_UNITS_ALL = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static int[] I_UNITS_SMALL = {0, 1, 2, 3, 4};
    public static int[] I_UNITS_MED = {5, 6, 7};
    public static int[] I_UNITS_LARGE = {8, 9};

    //0 = Locked; 1 = Standing, 2 = Chosen, 3 = Moving, 4 = Done (units moves)
    // 5 = Attacked/Locked, 6 = Dead;
    public static int[] UNIT_STATES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};


/*------------MENU parameters/attributes--------------
 */

    public static final String[] UNIT_IMAGE_LABELS = {"Small Units","Medium Units", "Large Units"};
    public static final String UNIT_IMAGE_LABELSTYLE = "unitText";

    //UNIT selection menu
    //some calculations
    public static final float UNITS_TABLE_X = Constants.SCREENWIDTH/4 - 5*32+25/2;
    public static final float UNITS_TABLE_Y = Constants.SCREENHEIGHT - 300f;
    public static final float UNITS_TABLE_W = 280;
    public static final float UNITS_TABLE_H = 64+4+64+4+64; //4 pixel padding
    public static final float UNIT_ROSTER_X = SCREENWIDTH - 100f; //10 pixels from end of screen
    public static final float UNIT_ROSTER_Y = SCREENHEIGHT - 400f;
    public static final float UNIT_ROSTER_H = 32*6 + 64*2 + 32; //32 pixels of padding total
    public static final float UNIT_ROSTER_W = 74f; //width of unit roster
    public static final float[][] UNIT_LABEL_POS = {{52f, 435f}, {52f, 390f}, {52f, 310f}};
    public static final float[][] UNIT_IMAGE_POS = {{52f, 400f}, {90f, 400f}, {132f, 400f}, {172f, 400f}, {212f, 400f},
                                                    {52f, 320f}, {124f, 320f}, {196f, 320f},
                                                    {52f, 240f}, {124f, 240f}};

	
/*--------------GAME HUD INFO--------------------
 * 
 */
    //text for when player goes
    public static final float[] TURN_MSG_COORD = {Constants.SCREENWIDTH / 4, Constants.SCREENHEIGHT / 2};

	/* unit information
	 * 
	 */
	//health
	public static final float HEALTH = 4f; //health is out of 4
	public static final int HLTH_W = 12; //this is the width in pixels of the health bar when full
	public static final int HLTH_H = 4;  //height of health bar

	//related to game timer and info panel
	public static final float TIMER_WIDTH = 64; //timer width in pixels
	public static final float TIMER_HEIGHT = 100; //timer height
	public static final float MAX_TIME = 15f; //max game time
	
	//for the info panel below game board
	public static final float INFO_X = MAP_X + TIMER_WIDTH; //
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
	


/**---------------PAUSE MENU---------------
 *
 */
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



/**--------------AUDIO File Paths------------
 */
    public static final String music1 = "audio/music/FS-music1.mp3";



/*----------APP WARP Game Info (Multiplayer)----------*/

    public static class App42 {

        public static final String API_KEY = "ae7493bafe3ef380323eb41c1032f62b5ddd0b940017648c62a7ea183471c408";
        public static final String SECRET_KEY = "55e85dd41157782e533246712d06b7913c3d56c2eec0d615f19c6ec08f942e66";
        public static final String ROOM_A = "506734404"; //ADMIN room
        public static final int[] UPDATE_STATES = {0, 1, 2, 3, 4, 5};
    }



/*-----------SAVE JSON FILE Names---------------*/
    public static FileHandle userDataFile = Gdx.files.local("data/userData.json");
    public static FileHandle userProfile = Gdx.files.internal("data/userProject.json");



 }
