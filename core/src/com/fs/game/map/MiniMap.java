package com.fs.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.fs.game.ai.pf.PanelNode;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;

/** A small minimap so players can tell where Units are in main grid.
 *  This group of actors goes on InfoStage and renders Shapes in actors using ShapeRenderer
 *  3x3 pixel tiles, so for example, 40x30 of 32x32 tiles would make 120x90 window
 *
 * Created by Allen on 5/23/15.
 */
public class MiniMap implements GameStage.MinimapListener {

    Skin skin;

    private final float MM_X = Constants.MM_X;
    private final float MM_Y = Constants.MM_Y;

    private float scale = 3/32; //minimap panel to actual panel size is scale
    private float viewScaleX = 16/40; //scale is portion of full map seen in view
    private float viewScaleY = 12/40; //same as above but for y-axis

    int rows;
    int cols;
    float width;
    float height;
    float[] tileSize = {3f, 3f}; //size of minimap tiles

    MinimapViewController viewController;

    // A mini map panel texture array containing textures
    // which represent what is on the board
//    Array<Texture> mpanelTextures = new Array<Texture>();
    Array<MiniPanel> markers = new Array<MiniPanel>();
    Batch batch; //batch for map
    Batch viewBatch; //batch for part of map hightlighted
    ShapeRenderer renderer; //renders small rectangle which represents where player is
    Rectangle viewBounds; //current viewBounds of game set to minimap size
    boolean viewChanged = false; //if true, view changed


    MapviewSetter mapviewSetter;
    //updates actual map view based on rectangle position of view bounds
    public interface MapviewSetter {
        void updateCameraPosition(float x, float y);
    }


    public MiniMap(int rows, int cols) {

        this.rows = rows;
        this.cols = cols;

        this.width = rows * 3;
        this.height = cols * 3;

        //set window bounds
//        setBounds(MM_X, MM_Y, width, height);

        batch = new SpriteBatch(); //new batch for minimap

        //create shape renderer
        renderer = new ShapeRenderer();

        //set viewBounds Rectangle - portion of game map scaled in minimap
        viewBounds = new Rectangle(MM_X, MM_Y, width*viewScaleX, height*viewScaleY);
        viewController = new MinimapViewController(viewBounds);


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


//        add(table);
    }

    public void setMapviewSetter(GameStage.CameraController controller){
        this.mapviewSetter = controller;
    }

//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//
//
//
//
//        super.draw(batch, parentAlpha);
//    }
//
//    @Override
//    public void act(float delta) {
//        super.act(delta);
//
//        if (viewChanged)
//            mapviewSetter.updateCameraPosition(viewBounds.x, viewBounds.y);
//    }

    public void render(Batch batch){
        batch.begin();
        //need to end current batch, causes to be flushed
        batch.end();

        //transform & projection matrices used for renderer
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.translate(MM_X, MM_Y, 0);
        //render shape with ShapeRenderer
        renderer.begin(ShapeRenderer.ShapeType.Line); //just an outline
        renderer.setColor(Color.CYAN);
        renderer.rect(MM_X, MM_Y, width, height);
        renderer.end();

        //start batch again
        batch.begin();

        for (MiniPanel mp : markers){
            mp.renderMinipanel(batch);
        }

        renderView(batch);
    }

    public void renderView(Batch batch){
        batch.end();

        //transform & projection matrices used for renderer
        renderer.setProjectionMatrix(batch.getProjectionMatrix());
        renderer.setTransformMatrix(batch.getTransformMatrix());
        renderer.translate(MM_X, MM_Y, 0);
        //render shape with ShapeRenderer
        renderer.begin(ShapeRenderer.ShapeType.Line); //just an outline
        renderer.setColor(Color.CYAN);
        renderer.rect(viewBounds.x, viewBounds.y, viewBounds.getWidth(), viewBounds.getHeight());
        renderer.end();

        //start batch again
        batch.begin();
    }


