package com.fs.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.screens.InfoScreen;
import com.fs.game.screens.MainScreen;
import com.fs.game.screens.menus.FactionScreen;
import com.fs.game.screens.menus.MapScreen;
import com.fs.game.screens.menus.MenuScreen;

public class MainGame extends Game{

	//the main game batch & font
	public SpriteBatch batch;
	public BitmapFont font;
    public TextureRegion splashTitle; //the splash screen title
    public AssetManager manager; //manages assets game
 	
	/*All the screens that will be used*/
	public MainScreen mainScreen;
    public InfoScreen infoScreen;
    public MenuScreen menuScreen;

    public FactionScreen factionScreen;
    public MapScreen mapScreen;
    public boolean screenSet;
 
	@Override
	public void create() {

        Assets.loadAssets();
        GameData.initGameData();
        manager = Assets.assetManager;

		//the splash screen while assets load
 		splashTitle = new TextureRegion(new Texture(Gdx.files.internal(Constants.TITLE_PATH)));

        screenSet = false; //whether or not the screen is set
		//creates spritebatch for game
        batch = new SpriteBatch();
        font = new BitmapFont(); //libgdx default is Arial font.

    }

    public void setupScreens(){
        setMainScreen(new MainScreen(this));
        setMenuScreen(new MenuScreen(this));
        setFactionScreen(new FactionScreen(this));
        setMapScreen(new MapScreen(this));
        setInfoScreen(new InfoScreen(this));
    }

    @Override
	public void render() {

    	//until manager updates, shows splash screen
    	//when manager finished, returns true, sets main screen
    	if (manager.update()) {
            Gdx.gl.glClearColor(0, 0, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            super.render();

            if (!screenSet){
                setupScreens();
                this.setScreen(mainScreen);
                screenSet = true;
            }
        }
    	else{
            //this shows a splash screen while assets load
            batch.begin();
            batch.draw(splashTitle, 0, 0, splashTitle.getRegionWidth(), splashTitle.getRegionHeight());
            batch.end();
        }
    }

    @Override
	public void dispose() {
        if (getScreen()!=null)
            getScreen().dispose();

        batch.dispose();
        font.dispose();
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        if (getScreen()!=null)
            getScreen().resize(width, height);
    }

    /**
	 * @return the main
	 */
	public MainScreen getMainScreen() {
		return mainScreen;
	}

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public InfoScreen getInfoScreen() {
        return infoScreen;
    }

    public void setInfoScreen(InfoScreen infoScreen) {
        this.infoScreen = infoScreen;
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

    public MapScreen getMapScreen() {
        return mapScreen;
    }

    public void setMapScreen(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
    }



}
