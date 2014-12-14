package com.fs.game.screens;

import appwarp.WarpController;
import appwarp.WarpListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.utils.GameUtils;

/** the start screen for multiplayer
 * - manages waiting & game over states
 * - runs MultiplayerScreen
 *
 * Created by Allen on 12/6/14.
 */
public class StartMultiplayerScreen implements Screen, WarpListener{

    final MainGame game;
    MainScreen prevScreen;

    OrthographicCamera camera;
    Stage stage; //screen stage for click buttons

    GameState gameState;

    BitmapFont font;
    private String msg = ""; //message displayed when connecting/disconnecting

    private final String tryingToConnect = Constants.TRY_CONNECT_MSG;
    private final String WAITING_FOR_USER = "Waiting for other player\n";
    private final String errorInConnection = Constants.ERROR_CONNECT_MSG;

    //messages for when won, lose or disconnecting
    private final String game_win = Constants.GAME_WIN_MSG;
    private final String game_lose = Constants.GAME_LOSE_MSG;
    private final String enemy_left = Constants.PLAYER_LEFT_MSG;
    private final String[] gameOver = {game_win, game_lose, "Tied Game! Going to main screen in "};


    private String startMsg = "Game Starting for player: "+GameData.playerName;
    private String startCount = startMsg + "\n Game Starting in ";
    private int playerID; //for position purposes only (id length tested)

    float counter = 0;  //countdown after game finished till go back to main screen
    boolean returnToMain = false; //return to main screen


    boolean playerWon = false;
    boolean playerLeft = false;


    public StartMultiplayerScreen(final MainGame game, MainScreen mainScreen){
        this.game = game;
        this.gameState = GameState.WAITING;
        this.prevScreen = mainScreen;
        this.font = Assets.uiSkin.getFont("retro1");
        this.font.scale(1.5f);

        setupCamera();

        GameData.currPlayer = 0; //set to 0 at first
        GameData.playerFaction = GameUtils.Player.randomFaction(); //pick random faction (or will get elsewhere)


        WarpController.getInstance().setListener(this);
    }

    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 500);
    }


    public void drawMessage(){
        game.batch.begin();

        float y = Constants.SCREENHEIGHT/2;
        float x = Constants.SCREENWIDTH/4;
        font.drawMultiLine(game.batch, msg, x, y);
        game.batch.end();
    }


    public void gameIsStarting(){

        if (counter > 3.5) {
            counter = 0;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MultiplayerScreen(game, StartMultiplayerScreen.this));

                }
            });
        }
        else{
            this.msg = startCount + Integer.toString((int)counter);
            drawMessage();
        }
    }


    public void gameOver(){
        if (GameData.scoreP1 > GameData.scoreP1){
            this.msg = gameOver[0] + Integer.toString((int)counter);
        }
        else if (GameData.scoreP2 > GameData.scoreP1){
            this.msg = gameOver[1] + Integer.toString((int)counter);
        }
        else if (GameData.scoreP2 == GameData.scoreP1){
            this.msg = gameOver[2] + Integer.toString((int)counter);
        }
        else{
            this.msg = "Player left the game " + Integer.toString(5 - (int)counter);
        }

        if (counter > 5.5){
            counter = 0;
            prevScreen.gameState = GameState.START_SCREEN;
            gameState = gameState.START_SCREEN;
        }
        else{
            drawMessage();
        }

    }




    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch(gameState){
            case WAITING:
                drawMessage();
                break;
            case STARTING:
                counter += delta;
                gameIsStarting();
                break;
            case GAME_OVER:
                counter += delta;
                gameOver();
                break;
            case START_SCREEN:
                game.setScreen(prevScreen);
                this.dispose();
                break;

        }

    }


    @Override
    public void resize(int width, int height) {

    }

    /**
     */
    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }


    public void resume() {

    }


    @Override
    public void dispose() {

    }

    @Override
    public void onWaitingStarted(String message) {

        this.msg = WAITING_FOR_USER  + "Player Name: " + GameData.playerName;

    }

    @Override
    public void onError(String message) {
        this.msg = errorInConnection;
    }

    @Override
    public void onGameStarted(String message) {

        gameState = GameState.STARTING;

    }

    @Override
    public void onGameFinished(int code, boolean isRemote) {
        if(code== WarpController.GAME_WIN){
            this.msg = game_lose;
        }else if(code==WarpController.GAME_LOST){
            this.msg = game_win;
        }else if(code==WarpController.ENEMY_LEFT){
            this.msg = enemy_left;
        }
        game.setScreen(this);
        gameState = GameState.GAME_OVER;

    }

    @Override
    public void onGameUpdateReceived(String message) {

    }


    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
