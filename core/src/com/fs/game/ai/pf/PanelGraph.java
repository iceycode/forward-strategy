package com.fs.game.ai.pf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.fs.game.map.Locations;
import com.fs.game.map.Panel;
import com.fs.game.units.Unit;

import java.util.Iterator;

/** PanelNodeGraph containing graphical representation of tiled nodes on GameStage tiled map
 * - based on size,
 *
 * NOTES on generating index
 * - need to set the create a double array for x & y that describes that location animState
 *      - states: Obstacle, Water, End (edge), open (can move to)
 * NOTES:
 * - there is no diagonal movement, so no diagonal connection
 * - panels on outer edges have 2 connections
 * - panels on inside have 4 connections
 *  Size: 16x12
 *  Each node is 32x32
 * Visual: Graph of PanelNodes
 *  - rows/columns start at 0, index start in Array
 *  - to calculate index, x*numRows + numRows
 *
 *  y-axis
 *       _________________       _____
 *      |     |     |     |     |     |
 *  11  |  P--|--P--|--P--|     |--P  |
 *      |__|__|__|__|__|__|. . .|__|__|
 *      |  |  |  |  |  |  |     |  |  |
 *  10  |  P--|--P--|--P--|     |--P  |
 *      |__|__|__|__|__|__|     |__|__|
 *      |  |  |  |  |  |  |     |  |  |
 *  9   |  P--|--P--|--P--|     |--P  |
 *      |__|__|__|__|__|__|     |__|__|
 *      |                             |
 *         .                       .
 *         .                       .    <--- index = x*sizeY + y
 *      |__________________      _____|
 *      |  |  |  |  |  |  |     |  |  |
 *  1   |  P--|--P--|--P--|     |--P  | <--- index = 1*16 + 15 = 31
 *      |__|__|__|__|__|__|     |__|__|
 *      |  |  |  |  |  |  |     |  |  |
 *  0   |  P--|--P--|--P--|. . .|--P  | <--- PanelNode index = 16*0 + 15 = 15
 *      |_____|_____|_____|     |_____|
 *         0     1     2          16      x-axis
 *
 * Cost per unit: 32
 * Max distance traveled b/w 2 nodes: 5
 * Some are panels are un-passable, depending on Unit moving over
 *
 * - PanelNodes added to Array by each column in each row first, then next row
 *
 * - All panels surrounding units max move distance are potential choices, so paths need to be found for each
 *   panel that is within max distance from unit. Then when selected, shortest path is used to get unit there.
 *
 * Setting Unit panels in range:
 * while (all Nodes in range are not checked) do:
 *
 *
 * Created by Allen on 5/7/15.
 */
public class PanelGraph extends DefaultIndexedGraph<PanelNode> implements IndexedGraph<PanelNode>  {

    public int sizeX;
    public int sizeY;

    public int width = 32; //width of Panel
    public int height = 32;  //height of Panel

    public Array<PanelNode> nodes; // nodes in graph
    public PanelNode startNode; //node unit that is start point
    public PanelNode targetNode; //the target node chosen

    public Locations.PositionData data; //current unit position data


    public PanelGraph(int sizeX, int sizeY){
        super();
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.startNode = null; //for game, is null
    }


