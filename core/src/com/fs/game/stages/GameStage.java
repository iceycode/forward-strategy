/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.map.Locations;
import com.fs.game.map.MiniMap;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.TextActor;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameMapUtils;


/** The stage which contains the tiled map
 *
 * @author Allen
 *
 * NOTES:
 * CLIPPING: Took me a while to figure out to clip stage but found the solution!
 *  {@link src="https://stackoverflow.com/questions/22576569/libgdx-how-to-clip" }
 *  Need to clip other stages as well, in order to see all actors
 *
 * TILEDMAP: instead of using "world units", I am using screen pixels to make things less confusing.
 *
 * CAMERA SCROLLING: using InputAdapter for mouse (or touch) drag across tiled map
 *
 */
public class GameStage extends Stage implements StageListener {

	final String LOG = "GAMESTAGE LOG: ";

 	public TiledMap tiledMap; 	//creates the actual map
    boolean largeMap = false; //if true, then large, scrollable map

	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
    final float ASPECT_RATIO = SCREENHEIGHT/SCREENWIDTH; //aspect ratio of view

    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();

    final Vector3 P1_POS = Constants.P1_POS;
    final Vector3 P2_POS = Constants.P2_POS;

    //tile width/height in pixels
    float tiledMap_width;
    float tiledMap_height;
    final float TILE_WIDTH = 32;

    float unitScale = 1/TILE_WIDTH;

    //what can be seen by map view
	final float MAP_VIEW_WIDTH = Constants.MAP_VIEW_WIDTH;
	final float MAP_VIEW_HEIGHT = Constants.MAP_VIEW_HEIGHT;
    final float MAP_WIDTH = 40*32;
    final float MAP_HEIGHT = 30*32;
    // X & Y screen position of TileMap on Screen
    final float MAP_X = Constants.MAP_X;
    final float MAP_Y = Constants.MAP_Y;

    TiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
	Viewport viewport;

    OrthographicCamera miniCamera; //minimap camera, which draws actors in corner scaled down
    Batch miniBatch; //draws minimap batch
    final float SCALE = 3; //scale everything down by factor of 3

    int mPlayer ; //multiplayer player id (1 or 2)
    public int currPlayer = 1; //player currently going, starts at 1
    Unit chosenUnit; //current chosen unit

    String aiName = "GameAI";
    public boolean aiTurn = false; //whether it is ai turn

    MessageDispatcher aiMessenger;

    StageListener infoStageListener; //listener is InfoStage
    MinimapListener minimapListener;

    //global fields for showing message over GameMap
    float msgTime = 0f; //time start for message
    float endTime = 2f; //time message stops
    TextActor textActor;
    String msg = "START GAME!"; // game message, initially start game
    String playerTurnMsg = "PLAYER " + currPlayer + " TURN";


    //updates minimap view based on current camera view
    public interface MinimapListener {
        void updateView(float x, float y);
    }

//    private Unit attacker;
//    private Array.ArrayIterator<Unit> unitIter; //iterates over units
//    Array<Panel> panelArray;
    //		super(new ScalingViewport(Scaling.stretch, VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
//        new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)));
	public GameStage(int mapID) {

        setupTiledMap(mapID);
        //this also sets up this stages panelArray
        GameMapUtils.setupGridElements(this); //setup Table & Actors thru GameMapUtils
//        Locations.getLocations().generatePanelGraph(GameData.panelMatrix); //generate PanelGraph in Locations

        if (mapID == 4){
            setupViewLarge(); //only 1/4 of tiled map will show
        }
        else{
            setupView();
        }

        setTextActor(); //textActor for displaying messages
    }


