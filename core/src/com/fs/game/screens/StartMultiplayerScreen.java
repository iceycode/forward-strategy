package com.fs.game.screens;

import appwarp.WarpController;
import appwarp.WarpListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.fs.game.MainGame;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.utils.AppWarpAPI;
import com.fs.game.utils.PlayerUtils;

/** the start screen for multiplayer
 * - manages waiting & game over states
 * - runs MultiplayerScreen
 *
 * All UserData will be setup HERE since switching to Multiplayer screen and then
 * sending seems to create confusion in terms of which WarpListener gets the
 * updated data from the connected player online.
 *
 * Created by Allen on 12/6/14.
 */
public class StartMultiplayerScreen implements Screen, WarpListener{

    final MainGame game;
//    MainScreen prevScreen;

    MultiplayerScreen multiplayerScreen;

    OrthographicCamera camera;
    GameState gameState;

    boolean isReceived = false; //if true, means setup (other player) data is received
    public UserData userData; //userData from this screen, if any received

    BitmapFont font;
    private String msg = ""; //message displayed when connecting/disconnecting

    private final String tryingToConnect = Constants.TRY_CONNECT_MSG;
    private final String WAITING_FOR_USER = "Waiting for other player\n";
    private final String errorInConnection = Constants.ERROR_CONNECT_MSG;

    //messages for when won, lose or disconnecting
    private final String game_win = "Congrats You Win!\nEnemy Defeated";
    private final String game_lose = "You Lose!\nEnemy Won";
    private final String enemy_left = "Congrats You Win!\nEnemy Left the Game";
    private final String game_tied = "Tied Game! Going to main screen in ";
    private final String[] gameOver = {game_win, game_lose, game_tied, enemy_left};
    private final String leavingMsg = "\n" + "Leaving in ";

    private int playerID; //for position purposes only (id length tested)

    float counter = 0;  //count down after game finished till go back to main screen
    boolean returnToMain = false; //return to main screen


    boolean playerWon = false;
    boolean playerLeft = false;


    public StartMultiplayerScreen(final MainGame game){
        this.game = game;
        this.gameState = GameState.WAITING;
        this.font = Assets.uiSkin.getFont("retro1");
        this.font.scale(1.5f);
        this.msg = "Connecting to Server"; //initial message, before connected

        MainGame.setGameState(GameState.MULTIPLAYER); //set state to Multiplayer while waiting

        setupCamera();

        GameData.playerFaction = PlayerUtils.randomFaction();

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


    public void gameOver(){
        if (!playerLeft){
            if (GameData.scoreP1 > GameData.scoreP1){
                this.msg = gameOver[0] + leavingMsg + Integer.toString(5 - (int)counter);
            }
            else if (GameData.scoreP2 > GameData.scoreP1){
                this.msg = gameOver[1] + leavingMsg + Integer.toString(5 - (int)counter);
            }
            else {
                this.msg = gameOver[2] + leavingMsg + Integer.toString(5 - (int)counter);
            }
        }
        else{
            this.msg = gameOver[3] + Integer.toString(5 - (int)counter);
        }

        if (counter > 5.5){
            counter = 0;
            this.font.scale(0.5f);
            WarpController.getInstance().stopApp(); //stop the appwarp api
            gameState = GameState.START_SCREEN;
        }
        else{
            drawMessage();
        }

    }

    public void startMultiGame(){
        multiplayerScreen = new MultiplayerScreen(game, StartMultiplayerScreen.this);
        WarpController.getInstance().setListener(multiplayerScreen);
        game.setScreen(multiplayerScreen);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch(gameState){
            case WAITING:
                drawMessage();
                break;
            case STARTING_MULTI:
                msg = "Starting game in " + Integer.toString(3 - (int)counter);
                drawMessage();
                if (counter > 3.5) {
                    gameState = GameState.STARTING;
                    counter = 0;
                }
                break;
            case STARTING:
                startMultiGame();
                break;
            case GAME_OVER:
                counter += delta;
                gameOver();
                break;
            case START_SCREEN:
                game.setScreen(new MainScreen(game));
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
        System.out.println(" player waiting is " + WarpController.getInstance().getLocalUser());
        this.msg = WAITING_FOR_USER  + "Player Name: " + GameData.getInstance().playerName;

    }

    @Override
    public void onError(String message) {
        this.msg = errorInConnection;
    }

    @Override
    public void onGameStarted(String message) {
        userData = AppWarpAPI.getInstance().decodeUserData(message);

    	gameState = GameState.STARTING_MULTI;


//        log("onGameStarted..sending setup update");
//        AppWarpAPI.getInstance().sendGameSetupUpdate(); //update sent out here
    }

    @Override
    public void onGameFinished(int code, boolean isRemote) {

        msg = code == WarpController.GAME_WIN ? game_lose
                : code == WarpController.GAME_LOST ? game_win
                : enemy_left;

        if (code == WarpController.ENEMY_LEFT)
            playerLeft = true;

        game.setScreen(this);
        gameState = GameState.GAME_OVER;
    }

    @Override
    public void onGameUpdateReceived(String message) {
        UserData userData = AppWarpAPI.getInstance().decodeUserData(message);

        switch (userData.getUpdateState()){
            case AppWarpAPI._SETUP:
                this.userData = userData;
                AppWarpAPI.getInstance().sendReadyUpdate();
                break;
            case AppWarpAPI._READY_TO_START:
                isReceived = true;
                break;
        }

        log("Player " + GameData.playerName + ", received setup info from " + userData.getName());
    }

    public UserData getUserData(){
        return userData;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private void log(String message){
        Gdx.app.log("StartMulti.. LOG", message);
    }
}
