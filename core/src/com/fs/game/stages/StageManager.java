package com.fs.game.stages;

import com.fs.game.units.Unit;

/** Helps manages Stage settings
 *
 * Created by Allen on 5/28/15.
 */
public interface StageManager {

    void updateUnitInfo(Unit unit);

    void updatePlayerScore(String player, int score);

    void changeView(); //changes view from player 1 to player 2 on LARGE game board

    //sets area in minimap that is shown on actual game screen
    void setMMAreaPosition(float x, float y);
}
