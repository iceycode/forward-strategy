/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.maps.Panel;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.UnitUtils;


/** the stage which contains the tiled map
 * 
 * @author Allen
 *
 */
public class GameStage extends Stage {
	
	final String LOG = "GAMESTAGE LOG: ";

	public TiledMap tiledMap; 	//creates the actual map
	private Array<Panel> panelArray;
	
	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
	final float GRID_WIDTH = Constants.GRID_WIDTH;
	final float GRID_HEIGHT = Constants.GRID_HEIGHT;
	final float GRID_X = Constants.GAMEBOARD_X;
	float GRID_Y = Constants.GAMEBOARD_Y;
	float scale = 1/32f;
	
	OrthogonalTiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	Viewport viewport;

    Unit chosenUnit;
    int[] UNIT_STATE = Constants.UNIT_STATES;

    //for multiplayer setup
    public Array<Unit> allUnits;
    public Array<Unit> p1Units;
    public Array<Unit> p2Units;

    public Array<Unit> playerUnits;
    public Array<Unit> enemyUnits;

    public Array<UnitData> unitsData;

    public String playerName;
    public int currentState;


	/**
	 * instantiated by MapsFactory
	 */
	public GameStage(TiledMap tiledMap) {
//		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
		//sets up the tiled map & renderer
        this.tiledMap = tiledMap;
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        this.allUnits = new Array<Unit>();

        setupCamera();//sets up the camera

        //this also sets up this stages panelArray
        GameUtils.Map.setupGridElements(this);//get Table & Actors from GameBoard class

        addUnits();

    }
	
	//sets up camera
	public void setupCamera(){
		camera = new OrthographicCamera(Constants.SCREENHEIGHT, Constants.SCREENWIDTH);
		camera.setToOrtho(false, 800, 500);
		
 		//tiled map lines up well with this setup
		//camera.position.set(GRID_X-16, GRID_Y+50, 0);
        camera.position.set(GRID_X + 112, GRID_Y +50, 0);
		camera.update();	
        
        viewport = new ScreenViewport();
		viewport.setWorldHeight(SCREENHEIGHT); //sets the camera screen view dimensions
		viewport.setWorldWidth(SCREENWIDTH);
		viewport.setCamera(camera);
		
 	}


	/** initializes & sets units onto stage
	 * creates 7 units on board 
	 *  - gets info from an array in an array
	 *  
	 */
	public void addUnits() {

        if (GameData.testType==1) {
            TestUtils.test2Units(this);
        }
		else if (GameData.testType == 2){
            TestUtils.testBoardSetup2_16x12(this); //test setup b/w humans & reptoids
        }
        else{
            this.currentState = Constants.STAGE_STATES[2]; //=3
        }

	}


    public void addUnitsToStage(int player){
        for (Unit u : p1Units){
            addActor(u);
        }

        for (Unit u: p2Units){

        }
    }

    public void updateUnitData(int player, Array<Unit> units){
        if (player == 1){
            for (int i = 0; i < units.size; i++){
                p1Units.get(i).setX(units.get(i).getX());
                units.get(i).getY();
            }
        }
    }

    public void initialPlayerSetup(int player, String name){
        Array<UnitInfo> unitInfoArray = new Array<UnitInfo>();



        if (player == 1){
            playerUnits = UnitUtils.Setup.setupUnits(name, unitInfoArray, player, Constants.UNITS_POS_LEFT, this);
        }
        else{
            playerUnits = UnitUtils.Setup.setupUnits(name, unitInfoArray, player, Constants.UNITS_POS_LEFT, this);
        }
    }


    public void initialEnemySetup(int player, String name, Array<UnitData> unitDataArr){
        Array<UnitInfo> unitInfoArray = new Array<UnitInfo>();
        for (UnitData unitData : unitDataArr) {
            UnitInfo unitInfo = Assets.unitInfoArray.get(unitData.getUnitID() - 1);
            unitInfoArray.add(unitInfo);
        }

        if (player == 1){
            enemyUnits = UnitUtils.Setup.setupUnits(name, unitInfoArray, player, Constants.UNITS_POS_LEFT, this);
        }

    }



    /**renders the tiled map
     * 
     */
    public void render() {

    	tiledMapRenderer.getSpriteBatch().setProjectionMatrix(camera.combined);
    	tiledMapRenderer.setView(camera.combined, GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);
    	camera.update();
    	tiledMapRenderer.setView(camera);
     	tiledMapRenderer.render();
    }



    public void updateStage(Array<Unit> updatedUnits, int currPlayer){
        if (currPlayer == 1){
            if (p1Units != null)
                p1Units.clear();
            else
                p1Units = updatedUnits;

            for (Unit u: updatedUnits){
                p1Units.add(u);
            }
        }
        else{
            if (p2Units != null)
                p2Units.clear();
            else
                p1Units = updatedUnits;

            for (Unit u: updatedUnits){
                p2Units.add(u);
            }
        }

    }


    public void updateEnemyUnits(Array<Unit> updatedUnits){

        for (int i = 0; i < updatedUnits.size; i++){


        }
    }



    /* stage maps draw method */
    @Override
    public void draw() {
    	render(); //renders the tiled map & actors
    	super.draw();
    }


    
    /**
     *  map stage act method 
     *  */
    @Override
    public void act(float delta) {

     	super.act(delta);

    }

	/**
	 * @return the camera
	 */
	@Override
	public OrthographicCamera getCamera() {
		return camera;
	}

	/**
	 * @return the viewport
	 */
	@Override
	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * @param viewport the viewport to set
	 */
	@Override
	public void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}


	public Array<Panel> getPanelArray() {
		return panelArray;
	}

	public void setPanelArray(Array<Panel> panelArray) {
		this.panelArray = panelArray;
	}


}
