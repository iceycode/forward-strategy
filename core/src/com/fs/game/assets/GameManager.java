/** GameManager
 * - holds gaims main assetmanager
 * - creates a UnitInfo array
 *   - also has method to create damage lists
 *   	damage lists : how much each unit damages all other
 *
 *  TODO: figure out if damageLists should be a seperate JSON
 *
 *
 *
 *  FILES NEEDED:
 *   sky_serpant still pic
 *
 *
 * @author Allen Jagoda
 *
 */
package com.fs.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fs.game.data.GameData;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.UnitUtils;


public class GameManager {
	
	//values held in GameManager can be accessed without new instance
	public static AssetManager assetManager; //asset manager for easy access
	public static Array<UnitInfo> unitInfoArr; //for access to unit info
	public static Array<int[]> damageLists;
	
 
	public static Skin uiSkin; 	//skin for menu/HUD elements
	public static Skin gameSkin;  //skin for units, game board panels, & other game play elements
	public static Skin gameBoardSkin;
	
	/** this returns the asset manager
 	 * TODO: organize, organize, organize
	 * @return
	 */
	public static void initializeAssets(){
		assetManager = new AssetManager();
		
		
		//gets the gamplay skins
		FileHandle handle = Gdx.files.internal(Constants.UNIT_STAT_JSON_PATH);
 		String jsonAsString = handle.readString();
		Array<UnitInfo> arrayUnitInfo = new Array<UnitInfo>(); //for the unitInfoArray
 
		Json json = new Json();		
		UnitInfo unitInfo = new UnitInfo();//create empty object
		json.addClassTag("unitInfo", UnitInfo.class);
		json.toJson(unitInfo, UnitInfo.class);
		json.setIgnoreUnknownFields(true);
		json.setTypeName("UnitInfo");
 
		JsonValue root = new JsonReader().parse(jsonAsString);
 		
		/*
		//GameMananager will hold templates of these scene2d & ui objects
		//GameInfo holds versions of templates updated for current game
		// ie, UnitInfo always holds 7-8 fields, but will vary based on unit
		// Skin always will hold certain elements related to UI, but unit's state/relationships
		 * 	ie. when unit hit, health bar declines, also unit animates attacks
		*/
		for (JsonValue entry = root.child; entry!=null; entry = entry.next) {
			for (int i = 0; i < entry.size; i++) {
				UnitInfo uInfo = json.fromJson(UnitInfo.class, entry.get(i).toString()); //stores stat
 				/*
				 *  this stores the actual number of unit pathways
				 * NOTE: some units don't have certain animations 
				 *   - they are not needed since they cannot move a certain way
				 *   - for example, an AirBase can move any way, so has only 1 move animation
				 *     while a smaller unit has 4 instead of the 1 
				 */
				Array<String> unitTexPaths = new Array<String>(); 
				
 				for (String path : Constants.UNIT_TEX_PATHS){
 					String fullPath = uInfo.getUnitPath()+path;
   					if (Gdx.files.internal(fullPath).exists()){
 						unitTexPaths.add(fullPath);
                        assetManager.load(uInfo.getUnitPath()+path, Texture.class);
  					}
 				}
                float size[] = UnitUtils.convertStringSizeToFloat(uInfo.getSize());
                uInfo.setWidth(size[0]);
                uInfo.setHeight(size[1]);

  				uInfo.setTexPaths(unitTexPaths);
  				arrayUnitInfo.add(uInfo); 
 			}//gets all individual units
		}//maps jsonvalue as array to string, then to object UnitInfo, which is stored in array
		
 		//loading the textures for grids on game board
        assetManager.load(Constants.GRID_DOWN_PATH, Texture.class);
        assetManager.load(Constants.GRID_PATH, Texture.class);

		//creates info about units
		loadUnitDamageLists(arrayUnitInfo); //creates & adds damage lists to current array

        loadAudio(); //loads the audio into asset manager

		//the skins
		uiSkin = createUISkin(); //for menus & HUD (displays info during game play)
		gameSkin = gameSkin();     //for game board


        //GameManager.setAssetManager(assetManager);
 	}