    protected void setupTiledMap(int mapID){
        //set up the tiled map & renderer
        this.tiledMap = GameMapUtils.getTiledMap(mapID);

        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

	
	//sets up camera
	public void setupView(){
		camera = new OrthographicCamera(Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
//		camera.setToOrtho(false, 800, 500); //FIXED: got rid of it

 		//tiled map lines up well with this setup
        camera.position.set(MAP_X + 112, MAP_Y + 50, 0);
		camera.update();

        //create and set the viewport to manage camera
        viewport = new ScalingViewport(Scaling.fit, SCREENWIDTH , SCREENHEIGHT , camera);
        setViewport(viewport);
//        getViewport().setScreenBounds((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        camera.update();

 	}

    protected void setupViewLarge(){
        largeMap = true;
        tiledMap_width = 40*32;
        tiledMap_height = 30*32 ;

        camera = new OrthographicCamera(MAP_WIDTH, MAP_HEIGHT); //MAP_WIDTH, MAP_HEIGHT

        camera.position.set(MAP_X, MAP_Y, 0);
//        camera.update();

        viewport = new ScalingViewport(Scaling.fill, SCREENWIDTH , SCREENHEIGHT, camera);
//        viewport.setScreenBounds((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        setViewport(viewport);

        log("Camera position when starting: " + camera.position.x + ", " + camera.position.y);
        log("Viewport position when starting (should be 0,0) : " + viewport.getScreenX() + ", " + viewport.getScreenY());
    }

//
//    protected void setupMessangers(){
//        aiMessenger = new MessageDispatcher();
//        playerMessenger = new MessageDispatcher();
//    }

    //sets up textActor & adds to HUD stage, stage
    protected void setTextActor(){
        textActor = new TextActor("retro1", Constants.TURN_MSG_COORD);
        addActor(textActor);
    }

    /** adds all player's units to stage
     *
     * @param unitArray : an Array of Units that are added to stage
     */
    public void addUnits(Array<Unit> unitArray ){
        for (Unit u : unitArray){
            if (u.getOwner() != GameData.playerName)
                u.setTouchable(Touchable.disabled);
            addActor(u);
        }
    }


    public void setupUnitsMulti(int player, String faction, String enemyFaction){
        this.mPlayer = player; //set the player

        GameData.playerUnits = TestUtils.randomMultiplayerSetup(mPlayer, GameData.playerName, faction);
        addUnits(GameData.playerUnits);

        GameData.enemyUnits = TestUtils.randomMultiplayerSetup(player == 1 ? 2 : 1, GameData.enemyName, enemyFaction);
        addUnits(GameData.enemyUnits);

        log("GameData p1Units: " + GameData.p1Units.toString(", "));
        log("GameData p2Units: " + GameData.p2Units.toString(", "));


        Locations.getLocations().initLocations();

        GameMapUtils.togglePlayerUnits(1, this);
    }

    /** gets updated data from multiplayerscreen
     *
     * @param player : player who is doing the sending
     * @param data : data from multiplayer screen
     */
    public void updateUnit(int player, UnitData data){
        String name = data.getOwner();

        Unit u = player == 1 ? GameData.p1Units.get(data.getName()) : GameData.p2Units.get(data.getName());
        System.out.println("Unit " + data.getName() + " is enemy, owned by "+ name);
        u.updateUnit(data);

//        int unitID = data.getUnitID();
//        Array<Unit> unitsInGame = GameMapUtils.findAllUnits(getActors());
//        for (Unit u : unitsInGame){
//            if (unitID == u.getUnitID() && u.getOwner().equals(name)){
//                System.out.println("Unit " + u.getName() + " is enemy, owned by "+ name);
//                u.updateUnit(data);
//
//                break;
//            }
//        }
    }


    boolean showMsg = true; //if true, shows message
    public void showMessage(float delta){
        msgTime += delta;

        if (msgTime < endTime){
            //check if actor is in back, since it will have ZIndex of 0
            if (textActor.getZIndex() == 0)
                textActor.toFront();
        }
        else{
            textActor.toBack();
            msgTime = 0;
            showMsg = false;
        }
    }

    /** Shows a message over all other actors on stage
     *
     * @param message : message to show
     * @param msgTime : time to show it for
     */
    public void setStageMessage(String message, float msgTime){
        showMsg = true;
        textActor.setText(message);
        this.endTime = msgTime;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX < MAP_WIDTH && screenY < MAP_HEIGHT && screenY > MAP_Y && screenX > MAP_X)
            return super.touchDown(screenX, screenY, pointer, button);

        return false;
    }



    boolean isDragging = false; //flag for dragging
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if (largeMap){
            isDragging = true;
            camera.unproject(curr.set(screenX, screenY, 0));
            if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
                camera.unproject(delta.set(last.x, last.y, 0));
                delta.sub(curr);
                float nx = camera.position.x + delta.x;
                float ny = camera.position.y + delta.y;
                if (nx <= MAP_WIDTH + MAP_X/2 && ny <= MAP_HEIGHT + MAP_Y/2 && nx >= MAP_X*2 && ny >= MAP_Y*2){
                    camera.position.add(delta.x, delta.y, 0);
                }
                else{
                    log("Went past bounds!");
                    return false;
                }
            }
            last.set(screenX, screenY, 0);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        last.set(-1, -1, -1);
        if (isDragging){
            log("New Camera position after dragging: " + camera.position.x + ", " + camera.position.y);
            isDragging = false;
        }

        if (screenX < MAP_VIEW_WIDTH && screenY < MAP_VIEW_HEIGHT && screenY > MAP_Y && screenX > MAP_X)
            return super.touchUp(screenX, screenY, pointer, button);

        return false;
    }

