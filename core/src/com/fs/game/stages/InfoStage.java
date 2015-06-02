package com.fs.game.stages;

import appwarp.WarpController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UserData;
import com.fs.game.map.MiniMap;
import com.fs.game.units.TextActor;
import com.fs.game.units.Unit;
import com.fs.game.utils.AppWarpAPI;
import com.fs.game.utils.UIUtils;
import com.fs.game.utils.UnitUtils;

/** Show information about Game play
 *  Contains timer, score, unit info & damage, etc.
 *
 * NOTE: need to offset clipping on GameStage in this Stage draw method
 *  using gl instead of gl20
 *
 * Created by Allen on 5/25/15.
 */
public class InfoStage extends Stage implements StageListener {

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;

    final float TIME_PER_TURN = Constants.MAX_TIME;

    //widgets for HUD
    TextButton[] uiButtons = new TextButton[3]; //go button, p1 & p2 side button,
    Label[] labels = new Label[5]; //timer, unit detail, unit damages, score p1, score p2

    TextActor textActor;
    public MiniMap miniMap; //minimap
    float[] MM_BOUNDS_X = {Constants.MM_X, Constants.MM_X + Constants.MM_WIDTH};
    float[] MM_BOUNDS_Y = {Constants.MM_Y, Constants.MM_Y + Constants.MM_HEIGHT};

    Viewport viewport;

    Vector3 touchPoint = new Vector3();
    float timerCount = 0; //came time
    int currPlayer = 1; //current player's turn
    int[] scores = {0, 0};
    int mPlayer; //multiplayer ID

    boolean multiChange = false; // for multiplayer, whether players have been changed

    float msgTime = 0;
    float endTime = 3f;
    String playerTurnMsg = "PLAYER " + currPlayer + " TURN";

    StageListener gameStageListener;


    public InfoStage(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        setViewport(viewport);


        UIUtils.setupUI(uiButtons, labels, this);

        setTextActor();
    }

    //sets up textActor & adds to HUD stage, stage
    protected void setTextActor(){
        textActor = new TextActor("retro1", Constants.TURN_MSG_COORD);
        addActor(textActor);
    }


    /** Sets up the MiniMap Window on this Stage and adds
     *  sets interface with GameStage for MiniMap  .
     *
     */
    public void setupMiniMap(GameStage stage){
        if (GameData.testType == 4){
            miniMap = new MiniMap(40, 30);
        }

        // Set minimap interfaces b/w two stages so that info sent:
        //  InfoStage.MiniMap <--> GameStage.Camera
//        stage.setMapListener(this);
//        stage.setMinimapListener(miniMap);

        this.gameStageListener = stage; //set listeners
    }



    boolean showMsg = true; //if true, shows message
    public void showMessage(float delta){
        msgTime += delta;

        if (msgTime < endTime){
            //check if actor is in back, since it will have ZIndex of 0
            if (textActor.getZIndex() == 0)
                textActor.toFront();
        }
        else{
            textActor.toBack();
            msgTime = 0;
            showMsg = false;
        }
    }
    /** Shows a message over all other actors on stage
     *
     * @param message : message to show
     * @param msgTime : time to show it for
     */
    public void setStageMessage(String message, float msgTime){
        showMsg = true;
        textActor.setText(message);
        this.endTime = msgTime;
    }


    //resets players turn time & displays message alert
    public void resetTurn(){
        //switch the current player
        int nextPlayer = currPlayer == 1 ? 2 : 1;

        uiButtons[currPlayer].toggle();

        // Toggle the other side button based on player turn
        uiButtons[nextPlayer].toggle();
        timerCount = 0;

        //change currPlayer assignents on GameStage & InfoStage
        gameStageListener.changePlayer(nextPlayer);
        currPlayer = nextPlayer;

//        //need to send the updating data if is Multiplayer game
//        if (MainGame.isMultiGame()){
//            multiTurnEnd(true);
//        }
    }

