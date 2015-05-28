package com.fs.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.actors.UnitImage;
import com.fs.game.actors.UnitInfo;

import java.util.HashMap;

/** a class containing methods for creating menu components
 * 
 * @author Allen
 *
 */

public class MenuUtils {

    final static String LOG_PAUSE_MENU = "LOG Pause Menu: ";


    public static class MainMenu {
        public static Array<TextButton> mainMenuButtons(Stage stage) {
            Array<TextButton> mmButtons = new Array<TextButton>();

            TextButton.TextButtonStyle mapsStyle = new TextButton.TextButtonStyle();
            TextButton.TextButtonStyle factionStyle = new TextButton.TextButtonStyle();
            TextButton.TextButtonStyle settingStyle = new TextButton.TextButtonStyle();

		/* TextButtonStyle for the maps menu button */
            mapsStyle.up = Assets.uiSkin.newDrawable("maps");
            mapsStyle.down = Assets.uiSkin.newDrawable("maps", Color.GRAY);
            mapsStyle.font = Assets.uiSkin.getFont("default");

		/* TextButtonStyle for the factionButton menu */
            factionStyle.up = Assets.uiSkin.newDrawable("factionButton");
            factionStyle.checked = Assets.uiSkin.newDrawable("factionButton", Color.GRAY);
            factionStyle.font = Assets.uiSkin.getFont("default");

		/* TextButtonStyle for the settings menu button */
            settingStyle.up = Assets.uiSkin.newDrawable("settings");
            settingStyle.checked = Assets.uiSkin.newDrawable("settings", Color.GRAY);
            settingStyle.font = Assets.uiSkin.getFont("default");

		/*TextButtonStyle for the info panel (same as one in game play) */
            TextButton.TextButtonStyle infoStyle = new TextButton.TextButtonStyle();
            infoStyle.up = Assets.uiSkin.newDrawable("infoPanel");
            infoStyle.font = Assets.uiSkin.getFont("default");


		/* creates the TextButton actors */
            TextButton mapsButton = new TextButton("Choose a map", mapsStyle); //1st submenu
            mapsButton.setPosition(150, 280); //set position of the map menu
            mapsButton.setName("Maps");

            TextButton factionButton = new TextButton("Choose a faction", factionStyle); //1st submenu
            factionButton.setPosition(480, 280); //set position of the map menu
            factionButton.setName("Factions");

            TextButton settings = new TextButton("SETTINGS", settingStyle); //1st submenu
            settings.setPosition(300, 90); //set position of the map menu
            settings.setName("Settings");

            //TODO: make quickstart & multiplayer buttons for main menu

            stage.addActor(mapsButton);
            stage.addActor(factionButton);
            stage.addActor(settings);

            mmButtons.add(mapsButton);
            mmButtons.add(factionButton);
            mmButtons.add(settings);


            return mmButtons;
        }

    }



    public static class FactionMenu {


        public static Array<Button> factionMenuButtons(Stage stage){
            Array<Button> fmButtons = new Array<Button>();
//
//            Button.ButtonStyle humStyle= new Button.ButtonStyle();
//            humStyle.up = Assets.uiSkin.getDrawable("humButton");
//
//            Button.ButtonStyle repStyle = new Button.ButtonStyle();
//            repStyle.up = Assets.uiSkin.getDrawable("repButton");
//
//            Button.ButtonStyle artStyle= new Button.ButtonStyle();
//            artStyle.up = Assets.uiSkin.getDrawable("artButton");

            //create the textbuttons
            final Button humButton = new Button(Assets.uiSkin, "humStyle");
            humButton.setPosition(50, 40);
            factionListener(humButton, Constants.HUMAN);
            humButton.setName(Constants.HUMAN);

            Button repButton = new Button(Assets.uiSkin, "repStyle");
            repButton.setPosition(300, 40);
            factionListener(repButton, Constants.REPTOID);
            repButton.setName(Constants.REPTOID);

            final Button artButton = new Button(Assets.uiSkin, "artStyle");
            artButton.setPosition(550, 40);
            factionListener(artButton, Constants.ARTHROID);
            artButton.setName(Constants.ARTHROID);

            stage.addActor(humButton);
            fmButtons.add(humButton);
            stage.addActor(artButton);
            fmButtons.add(artButton);
            stage.addActor(repButton);
            fmButtons.add(repButton);

            return fmButtons;
        }


