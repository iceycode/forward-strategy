package com.fs.game.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/** A mini map representation of a Panel/MapActor
 *  Shows where Units & Obstacles are on large map
 *  Uses a ShapeRenderer to do so
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
            case Panel.OCCUPIED:
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