    public void checkMinimapInput(){
        if (GameData.cols > 16 && GameData.rows > 12 && Gdx.input.justTouched()){
            getCamera().unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
//            touchPoint.set(Gdx.input.getX(), Gdx.input.getY() - Constants.MAP_SIZE_L[0], 0);
            //TODO: figure this out
            if (touchPoint.x > MM_BOUNDS_X[0] && touchPoint.x < MM_BOUNDS_X[1] &&
                    touchPoint.y > MM_BOUNDS_Y[0] && touchPoint.y < MM_BOUNDS_Y[1]){
                Gdx.app.log("InfoStage LOG", "Touched minimap at point: " + touchPoint.x + ", " + touchPoint.y);
                //NOTE: get the actual positions, not world positions from touchPoint, then unproject when changing view
                float dx = (Constants.MM_WIDTH + 1 - (touchPoint.x - Constants.MM_X))*(32/3); //scale up as well
                float dy = (Constants.MM_HEIGHT - (touchPoint.y - Constants.MM_Y))*(32/3); //scale it up
                Vector3 pos = new Vector3(dx, dy, 0);
                gameStageListener.changeView(pos);
            }
        }
    }


    @Override
    public void draw() {
        viewport.apply();

        //need to offset the clipping done on GameStage
        Gdx.gl.glScissor(0, 0, (int) VIEWPORTWIDTH, (int)VIEWPORTHEIGHT);
//        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
//        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);

        if (miniMap != null){
            miniMap.render(getBatch());
        }



        super.draw();


    }


    @Override
    public void act(float delta) {
        super.act(delta); //update any actors first

        checkMinimapInput(); //checks for minimap input

        //update the timer
        timerCount += delta;
        labels[0].setText("" + (int) timerCount);

        //if the timer reached max time, button or "G" pressed, then player is done
        if (timerCount > TIME_PER_TURN|| Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            resetTurn();// toggle side buttons to red (should be green if player's turn)
        }

        if (showMsg)
            showMessage(delta);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (miniMap!=null)
            miniMap.dispose();
    }


    public void setGameStageListener(StageListener listener){
        this.gameStageListener = listener;
    }


    /** Sends data about player during end of turn (score & to go or not)
     *
     * @param auto : if true, then signals other player to not manually reset InfoStage timer/info,
     *             since this already occured b/c of time change
     */
    public void multiTurnEnd(boolean auto){
        UserData userData = new UserData();
        userData.setScore(scores[mPlayer - 1]);
        userData.setPlayerTurn(auto); //sets other player turn as true
        userData.setPlayer(mPlayer);
        userData.setName(GameData.playerName);
        userData.setUpdateState(AppWarpAPI._TURN_CHANGE);

        String data = AppWarpAPI.getInstance().encodeUserData(userData);
        WarpController.getInstance().sendGameUpdate(data);

    }

//    /** Returns player score
//     *
//     * @param player : player whose score is to be retrieved
//     * @return : the score
//     */
//    public int getPlayerScore(int player){
//        return scores[player];
//    }


    /** Sets player for multiplayer
     *
     * @param player : either 1 or 2
     */
    public void setPlayer(int player){
        this.mPlayer = player;
    }


    @Override
    public void updateUnitInfo(Unit unit) {
        labels[1].setText(UnitUtils.Info.unitDetails(unit));
        labels[2].setText(UnitUtils.Info.unitDamageList(unit));
    }

    @Override
    public void updatePlayerScore(int player, int score) {
        //increment player score
        scores[player-1] += score;

        String name = player == 1 ? GameData.playerName : GameData.enemyName;

        labels[player+2].setText(name + "\n" + Integer.toString(scores[player-1]));
    }

    @Override
    public void changeView(Vector3 pos) {
        //DO NOTHING HERE
    }

    @Override
    public void changePlayer(int nextPlayer) {
        //DO NOTHING HERE
    }

    @Override
    public void setMMAreaPosition(float x, float y) {
        //TODO: figure out if this will be useful
   }
}
