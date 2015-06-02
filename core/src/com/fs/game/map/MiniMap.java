package com.fs.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;
import com.fs.game.stages.MapController;

/** A small minimap so players can tell where Units are in main grid.
 *  This group of actors goes on InfoStage and renders Shapes in actors using ShapeRenderer
 *  3x3 pixel tiles, so for example, 40x30 of 32x32 tiles would make 120x90 window
 *
 * Created by Allen on 5/23/15.
 */
public class MiniMap {

    private final float MM_X = Constants.MM_X;
    private final float MM_Y = Constants.MM_Y;
    int rows;
    int cols;
    float width;
    float height;

    private float scale = 3/32; //minimap panel to actual panel size is scale
    private float viewScaleX = 16/40; //scale is portion of full map seen in view
    private float viewScaleY = 12/40; //same as above but for y-axis


    // A mini map panel array containing objects with ShapeRenderer
    // which represent what is on the board, same with board outlines
    // and view representing area player sees on actual map.
    ShapeRenderer renderer; //renders outline of minimap
    Array<MiniPanel> markers = new Array<MiniPanel>();
    MinimapController mmView; //is the view representing where player is on map

    OrthographicCamera miniCam; //minimap camera

    //updates actual map view based on rectangle position of view bounds
    public interface MapviewSetter {
        void updateCameraPosition(float x, float y);
    }


    public MiniMap(int rows, int cols) {

        this.rows = rows;
        this.cols = cols;

        this.width = rows * 3;
        this.height = cols * 3;

        miniCam = new OrthographicCamera(width, height);
        miniCam.zoom = scale;

        //create shape renderer
        renderer = new ShapeRenderer();

        //set viewBounds Rectangle - portion of game map scaled in minimap
        mmView = new MinimapController(new Rectangle(MM_X, MM_Y, width*viewScaleX, height*viewScaleY));

        setupMiniPanels(); //adds minipanels to this group

    }

    protected void setupMiniPanels(){
        for (int x = 0; x < rows; x++){
            for (int y = 0; y < cols; y++){
                Vector2 position = new Vector2(x*3 + MM_X, y*3 + MM_Y);
                MiniPanel mp = new MiniPanel(GameData.panelMatrix[x][y], position);
                markers.add(mp);
            }
        }
    }



    public void render(Batch batch){
        batch.begin();
        //need to end current batch, causes to be flushed
        batch.end();

        //transform & projection matrices used for renderer
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.translate(MM_X, MM_Y, 0);

        //render minimap BOUNDS shape with ShapeRenderer
        renderer.begin(ShapeRenderer.ShapeType.Line); //just an outline
        renderer.setColor(Color.CYAN);
        renderer.rect(MM_X, MM_Y, width, height);
        renderer.end();

        //start batch again
        batch.begin();

        //render minimap markets (aka minipanels)
        for (MiniPanel mp : markers){
            mp.renderMinipanel(batch);
        }

        //renders small rectangle representing player's view on actual map
        mmView.renderView(batch);

        batch.end();
    }



    public void dispose(){
        renderer.dispose();
        for (MiniPanel p : markers){
            p.dispose();
        }
        mmView.viewRenderer.dispose();
    }

    public MinimapController getMmView(){
        return mmView;
    }


    /** InputAdapter for minimap rectangle area
     *
     */
    public class MinimapController extends InputAdapter implements GameStage.MinimapListener{

        Rectangle viewBounds;
        public MapviewSetter mapviewSetter; //sets map view on GameStage

        float[] BOUNDS_X = {MM_X, MM_X + width};
        float[] BOUNDS_Y = {MM_Y, MM_Y + height};

        ShapeRenderer viewRenderer; //renders box in minimap area of what player sees


        public MinimapController(Rectangle view){
            this.viewBounds = view;
            this.viewRenderer = new ShapeRenderer();
        }


        public void renderView(Batch batch){
            batch.end();

            //transform & projection matrices used for renderer
            viewRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            viewRenderer.setTransformMatrix(batch.getTransformMatrix());
            viewRenderer.translate(viewBounds.x, viewBounds.y, 0);

            //render shape with ShapeRenderer
            viewRenderer.begin(ShapeRenderer.ShapeType.Line); //just an outline
            viewRenderer.setColor(Color.NAVY);
            viewRenderer.rect(viewBounds.x, viewBounds.y, viewBounds.getWidth(), viewBounds.getHeight());
            viewRenderer.end();

            //start batch again
            batch.begin();
        }


        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (screenX > BOUNDS_X[0] && screenX < BOUNDS_X[1] && screenY > BOUNDS_Y[0] && screenY < BOUNDS_Y[1]){
                float dx = (screenX-BOUNDS_X[0])/2;
                float dy = (screenY-BOUNDS_Y[0])/2;

                //need to make sure that rectangel & camera do not go out of minimap & map bounds respectively
                if (dx > BOUNDS_X[0] && dx < BOUNDS_X[1] && dy > BOUNDS_Y[0] && dy < BOUNDS_Y[1]){
//                    viewBounds.setPosition(dx, dy); //set viewbounds to new position
//                    mapviewSetter.updateCameraPosition(dx/scale - width, dy/scale - height);
                }

            }

            return true;
        }

        @Override
        public void updateView(float x, float y) {
            // x & y come from map screen coord, so need to convert them
            // to minimap coordinates
            viewBounds.setX(x*scale + MM_X);
            viewBounds.setY(y*scale + MM_Y);
        }


        public void setMapviewSetter(MapController controller){
            this.mapviewSetter = controller;
        }


    }

    private void log(String message){
        Gdx.app.log("MiniMap LOG", message);
    }
}