        public static void factionListener(Button button, final String faction){
            ChangeListener listener = new ChangeListener(){
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.app.log("Faction Menu LOG: ", "selected " + faction);
                        if (GameData.getInstance().playerFaction == null) {
                            GameData.getInstance().playerFaction = faction;

                        }

                    }
                };

            button.addListener(listener);
        }


    }


    /** UnitMenu class
     * - contains methods for unit menu widget creation
     */
    public static class UnitMenu {

        final static String LOG = "UNITMENU UTILS LOG: ";

        /** recursively creates text labels that correlate with unit size
         * 0=small, 1=med, 2=large
         * @return
         */
        public static Array<Label> createUnitLabels(String[] unitTexts){

            Array<Label> textLabels = new Array<Label>();

            for (int i = 0; i < unitTexts.length; i ++){
                Label textLabel = UIUtils.createLabel(unitTexts[i], Constants.UNIT_IMAGE_LABELSTYLE);
//                textLabel.setX(Constants.UNIT_LABEL_POS[i][0]);
//                textLabel.setY(Constants.UNIT_LABEL_POS[i][1]);

                textLabel.setAlignment(Align.top, Align.left); //sets text alignment
                textLabel.setWidth(64);	//sets width
                textLabel.setHeight(32);	//sets height

//                textLabel.setWrap(true);
                textLabel.setFillParent(false);

                textLabels.add(textLabel);
            }

            return textLabels;
        }

        /** creates a HashMap containing keys for UnitImage Arrays by type
         * 0=small, 1=med, 2=large
         * - correlates with the Labels of unit type
         *
         * @return
         */
        public static HashMap<Integer, Array<UnitImage>> createUnitImages(String faction, Array<UnitImage> allUnits, final Stage stage){
            HashMap<Integer, Array<UnitImage>> unitImages = new HashMap<Integer, Array<UnitImage>>(3);
            Array<UnitImage> smallUnits = new Array<UnitImage>();
            Array<UnitImage> medUnits = new Array<UnitImage>();
            Array<UnitImage> largeUnits = new Array<UnitImage>();

            for (UnitInfo uniInfo : Assets.unitInfoMap.get(faction)){
                if (uniInfo.getSize().equals("32x32")){
                    UnitImage image = new UnitImage(uniInfo);
                    smallUnits.add(image);
                    allUnits.add(image);
                }
                else if (uniInfo.getSize().equals("64x32")){
                    UnitImage image = new UnitImage(uniInfo);
                    medUnits.add(image);
                    allUnits.add(image);
                }
                else{
                    UnitImage image = new UnitImage(uniInfo);
                    largeUnits.add(image);
                    allUnits.add(image);
                }

            }

            unitImages.put(0, smallUnits);
            unitImages.put(1, medUnits);
            unitImages.put(2, largeUnits);

            return unitImages;
        }


        public static Table unitImageTable(HashMap<Integer, Array<UnitImage>> unitImages){
            Table imageTable = new Table();
            imageTable.setBackground(Assets.uiSkin.getDrawable("unitTableBack"));

            Array<Label> unitLabels = createUnitLabels(Constants.UNIT_IMAGE_LABELS); //create table labels

            //creates the labels & UnitImages in a table format
            for (int i = 0; i < unitLabels.size; i++){
                Label label = unitLabels.get(i);
                imageTable.add(label).width(label.getWidth()).height(label.getHeight());

                for (int j = 0; j < unitImages.get(i).size; j++){
                    UnitImage unitImage = unitImages.get(i).get(j);
                    GameData.factUnitImages.add(unitImage);
                    imageTable.add(unitImage).width(unitImage.getWidth()).height(unitImage.getHeight());

                }
                imageTable.row();
            }

            imageTable.setBounds(Constants.UNITS_TABLE_X, Constants.UNITS_TABLE_Y, 450, 200);
//            imageTable.layout();

            return imageTable;
        }

        /** panels created that serve as background for units
         * - small units :: medium units :: large units
         *
         */
        public static Table infoPanel(Label unitDetail, Label unitDamageList, final Stage stage) {

            //----setup for ScrollPane panels as individual units within table----
            //the main pop-up window & widgets
            unitDetail = UIUtils.createLabelInfo();
            unitDamageList = UIUtils.createLabelDamage();

            //scrollTable is the Table which holds the ScrollPane objects
            Table scrollTable = UIUtils.createUnitScrollTable(unitDetail, unitDamageList);

            return scrollTable;
        }


        public static Table unitInfoTable(Label unitDetail, Label unitDamageList){
            Table mainTable = new Table(); //main table holding other 2
            Table detailTable = new Table(); //is not scrollable
            Table damageTable = new Table(); //damage tabe is scrollable

            detailTable.addActor(unitDetail);
            detailTable.add(unitDetail).align(Align.left).width(Constants.INFO_W / 2).height(15f).pad(10f);
            detailTable.setBounds(Constants.UNITS_TABLE_X, 0, Constants.INFO_W, Constants.INFO_H);

            damageTable.addActor(unitDamageList);
            damageTable.add(unitDamageList).align(Align.right).width(Constants.INFO_W / 2).height(15f).pad(10f);
            ScrollPane damageScroll = UIUtils.createInfoScroll(damageTable, Constants.INFO_X + Constants.INFO_W,
                    Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);

            //table.setFillParent(false);
            mainTable.add(detailTable).width(detailTable.getWidth()).height(detailTable.getHeight());
            mainTable.add(damageScroll).width(damageScroll.getWidth()).height(damageScroll.getHeight());
            mainTable.setBounds(Constants.INFO_X, 0, Constants.INFO_W * 2, Constants.INFO_H + 100f);
            mainTable.layout();

            return mainTable;
        }


        //creates a table for units that will be added
        public static Table unitRosterTable(){
            Table table = new Table();
            table.add(new Label("UNITS", Assets.uiSkin, "unitText"));


            table.setBackground(Assets.uiSkin.getDrawable("unitRosterBack"));
            table.setBounds(Constants.UNIT_ROSTER_X, Constants.UNIT_ROSTER_Y,
                            Constants.UNIT_ROSTER_W, Constants.UNIT_ROSTER_H);

            return table;

        }


        public static Array<String> updateUnitText(UnitInfo unitInfo){
            Array<String> unitTextInfo = new Array<String>();

            String unitDamage = "Name : Attack \n";

            for (int i = 0; i < unitInfo.getDamageList().length; i++) {
                int id = i+1; //since unit id assign start is 1


                String name = Assets.unitInfoArray.get(i).getUnit();
                String damage = Integer.toString(Math.abs(unitInfo.getDamageList()[i])); //gets damage
                unitDamage += name + " : " + damage + "\n";
            }

            unitTextInfo.add(unitDamage);
            String unitDetails = "Name: " + unitInfo.getUnit() +
                    "\nFaction: " + unitInfo.getFaction() +
                    "\nTerrain: " + unitInfo.getType() +
                    "\nAttacks:  " + unitInfo.getUnitAnti()  +
                    "\nType: " + unitInfo.getType() +
                    "\nCrosses:\n * water? " +  unitInfo.isCrossWater() +
                    "\n *land obstacle? "+ unitInfo.isCrossLandObst() ;

            unitTextInfo.add(unitDetails);

            return unitTextInfo;

        }


//        /** this is a POTENTIAL option for showing unit information
//         * ATM, going with an info panel under unit images
//         *
//         * @param infoLabel
//         * @param damageLabel
//         * @return
//         */
//        public static Dialog showUnitInfo(Label infoLabel, Label damageLabel, StageUtils stage){
//            //individual ScrollPane for each Label sets widget to Table to display:
//            //UnitInfo
//            Dialog dialogUnit = new Dialog("Unit Selected", Assets.uiSkin, "dialogUnit");
//            Table infoTable = new Table();
//            infoTable.add(infoLabel).width(infoLabel.getWidth()).height(infoLabel.getHeight());
//
//            //unit damageList
//            Table damTable = new Table();
//            damTable.add(damageLabel).width(damageLabel.getWidth()).height(damageLabel.getHeight());
//
//            //the scrollpanes
//            ScrollPane infoScroll = UIUtils.createInfoScroll(infoTable, Constants.INFO_X, Constants.INFO_Y,
//                    Constants.INFO_W, Constants.INFO_H);
//            ScrollPane damageScroll = UIUtils.createInfoScroll(damTable, Constants.INFO_X + Constants.INFO_W,
//                    Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);
//
//            //adding the scrollpanes to dialog
//            dialogUnit.add(infoScroll).width(infoScroll.getWidth()).height(infoScroll.getHeight());
//            dialogUnit.add(damageScroll).width(damageScroll.getWidth()).height(damageScroll.getHeight());
//            dialogUnit.row();
//
//            //adding yes/no buttons
//            TextButton textBtn = new TextButton("Yes", Assets.uiSkin, "dialogTB");
//            dialogUnit.button(textBtn, true).align(Align.bottomLeft);
////            textBtn = new TextButton("No", Assets.uiSkin, "dialogTB");
////            dialogUnit.button(textBtn, false).align(Align.bottomRight);
//
//            dialogUnit.button("Yes", true).button("No", false).key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);
//
//            return dialogUnit;
//        }


        //for unit addition confirmation
        public static Dialog confirmUnitAdd(UnitImage currUnit){
            TextButton yesBtn = new TextButton("Yes", Assets.uiSkin, "default");
            TextButton noBtn = new TextButton("No", Assets.uiSkin, "default");

            Dialog confirm = new Dialog("Add unit?", Assets.uiSkin, "dialogUnit")
                    .button(yesBtn, currUnit.copy = true).button(noBtn, false)
                    .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).show(currUnit.getStage());
            System.out.println("Did it copy to roster?? " + currUnit.copy);
            return confirm;
        }


    }


    /** PauseMenu class
     * - contains methods for creation of pause menu widgets & functions
     * TODO: create the other pause menu features
     *
     */
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
            winStyle.titleFont = Assets.uiSkin.getFont("default-small");
            winStyle.titleFont.scale(.01f); //scale it down a bit
            winStyle.background = Assets.uiSkin.getDrawable("pause-background");
            //create the window
            Window win = new Window("PAUSED", winStyle);

            //+/- 64 accounts for timer width (64 pix)
            win.setBounds(ORI_X, ORI_Y, WIDTH, HEIGHT);
            win.setFillParent(false);

