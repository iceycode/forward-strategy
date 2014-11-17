package com.fs.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fs.game.assets.Constants;
import com.fs.game.assets.GameManager;
import com.fs.game.enums.GameState;
import com.fs.game.menus.FactionScreen;
import com.fs.game.menus.MapScreen;
import com.fs.game.menus.UnitsScreen;
import com.fs.game.screens.MainScreen;
import com.fs.game.screens.MenuScreen;

public class MainGame extends Game{
	
	//the main game batch & font
	public SpriteBatch batch;
	public BitmapFont font;
    public TextureRegion splashTitle; //the splash screen title
    public AssetManager manager; //manages assets game
 	
	/*All the screens that will be used*/
	public MainScreen mainScreen;
    public MenuScreen menuScreen;



    protected GameState gameState;
    public FactionScreen factionScreen;
    public MapScreen mapScreen;
    public UnitsScreen unitsScreen;

 
	@Override
	public void create() {
        GameManager.initializeAssets();
		manager = GameManager.assetManager;
 		
		//the splash screen while assets load
 		splashTitle = new TextureRegion(new Texture(Gdx.files.internal(Constants.TITLE_PATH)));
		//creates spritebatch
        batch = new SpriteBatch();
        font = new BitmapFont(); //libgdx default is Arial font.



    }

    public void setupScreens(){
        mainScreen = new MainScreen(this);
        menuScreen = new MenuScreen(this);
        factionScreen = new FactionScreen(this);

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
    	else{
            setupScreens();
            this.setScreen(mainScreen);


        }
    	
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
	public AssetManager getManager() {
		return manager;
	}

	/**
	 * @param manager1 the manager1 to set
	 */
	public void setManager(AssetManager manager1) {
		this.manager = manager1;
	}
 

	/**
	 * @return the main
	 */
	public MainScreen getMainScreen() {
		return mainScreen;
	}


    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }



    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public void setMenuScreen(MenuScreen menuScreen) {
        this.menuScreen = menuScreen;
    }

    public FactionScreen getFactionScreen() {
        return factionScreen;
    }

    public void setFactionScreen(FactionScreen factionScreen) {
        this.factionScreen = factionScreen;
    }

}
