package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;

public class MainScreen implements Screen{
	
	final MainGame game;
	
	OrthographicCamera camera;

    protected GameState gameState;
    public GameScreen gameScreen;
    public MenuScreen menuScreen;


    public MainScreen(final MainGame game) {
		// TODO Auto-generated constructor stub
		this.game = game;
        gameState = GameState.START_SCREEN;

        GameData.currPlayer = 1;

        setupCamera();
    }

    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 500);
    }

	@Override
	public void render(float delta) {

        switch(gameState){
            case START_SCREEN:
                updateCurrent();
                break;
            case MAIN_MENU:
                runMenu();
                break;
            case RUN:
                runGame();
                break;

        }

	}

    public void updateCurrent(){

        /*
         * Sets whether the BACK button on Android should be caught.
         * This will prevent the app from being paused.
         * Will have no effect on the desktop.
         */
        Gdx.input.setCatchBackKey(true);

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameState = GameState.MAIN_MENU;
        }

        //a regular setup of what map stage will look like normally
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            //the default test setup; how game play will look with full array of units for both players
            GameData.testType = 1;
            gameState = GameState.RUN;

        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            GameData.testType = 2;
            gameState = GameState.RUN;

        }

        show();
    }


    public void runGame(){
        gameScreen = new GameScreen(game);
        game.setScreen(gameScreen);
    }

    public void runMenu(){
        menuScreen = new MenuScreen(game);
        game.setScreen(menuScreen);
    }


	@Override
	public void show() {
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
	}

	@Override
	public void hide() {

		
	}

	@Override
	public void pause() {

		
	}

	@Override
	public void resume() {
        gameState = GameState.START_SCREEN;
	}

	@Override
	public void dispose() {
        gameScreen.dispose();
        menuScreen.dispose();
	}


    @Override
    public void resize(int width, int height) {

    }


}
