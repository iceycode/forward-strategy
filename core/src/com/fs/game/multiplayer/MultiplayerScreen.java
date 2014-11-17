package com.fs.game.multiplayer;

import appwarp.WarpListener;
import com.fs.game.main.MainGame;
import com.fs.game.screens.GameScreen;

/** The multiplayer screen extends LevelScreen, implements WarpListener
 *
 *
 * Created by Allen on 11/16/14.
 */
public class MultiplayerScreen extends GameScreen implements WarpListener{


    public MultiplayerScreen(final MainGame game){
        super(game);

    }

    @Override
    public void onWaitingStarted(String message) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onGameStarted(String message) {

    }

    @Override
    public void onGameFinished(int code, boolean isRemote) {

    }

    @Override
    public void onGameUpdateReceived(String message) {

    }
}
