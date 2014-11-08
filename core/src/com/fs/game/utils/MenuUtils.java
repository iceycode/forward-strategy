package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;

/** a class containing methods for creating menu components
 * 
 * @author Allen
 *
 */

public class MenuUtils {

    final static String LOG = "Pause Menu LOG";
    static Skin skin = GameManager.uiSkin;

	/** creates a pause window
	 * 
	 * @return
	 */
	public static Window pauseWindow(){

        float WIDTH = Constants.PAUSE_WIDTH;
        float HEIGHT = Constants.PAUSE_HEIGHT;
        float ORI_X = Constants.PAUSE_X;
        float ORI_Y = Constants.PAUSE_Y;

		//sets the window style
		WindowStyle winStyle = new WindowStyle();
		winStyle.titleFont = GameManager.uiSkin.getFont("default-small");
		winStyle.titleFont.scale(.01f); //scale it down a bit
		winStyle.background = GameManager.uiSkin.getDrawable("pause-background");
 		//create the window
		Window win = new Window("PAUSED", winStyle);

		//+/- 64 accounts for timer width (64 pix)
		win.setBounds(ORI_X, ORI_Y, WIDTH, HEIGHT);
		win.setFillParent(false);


        //TODO: create the other pause menu features
        /*
            add methods here
         */

        createVolumeSliders(win);


		return win;
		
 	}



    public static void createVolumeSliders(Window win){

        //skin = new Skin(Gdx.files.internal("menu/pause_menu/pauseMenu.json"));
        Table volTable = new Table();

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

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("retro2");

        //----music control label & slider
        volTable.add(new Label("Sounds",labelStyle));
        volTable.row();

        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = skin.getDrawable("pause-sounds-slider");
        style.knob = skin.getDrawable("pause-slider-knob");
        final Slider soundSlider = new Slider(0, 100, 10, false, style); //false means horizantal scroll
        soundSlider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.
        soundSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                Gdx.app.log(LOG, "slider at: " + soundSlider.getValue());
                GameData.currVolumeSounds = soundSlider.getValue()/10000f; //volume in range [0,1]

            }
        });
        volTable.add(soundSlider).align(Align.left);
        volTable.row();

        //----music control label & slider
        volTable.add(new Label("Music", labelStyle));
        volTable.row();

        style.background = skin.getDrawable("pause-music-slider"); //change background for music
        final Slider musicSlider = new Slider(0, 100, 10, false, style);
        musicSlider.addListener(stopTouchDown);
        musicSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log(LOG, "slider at: " + musicSlider.getValue());
                GameData.currVolumeMusic = musicSlider.getValue()/10000f;
            }
        });
        volTable.add(musicSlider).align(Align.left);


        win.add(volTable).align(Align.topLeft); //add volume buttons table to window
        //win.addActor(volTable);

    }


    public static Array<Label> pauseOptionLabels(){
        Array<Label> pauseOptions = new Array<Label>();



        return pauseOptions;
    }

}