    /** loads all the audio (sound effects, unit sounds, music, etc)
     * - into the asset manager
     */
    public static void loadAudio(){
        assetManager.load(Constants.music1, Music.class);
    }
	
	
	/** returns the damage list as array of integers
	 * 
	 * @return
	 */
	public static void loadUnitDamageLists(Array<UnitInfo> uniInfoArr){
		Array<int[]> damageList = new Array<int[]>();
		
 		FileHandle handle = Gdx.files.internal(Constants.UNIT_DAMAGE_JSON_PATH);
		String jsonAsString = handle.readString();
		JsonValue root = new JsonReader().parse(jsonAsString);
 		
		for (JsonValue entry = root.child; entry!=null; entry = entry.next) {
 			damageList.add(entry.asIntArray());
 		}//returns counter lists as entries

        //adds the dasmage lists to unit info array
        addUnitDamageLists(damageList, uniInfoArr);
        //TODO: THIS IS TEMPORARY SETUP; STILL NEED TO CLEAN THIS UP
        GameData.unitInfoArray = uniInfoArr; //<---stores in the GameData class
        unitInfoArr = uniInfoArr;
        damageLists = damageList;

// 		return damageList;
	}
 
	
	/** adds damageList info to the uniInfoArr UnitInfo objects
	 *  
	 * @param damageLists
	 * @param uniInfoArr
	 * @return
	 */
	public static void addUnitDamageLists(Array<int[]> damageLists, Array<UnitInfo> uniInfoArr) {
		for (int i = 0; i < damageLists.size; i ++) {
			uniInfoArr.get(i).setDamageList(damageLists.get(i));
		}
	}
	
	
	/** the skin for the game baord grid/panel/tile
	 * Also contains damage label for units
     *
	 */
	public static Skin gameSkin(){
		Skin skin = new Skin();
		
		skin.add("panelDown", new Texture(Gdx.files.internal("maps/tiles/gridDown.png")));
 		skin.add("panelUp", new Texture(Gdx.files.internal("maps/tiles/grid.png")));
 		skin.add("panelView", new Texture(Gdx.files.internal("maps/tiles/gridView.png")));

        //for damage label of units
        BitmapFont damageFont = fontGenerator(Constants.FONT_MEGAMAN, 12, Color.RED);
        skin.add("damageFont", damageFont);
        skin.add("dmgTex", new Texture(Gdx.files.internal(Constants.DMG_LABL_TEX)));

 		return skin;
	}
	
	
	/** creates the skins for unit UI
	 * 
	 * @return skin
	 */
	public static Skin createUISkin() {
		Skin skin = new Skin();

		/* font acknoledgments from 
		 * Res: www.pentacom.jp/pentacom/bitfontmaker2/gallery/
		 *  - battlenet by Tom Israels
		 *  - MEGAMAN10 by YahooXD
		 * 
		 */
        //default regular font via libgdx
        BitmapFont small = new BitmapFont();
        small.scale(.1f);
		//adding some truetypefont's as BitmapFont
        BitmapFont retro1 = fontGenerator(Constants.FONT_BATTLENET, 16, Color.GREEN);
 		BitmapFont retro2 = fontGenerator(Constants.FONT_MEGAMAN, 16, Color.RED);

		//populate the skin w/ fonts
        skin.add("default-small", small);
		skin.add("font1", new BitmapFont(Gdx.files.internal(Constants.FONT_DEFAULT1))); //adding custom font (made with BMFont)
		skin.add("retro1", retro1);
		skin.add("retro2", retro2);

        BitmapFont defaultFont = new BitmapFont();
        skin.add("default", defaultFont);
		
		/* All UI textures
		 *
		 */
		skin.add("infoPanDown", new Texture(Gdx.files.internal("infopanel/infoPanelUp.png")));
	    skin.add("enemyUInfo", new Texture(Gdx.files.internal("infopanel/enemyUInfo.png")));
	    skin.add("mainPanel", new Texture(Gdx.files.internal("infopanel/mainpanel.png")));
	    skin.add("infoPane", new Texture(Gdx.files.internal("infopanel/infopanel.png")));
		skin.add("timer", new Texture(Gdx.files.internal("infopanel/timer.png")));
 
		//w/ additional textures added to skin which are programmatically created
		skin.add("detail-popup", new Texture(createPixmap(384/2, 100, Color.GRAY)));
		skin.add("damage-popup", new Texture(createPixmap(384/2, 100, Color.LIGHT_GRAY)));
		skin.add("vert-scroll", new Texture(createPixmap(10, 100, Color.BLACK)));
		
		//w/ timer skin
		skin.add("timer", new Texture(Gdx.files.internal("infopanel/timer.png")));
		
		//textures for side panel buttons
		skin.add("go-tex", new Texture(Gdx.files.internal("sidepanel/go.png")));
		skin.add("stop-tex", new Texture(Gdx.files.internal("sidepanel/stop.png")));
		
		//texture for the advance button
		//w/ some of the widget styles added to skin 
		LabelStyle styleDamage = new LabelStyle();
		styleDamage.background = skin.getDrawable("damage-popup");
		styleDamage.font = skin.getFont("retro1");
		skin.add("labelStyle-damage", styleDamage);
		
		//w/ images added to table which is added to scroll pane
		LabelStyle styleDetail = new LabelStyle();
		styleDetail.background = skin.getDrawable("detail-popup");
		styleDetail.font = skin.getFont("default-small");
		skin.add("labelStyle-detail", styleDetail);

		//some more skins  
		/* NOTE: at the moment some elements are being generated 
		 *  by the TextureUtils class
		 * 
		 */
		// creates an advance (player turn end) button  
 		skin.add("lets-go-tex", new Texture(Gdx.files.internal("infopanel/advanceButton.png")));


        //------assets for menus-------
        loadMenuAssets(skin);


		return skin;
	}


