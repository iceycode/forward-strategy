package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.GameManager;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.menus.FactionScreen;
import com.fs.game.menus.MapScreen;
import com.fs.game.utils.MenuUtils;

/** NOTES:
 *  - 12 x 12 grid each box of size 32 px
 *  - 384 pixels wide 
 *  - 384 pixels length
 *  
 *  Menu buttons sizes:
 *   200 pixels x 100 pixels
 *
 * @author Allen
 *
 */

public class MenuScreen implements Screen{
	
	final MainGame game; //game with the main Sprite3
	
	final String LOG = "Main menu Log: ";
	
	OrthographicCamera camera;
	Stage stage;
	Skin skin = GameManager.uiSkin;
    Array<TextButton> mmButtons;

    FactionScreen factionScreen;
    MapScreen mapScreen;
    GameState gameState;
	
	public MenuScreen(final MainGame game) {
		this.game = game;

        gameState = GameState.MAIN_MENU;

        setupCamera();
        setupStage();
	}

    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,500);
    }

    public void setupStage(){
        stage = new Stage();
        mmButtons = MenuUtils.MainMenu.mainMenuButtons(stage);
    }

    /** selects next screen based on game state
     *
     * @param buttonName
     */
    public void nextMenu(String buttonName){
        if (buttonName.equals("Factions")){
            gameState = GameState.FACTION_SELECT;
        }
        else if (buttonName.equals("Settings")){
            gameState = GameState.SETTINGS_GLOBAL;
        }
        else if (buttonName.equals("Maps")){
            gameState = GameState.MAP_SELECT;
        }
    }


	@Override
	public void render(float delta) {


        switch(gameState){
            case MAIN_MENU:
                updateCurrent(delta);
                break;
            case FACTION_SELECT:
                updateMenu();
                break;
//            case RUN:
//                //resume();
//                break;

        }

	
	}//render method

    public void updateCurrent(float delta){

        for (TextButton tb: mmButtons){
            if (tb.isPressed()){
                nextMenu(tb.getName());
            }
        }

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameState = GameState.FACTION_SELECT;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            gameState = GameState.START_SCREEN;
        }

        stage.act(delta);
        show();
    }


    public void updateMenu(){
        switch(gameState){
            case FACTION_SELECT:
                factionMenu();
                break;
        }
    }

    public void factionMenu(){
        if (GameData.p1Faction==null && GameData.p2Faction==null) {
            factionScreen = new FactionScreen(game);
            pause();
        }
        else if (GameData.p1Faction!=null || GameData.p2Faction!=null)
            hide();
    }

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        stage.draw();

	}

	@Override
	public void hide() {


	}

	@Override
	public void pause() {
        switch(gameState){
            case FACTION_SELECT:
                game.setScreen(factionScreen);
        }
	}

	@Override
	public void resume() {
        gameState = GameState.MAIN_MENU;
        show();
	}

	@Override
	public void dispose() {
        stage.dispose();
	}

}