    // Initializes PanelGraph with map of ALL panel actors at start of game
    public void init(Panel[][] panelMap){
        nodes = new Array<PanelNode>(sizeX*sizeY);

        //creates nodes
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                int index = (x * sizeY) + y;
                nodes.add(new PanelNode(x, y, panelMap[x][y], 4, index));
            }
        }

        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
        // adds nodes based on location in grid & offset of node in direction
        for (int x = 0; x < sizeX; x++) {
            int idx = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                PanelNode n = nodes.get(idx + y);
                if (x > 0) addNodeConnection(n, -1, 0);
                if (y > 0) addNodeConnection(n, 0, -1);
                if (x < sizeX - 1) addNodeConnection(n, 1, 0);
                if (y < sizeY - 1) addNodeConnection(n, 0, 1);
            }
        }
    }



    /** Adds connection when setting up PanelGraph
     *
     * @param node : panel node connection coming from
     * @param xOffset : offset x
     * @param yOffset : offset y
     */
    protected void addNodeConnection(PanelNode node, int xOffset, int yOffset){
        PanelNode target = getNode(node.x + xOffset, node.y + yOffset); //end of connection
        node.addConnection(new PanelConnection(node, target, this));

    }

    protected int[] getStartNodePosition(Locations.PositionData posData){
        int[] ori = posData.positions.get(0);

        if ((posData.size == Unit.MEDIUM || posData.size == Unit.LARGE) && posData.side == Unit.LEFT_SIDE){
            ori = posData.positions.get(1);
        }

        return ori;
    }

    /** Checks neighboring nodes for large unit moves
     *
     * @param node : node being checked
     * @param offsetX : x offset for large unit
     * @param offsetY : y offset
     * @param type : unit terrain type
     * @return : true if no obstacles, false if not
     */
    public boolean canUnitMoveTo_Large(PanelNode node, int offsetX, int offsetY, int type){
        //check node to right/left
        if (isInBounds(node.x + offsetX, node.y)){
            if (!isNodePassable(getNode(node.x + offsetX, node.y), type)) return false;
        }

        //check node diagonally (right/left)
        if (isInBounds(node.x + offsetX, node.y + offsetY)){
            if (!isNodePassable(getNode(node.x + offsetX, node.y + offsetY), type)) return false;
        }

        //check node above (same for right/left side)
        if (isInBounds(node.x, node.y + offsetY))
            if (!isNodePassable(getNode(node.x, node.y+offsetY), type)) return false;


        return true;
    }


    /** Checks for medium unit, whether neighboring panel node can be moved to.
     *
     * @param node : node being checked
     * @param offsetX : offsetX
     * @param type : unit terrain type
     * @return : true if no obstacles, false otherwise
     */
    public boolean canUnitMoveTo_Medium(PanelNode node, int offsetX, int type){
        if (isInBounds(node.x + offsetX, node.y)){
            if (!isNodePassable(getNode(node.x + offsetX, node.y), type)) return false;
        }

        return true;
    }



    public boolean isInBounds(int x, int y){
        return x >= 0 && x < sizeX && y >= 0 && y < sizeY;
    }

    /** Checks to see if PanelNode is startNode
     *
     * @param n : node being checked
     * @return : true if it is, false if not
     */
    public boolean isNodeStart(PanelNode n){
        return n.x==startNode.x && n.y==startNode.y;
    }


    /** Checks to make sure x/y position is within unit range
     *  This checks range within taxi cab metric
     *
     * @param current : current node being checked
     * @param range : unit range - NO DIAGONAL movements (taxi cab metric)
     * @return : true if within range
     */
    public boolean isWithinRange(PanelNode current, int range){
        return Math.abs(startNode.x - current.x) + Math.abs(startNode.y - current.y) <= range;
    }


    public boolean isNodePassable(PanelNode node, int type){
        return node.isPassable(type);
    }

    /** Updates node(s) based on what Panels Unit occupies
     *  Resets no longer occupied nodes as well, changing to original "terrainType"
     *
     * @param positions : position(s) Unit occupies in Graph
     */
    public void updateOccupiedNodes(Array<int[]> positions){
        Iterator<int[]> iterator = new Array.ArrayIterator<int[]>(positions);

        while (iterator.hasNext()){
            int[] pos = iterator.next();
            getNode(pos[0], pos[1]).updateNodeType();
        }

    }


    /** Checks node position against unit occupied position
     *
     * @param node : node to check against
     * @return : true if node is under a unit position
     */
    public boolean isNodeUnitPosition(PanelNode node){
        for (int[] p : data.positions){
            if (node.x == p[0] && node.y == p[1])
                return true;
        }

        return false;
    }

    public void setTargetNode(int[] pos){
        targetNode = getNode(pos[0], pos[1]);
    }


    //returns node based on {x,y} GRID coordinates
    public PanelNode getNode(int x, int y){
//        log("getting node...y = " + y + ", x = " + x + ", sizeY = " + sizeY);
//        log("index = " + (x * sizeY + y)); //should be positive

        return nodes.get(x * sizeY + y);
    }


    //returns node based on index
    public PanelNode getNode(int index){
        return nodes.get(index);
    }


    public Array<PanelNode> getNodes(){
        return nodes;
    }


    @Override
    public int getNodeCount() {
        return nodes.size;
    }


    @Override
    public Array<Connection<PanelNode>> getConnections(PanelNode fromNode) {

        return fromNode.getConnections();
    }

    private void log(String message){
        Gdx.app.log("PanelGraph LOG", message);
    }

}

/** Checks to see whether connected nodes are "passable" (not blocked) based on Unit terrain traversal type.
 *  Needs to be done since an open node may be blocked by a an obstacle and unit cannot get to it.
 *
 * @param node : node whose connections are checked
 * @param pos : position data of unit
 * @return : true if connections are all passable (can be moved to)
 */
//    protected boolean hasNodesInRange(PanelNode node, Locations.PositionData pos){
//
//        for (Connection<PanelNode> c : node.getConnections()){
//            //check to see that connected toNode is within range
//            if (isWithinRange(startNode, c.getToNode(), pos.range)){
//                //if within range, then check if this node is passable
//                if (c.getCost() <= pos.type){
//                    moveNodes.add(c.getToNode());
//                }
////                if (c.getToNode().isPassable(pos.type) && !isNodeStart(c.getToNode())) {
////                    moveNodes.add(c.getToNode());
////                }
//            }
//        }
//        return true;
//    }


//    public void setUnitNodes(Locations.PositionData pos){
//        unitNodes = new Array<PanelNode>();
//        for (int[] p : pos.positions){
//            unitNodes.add(getNode(p[0], p[1]));
//        }
//    }