    public static void loadMenuAssets(Skin skin){

        mainMenuAssets(skin);
        factionMenuAssets(skin);
        unitMenuAssets(skin);
        pauseMenuAssets(skin);
        unitMenuAssets(skin);

    }


    public static void mainMenuAssets(Skin skin){
        BitmapFont bfont = new BitmapFont();
        skin.add("default", bfont); //add font to skin

        //store the components of first menu
        skin.add("maps", new Texture(Gdx.files.internal("menu/mapsButton.png")));
        skin.add("settings", new Texture(Gdx.files.internal("menu/settingButton.png")));
        skin.add("factionButton", new Texture(Gdx.files.internal("menu/factionsButton.png")));

        //info panel for units when choosing units
        skin.add("infoPanel", new Texture(Gdx.files.internal("infopanel/mainpanel.png")));
    }



    public static void factionMenuAssets(Skin skin){
        //add faction menu Texture images
        skin.add("humButton", new Texture(Gdx.files.internal("menu/humansButton.png")));
        skin.add("artButton", new Texture(Gdx.files.internal("menu/artButton.png")));
        skin.add("repButton", new Texture(Gdx.files.internal("menu/repButton.png")));
    }



    public static void unitMenuAssets(Skin skin){
        /*** populates the skin with panel textures**
         *
         */
        //the Textures
        skin.add("infoPanel", new Texture(Gdx.files.internal("menu/units/infoUnits.png")));
        skin.add("smallPanel", new Texture(Gdx.files.internal("menu/units/smallUnits.png")));
        skin.add("medPanel", new Texture(Gdx.files.internal("menu/units/medUnits.png")));
        skin.add("largePanel", new Texture(Gdx.files.internal("menu/units/largeUnits.png")));
        skin.add("backBtn", new Texture(Gdx.files.internal("menu/backButton.png")));
        skin.add("dialog1", new Texture(Gdx.files.internal("menu/dialogBox.png")));
        skin.add("confirmBkgrnd", new Texture(Gdx.files.internal("menu/confirm1.png")));
        skin.add("counter", new Texture(Gdx.files.internal("menu/counterBox.png")));
        skin.add("checkS", new Texture(Gdx.files.internal("menu/checkedUniSmall.png")));
        skin.add("checkM", new Texture(Gdx.files.internal("menu/checkedUniMed.png")));
        skin.add("checkL", new Texture(Gdx.files.internal("menu/checkedUniLarge.png")));



        //simple label style for unit types in menu
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = skin.getFont("retro2");
        labelStyle.fontColor = Color.OLIVE;
        skin.add("unitText", labelStyle);


        TextButton.TextButtonStyle textStyle = new TextButton.TextButtonStyle();
        textStyle.font = skin.getFont("default");

        Window.WindowStyle confirmStyle = new Window.WindowStyle();
        confirmStyle.background = skin.getDrawable("confirmBkgrnd");
        confirmStyle.titleFont = skin.getFont("default");
        confirmStyle.titleFontColor = Color.BLACK;
        skin.add("dialog", confirmStyle);		//added confirm style 1


    }


    public static void pauseMenuAssets(Skin skin){
        skin.add("pause-background", new Texture(Gdx.files.internal("menu/pause menu/pauseMenu-background.png")));
        skin.add("pause-music-slider", new Texture(Gdx.files.internal("menu/pause menu/pauseMenu-music-slider.png")));
        skin.add("pause-sounds-slider", new Texture(Gdx.files.internal("menu/pause menu/pauseMenu-sounds-slider.png")));
        skin.add("pause-slider-knob", new Texture(Gdx.files.internal("menu/pause menu/pauseMenu-sound-knob.png")));
    }






    /** TODO: get user preferences, score & other info here
     *
     * @return
     */
    public static void createPrefs() {

    }


    //------GameManager helper methods----------
    public static BitmapFont fontGenerator(String fontPath, int size, Color color){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;

        //1st retro font
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        font.setColor(color);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        return font;
    }


    public static Pixmap createPixmap(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // pixmap.drawRectangle(200, 200, width, height);
        pixmap.setColor(color);
        pixmap.fill(); // fill with the color

        return pixmap;
    }








    /**
	 * @return the assetManager
	 */
	public static AssetManager getAssetManager() {
		return assetManager;
	}


}
