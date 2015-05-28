/**
 * 
 */
package com.fs.game.screens.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.screens.GameState;
import com.fs.game.MainGame;
import com.fs.game.stages.UnitStage;
import com.fs.game.utils.MenuUtils;

/** FactionScreen.java
 * implements Screen from Libgdx
 * - creates the screen for choosing species
 * 
 * @author Allen Jagoda
 *
 */
public class FactionScreen implements Screen {
	final MainGame game;
    public GameState gameState;

    Array<Button> factionButtons;
    OrthographicCamera camera;
	String LOG = "Faction Select Log: ";

    Stage stage;
    UnitStage unitStage;



	public FactionScreen(final MainGame game) {
        this.game = game;
        this.gameState = GameState.FACTION_SELECT;

        setupCamera();
		setupStage();
		
	}

	public void setupStage() {
        stage = new Stage();
        MenuUtils.FactionMenu.factionMenuButtons(stage);
	}

    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
    }


    public void unitMenu(){
        gameState = GameState.UNIT_SELECT;
        unitStage = new UnitStage();
        Gdx.input.setInputProcessor(unitStage);
        //hide();
    }

    public void updateMenu(){

        for (String s : Constants.FACTION_LIST){
            if (GameData.getInstance().playerFaction.equals(s)){
                unitMenu();
            }
        }

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.ESCAPE)){
            gameState = GameState.MAIN_MENU;
        }

        stage.act();
    }


    @Override
    public void render(float delta) {

        //goes in act method on stage
        switch (gameState){
            case FACTION_SELECT:
                show();
                break;
            case UNIT_SELECT:
                game.setScreen(new UnitScreen(game));
                break;
            case MAIN_MENU:
                game.setScreen(game.menuScreen);
                break;
        }

    }


	@Override
	public void show() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        updateMenu();
        stage.act();
        stage.draw();
	}


	@Override
	public void hide() {


	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {

	}

    /**
     */
    @Override
    public void resume() {

        gameState = GameState.FACTION_SELECT;
        game.setScreen(this);
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
