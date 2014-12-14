/**
 * 
 */
package com.fs.game.stages;

import appwarp.WarpController;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.fs.game.maps.Panel;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameUtils;


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

    public Array<UnitData> unitDataArray;

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

        playerUnits = new Array<Unit>();
        enemyUnits = new Array<Unit>();
        unitDataArray = new Array<UnitData>();

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

	}

    /** adds all player's units to stage
     *
     * @param unitArray
     * @param playerName
     */
    public void addUnits(Array<Unit> unitArray, String playerName){
        for (Unit u : unitArray){

            addActor(u);

            if (GameData.playerName.equals(playerName)){
                UnitData unitData = setUnitData(u);
                unitDataArray.add(unitData);
                playerUnits.add(u);
            }
            else{
                enemyUnits.add(u);
            }
        }
    }


    /** writes & sends a JSONObject with updated unit states
     *
     * @param player : current player
     * @param score : player's score
     *
     */
    public void sendPlayerData(int player, int score){

        try {
            Array<UnitData> unitDatas = getPlayerUnitData();

            UserData userData = new UserData();
            userData.setPlayer(player);
            userData.setScore(score);
            userData.setUnitList(unitDatas);
            userData.setName(GameData.playerName);
            userData.setPlayerTurn(GameData.playerTurn);

            Json json = new Json();
            json.setIgnoreUnknownFields(true);
            String data = json.toJson(userData);

            WarpController.getInstance().sendGameUpdate(data);

        } catch (Exception e) {
            // exception in sendPlayerData
            System.out.println("Error writing json! ");
            e.printStackTrace();
        }
    }


    public Array<UnitData> getPlayerUnitData(){
        Array<UnitData> unitDatas = new Array<UnitData>();
        Array<Unit> playerUnits = GameUtils.StageUtils.findPlayerUnits(GameData.playerName, this);

        for (Unit unit: playerUnits)
            unitDatas.add(unit.getUnitData());

        return unitDatas;
    }


    /** updates stage units based on name
     *
     * @param unitDataArray
     * @param playerName
     */
    public void updateStageUnits(Array<UnitData> unitDataArray, String playerName){
        Array<Unit> playerUnits = GameUtils.StageUtils.findPlayerUnits(playerName, this);

        for (int i = 0; i < playerUnits.size; i++){

            if (playerUnits.get(i).moving || playerUnits.get(i).underattack){
                System.out.println("Unit updated with unitdata");

                float x = unitDataArray.get(i).getUnitPosition().x;
                float y = unitDataArray.get(i).getUnitPosition().y;

                playerUnits.get(i).state.setValue(unitDataArray.get(i).getState());
                playerUnits.get(i).setPosition(x, y);
            }
        }
    }


    public UnitData setUnitData(Unit unit){

        UnitData unitData = new UnitData();
        unitData.setUnitID(unit.getUnitID());
        unitData.setSize(unit.getUnitSize());

        unitData.setOwner(GameData.playerName);
        unitData.setState(unit.state.getValue());
        unitData.setDamage(unit.damage);
        unitData.setHealth(unit.health);
        unitData.setUnitPosition(new Vector2(unit.getX(), unit.getY()));

        unit.setUnitData(unitData);

        return unitData;
    }



    public void updateUnit(UnitData data){
        Array<Unit> playerUnits = GameUtils.StageUtils.findPlayerUnits(GameData.enemyName, this);

        String name = data.getOwner();
        int unitID = data.getUnitID();

        for (Unit u : playerUnits){
            if (name.equals(GameData.enemyName) && unitID==u.getUnitID()){
                u.updateUnit(data);
                break;
            }
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

    public Array<UnitData> getUnitDataArray() {
        return unitDataArray;
    }

    public void setUnitDataArray(Array<UnitData> unitDataArray) {
        this.unitDataArray = unitDataArray;
    }
}
