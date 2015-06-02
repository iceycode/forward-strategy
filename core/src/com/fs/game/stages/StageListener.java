package com.fs.game.stages;

import com.badlogic.gdx.math.Vector3;
import com.fs.game.units.Unit;

/** Helps manages Stage settings
 *
 * Created by Allen on 5/28/15.
 */
public interface StageListener {

    void updateUnitInfo(Unit unit);

    void updatePlayerScore(int player, int score);

    void changeView(Vector3 pos); //changes view from player 1 to player 2 on LARGE game board

    void changePlayer(int nextPlayer); //change player turns

    //sets area in minimap that is shown on actual game screen
    void setMMAreaPosition(float x, float y);
}
