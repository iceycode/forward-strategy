/**
 * 
 */
package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.map.MiniMap;
import com.fs.game.tests.TestUtils;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameMapUtils;


/** the stage which contains the tiled map
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
public class GameStage extends Stage {
	
	final String LOG = "GAMESTAGE LOG: ";

 	public TiledMap tiledMap; 	//creates the actual map
    int[] backgroundLayers = { 0, 1, 2, 3 }; // don't allocate every frame!
    int[] foregroundLayers = {4, 5, 6}; //the layers showing terrain

	//variables related to stage/screen placements
	final float SCREENWIDTH = Constants.SCREENWIDTH;
	final float SCREENHEIGHT = Constants.SCREENHEIGHT;
    final float ASPECT_RATIO = SCREENHEIGHT/SCREENWIDTH; //aspect ratio of view

    //tile width/height in pixels
    float tiledMap_width;
    float tiledMap_height;
    final float TILE_WIDTH = 32;
    final float TILE_HEIGHT = 32;
    float unitScale = 1/TILE_WIDTH;

    //what can be seen by map view
	final float MAP_VIEW_WIDTH = Constants.MAP_VIEW_WIDTH;
	final float MAP_VIEW_HEIGHT = Constants.MAP_VIEW_HEIGHT;
    //x & y screen position of TileMap on Screen
    final float MAP_X = Constants.MAP_X;
    final float MAP_Y = Constants.MAP_Y;

    TiledMapRenderer tiledMapRenderer;
	OrthographicCamera camera;
    CameraController cameraController;
	Viewport viewport;


    String aiName = "GameAI";
    public boolean aiTurn = false; //whether it is ai turn

    MessageDispatcher aiMessenger;


    MinimapListener minimapListener;

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



        if (mapID == 4){
            setupViewLarge(); //only 1/4 of tiled map will show
        }
        else{
            setupView();
        }



        cameraController = new CameraController(camera);
        addListener(cameraController); //add custom drag listener class
//        setCameraDragListener(); //sets DragListener
//        setDebugAll(true);
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
        getViewport().setScreenBounds((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        camera.update();

 	}

    protected void setupViewLarge(){
        tiledMap_width = 40*32;
        tiledMap_height = 30*32 ;

        camera = new OrthographicCamera(MAP_VIEW_WIDTH, MAP_VIEW_HEIGHT);
        camera.position.set(MAP_X, MAP_Y, 0);
        camera.update();

        viewport = new ScalingViewport(Scaling.none, SCREENWIDTH, SCREENHEIGHT, camera);
        viewport.setCamera(camera);
//        viewport.setScreenBounds((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        setViewport(viewport);
    }

//
//    protected void setupMessangers(){
//        aiMessenger = new MessageDispatcher();
//        playerMessenger = new MessageDispatcher();
//    }



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
        Array<Unit> unitsInGame = GameMapUtils.findAllUnits(getActors());

        for (Unit u : unitsInGame){
            if (unitID == u.getUnitID() && u.getOwner().equals(name)){
                System.out.println("Unit " + u.getName() + " is enemy, owned by "+ name);
                u.updateUnit(data);

                break;
            }
        }
    }


    /** Renders the tiled map, updating & setting camera view first.
     *
     *
     */
//    public void render() {
//        //set tiledMapRenderer view
////    	tiledMapRenderer.getBatch().setProjectionMatrix(camera.combined);
////    	tiledMapRenderer.setView(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight);
//        camera.update();
//        tiledMapRenderer.setView(getBatch().getProjectionMatrix(), MAP_X, MAP_Y, tiledMap_width, tiledMap_height);
////        tiledMapRenderer.setView(camera);
//
////        getBatch().end();
//     	tiledMapRenderer.render();
////        getBatch().begin();
//    }


    /* stage maps draw method */
    @Override
    public void draw() {

        viewport.apply();
        Gdx.gl20.glScissor((int) MAP_X, (int) MAP_Y, (int) MAP_VIEW_WIDTH, (int) MAP_VIEW_HEIGHT);
        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);

        //render(); //renders the tiled map & actors

        super.draw(); //so that Units & Panels draw on top of tiled map
    }


    /**
     *  map stage act method
     *
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        showPosInfo();
    }

    //shows positions of camera/viewport
    boolean showPosInfo = true;
    protected void showPosInfo(){
        if (showPosInfo){
            //show pos of camera
            log("Position of Camera: " + camera.position.x + ", " + camera.position.y);
            log("Position of Viewport: " + viewport.getScreenX() + ", " + viewport.getScreenY());
//            log("TiledMapRenderer positions is: (" + Float.toString(tiledMapRenderer.getViewBounds().getX()) + ", "
//                    + Float.toString(tiledMapRenderer.getViewBounds().getY()));
            showPosInfo = false;
        }
    }


    /** Sets listener for minimap input listener
     *
     * @param stage
     */
    public void setMapListener(InfoStage stage){
        stage.miniMap.setMapviewSetter(cameraController);
    }


    /** Controls camera via DragListener
     *
     */
    public class CameraController extends DragListener implements MiniMap.MapviewSetter {

        private final OrthographicCamera camera;
        final Vector2 curr = new Vector2();
        final Vector2 last = new Vector2(-1, -1);
        final Vector2 delta = new Vector2(); //how much tiled map has moved by

        public CameraController(OrthographicCamera camera) {
            this.camera = camera;
            setTapSquareSize(32);
        }

        @Override
        public void dragStart(InputEvent event, float x, float y, int pointer) {
            last.set(x, y);
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            curr.set(x, y);
            camera.translate(getDeltaX(), getDeltaY(), 0);

            updateMinimapView(camera.position.x/2, camera.position.y/2);
        }

        @Override
        public void dragStop(InputEvent event, float x, float y, int pointer) {
            camera.update();
        }

        @Override
        public void updateCameraPosition(float x, float y) {

        }
    }


    public void updateMinimapView(float x, float y){
        Vector3 screenLoc = camera.project(new Vector3(x, y, 0));
        minimapListener.updateView(x, y);
    }

    public void setMinimapListener(MiniMap mm){
        this.minimapListener = mm;
    }

    private void log(String message){
        Gdx.app.log(LOG, message);
    }
}
