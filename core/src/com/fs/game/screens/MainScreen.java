package com.fs.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.fs.game.MainGame;
import com.fs.game.appwarp.WarpController;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.utils.PlayerUtils;
import com.fs.game.utils.UIUtils;


public class MainScreen implements Screen {
	final MainGame game;

	OrthographicCamera camera;
    Stage stage; //screen stage for click buttons
    InputMultiplexer in; //handles input events for stage & screen
    Array<InputProcessor> processors; //processors

    public static GameState gameState; //singleplayer or mulitplayer game

    Vector3 touchPoint;

    //for main button/texture to start menu screens
    TextButton welcomeBtn;
    Texture welcomeTex;
    Rectangle welcomeBounds;

    public Table scrollTable;
    Texture rulesTex;
    Rectangle rulesBounds;
    final float[] rulesPos = Constants.RULE_TEX_POS;

    //for test buttons
    Texture[] testTextures;
    final float[][] testTexPos = Constants.MAIN_TEX_POSITIONS;
    Rectangle[] bounds;

    BitmapFont font;
    private final String welcomeMsg = Constants.WELCOME;
    private final String[] mainMsgs = Constants.MAIN_MSGS;
    private final String rulesMsg = Constants.TO_RULES;

    public MainScreen(final MainGame game) {
		this.game = game;

        this.gameState = GameState.START_SCREEN;
        this.touchPoint = new Vector3();

        this.font = Assets.uiSkin.getFont("retro2");
//        this.font.scale(.03f); //scale to 3/4 original size
        this.font.setColor(Color.RED);

        GameData.playerName = PlayerUtils.setupUsername();
        setupTextures();
        setupCamera();
        setupStage();

        Gdx.input.setInputProcessor(stage);

    }

    public void setupTextures(){
        welcomeTex = Assets.uiSkin.get("welcomeTex", Texture.class);
        welcomeBounds = new Rectangle(Constants.SCREENWIDTH/2 - 250/2, Constants.SCREENHEIGHT - 120f, 350, 50);

        rulesTex = Assets.uiSkin.get("rulesTex", Texture.class);
        rulesBounds = new Rectangle(rulesPos[0], rulesPos[1], 100, 50);

        testTextures = new Texture[4]; //currently 3 tests
        bounds = new Rectangle[4];
        for (int i = 0; i < 4; i++){
            testTextures[i] = Assets.uiSkin.get("testTex", Texture.class);

            bounds[i] = new Rectangle(testTexPos[i][0], testTexPos[i][1],
                    testTextures[i].getWidth(), testTextures[i].getHeight());
        }
    }


    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
    }

    public void setupStage(){
        stage = new Stage();

        welcomeBtn = new TextButton(Constants.WELCOME, Assets.uiSkin, "startStyle1");
        welcomeBtn.setBounds(Constants.SCREENWIDTH/2 - 350/2, Constants.SCREENHEIGHT - 120f, 350f, 100f);

        scrollTable = UIUtils.rulesScrollPane(this, Constants.RULES);


        stage.addActor(welcomeBtn);
    }

    public void startMultiplayer(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                WarpController.getInstance().startApp(GameData.playerName); //starts com.fs.game.appwarp
                System.out.println("Player name: " + GameData.playerName);
                game.setScreen(new StartMultiplayerScreen(game));
            }
        });

    }

    public void startSingleplayer(){
        GameData.playerName = PlayerUtils.setupUsername();
        GameData.enemyName = "testerAI";
        GameData.playerFaction = "Human";
        GameData.enemyFaction = "Arthroid";

//        Gdx.app.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                game.setScreen(new AbstractScreen(game));
//            }
//        });

    }


    public void updateScreen(){

        if (welcomeBtn.isPressed()) {
            gameState = GameState.MAIN_MENU;
        }

        /*
         * Sets whether the BACK button on Android should be caught.
         * This will prevent the app from being paused.
         * Will have no effect on the desktop.
         */
        Gdx.input.setCatchBackKey(true);


        if (Gdx.input.justTouched()){
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if(welcomeBounds.contains(touchPoint.x, touchPoint.y)){
                gameState = GameState.MAIN_MENU;
            }

            if(rulesBounds.contains(touchPoint.x, touchPoint.y)){
                gameState = GameState.GAME_RULES;
                stage.addActor(scrollTable); //adds the scrollPane to stage
                Gdx.input.setInputProcessor(stage);
            }

            if (bounds[0].contains(touchPoint.x, touchPoint.y)){
                GameData.testType = 1;
                gameState = GameState.TEST_SCREEN;
            }

//            if (bounds[1].contains(touchPoint.x, touchPoint.y)){
//                GameData.testType = 2;
//                gameState = GameState.SINGLEPLAYER;
     //           MainGame.setGameState(gameState);
//            }
//
//            if (bounds[2].contains(touchPoint.x, touchPoint.y)){
//                GameData.testType = 3;
//                gameState = GameState.MULTIPLAYER;
//                  MainGame.setGameState(gameState);
//            }
//
//            if (bounds[3].contains(touchPoint.x, touchPoint.y)){
//                GameData.testType = 4;
//                gameState = GameState.SINGLEPLAYER;
//            }

        }
        else if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            gameState = GameState.MAIN_MENU;
        }
