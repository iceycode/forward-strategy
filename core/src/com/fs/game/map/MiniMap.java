package com.fs.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.fs.game.assets.AssetHelper;

/** A small minimap so players can tell where Units are in main grid.
 *  This window goes on InfoStage
 *  3x3 pixel tiles, so for example, 40x30 of 32x32 tiles would make 120x90 window
 *
 * Created by Allen on 5/23/15.
 */
public class MiniMap extends Window {

    Skin skin;
    Table table; //table with 3x3px cells

    int rows;
    int cols;
    float[] tileSize = {3f, 3f}; //size of minimap tiles

    // A mini map panel texture array containing textures
    // which represent what is on the board
    Array<Texture> mpanelTextures = new Array<Texture>();


    public MiniMap(Skin skin, int rows, int cols) {
        super("", skin);
        this.rows = rows;
        this.cols = cols;
        this.skin = skin;
        this.mpanelTextures = AssetHelper.getMMAssets();

        //set window title
        Label title = new Label("MiniMap", skin, "tab");
        add(title).width(cols*tileSize[0]).height(20);

        setTable();
    }

    protected void setTable(){
        table = new Table();
        table.setFillParent(true);

        for (int x = 0; x < rows; x++){
            for (int y = 0; y < cols; y++){
                table.add().size(tileSize[0], tileSize[1]);
            }
        }


        add(table);
    }


    /** A mini map representation of a Panel/MapActor
     *  Shows where Units & Obstacles are on large map
     *
     */
    public static class MiniPanel extends Actor {

        Texture texture;
        Color color; //color of filled rectangle shape

        PanelState state; //state of the panel

        public MiniPanel(){
            //initial state set to None
            state = PanelState.NONE; //just regular non-occupied panel




            setSize(3, 3);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {

        }

        //sets color based on state of Panel
        public void setColorByState(){
            switch(state){
                case NONE:
                    color = Color.LIGHT_GRAY;
                    break;
                case SELECTED:
                    ;
            }
        }


    }


    /** Shows a rectangle on minimap which represents the
     *  grid area player sees on the main board.
     *
     */
    public static class AreaBox extends Actor{
        ShapeRenderer renderer; //renders small shape representing unit/panel

        public AreaBox(){
            this.renderer = new ShapeRenderer();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            renderShape(batch);
        }

        @Override
        public void act(float delta) {

        }

        public void renderShape(Batch batch){
        //need to end current batch, causes to be flushed
            batch.end();

            //transform & projection matrices used for renderer
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.setTransformMatrix(batch.getTransformMatrix());
            renderer.translate(getX(), getY(), 0);

            //render shape with ShapeRenderer
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.MAROON);
            renderer.rect(0, 0, getWidth(), getHeight());
            renderer.end();

            //start batch again
            batch.begin();
        }

    }
}
