/**
 * 
 */
package com.fs.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;
import com.fs.game.screens.GameScreen;
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
public class UnitsScreen implements Screen {
	
	final MainGame game; //game with the main Sprite
	final String LOG = "unit menu log: ";
	final String REMOVE_ASK = "Are you sure you want to remove this unit?";
	final String ADD_ASK = "Are you sure you want to add this unit?";
			
	OrthographicCamera camera;
	Stage stage;
    GameState gameState;

    HashMap<Integer, Array<UnitImage>> unitImages; //1=small, 2=medium, 3=large
    Array<UnitImage> factionUnits;

    Label unitDetail; //shows unit attributes
    Label unitDamageList; //shows unit damage to all other units

	Array<Unit> unitsP1;
	Array<Unit> unitsP2;
	HashMap<Integer, Unit> currChosenUnits;

	String faction; //the faction of units
	Window infoWindow ;
	TextButton textBtn; 		//button for dialog box
	
	ScrollPane scrollPane;  //a scrollable window
	boolean choseUnit = false; //when player officially adds unit





	public UnitsScreen(final MainGame game, String faction) {
		this.game = game;
		this.faction = faction;
        this.gameState = GameState.UNIT_SELECT;

        setupCamera();
		setupStage();
	
	}
	
    public void setupCamera(){
        //sets the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 500);
        camera.update();
    }
	

    public void setupStage(){
        stage = new Stage();
        currChosenUnits = new HashMap<Integer, Unit>(); //stores single players data
        factionUnits = new Array<UnitImage>(); //store the available image units

//        unitTexArr = GameManager.assetManager.getAll(Texture.class, unitTexArr); //get all Textures

        createInfoPanel(); //adds boxes that contain units to the screen
        createUnitImageTable(); //creates image Buttons containing actors/listeners

    }


	/** creates the image buttons which go on stage
	 * 
	 */
	public void createUnitImageTable() {

        Table imageTable = new Table();
        Array<Label> unitLabels = MenuUtils.UnitMenu.createUnitLabels(Constants.UNIT_IMAGE_LABELS, new Array<Label>(), 0);

        unitImages = MenuUtils.UnitMenu.createUnitImages();
        GameData.factUnitImages = new Array<UnitImage>(); //stores all units to choose from

        //creates the labels & UnitImages in a table format
        for (int i = 0; i < unitLabels.size; i++){
            imageTable.add(unitLabels.get(i)).pad(10f).align(Align.left);
            for (int j = 0; j < unitImages.get(i).size; j++){
                imageTable.add(unitImages.get(i).get(j)).pad(5f,5f,5f,5f);
                GameData.factUnitImages.add(unitImages.get(i).get(j));
            }
            imageTable.row();
        }
        imageTable.setPosition(Constants.UNITS_TABLE_X, Constants.UNITS_TABLE_Y);

        stage.addActor(imageTable);
	}


	
	/** panels created that serve as background for units
	 * - small units :: medium units :: large units
	 * 
	 */
	public void createInfoPanel() {

        //----setup for ScrollPane panels as individual units within table----
        //the main pop-up window & widgets
        unitDetail = UIUtils.createLabelInfo();
        unitDamageList = UIUtils.createLabelDamage();

        //scrollTable is the Table which holds the ScrollPane objects
        Table scrollTable = UIUtils.createUnitScrollTable(unitDetail, unitDamageList);
        //scrollTable.setPosition(Constants.INFO_X, Constants.UNITS_TABLE_Y);

        stage.addActor(scrollTable);
		
	}

	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {

		switch(gameState){
            case UNIT_SELECT:
                updateGame();
                break;
            case FACTION_SELECT:
                pause();
                break;
            case MAIN_MENU: //if/when 1st or 2nd player is done
                backToMainMenu();
                break;
            case RUN:
                hide();
                break;
        }
		
	}

    public void updateGame(){
        if (GameData.p1Faction!=null && GameData.p1Units!=null && GameData.p2Faction==null){
            game.setScreen(game.menuScreen);
            GameData.currPlayer = 2;
        }
        else if (GameData.p2Faction!=null && GameData.p2Units!=null && GameData.p1Faction==null){
            game.setScreen(game.menuScreen);
            GameData.currPlayer = 2;
        }
    }


    public void backToMainMenu(){

    }

	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
 
	}


	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        //draws the stage
        stage.act();
        stage.draw();



        if (unitsP1.size == 7){
            gameState = GameState.FACTION_SELECT;
        }//if player 1 has filled units

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            gameState = GameState.FACTION_SELECT;
        }//if escape, goes back to faction screen
	}


	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
        //going on to actual game play if finished with everything
        game.setScreen(new GameScreen(game));
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
        //going to look at other faction screens if paused
        game.setScreen(game.getFactionScreen());
	}


	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {

	}


	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
        //finished if dispose
		game.setScreen(game.getMenuScreen());
		
	}
	 

}
