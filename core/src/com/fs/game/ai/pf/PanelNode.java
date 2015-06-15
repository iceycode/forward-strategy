package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;

/** A Panel Node
 * - contains properties of a Panel actor
 *
 * Created by Allen on 5/7/15.
 */
public class PanelNode implements IndexedNode<PanelNode>{


    public final int x;  //x coordinate
    public final int y; //y coordinate

    int index;
    public int[] graphPosition; //position as array

    Panel panel; //panel associated with this PanelNode

    public int type; //the type. which changes (eg, Unit occupies space, so OCCUPIED, else could be LAND or WATER)
    protected int terrainType; //this is set initially and is a constant to compare unit type to

    protected Array<Connection<PanelNode>> connections;


    /** A PanelNode constructor
     *
     * @param x : x grid coordinate
     * @param y : y grid coordinate
     * @param panel : panel associated with this node
     * @param connectionCapacity : max connection capacity (4 is max)
     * @param index : index in node array of graph
     */
    public PanelNode(int x, int y, Panel panel, int connectionCapacity, int index){
        this.x = x;
        this.y = y;
        this.panel = panel;
        this.index = index;

        terrainType = panel.getTerrainType();
        type = terrainType; //set to terrainType until it is occupied by Unit
        connections = new Array<Connection<PanelNode>>(connectionCapacity);
    }

    /** Adds connection to other node
     *
     * @param connection : connection, with fromNode being this PanelNode
     */
    public void addConnection(PanelConnection connection){
        connections.add(connection);
    }


    //updates the node type status to OCCUPIED or a terrain type
    public void updateNodeType(){
        type = type != Panel.OCCUPIED ? Panel.OCCUPIED : terrainType;
        panel.setPanelState(PanelState.NONE); //update the actual panel state
    }

    public void setPanelMoveState(boolean move){
        if (move) GameData.panelMatrix[x][y].setPanelState(PanelState.MOVEABLE);
        else GameData.panelMatrix[x][y].setPanelState(PanelState.NONE);
    }

    /** Returns true if unit can pass. Compares type values of both
     *
     * @param unitType : unit type ; 1 - 3, three being air
     * @return : true if can pass
     */
    public boolean isPassable(int unitType){
        return unitType >= type;
    }


    /** Checks if node is occupied IF not a node current unit is on
     *
     * @param positions : positions unit is occupying
     * @return : true if occupied & NOT unit node
     */
    public boolean isNodeOccupied(Array<int[]> positions){
        if (isUnitNode(positions))
            return false;

        return type == Panel.OCCUPIED;
    }

    /** Check if the node is actually under current unit
     *
     * @param positions : positions of unit
     * @return : true if unit node
     */
    public boolean isUnitNode(Array<int[]> positions){
        for (int[] p : positions){
            if (p[0] == x && p[1] == y)
                return true;
        }

        return false;
    }

    /** Returns ordered list of toNode connection costs
     *  Order is: left, right, above, below. If one or more connections does not exist, value will be 10.
     *
     * @return int array of connection costs in order
     */
    public float[] getConnectionCosts(){
        float[] costs = new float[connections.size];

        for (Connection<PanelNode> c : connections){
            PanelConnection con = (PanelConnection)c;

            if (isLeft(con.getToNode()))costs[0] = con.getToNode().getTypeCost();
            else if (isRight(con.getToNode())) costs[1] = con.getToNode().getTypeCost();
            else if (isAbove(con.getToNode())) costs[2] = con.getToNode().getTypeCost();
            else if (isBelow(con.getToNode())) costs[3] = con.getToNode().getTypeCost();
        }

        return costs;
    }

    public boolean isAbove(PanelNode node){
        return node.x == x && node.y + 1 == y;
    }

    public boolean isBelow(PanelNode node){
        return node.x == x && node.y - 1 == y;
    }

    public boolean isRight(PanelNode node){
        return node.x + 1 == x && node.y == y;
    }

    public boolean isLeft(PanelNode node){
        return node.x - 1== x && node.y == y;
    }


    public float getTypeCost(){
        return  type; //returns square root cost
    }


    public Vector2 getScreenPosition(){
        return new Vector2(panel.getX(), panel.getY());
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Array<Connection<PanelNode>> getConnections() {
        return connections;
    }

}
