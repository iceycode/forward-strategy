package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Constants;
import com.fs.game.assets.GameManager;
import com.fs.game.data.GameData;
import com.fs.game.units.UnitImage;
import com.fs.game.units.UnitInfo;

import java.util.HashMap;

/** a class containing methods for creating menu components
 * 
 * @author Allen
 *
 */

public class MenuUtils {

    final static String LOG_PAUSE_MENU = "LOG Pause Menu: ";
    final static String LOG_FACT_MENU = "LOG Faction Menu: ";
    static Skin skin = GameManager.uiSkin;



    public static class MainMenu {
        public static Array<TextButton> mainMenuButtons(Stage stage) {
            Array<TextButton> mmButtons = new Array<TextButton>();

            TextButton.TextButtonStyle mapsStyle = new TextButton.TextButtonStyle();
            TextButton.TextButtonStyle factionStyle = new TextButton.TextButtonStyle();
            TextButton.TextButtonStyle settingStyle = new TextButton.TextButtonStyle();

		/* TextButtonStyle for the maps menu button */
            mapsStyle.up = skin.newDrawable("maps");
            mapsStyle.down = skin.newDrawable("maps", Color.GRAY);
            mapsStyle.font = skin.getFont("default");

		/* TextButtonStyle for the factionButton menu */
            factionStyle.up = skin.newDrawable("factionButton");
            factionStyle.down = skin.newDrawable("factionButton", Color.GRAY);
            factionStyle.font = skin.getFont("default");

		/* TextButtonStyle for the settings menu button */
            settingStyle.up = skin.newDrawable("settings");
            settingStyle.down = skin.newDrawable("settings", Color.GRAY);
            settingStyle.font = skin.getFont("default");

		/*TextButtonStyle for the info panel (same as one in game play) */
            TextButton.TextButtonStyle infoStyle = new TextButton.TextButtonStyle();
            infoStyle.up = skin.newDrawable("infoPanel");
            infoStyle.font = skin.getFont("default");


		/* creates the TextButton actors */
            final TextButton mapsButton = new TextButton("Choose a map", mapsStyle); //1st submenu
            mapsButton.setPosition(150, 280); //set position of the map menu
            mapsButton.setName("Maps");

            final TextButton factionButton = new TextButton("Choose a faction", factionStyle); //1st submenu
            factionButton.setPosition(480, 280); //set position of the map menu
            factionButton.setName("Factions");

            final TextButton settings = new TextButton("SETTINGS", settingStyle); //1st submenu
            settings.setPosition(300, 90); //set position of the map menu
            settings.setName("Settings");

            //TODO: make quickstart & multiplayer buttons for main menu
//        final TextButton infoPan = new TextButton("", infoStyle);
//        infoPan.setPosition(800/2 - 512/2, 0);
//        infoPan.setVisible(false);
//        infoPan.setName("InfoPan");
//        mmButtons.add(infoPan);//IDK what this is???


            mmButtons.add(mapsButton);
            stage.addActor(mapsButton);
            mmButtons.add(factionButton);
            stage.addActor(factionButton);
            mmButtons.add(settings);
            stage.addActor(settings);

            return mmButtons;
        }

    }



    public static class FactionMenu {


