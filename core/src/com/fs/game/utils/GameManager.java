/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.fs.game.units.UnitInfo;
import com.fs.game.unused_old_classes.TextureUtils;

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
public class GameManager {
	
	//values held in GameManager can be accessed without new instance
	public static AssetManager am; //asset manager for easy access
	public static Array<UnitInfo> unitInfoArr; //for access to unit info
	public static Array<int[]> damageLists;
	
 
	public static Skin uiSkin; 	//skins for other elements
	public static Skin gameSkin;  //skin for units, game board panels, & other game play elements
	public static Skin gameBoardSkin;
	
	/** this returns the asset manager
 	 * 
	 * @return
	 */
	public static AssetManager createAssetManager(){
		AssetManager am = new AssetManager();
		
		
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
 		
		/*TODO: load static classes, Skin, UnitInfo, Label, Window, etc into assetmanager
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
				 *
				 *
				 */
				Array<String> unitTexPaths = new Array<String>(); 
				
 				for (String path : Constants.UNIT_TEX_PATHS){
 					String fullPath = uInfo.getUnitPath()+path;
   					if (Gdx.files.internal(fullPath).exists()){
 						unitTexPaths.add(fullPath);
  						am.load(uInfo.getUnitPath()+path, Texture.class);
  					}
 				}
 				
  				uInfo.setTexPaths(unitTexPaths);
  				arrayUnitInfo.add(uInfo); 
 			}//gets all individual units
		}//maps jsonvalue as array to string, then to object UnitInfo, which is stored in array
		
 		//loading the textures for grids on game board
		am.load(Constants.GRID_DOWN_PATH, Texture.class);
		am.load(Constants.GRID_PATH, Texture.class);
		
		GameManager.setAm(am);
 		
		//creates info about units
		damageLists = createDamageList(); //creates & adds damage lists to current array
		unitInfoArr = addDamageInfo(damageLists, arrayUnitInfo); //adds that info the array list;

		//the skin for the HUD
		uiSkin = infoPanelSkin();
		gameSkin = panelSkin();
 
 		return am;
	}
 	
	
	/** takes in an object and adds to the assetmanager
	 * 
	 * @param obj
	 */
	public static void updateManager(Object obj) {
		if (obj instanceof LabelStyle) {
			
		}
	}
	
	
	/** returns the damage list as array of integers
	 * 
	 * @return
	 */
	public static Array<int[]> createDamageList(){
		Array<int[]> damageList = new Array<int[]>();
		
 		FileHandle handle = Gdx.files.internal("units/damageColArr.json");
		String jsonAsString = handle.readString();
 
		JsonValue root = new JsonReader().parse(jsonAsString);
 		
		for (JsonValue entry = root.child; entry!=null; entry = entry.next) {
 			damageList.add(entry.asIntArray());
 		}//returns counter lists as entries

 		return damageList;
	}
 
	
	/** adds damageList info to the uniInfoArr UnitInfo objects
	 *  
	 * @param damageLists
	 * @param uniInfoArr
	 * @return
	 */
	public static Array<UnitInfo> addDamageInfo(Array<int[]> damageLists, Array<UnitInfo> uniInfoArr) {
		for (int i = 0; i < damageLists.size; i ++) {
			uniInfoArr.get(i).setDamageList(damageLists.get(i));
		}
		
		return uniInfoArr;
	}
	
	
	/** the skin for the game baord grid/panel/tile
	 * 
	 */
	public static Skin panelSkin(){
		Skin skin = new Skin();
		
		skin.add("panelDown", new Texture(Gdx.files.internal("maps/tiles/gridDown.png")));
 		skin.add("panelUp", new Texture(Gdx.files.internal("maps/tiles/grid.png")));
 		skin.add("panelView", new Texture(Gdx.files.internal("maps/tiles/gridView.png")));

  		//am.load("gameSkin", Skin.class);
 		
 		return skin;
	}
	
	
	/** creates the skins for unit UI
	 * 
	 * @return skin
	 */
	public static Skin infoPanelSkin() {
		Skin skin = new Skin();
		
		//the font scalled down
		BitmapFont small = new BitmapFont();
		small.scale(.1f);
		skin.add("default-small", small);
		
		/* font acknoledgments from 
		 * Res: www.pentacom.jp/pentacom/bitfontmaker2/gallery/
		 *  - battlenet by Tom Israels
		 *  - MEGAMAN10 by YahooXD
		 * 
		 */
		//adding some truetypefont's as BitmapFont
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/battlenet.ttf"));
 		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
 		parameter.size = 16;
		
 		//1st retro font 
 		BitmapFont retro1 = generator.generateFont(parameter); // font size 12 pixels
 		retro1.setColor(Color.GREEN);
 		
 		//2nd retro font 
 		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/MEGAMAN10.ttf"));
 		parameter = new FreeTypeFontParameter();
 		parameter.size = 16;
 		
 		BitmapFont retro2 = generator.generateFont(parameter);
 		retro2.setColor(Color.RED);
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
 
 
		//populate the skin
		// fonts
		skin.add("font1", new BitmapFont(Gdx.files.internal("fonts/default1.fnt"))); //adding custom font (made with BMFont)
		skin.add("retro1", retro1);
		skin.add("retro2", retro2);
		
		/* All UI textures
		 * TODO: create a uiSkin.json 
		 */
		skin.add("infoPanDown", new Texture(Gdx.files.internal("infopanel/infoPanelUp.png")));
	    skin.add("enemyUInfo", new Texture(Gdx.files.internal("infopanel/enemyUInfo.png")));
	    skin.add("mainPanel", new Texture(Gdx.files.internal("infopanel/mainpanel.png")));
	    skin.add("infoPane", new Texture(Gdx.files.internal("infopanel/infopanel.png")));
		skin.add("timer", new Texture(Gdx.files.internal("infopanel/timer.png")));
 
		//w/ additional textures added to skin which are programmatically created
		skin.add("detail-popup", new Texture(TextureUtils.createPixmap(384/2, 100, Color.GRAY)));
		skin.add("damage-popup", new Texture(TextureUtils.createPixmap(384/2, 100, Color.LIGHT_GRAY)));
		skin.add("vert-scroll", new Texture(TextureUtils.createPixmap(10, 100, Color.BLACK)));
		
		//w/ timer skin
		skin.add("timer", new Texture(Gdx.files.internal("infopanel/timer.png")));
		
		//textures for side panel buttons
		skin.add("go-tex", new Texture(Gdx.files.internal("sidepanel/go.png")));
		skin.add("stop-tex", new Texture(Gdx.files.internal("sidepanel/stop.png")));
		
		//texture for the advance button
		//TODO: organize these
		
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
 
		/* set GameManger uiSkin */
		GameManager.uiSkin = skin; //sets this manager to hold this skin
		
		return skin;
	} 
 
	
	public static Skin animSkin(){
		Skin skin = new Skin();
		
		//skin.add("exoMove", Gdx.files.internal("units/arthropodan/32x32/exoguardMove1.png"));
		
		return skin;
	}
	
	/** TODO: get user preferences, score & other info here
	 * 
	 * @return
	 */
	public static void createPrefs() {
		
	}
 
	
	/**
	 * @return the am
	 */
	public static AssetManager getAm() {
		return am;
	}

	/**
	 * @param am the am to set
	 */
	public static void setAm(AssetManager am) {
		GameManager.am = am;
	}

}
