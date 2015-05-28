package com.fs.game.stages;

import com.fs.game.actors.Unit;

/** Helps manages Stage settings
 *
 * Created by Allen on 5/28/15.
 */
public interface StageManager {

    void updateUnitInfo(Unit unit);

    void updatePlayerScore(String player, int score);

    void moveCamera(int x, int y); //moves camera position
}