        public static Array<Button> factionMenuButtons(Stage stage){
            Array<Button> fmButtons = new Array<Button>();

            Button.ButtonStyle humStyle= new Button.ButtonStyle();
            humStyle.up = skin.getDrawable("humButton");

            Button.ButtonStyle repStyle = new Button.ButtonStyle();
            repStyle.up = skin.getDrawable("repButton");

            Button.ButtonStyle artStyle= new Button.ButtonStyle();
            artStyle.up = skin.getDrawable("artButton");

            //create the textbuttons
            final Button humButton = new Button(humStyle);
            humButton.setPosition(50, 40);
            humButton.addCaptureListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log(LOG_PAUSE_MENU, "selected humans ");
                    GameData.currFaction = "Human";
                    if (GameData.p1Faction==null) {
                        GameData.p1Faction = "Human";
                    }
                    else if (GameData.p2Faction==null){
                        GameData.p2Faction = "Human";
                    }

                }
            });
            humButton.setName("Human");

            Button repButton = new Button(repStyle);
            repButton.setPosition(300, 40);
            repButton.addCaptureListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log(LOG_PAUSE_MENU, "selected reptoids ");
                    GameData.currFaction = "Reptoid";
                    if (GameData.p1Faction==null) {
                        GameData.p1Faction = "Reptoid";
                    }
                    else if (GameData.p2Faction==null){
                        GameData.p2Faction = "Reptoid";
                    }
                }
            });
            repButton.setName("Reptoid");

            final Button artButton = new Button(artStyle);
            artButton.setPosition(550, 40);
            artButton.addCaptureListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log(LOG_PAUSE_MENU, "selected arts ");
                    GameData.currFaction = "Arthroid";
                    if (GameData.p1Faction==null) {
                        GameData.p1Faction = "Arthroid";
                    }
                    else if (GameData.p2Faction==null){
                        GameData.p2Faction = "Arthroid";
                    }
                }
            });
            artButton.setName("Arthroid");

            stage.addActor(humButton);
            fmButtons.add(humButton);
            stage.addActor(artButton);
            fmButtons.add(artButton);
            stage.addActor(repButton);
            fmButtons.add(repButton);

            return fmButtons;
        }

    }



    public static class UnitMenu {

        Skin skin = GameManager.uiSkin;

        /** recursively creates text labels that correlate with unit size
         * 0=small, 1=med, 2=large
         * @return
         */
        public static Array<Label> createUnitLabels(String[] unitTexts, Array<Label> textLabels, int index){
            while(unitTexts.length<index){
                index++;
                Label textLabel = UIUtils.createLabel(unitTexts[index], Constants.UNIT_IMAGE_LABELSTYLE);
                textLabels.add(textLabel);
                createUnitLabels(unitTexts , textLabels, index);
            }

            return textLabels;
        }

        /** creates a HashMap containing keys for UnitImage Arrays by type
         * 0=small, 1=med, 2=large
         * - correlates with the Labels of unit type
         *
         * @return
         */
        public static HashMap<Integer, Array<UnitImage>> createUnitImages(){
            HashMap<Integer, Array<UnitImage>> unitImages = new HashMap<Integer, Array<UnitImage>>(3);
            Array<UnitImage> smallUnits = new Array<UnitImage>();
            Array<UnitImage> medUnits = new Array<UnitImage>();
            Array<UnitImage> largeUnits = new Array<UnitImage>();


            for (UnitInfo uniInfo : GameData.unitInfoArray){
                if (GameData.currFaction == uniInfo.getFaction()){
                    if (uniInfo.getSize().equals("32x32")){
                        UnitImage image = new UnitImage(uniInfo);
                        smallUnits.add(image);

                    }
                    else if (uniInfo.getSize().equals("64x32")){
                        UnitImage image = new UnitImage(uniInfo);
                        medUnits.add(image);
                    }
                    else{
                        UnitImage image = new UnitImage(uniInfo);
                        largeUnits.add(image);
                    }

                }
            }
            unitImages.put(0, smallUnits);
            unitImages.put(1, medUnits);
            unitImages.put(2, largeUnits);

            return unitImages;
        }




        public static Window unitScreenInfo(Label infoLabel, Label damageLabel, Image uniImg){
            //individual ScrollPane for each Label sets widget to Table to display:
            //UnitInfo
            Table infoTable = new Table();
            infoTable.add(infoLabel).width(infoLabel.getWidth()).height(infoLabel.getHeight());

            //unit damageList
            Table damTable = new Table();
            damTable.add(damageLabel).width(damageLabel.getWidth()).height(damageLabel.getHeight());

            //the scrollpanes
            ScrollPane infoScroll = UIUtils.createInfoScroll(infoTable, Constants.INFO_X, Constants.INFO_Y,
                    Constants.INFO_W, Constants.INFO_H);
            ScrollPane damageScroll = UIUtils.createInfoScroll(damTable, Constants.INFO_X + Constants.INFO_W,
                    Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);

            //position of popup info at upper right corner of unit image
            float x = uniImg.getX()+uniImg.getWidth();
            float y = uniImg.getY()+uniImg.getHeight();
            float width = Constants.SCREENWIDTH - x - 10; //so window padded from edge of screen
            float height = Constants.SCREENHEIGHT - y - 10;
            Window popUpInfo = UIUtils.popUpInfo(infoTable, x, y, width, height);

            return popUpInfo;
        }


    }








    public static class PauseMenu {
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
                    Gdx.app.log(LOG_PAUSE_MENU, "slider at: " + soundSlider.getValue());
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
                    Gdx.app.log(LOG_PAUSE_MENU, "slider at: " + musicSlider.getValue());
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


}
