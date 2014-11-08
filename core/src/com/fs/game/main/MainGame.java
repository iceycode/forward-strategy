package com.fs.game.main;

import com.badlogic.gdx.Game; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fs.game.menus.FactionScreen;
import com.fs.game.menus.MapScreen;
import com.fs.game.menus.MenuScreen;
import com.fs.game.screens.MainScreen;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;

public class MainGame extends Game{
	
	//the main game batch & font
	public SpriteBatch batch;
	public BitmapFont font;
	
 	public AssetManager manager; //manages assets game
 	
	/*All the screens that will be used*/
	public MainScreen mainScreen;
	public MenuScreen menuScreen;
	public MapScreen mapScreen;
	public FactionScreen factionScreen;
  	
	public TextureRegion splashTitle; //the splash screen title
 
	int player = 0; //stores player turn num
	String faction = ""; //stores the player faction
 
	@Override
	public void create() {
        GameManager.initializeAssets();
		manager = GameManager.assetManager;
 		
		//the splash screen while assets load
 		splashTitle = new TextureRegion(new Texture(Gdx.files.internal(Constants.TITLE_PATH)));
 		
 		mainScreen = new MainScreen(this);
 		menuScreen = new MenuScreen(this);
        factionScreen = new FactionScreen(this);
  		
		//creates spritebatch
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
    }

    @Override
	public void render() {

    	//this creates a splash screen while assets load
    	batch.begin();
    	batch.draw(splashTitle, 0, 0, splashTitle.getRegionWidth(), splashTitle.getRegionHeight());
    	batch.end();

    	//until manager updates, above splash shown
    	//when manager finished, sets main screen
    	if (manager.update()) {
            super.render(); //important!
    	}
    	else
            this.setScreen(mainScreen);
    	
    	float progress = manager.getProgress();
    	Gdx.app.debug("output", "current AM progress" + progress);
    	
    	
    }

    @Override
	public void dispose() {
        batch.dispose();
        font.dispose();
    }
 
	/**
	 * @return the manager1
	 */
	public AssetManager getManager1() {
		return manager;
	}

	/**
	 * @param manager1 the manager1 to set
	 */
	public void setManager1(AssetManager manager1) {
		this.manager = manager1;
	}
 

	/**
	 * @return the main
	 */
	public MainScreen getMainScreen() {
		return mainScreen;
	}

	/**
	 * @param main the main to set
	 */
	public void setMainScreen(MainScreen mainScreen) {
		this.mainScreen = mainScreen;
	}

	/**
	 * @return the menuScreen
	 */
	public MenuScreen getMenuScreen() {
		return menuScreen;
	}

	/**
	 * @param menuScreen the menuScreen to set
	 */
	public void setMenuScreen(MenuScreen menuScreen) {
		this.menuScreen = menuScreen;
	}

	/**
	 * @return the mapScreen
	 */
	public MapScreen getMapScreen() {
		return mapScreen;
	}

	/**
	 * @param mapScreen the mapScreen to set
	 */
	public void setMapScreen(MapScreen mapScreen) {
		this.mapScreen = mapScreen;
	}

	/**
	 * @return the factionScreen
	 */
	public FactionScreen getFactionScreen() {
		return factionScreen;
	}

	/**
	 * @param factionScreen the factionScreen to set
	 */
	public void setFactionScreen(FactionScreen factionScreen) {
		this.factionScreen = factionScreen;
	}
 
	/**
	 * @return the player
	 */
	public int getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(int player) {
		this.player = player;
	}

	/**
	 * @return the faction
	 */
	public String getFaction() {
		return faction;
	}

	/**
	 * @param faction the faction to set
	 */
	public void setFaction(String faction) {
		this.faction = faction;
	}
	

}
