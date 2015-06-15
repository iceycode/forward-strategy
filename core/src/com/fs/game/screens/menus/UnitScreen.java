package com.fs.game.screens.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.screens.GameState;
import com.fs.game.MainGame;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitImage;
import com.fs.game.utils.MenuUtils;
import com.fs.game.utils.UIUtils;

import java.util.HashMap;

/** UnitScreen
 *
 * Created by Allen on 11/22/14.
 */
public class UnitScreen implements Screen{

    final MainGame game;
    final String LOG = "UNITSCREEN LOG: ";

    GameState gameState;
    OrthographicCamera camera;
    ScreenViewport viewport;

    Stage stage;
    Label unitDetail; //shows unit attributes
    Label unitDamageList; //shows unit damage to all other units

    int currPlayer;
    String currFaction;
    Array<UnitImage> factionUnits;
    HashMap<Integer, Array<UnitImage>> unitImages; //1=small, 2=medium, 3=large
    HashMap<Integer, Unit> currChosenUnits;

    Table unitImageTable;
    Table infoTable;
    Table rosterTable;

    Dialog unitInfoDialog;

    public UnitScreen(final MainGame game){
        this.game = game;
        this.gameState = GameState.UNIT_SELECT;
        this.currPlayer = GameData.player;
        this.currFaction = GameData.playerFaction;

        setupCamera();
        setupStage();
    }

    public void setupCamera(){
        //sets the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 500);
        camera.update();

        viewport = new ScreenViewport();
        viewport.setWorldHeight(Constants.SCREENHEIGHT); //sets the camera screen view dimensions
        viewport.setWorldWidth(Constants.SCREENWIDTH);
        viewport.setCamera(camera);
    }


    public void setupStage(){
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        //stage.setViewport(viewport);

        currChosenUnits = new HashMap<Integer, Unit>(); //stores single players data

        //the info about units
        unitDetail = UIUtils.createLabelInfo();
        unitDamageList = UIUtils.createLabelDamage();
        unitDamageList.setHeight(unitDamageList.getHeight()+200f); //need to add extra height to show ALL units
        infoTable = MenuUtils.UnitMenu.unitInfoTable(unitDetail, unitDamageList);
        //infoTable = UIUtils.createUnitScrollTable(unitDetail, unitDamageList);
        stage.addActor(infoTable);

        //where player units that will go on GameScreen go
        rosterTable = MenuUtils.UnitMenu.unitRosterTable();
        stage.addActor(rosterTable);

        //the choices of units player has
        factionUnits = new Array<UnitImage>(); //initialize current units to select from
        unitImages = MenuUtils.UnitMenu.createUnitImages(currFaction, factionUnits, stage);
        unitImageTable = MenuUtils.UnitMenu.unitImageTable(unitImages); //creates image Buttons containing actors/listeners
        stage.addActor(unitImageTable);

    }


    public void prevMenu(){
        Gdx.input.setInputProcessor(game.factionScreen.stage);
        game.factionScreen.resume();
        //game.setScreen(game.factionScreen);

    }


    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        switch(gameState){
            case UNIT_SELECT:
                show();
                break;
            case FACTION_SELECT:
                prevMenu();
                break;
            case MAIN_MENU:
                game.setScreen(game.menuScreen);
                break;
        }
    }



    /**
     * Called when this screen becomes the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void show() {
        for (UnitImage u : factionUnits){
            Array<String> unitDetailText = MenuUtils.UnitMenu.updateUnitText(u.unitInfo);

            if (u.selected){
                unitDetail.setText(unitDetailText.get(1));
                unitDamageList.setText(unitDetailText.get(0));
            }

            if (u.copy){
                rosterTable.add(new UnitImage(u.unitInfo)).align(Align.top);
                rosterTable.row();
            }
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (Gdx.app.getInput().isKeyJustPressed(Input.Keys.ESCAPE)){
            gameState = GameState.FACTION_SELECT;
        }
    }

    /**
     * Called when this screen is no longer the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * @see com.badlogic.gdx.ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see com.badlogic.gdx.ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

    }

    /**
     * @param width
     * @param height
     * @see  com.badlogic.gdx.ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

}
