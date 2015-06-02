package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.utils.Array;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;
import com.fs.game.units.Unit;

/** A Panel Node
 * - contains properties of a Panel actor
 *
 * Created by Allen on 5/7/15.
 */
public class PanelNode implements IndexedNode<PanelNode>{

    //the different types of PanelNodes possible
    public final static int OCCUPIED = 11; //space is occupied by a unit

    public final int x;  //x coordinate
    public final int y; //y coordinate

    Panel panel; //panel associated with this PanelNode

    public int type; //the type. which changes (eg, Unit occupies space, so OBSTACLE_UNIT)
    protected int terrainType; //this is set initially and does not change

    protected Array<Connection<PanelNode>> connections;


    NodeListener listener; //Panel listening to this node


    //sets Panel action based on Node property
    public interface NodeListener {
        void updateMinipanelState();
    }

    /** A PanelNode constructor
     *
     * @param x : x grid coordinate
     * @param y : y grid coordinate
     * @param panel : panel associated with this node
     * @param connectionCapacity : max connection capacity (4 is max)
     */
    public PanelNode(int x, int y, Panel panel, int connectionCapacity){
        this.x = x;
        this.y = y;
        this.panel = panel;

        terrainType = panel.getTerrainType();
        type = terrainType; //set to terrainType until it is occupied by Unit
        connections = new Array<Connection<PanelNode>>(connectionCapacity);
    }

    public void addConnection(PanelConnection connection){
        connections.add(connection);
    }

    //updates the node type status to OCCUPIED or a terrain type
    public void updateNodeType(){
        this.type = type != OCCUPIED ? OCCUPIED : terrainType;
        this.panel.setPanelState(PanelState.NONE); //update the actual panel state
    }

    //check if based on Unit type, if node is passable
    public boolean isNodePassable(int unitType){
        return (type == Panel.LAND) || (type==Panel.OBSTACLE && type== Unit.AIR) ||
                (type==Panel.WATER && (unitType==Unit.WATER || unitType == Unit.AIR));
    }


    public boolean isNodeOccupied(){
        return type==Panel.OCCUPIED;
    }


//    //sets node as target for Unit to move to
//    public void setSelected(){
//        panel.setSelected(true);
//    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public Array<Connection<PanelNode>> getConnections() {
        return connections;
    }


}
