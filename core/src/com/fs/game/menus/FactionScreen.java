/**
 * 
 */
package com.fs.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.utils.MenuUtils;

/** FactionScreen.java
 * implements Screen from Libgdx
 * - creates the screen for choosing species
 * 
 * @author Allen Jagoda
 *
 */
public class FactionScreen implements Screen{
	
	final MainGame game;
    public GameState gameState;

	Stage stage;

    Array<Button> factionButtons;
    OrthographicCamera camera;
	String LOG = "Faction Select Log: ";


    protected UnitsScreen unitsScreen;

	/**
	 * 
	 */
	public FactionScreen(final MainGame game) {
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);

        gameState = GameState.FACTION_SELECT;

        GameData.currFaction = "";
		setupStage();
		
	}

	public void setupStage() {
        stage = new Stage();

        MenuUtils.FactionMenu.factionMenuButtons(stage);
	}

	@Override
	public void render(float delta) {


        switch (gameState){
            case FACTION_SELECT:
                updateCurrent(delta);
                break;
            case UNIT_SELECT:
                nextMenu();
                break;
//            case MAP_SELECT:
//                updateMenu();
//                break;
            case START_SCREEN:
                prevMenu();
                break;
        }
	}

    public void nextMenu(){
        String nextFaction = GameData.currFaction;
        unitsScreen = new UnitsScreen(game, nextFaction);
        hide();
    }

    public void prevMenu(){
        game.mainScreen.menuScreen.resume();
        hide();
    }


    public void updateCurrent(float delta){

        Gdx.input.setInputProcessor(stage);
        stage.act(delta);

        for (String s : Constants.FACTION_LIST){
            if (GameData.currFaction.equals(s)){
                gameState = GameState.UNIT_SELECT;
            }
        }

        if (Gdx.app.getInput().isKeyPressed(Input.Keys.ESCAPE)){
            gameState = GameState.MAIN_MENU;
        }




        show();



    }

	@Override
	public void show() {
        stage.draw();
	}


	@Override
	public void hide() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState == GameState.UNIT_SELECT)
            game.setScreen(unitsScreen);
        else if (gameState == GameState.MAIN_MENU)
            game.setScreen(game.menuScreen);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
        game.setScreen(game.getMenuScreen());
	}

    /**
     */
    @Override
    public void resume() {
        gameState = GameState.FACTION_SELECT;
    }


    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#dispose()
     */
	@Override
	public void dispose() {
		stage.dispose();
	}


    /* (non-Javadoc)
     * @see com.badlogic.gdx.Screen#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }
}
