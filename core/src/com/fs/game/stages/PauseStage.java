package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.fs.game.assets.Assets;
import com.fs.game.screens.GameState;
import com.fs.game.screens.GameScreen;
import com.fs.game.utils.MenuUtils;

/** Pause Stage
 * Simple pause menu
 *
 * TODO: countdown to resume
 * TODO: add more widgets
 *
 * Created by Allen on 5/21/15.
 */
public class PauseStage extends Stage {

    GameScreen screen;
    Window window; //pause window

    boolean isPaused = false; //if true, then game is paused

    public PauseStage(GameScreen screen, ScalingViewport viewport){
        this.screen = screen;
        this.setViewport(viewport);


        this.window = MenuUtils.PauseMenu.pauseWindow();

        //NOTE: temporary setup for now...need to create a skin for this game
        MenuUtils.PauseMenu.createVolumeSliders(window, Assets.getDarkSkin());

        addActor(window);
    }


    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (isPaused){
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                Gdx.app.log("PauseStage LOG: ", "game is resuming");
                screen.setGameState(GameState.RESUME);
                isPaused = false;
            }
        }
    }

    public void setActive(boolean pause){
        this.isPaused = pause;
        Gdx.input.setInputProcessor(this);
    }
}
