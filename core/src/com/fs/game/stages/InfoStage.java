package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.constants.Constants;
import com.fs.game.actors.TextActor;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
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
public class InfoStage extends Stage{

    final float VIEWPORTWIDTH = Constants.SCREENWIDTH;
    final float VIEWPORTHEIGHT = Constants.SCREENHEIGHT;

//    //positions of map (camera and tiledmap)
//    final float MAP_X = Constants.MAP_X;
//    final float MAP_Y = Constants.MAP_Y;
//
//    final float MAP_VIEW_WIDTH = Constants.MAP_VIEW_WIDTH;
//    final float MAP_VIEW_HEIGHT = Constants.MAP_VIEW_HEIGHT;

    //widgets for HUD
    TextButton[] uiButtons = new TextButton[3]; //go button, p1 & p2 side button,
    Label[] labels = new Label[5]; //timer, unit detail, unit damages, score p1, score p2

    BitmapFont font;
    TextActor textActor;

    Skin skin;
    Viewport viewport;


    float timerCount = 0; //came time
    boolean playerTurn = false; //this player (for multiplayer)
    int currPlayer = 1; //current player's turn
    int player = 1; //the main player (AI is always 2)
    int playerScore = 0;
    int enemyScore = 0;

    String playerTurnMsg = "PLAYER " + currPlayer + " TURN";


    public InfoStage(){
        viewport = new ScalingViewport(Scaling.stretch, VIEWPORTWIDTH, VIEWPORTHEIGHT);
        setViewport(viewport);

        this.skin = Assets.uiSkin; //set the skin

        UIUtils.setupUI(uiButtons, labels, this);

        setTextActor();
    }

    //sets up textActor & adds to HUD stage, stage
    protected void setTextActor(){
        this.font = skin.getFont("retro1");
        textActor = new TextActor(font, Constants.TURN_MSG_COORD);
        addActor(textActor);
    }

    public void updatePlayerScores(){
        if (GameData.getInstance().player == 1){
            labels[3].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
            labels[4].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
        }
        else{
            labels[3].setText(GameData.getInstance().enemyName + "\n" + Integer.toString(enemyScore));
            labels[4].setText(GameData.getInstance().playerName + "\n" + Integer.toString(playerScore));
        }
    }


    /** Updates widgets containing info about time, players & units
     *
     * @param delta
     */
    public void updateWidgets(float delta){
        timerCount += delta;
        labels[0].setText("" + (int) timerCount);

        updatePlayerScores();
        if (GameData.chosenUnit!=null) {
            labels[1].setText(UnitUtils.Info.unitDetails(GameData.chosenUnit));
            labels[2].setText(UnitUtils.Info.unitDamageList(GameData.chosenUnit));
        }
    }


    //checks if player is done, if so, resets turn
    public void isPlayerDone() {

        //if the timer reached max time, button or "G" pressed, then player is done
        if (timerCount > Constants.MAX_TIME || uiButtons[0].isPressed() || Gdx.input.isKeyJustPressed(Input.Keys.G)) {

            if (playerTurn){
                playerTurn = false;

                //switch the current player
                if (player == 1)
                    currPlayer = 2;
                else
                    currPlayer = 1;

                //toggle button to red (should be green if player's turn)
                //stageMap.lockPlayerUnits(GameData.getInstance().playerName);  //lock these player units
                resetTurn();
            }
        }

    }

    //resets players turn time & displays message alert
    public void resetTurn(){
        timerCount = 0;
        uiButtons[player].toggle();
        if (player == 1)
            uiButtons[2].toggle(); //since player is either 1 or 2
        else
            uiButtons[1].toggle();

        textActor.setText(playerTurnMsg);
        textActor.act(Gdx.graphics.getDeltaTime());
    }





    @Override
    public void draw() {
        viewport.apply();

        //need to offset the clipping done on GameStage
        Gdx.gl.glScissor(0, 0, (int) VIEWPORTWIDTH, (int)VIEWPORTHEIGHT);
//        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
//        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);

        super.draw();


    }

    @Override
    public void act(float delta) {
        super.act(delta); //update any actors first

        //perform update tasks
        updateWidgets(delta);
        isPlayerDone(); //checks to see if next player will go
    }
}
