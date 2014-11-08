package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fs.game.main.MainGame;
import com.fs.game.menus.MenuScreen;

public class MainScreen implements Screen{
	
	final MainGame game;
	
	OrthographicCamera camera;
	

	public MainScreen(final MainGame game) {
		// TODO Auto-generated constructor stub
		this.game = game;
		
		//load assets into textures
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 500);
		
	}//create camera objects here

	@Override
	public void render(float delta) {
 		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to the Game!!! ", 100, 400);
        game.font.draw(game.batch, "Tap anywhere (or press enter) to get to first menu!", 100, 350);
        game.font.draw(game.batch, "For TEST setup 1 (two units only), press Control-Left + 1 ", 75, 200);
        game.font.draw(game.batch, "For TEST setup 2 (4 units, 2 medium, 2 small), press Control-Left + 2", 75, 170);
        game.font.draw(game.batch, "For TEST setup (Human vs Retpoid), press Alt-left, 75, 140)", 75, 100);
        game.batch.end();

        /*
         * Sets whether the BACK button on Android should be caught. 
         * This will prevent the app from being paused. 
         * Will have no effect on the desktop.
         */
        Gdx.input.setCatchBackKey(true);

        /*
         * method for setting a new screen
         */
        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.ENTER)) {
            game.setScreen(new MenuScreen(game));
            hide();
        }		

        //a regular setup of what map stage will look like normally
        if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
            //the default test setup; how game play will look with full array of units for both players
            game.setScreen(new LevelScreen(game, 1));

        }

        if (Gdx.input.isKeyPressed(Keys.NUM_2))
            game.setScreen(new LevelScreen(game, 2));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
