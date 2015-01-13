/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.actors.Unit;
import com.fs.game.ai.GameAI;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.actors.Panel;
import com.fs.game.tests.TestUtils;
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

    GameAI ai;
    String aiName = "GameAI";
    public boolean aiTurn; //whether it is ai turn

    private Unit chosenUnit;
    private Unit attacker;

    private Array.ArrayIterator<Unit> unitIter; //iterates over units

    /**
	 * instantiated by MapsFactory
	 */
	public GameStage(TiledMap tiledMap) {
//		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
		//sets up the tiled map & renderer
        this.tiledMap = tiledMap;
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);


        setupCamera();//sets up the camera

        //this also sets up this stages panelArray
        GameUtils.Map.setupGridElements(this); //get Table & Actors from GameBoard class

        //TODO: this is temporary setup, maybe add parameter in constructor for game/test type
        if (GameData.testType < 3)
            addUnits();

    }
	
	//sets up camera
	public void setupCamera(){
		camera = new OrthographicCamera(Constants.SCREENHEIGHT, Constants.SCREENWIDTH);
		camera.setToOrtho(false, 800, 500);
		
 		//tiled map lines up well with this setup
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
            TestUtils.testBoardSetup3(this);
        }
	}

    /** adds all player's units to stage
     *
     * @param unitArray
     * @param playerName
     */
    public void addUnits(Array<Unit> unitArray, String playerName){
        for (Unit u : unitArray){
            addActor(u);
        }
    }


    public void setupUnits(int player, int enemyPlayer, String enemyName, String faction, String enemyFaction){
        GameData.playerUnits = TestUtils.randomMultiplayerSetup(player, GameData.getInstance().playerName, faction);
        addUnits(GameData.playerUnits, GameData.getInstance().playerName);

        GameData.enemyUnits = TestUtils.randomMultiplayerSetup(enemyPlayer, enemyName, enemyFaction);
        addUnits(GameData.enemyUnits, enemyName);

    }

    /** gets data from multiplayerscreen
     *
     * @param data
     */
    public void updateUnit(UnitData data){
        String name = data.getOwner();
        int unitID = data.getUnitID();
        Array<Unit> unitsInGame = GameUtils.StageUtils.findAllUnits(getActors());

        for (Unit u : unitsInGame){
            if (unitID == u.getUnitID() && u.getOwner().equals(name)){
                System.out.println("Unit " + u.getName() + " is enemy, owned by "+ name);
                u.updateUnit(data);

                break;
            }
        }
    }


    /**renders the tiled map
     * 
     */
    public void render() {

    	tiledMapRenderer.getBatch().setProjectionMatrix(camera.combined);
    	tiledMapRenderer.setView(camera.combined, GRID_X, GRID_Y, GRID_WIDTH, GRID_HEIGHT);
    	camera.update();
    	tiledMapRenderer.setView(camera);
     	tiledMapRenderer.render();
    }


    /* stage maps draw method */
    @Override
    public void draw() {
    	render(); //renders the tiled map & actors
    	super.draw();
    }

    /**
     *  map stage act method
     *
     */
    @Override
    public void act(float delta) {
     	super.act(delta);

        unitIter = GameUtils.StageUtils.unitIterator(getActors());


        if (chosenUnit != null && !chosenUnit.attacked){
            UnitUtils.Attack.attackNearbyUnits(chosenUnit, this);
            chosenUnit.attacked = true; //since unit is done, set to null
        }
    }

	public Array<Panel> getPanelArray() {
		return panelArray;
	}

	public void setPanelArray(Array<Panel> panelArray) {
		this.panelArray = panelArray;
	}

    public Unit getChosenUnit() {
        return chosenUnit;
    }

    public void setChosenUnit(Unit chosenUnit) {
        this.chosenUnit = chosenUnit;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public void setAttacker(Unit attacker) {
        this.attacker = attacker;
    }

    public Array.ArrayIterator<Unit> getUnitIter() {
        return unitIter;
    }


    private void log(String message){
        Gdx.app.log(LOG, message);
    }

}
