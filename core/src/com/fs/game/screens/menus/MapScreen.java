/**
 * 
 */
package com.fs.game.screens.menus;

import com.badlogic.gdx.Screen;
import com.fs.game.screens.GameState;
import com.fs.game.MainGame;

/** MapScreen.java
 *  TODO: add more maps
 * implements Screen to show menu of maps to select
 * 
 * 
 * @author Allen Jagoda
 *
 */
public class MapScreen implements Screen {
	final MainGame game;

    GameState gameState;
	
	/**
	 * 
	 */
	public MapScreen(final MainGame game) {
		this.game = game;

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		//
        if (gameState == GameState.MAP_SELECT)
		    show();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {

	}

}
