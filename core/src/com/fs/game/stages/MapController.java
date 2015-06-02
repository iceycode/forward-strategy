package com.fs.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.fs.game.constants.Constants;
import com.fs.game.map.MiniMap;

/** Custom {@link DragListener} class that implements MapViewSetter
 *
 * @author Allen
 */
public class MapController extends DragListener implements MiniMap.MapviewSetter {

    private GameStage gameStage;
    private final OrthographicCamera camera;
    final Vector2 curr = new Vector2();
    final Vector2 last = new Vector2(-1, -1);
    final Vector3 delta = new Vector3(); //stores position which camera lerps
    final float[] MAP_BOUNDS_X = {Constants.MAP_X, Constants.MAP_X + Constants.MAP_SIZE_L[0]};
    final float[] MAP_BOUNDS_Y = {Constants.MAP_Y, Constants.MAP_Y + Constants.MAP_SIZE_L[1]};
    final float[] VIEW_BOUNDS_X = {Constants.MAP_X, Constants.MAP_X + Constants.MAP_VIEW_WIDTH};
    final float[] VIEW_BOUNDS_Y = {Constants.MAP_Y, Constants.MAP_Y + Constants.MAP_VIEW_HEIGHT};
    float camX; //camera X position, lower left corner
    float camY; //camera Y position

    public MapController(GameStage gameStage, OrthographicCamera camera) {
        this.gameStage = gameStage;
        this.camera = camera;
        setTapSquareSize(32);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//        if (!isInViewBounds(x, y)) {
//            cancel();
//            return false;
//        }

        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void dragStart(InputEvent event, float x, float y, int pointer) {
        //set where camera bottom left is
        camX = camera.position.x - gameStage.MAP_WIDTH/2;
        camY = camera.position.y - gameStage.MAP_HEIGHT/2;

        log("Dragstart positions, screen/stage: " + getTouchDownX() + ", " + getTouchDownY());

        if (isInViewBounds(getTouchDownX(), getTouchDownY()))
            delta.set(x, y, 0);
        else
            cancel();
    }

    @Override
    public void drag(InputEvent event, float x, float y, int pointer) {
        //NOTE: need to multiply positions of delta by 2 since camera positions divided by 2 (centered)
        if (isInMapBounds(delta.x+getDeltaX(), delta.y+getDeltaY())){
            delta.add(getDeltaX(), getDeltaY(), 0);
//            camera.translate(getDeltaX()/2, getDeltaY()/2, 0);
            camera.position.lerp(delta, .5f);
            log("Dragging, position: " + x + "," + y);
            gameStage.updateMinimapView(camera.position.x / 2, camera.position.y / 2);
        }
        else{
            cancel();
        }

    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer) {
        delta.set(camera.position.x, camera.position.y, 0);
        log("Dragstop position: " + x + ", " + y);
    }

    protected boolean isInMapBounds(float x, float y){
        return x > MAP_BOUNDS_X[0] && x < MAP_BOUNDS_X[1] &&
                 y > MAP_BOUNDS_Y[0] &&  y< MAP_BOUNDS_Y[1];
    }

    /** Returns true if click happened within view bounds
     *
     * @param x : x position of click
     * @param y : y position of click
     * @return True if is within view bounds
     */
    protected boolean isInViewBounds(float x, float y){
        return x < VIEW_BOUNDS_X[1] && x > VIEW_BOUNDS_X[0] &&
                y > VIEW_BOUNDS_Y[0] &&  y < VIEW_BOUNDS_Y[1];
    }


    @Override
    public void updateCameraPosition(float x, float y) {
        camera.translate(x, y, 0);
    }


    private void log(String message){
        Gdx.app.log("MapController LOG", message);
    }
}
