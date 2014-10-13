package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fs.game.main.MainGame;

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
        game.font.draw(game.batch, "Welcome to the Game!!! ", 111, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.font.draw(game.batch, "(TEST: press enter to see Human vs Retpoid board setup)", 50, 50);
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
        if (Gdx.input.isTouched()) {
            game.setScreen(game.menuScreen);
            hide();
        }		
        
         if (Gdx.input.isKeyPressed(Keys.ENTER)) {
        	game.setScreen(new LevelScreen(game));
        	hide();
        }
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