    @Override
    public void updateView(float x, float y) {
        // x & y come from map screen coord, so need to convert them
        // to minimap coordinates
        viewBounds.setX(x*scale + MM_X);
        viewBounds.setY(y*scale + MM_Y);
    }

    /** A mini map representation of a Panel/MapActor
     *  Shows where Units & Obstacles are on large map
     *
     */
    public class MiniPanel {

        final Panel panel; //the Panel correlating to this minipanel

        float width = 3f;
        float height = 3f;
        Vector2 position; //position of MiniPanel

        ShapeRenderer renderer;
        Color color; //color of filled rectangle shape

        PanelState state; //state of the panel
        protected int terrainType = 0; //terrain type

        /** Constructs Panel marker for minimap
         *
         */
        public MiniPanel(Panel panel, Vector2 position){
            this.panel = panel;
            this.position = position;

            //set actor properties by getting Panel properties
            terrainType = panel.terrainType;
            state = panel.state;
            setColor(state);

            renderer = new ShapeRenderer();
        }


        /** Renders this minipanel object
         *
         * @param batch : batch on which shapes are drawn
         */
        public void renderMinipanel(Batch batch){
            setColor(panel.state);

            //need to end current batch, causes to be flushed
            batch.end();

            //transform & projection matrices used for renderer
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.setTransformMatrix(batch.getTransformMatrix());
            renderer.translate(position.x, position.y, 0);

            //render shape with ShapeRenderer
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(color);
            renderer.rect(0, 0, width, height);
            renderer.end();

            //start batch again
            batch.begin();
        }


        //sets color based on state of Panel
        public void setColor(PanelState state){

            switch(state){
                case NONE:
                    setTextureByTerrain();
                    break;
                case SELECTED:
                    color = Color.YELLOW;
                    break;
                case ALLY:
                    color = Color.GREEN;
                    break;
                case ENEMY:
                    color = Color.RED;
                default:
                    color = Color.GRAY;
                    break;
            }

        }

        public void setTextureByTerrain(){
            switch(terrainType){
                case PanelNode.OCCUPIED:
                    color = Color.GREEN;
                    break;
                case Panel.LAND:
                    color = Color.LIGHT_GRAY;
                    break;
                case Panel.OBSTACLE:
                    color = Color.OLIVE;
                    break;
                case Panel.WATER:
                    color = Color.BLUE;
                    break;
                default:
                    color = Color.GRAY;
                    break;
            }
        }

        public void dispose(){
            renderer.dispose();
        }
    }

    public void dispose(){
        renderer.dispose();
        for (MiniPanel p : markers){
            p.dispose();
        }
    }

//    /** Only TEMPORARY setup for Tests
//     *
//     * @return : an Array of MiniMap textures
//     */
//    public static Array<Texture> getMMAssets(){
//        Array<Texture> textures = new Array<Texture>();
//
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.LIGHT_GRAY)); //just regular panel
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.YELLOW)); //a selected Unit
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.GREEN)); //ALLY
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.RED)); //ENEMY
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.OLIVE)); //a land obstacle
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.BLUE)); //a water obstacle
//
//
//        return textures;
//    }


    /** InputAdapter for minimap rectangle area
     *
     */
    public static class MinimapViewController extends InputAdapter{
        final Rectangle mmView;
        final Vector2 curr;
        final Vector2 last = new Vector2(-1, -1);
        final Vector2 delta = new Vector2();

        public MinimapViewController(Rectangle view){
            this.mmView = view;
            curr = new Vector2(view.x, view.y);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            curr.set(screenX, screenY);
            if (last.x != -1 && last.y!=-1){
                delta.set(last.x, last.y);
                delta.sub(curr);

                mmView.setPosition(mmView.x + delta.x, mmView.y + delta.y);
            }
            last.set(screenX, screenY);

            return true;
        }


        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            last.set(-1, -1);

            return false;
        }
    }

    private void log(String message){
        Gdx.app.log("MiniMap LOG", message);
    }
}
