package com.fs.game.tests;

import com.fs.game.appwarp.WarpController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.fs.game.constants.Constants;
import com.fs.game.MainGame;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.screens.GameState;
import com.fs.game.screens.GameScreen;
import com.fs.game.screens.MainScreen;
import com.fs.game.screens.StartMultiplayerScreen;
import com.fs.game.utils.PlayerUtils;

/** TestScreen contains a dropdown (SelectBox) with list of tests
 *  Similar to GameScreen except that it just allows selection of gameplay
 *  related tests
 *
 * @author Allen
 * Created by Allen on 5/24/15.
 */
public class TestScreen implements Screen {
    final MainGame game;

    Skin skin = Assets.getDarkSkin();

    ScalingViewport viewport; //scaling viewport
    Stage stage; //stage
    Table table; //table containing widgets

    public static GameState gameState; //game state

    public TestScreen(final MainGame game) {
        this.game = game;
        this.gameState = GameState.TEST_SCREEN;

        setupStage();
        Gdx.input.setCatchBackKey(true);
    }

    public void setupStage(){
        viewport = new ScalingViewport(Scaling.fill, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
        stage = new Stage();

        table = new Table(); //table containing main widgets
        table.center();
        table.setBounds(0, 0, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);

        //ROW 1 - Title
        Label label = new Label("Testing Screen", skin, "title");
        table.add(label).size(label.getWidth(), label.getHeight()).center();

        //ROW 2 - SelectBox
        table.row().padTop(50);
        setSelectBox(Constants.TEST_NAMES, skin); //add selectbox to table

        //ROW 3 - back button
        table.row().padTop(50);
        final TextButton back = new TextButton("Back", skin);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (back.isPressed()){
                    gameState = GameState.START_SCREEN;
                }
            }
        });
        table.add(back).center().size(back.getWidth(), back.getHeight());


        stage.addActor(table);
    }

    /** Creates a selectbox for tests
     *
     * @param skin : skin used
     * @return : a SelectBox with test listener
     */
    public void setSelectBox(String[] tests, Skin skin){
        final SelectBox<String> selectBox = new SelectBox<String>(skin);
        selectBox.setItems(tests);

        selectBox.setSelectedIndex(0); //set to select test message

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //show a dialog, as long as item index > 0, not select test message
                if (selectBox.getSelectedIndex() > 0) {
                    //assign test type, then assign state, then show confirm dialog
                    int testType = selectBox.getSelectedIndex();
                    if (testType == 3) {
                        confirmTestDialog(selectBox.getSelected(), GameState.MULTIPLAYER, testType);
                    } else {
                        confirmTestDialog(selectBox.getSelected(), GameState.SINGLEPLAYER, testType);
                    }
                }
            }
        });

        table.add(selectBox).center();
    }

    /** Shows popup dialog with test name
     *
     * @param testName : testName
     * @param state : state of test, changes screen state so it runs
     */
    public void confirmTestDialog(final String testName, final GameState state, final int testType){
        new Dialog("Confirm Test", skin){
            @Override
            protected void result(Object object) {
                gameState = ((GameState)object);
                MainScreen.gameState = ((GameState)object); //FIXME: get rid of this after tests
                GameData.testType = testType;
            }
        }.text("Are you sure you want to run test:\n"+testName).button("Yes", state).button("No", gameState)
                .show(stage);
    }

    public void startMultiplayer(){
        MainGame.setGameState(GameState.MULTIPLAYER);


        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                MainGame.setGameState(GameState.MULTIPLAYER);
                WarpController.getInstance().startApp(GameData.playerName); //starts com.fs.game.appwarp
                System.out.println("Player name: " + GameData.playerName);
                game.setScreen(new StartMultiplayerScreen(game));
            }
        });

    }

    public void startSingleplayer(){
        MainGame.setGameState(gameState);

        GameData.playerName = PlayerUtils.setupUsername();
        GameData.enemyName = "testAI_V1";

        //FIXME: move these to a less awkware place (maybe TestUtils)
        GameData.playerFaction = Constants.FACTION_LIST[0];
        GameData.enemyFaction = Constants.FACTION_LIST[1];



//        Gdx.app.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                game.setScreen(new GameScreen(game));
//            }
//        });

    }



    @Override
    public void render(float delta) {

        switch(gameState){
            case TEST_SCREEN:
                show();
                break;
            case START_SCREEN:
                game.setScreen(game.getMainScreen());
                MainScreen.gameState = GameState.START_SCREEN;
                break;
            case GAME_RULES:
//                game.setScreen(game.getInfoScreen());
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
                show();
                break;
        }

    }


    @Override
    public void show() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        gameState = GameState.TEST_SCREEN;

        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.getViewport().update(width, height);
        stage.getCamera().update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        gameState = GameState.TEST_SCREEN;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
