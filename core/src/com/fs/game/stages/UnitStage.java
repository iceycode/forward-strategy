/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.screens.GameState;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitImage;
import com.fs.game.utils.MenuUtils;
import com.fs.game.utils.UIUtils;

import java.util.HashMap;

/** UnitScreen.java (implements Screen)
 * 
 * - creates a menu to choose Units 
 * - seperates units into small, medium, large
 * - shows unit info when clicked on
 * - 
 * 
 * @author Allen Jagoda
 *
 */
public class UnitStage extends Stage {
	
	final String LOG = "unit menu log: ";
	final String REMOVE_ASK = "Are you sure you want to remove this unit?";
	final String ADD_ASK = "Are you sure you want to add this unit?";
			
    GameState gameState;

    HashMap<Integer, Array<UnitImage>> unitImages; //1=small, 2=medium, 3=large
    Array<UnitImage> factionUnits;

    OrthographicCamera camera;
    ScreenViewport viewport; //viewport needs to coincide with that of Screen

    Label unitDetail; //shows unit attributes
    Label unitDamageList; //shows unit damage to all other units

    int currPlayer;
	HashMap<Integer, Unit> currChosenUnits;

    Table rootTable;
    Table unitImageTable;
    Table infoTable;

	String faction; //the faction of units
	Window infoWindow ;
	TextButton textBtn; 		//button for dialog box
	
//	ScrollPane scrollPane;  //a scrollable window
	boolean choseUnit = false; //when player officially adds unit


	public UnitStage() {

		this.faction = GameData.factions[currPlayer-1];
        this.gameState = GameState.UNIT_SELECT;


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

        this.setViewport(viewport);
    }
	

    public void setupStage(){
        currChosenUnits = new HashMap<Integer, Unit>(); //stores single players data
        factionUnits = new Array<UnitImage>(); //store the available image units

        //create the widgets & add to Table which is added as Actor on UnitStage
        unitImages = MenuUtils.UnitMenu.createUnitImages(faction, factionUnits, this);
        unitDetail = UIUtils.createLabelInfo();
        unitDamageList = UIUtils.createLabelDamage();

        //the tables, all which will be added to rootTable
        infoTable = MenuUtils.UnitMenu.infoPanel(unitDetail, unitDamageList, this); //adds boxes that contain units to the screen
        unitImageTable = MenuUtils.UnitMenu.unitImageTable(unitImages); //creates image Buttons containing actors/listeners

        addActor(infoTable);
        addActor(unitImageTable);

        //rootTable = MenuUtils.UnitMenu.createMainTable(infoTable, unitImageTable);
        //addActor(rootTable);
    }



    @Override
    public void draw(){
        super.draw();
    }




    @Override
    public void act(float delta) {
        //updateGame();
        super.act(delta);
    }

//    public void updateGame(){
//        if (GameData.p1Faction!=null && GameData.p1Units!=null && GameData.p2Faction==null){
//            gameState = GameState.MAIN_MENU;
//            GameData.player = 2;
//        }
//        else if (GameData.p2Faction!=null && GameData.p2Units!=null && GameData.p1Faction==null){
//            gameState = GameState.MAIN_MENU;
//            GameData.player = 1;
//        }
//    }
//




//	public void updateMenu(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        if (unitsP1.size == 7){
//            gameState = GameState.FACTION_SELECT;
//        }//if player 1 has filled units
//
//        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
//            gameState = GameState.FACTION_SELECT;
//        }//if escape, goes back to faction screen
//	}

//	/* (non-Javadoc)
//	 * @see com.badlogic.gdx.Screen#render(float)
//	 */
//	@Override
//	public void render(float delta) {
//
//		switch(gameState){
//            case UNIT_SELECT:
//                updateGame();
//                break;
//            case FACTION_SELECT:
//
//                break;
//            case MAIN_MENU: //if/when 1st or 2nd player is done
//
//                break;
//            case RUN:
//
//                break;
//        }
//
//	}

}
