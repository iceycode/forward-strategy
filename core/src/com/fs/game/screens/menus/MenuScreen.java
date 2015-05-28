package com.fs.game.screens.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Assets;
import com.fs.game.screens.GameState;
import com.fs.game.MainGame;
import com.fs.game.screens.MainScreen;
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
    MainScreen mainScreen; //previous screen
	
	final String LOG = "Main menu Log: ";
	
	OrthographicCamera camera;
	Skin skin;
    Array<TextButton> mmButtons;

    FactionScreen factionScreen; //for faction screen
    MapScreen mapScreen;
    GameState gameState;

    //StageUtils for menu widgets
    Stage stage; //main menu stage


    public MenuScreen(final MainGame game) {
		this.game = game;
        this.skin = Assets.uiSkin;
        this.gameState = GameState.MAIN_MENU;

        this.mainScreen = game.getMainScreen();
        this.factionScreen = game.getFactionScreen();

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



    /** selects next screen based on game animState
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
                updateCurrent();
                show();
                break;
            case FACTION_SELECT:
                game.setScreen(factionScreen);
                break;
            case MAP_SELECT:
                mapMenu();
                break;
            case START_SCREEN:
                mainScreen.gameState = GameState.START_SCREEN;
                game.setScreen(mainScreen);
                break;

        }

	}//render method

    public void updateCurrent(){

        for (TextButton tb: mmButtons){
            //tb.act(delta);
            if (tb.isPressed()){
                Gdx.app.log(LOG, "button press: " +  tb.getName());
                nextMenu(tb.getName());
                tb.clearActions();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            gameState = GameState.START_SCREEN;
        }


    }


    public void mapMenu(){
        game.setScreen(game.getMapScreen());
    }



	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {

        Gdx.input.setInputProcessor(stage); //sets input processor for current stage


        stage.act();
        stage.draw();
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {


	}

	@Override
	public void resume() {
        gameState = GameState.MAIN_MENU;
	}

	@Override
	public void dispose() {
        stage.dispose();
	}

}
