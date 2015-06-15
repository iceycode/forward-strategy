package com.fs.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.screens.GameState;
import com.fs.game.screens.GameScreen;

/** PauseWindow
 * - pause window for when pausing the game
 *
 *
 * Created by Allen on 5/5/15.
 */
public class PauseWindow extends Window {

    Skin skin;
    Table volTable; //volume table

    //size & position of window
    float WIDTH = Constants.PAUSE_WIDTH;
    float HEIGHT = Constants.PAUSE_HEIGHT;
    float ORI_X = Constants.PAUSE_X;
    float ORI_Y = Constants.PAUSE_Y;

    boolean showing = false; //if true, then is on screen

    private GameScreen screen; //game screen which pause window interacts with

    public PauseWindow(Skin skin, GameScreen screen){
        super("Game Paused", skin, "pause-window");
        this.screen = screen;
        this.skin = skin;

        setBounds(ORI_X, ORI_Y, WIDTH, HEIGHT);

        setVolTable();
    }


    //sets volume table - sliders with volume in them
    protected void setVolTable(){
        volTable = new Table();

        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
            }
        };

        volTable.pad(10).defaults().expandX().space(4); //used as defaults for all rows



        //----music control label & slider
        volTable.add(new Label("Sounds", skin, "label-pause"));
        volTable.row();

//            Slider.SliderStyle style = new Slider.SliderStyle();
//            style.background = Assets.uiSkin.getDrawable("pause-sounds-slider");
//            style.knob = Assets.uiSkin.getDrawable("pause-slider-knob");
        final Slider soundSlider = new Slider(0, 100, 10, false, skin, "slider-sound"); //false means horizantal scroll
        soundSlider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.
        soundSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                log( "slider at: " + soundSlider.getValue());
                GameData.volumes[1] = soundSlider.getValue() / 10000f; //volume in range [0,1]

            }
        });
        volTable.add(soundSlider).align(Align.left);
        volTable.row();

        //----music control label & slider
        volTable.add(new Label("Music", skin, "label-pause"));
        volTable.row();

//            style.background = Assets.uiSkin.getDrawable("pause-music-slider"); //change background for music
        final Slider musicSlider = new Slider(0, 100, 10, false, Assets.uiSkin, "slider-music");
        musicSlider.addListener(stopTouchDown);
        musicSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                log("slider at: " + musicSlider.getValue());
                GameData.volumes[1] = soundSlider.getValue() / 10000f; //volume in range [0,1]
            }
        });
        volTable.add(musicSlider).align(Align.left);

        add(volTable); //add to window
    }

    protected void setButtons(){
        add("Resume");
//
//        .addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (())
//            }
//        });

        add("Exit");


        setSize(WIDTH / 2, 50);
        center();

    }


    public void pause(){
        setVisible(true);
        setZIndex(getStage().getActors().size + 1);
    }

    public void stopPause(){
        setVisible(false); //make invisible
        setZIndex(0);
    }


    @Override
    public void act(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)){
            screen.setGameState(GameState.RESUME);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            screen.setGameState(GameState.PAUSE);

        }
    }

    private void log(String message){
        Gdx.app.log("PauseMenu LOG: ", message);
    }
}