//        //a regular game setup of what game play will look like normally
//        else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
//            //the default test setup; how game play will look with full array of units for both players
//            GameData.testType = 1;
//            gameState = GameState.SINGLEPLAYER;
//        }
//        else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
//            GameData.testType = 2;
//            gameState = GameState.SINGLEPLAYER;
//        }
//        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
//            GameData.testType = 3;
//            gameState = GameState.MULTIPLAYER;
//        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            gameState = GameState.GAME_RULES;
            stage.addActor(scrollTable); //adds the scrollPane to stage
            Gdx.input.setInputProcessor(stage);
        }


    }

    //draw textures & fonts with text
    public void draw(){
        game.batch.begin();

        game.batch.draw(welcomeTex, Constants.SCREENWIDTH/2 - 350/2, Constants.SCREENHEIGHT - 120f);
        font.draw(game.batch, welcomeMsg, Constants.SCREENWIDTH/2 - 350/2 + 40f, Constants.SCREENHEIGHT - 80f);

        game.batch.draw(rulesTex, Constants.RULE_TEX_POS[0], Constants.RULE_TEX_POS[1]);
        font.draw(game.batch, rulesMsg, Constants.RULE_TEX_POS[0]+5f, Constants.RULE_TEX_POS[1]+15f);

        for (int i = 0; i < 4; i++){

            game.batch.draw(testTextures[i], testTexPos[i][0], testTexPos[i][1]); //textures for test boxes

            float width = testTextures[i].getWidth();
            float height = testTextures[i].getHeight();

//            TextBounds bounds = font.getMultiLineBounds(mainMsgs[i]);
            GlyphLayout layout = new GlyphLayout();
            layout.setText(font, mainMsgs[i]);

            float x = testTexPos[i][0] ;
            float y = testTexPos[i][1] + height/2 + layout.height/2;

//            font.drawMultiLine(game.batch, mainMsgs[i], x, y, width, HAlignment.CENTER);
            font.draw(game.batch, layout, x, y);
        }

        game.batch.end();

        //draw the stage TODO: keep either stage or Textures w/ Rectangles
        stage.draw();
    }


    @Override
    public void render(float delta) {

        switch(gameState){

            case TEST_SCREEN:
                game.setScreen(game.testScreen);
                break;
            case START_SCREEN:
                updateScreen();
                show();
                break;
            case GAME_RULES:
                //game.setScreen(game.getInfoScreen());
                //stage does everything, prevents others from being clicked
                show();
                break;
            case MAIN_MENU:
                game.setScreen(game.getMenuScreen());
                break;
            case SINGLEPLAYER:
                startSingleplayer();
                game.setScreen(new GameScreen(game));
                break;
            case MULTIPLAYER:
                startMultiplayer(); //runnable is in here
                break;
            case QUIT:
                WarpController.getInstance().handleLeave();
                updateScreen();
                show();
                break;
        }

    }


    @Override
	public void show() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.act();
        draw();

	}

	@Override
	public void hide() {

    }

	@Override
	public void pause() {

		
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
        stage.dispose();
	}


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getCamera().update();

        camera.update();
    }

}