//            Table volTable = createVolumeSliders();
//
//            win.add(volTable).align(Align.topLeft); //add volume buttons table to window
            return win;

        }



        //returns a Table holding volume sliders
        public static void createVolumeSliders(Window window, Skin skin){

            //skin = new Skin(Gdx.files.internal("menu/pause_menu/pauseMenu.json"));
            Table volTable = new Table(); //this table added to window

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



//            //----music control label & slider
            volTable.add(new Label("Sounds", skin));
            volTable.row();

//            Slider.SliderStyle style = new Slider.SliderStyle();
//            style.background = Assets.uiSkin.getDrawable("pause-sounds-slider");
//            style.knob = Assets.uiSkin.getDrawable("pause-slider-knob");
            final Slider soundSlider = new Slider(0, 100, 10, false, skin); //false means horizantal scroll
            soundSlider.addListener(stopTouchDown); // Stops touchDown events from propagating to the FlickScrollPane.
            soundSlider.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log(LOG_PAUSE_MENU, "slider at: " + soundSlider.getValue());
                    GameData.volumes[1] = soundSlider.getValue() / 1000f; //volume in range [0,1]

                }
            });
            volTable.add(soundSlider).align(Align.left);
            volTable.row();

            //----music control label & slider
            volTable.add(new Label("Music", skin));
            volTable.row();

//            style.background = Assets.uiSkin.getDrawable("pause-music-slider"); //change background for music
            final Slider musicSlider = new Slider(0, 100, 10, false, skin);
            musicSlider.addListener(stopTouchDown);
            musicSlider.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.log(LOG_PAUSE_MENU, "slider at: " + musicSlider.getValue());
                    GameData.volumes[1] = soundSlider.getValue() / 1000f; //volume in range [0,1]
                }
            });
            volTable.add(musicSlider).align(Align.left);


            window.add(volTable).center(); //table added to window
        }


        public static Array<Label> pauseOptionLabels(){
            Array<Label> pauseOptions = new Array<Label>();

            return pauseOptions;
        }

    }


}