    /* stage maps draw method */
    @Override
    public void draw() {

        viewport.apply();
        Gdx.gl20.glScissor((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);

        //render(); //renders the tiled map & actors

        super.draw(); //updates viewport/camera & draws actors


    }


    /**
     *  map stage act method
     *
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (showMsg)
            showMessage(delta);
    }


    public void setCurrentUnit(Unit unit){
        if (unit==null || chosenUnit!=unit) {
            this.chosenUnit = unit;
            infoStageListener.updateUnitInfo(unit);
        }
    }


    public void setInfoStageListener(StageListener stage){
        this.infoStageListener = stage;
    }


    //returns multiplayer player number
    public int getPlayer(){
        return mPlayer;
    }


    public void updateScore(Unit unit){
        int player = unit.player; //get unit player

        //set score based on unit size
        int score = unit.getUnitSize() == Unit.SMALL ? 10
                : unit.getUnitSize() == Unit.MEDIUM ? 20
                : 30;

//        int resources = score*5; //resources for creating buildings TODO: implement resources for buildings
        infoStageListener.updatePlayerScore(player, score);
    }


    @Override
    public void updateUnitInfo(Unit unit) {
        //NOTHING DONE HERE
    }

    @Override
    public void updatePlayerScore(int player, int score) {
        //NOTHING DONE HERE
    }

    @Override
    public void changeView(Vector3 pos) {
        log("Changed camera based on minimap to positions: " + pos.x + ", " + pos.y);
        camera.project(pos);
        log("After unprojecting: " + pos.x + ", " + pos.y);
        camera.position.set(pos.x, pos.y, 0);
//        camera.translate(x, y, 0);
        camera.update();
    }

    @Override
    public void changePlayer(int nextPlayer) {
        if (largeMap){
            //set camera position to other half of board
            Vector3 playerPos = nextPlayer == 2 ? P2_POS : P1_POS;
            camera.position.set(playerPos);
            camera.update();
        }


        GameMapUtils.togglePlayerUnits(nextPlayer, this);
        currPlayer = nextPlayer;

        msg = playerTurnMsg;
        setStageMessage(msg, 3f);
    }

    @Override
    public void setMMAreaPosition(float x, float y) {
        //NOTHING DONE HERE
    }

    public int getCurrPlayer(){
        return currPlayer;
    }


    public void updateMinimapView(float x, float y){
//        Vector3 screenLoc = camera.project(new Vector3(x, y, 0));
        log("Minimap changed map view to: (" + x + ", "  + y + ")");
        minimapListener.updateView(x, y);
    }

    public void setMinimapListener(MiniMap mm){
        this.minimapListener = mm.getMmView();
    }

    private void log(String message){
        Gdx.app.log(LOG, message);
    }

}
