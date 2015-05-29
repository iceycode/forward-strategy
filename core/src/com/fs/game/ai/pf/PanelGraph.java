package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.pfa.indexed.DefaultIndexedGraph;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.fs.game.units.Unit;
import com.fs.game.map.Panel;
import com.fs.game.map.Locations;
import com.fs.game.constants.Constants;

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
 *         .                       .
 *         .                       .        <---index = x*sizeY + y
 *      |__________________      _____|
 *      |  |  |  |  |  |  |     |  |  |
 *  1   |  P--|--P--|--P--|     |--P  | <---index = 1*16 + 15 = 31
 *      |__|__|__|__|__|__|     |__|__|
 *      |  |  |  |  |  |  |     |  |  |
 *  0   |  P--|--P--|--P--|. . .|--P  |  <---PanelNode index = 16*0 + 15 = 15
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
 * Created by Allen on 5/7/15.
 */
public class PanelGraph extends DefaultIndexedGraph<PanelNode> implements IndexedGraph<PanelNode>  {

    //FIXME: change sizeX & sizeY based on size of map being tested
    public static int sizeX = Constants.GRID_ROWS;
    public static int sizeY = Constants.GRID_COLS;

    public int width = 32; //width of Panel
    public int height = 32;  //height of Panel

    public Array<PanelNode> nodes; // nodes in graph
    public Array<PanelNode> moveNodes; //nodes unit can move to
    public PanelNode startNode; //node unit that is start point
    public Locations.PositionData currPos; //current position representation of Unit


    public PanelGraph(){
        super(sizeX * sizeY); //sets size of Array
        this.startNode = null; //set to null for now
    }

    //initializes PanelGraph with map of ALL panel actors
    // at start of game
    public void init(Panel[][] panelMap){
        nodes = new Array<PanelNode>(sizeX*sizeY);

        //creates nodes
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                nodes.add(new PanelNode(x, y, panelMap[x][y], 4));
            }
        }
//
//        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
//        // adds nodes based on location in grid & offset of node in direction
//        for (int x = 0; x < sizeX; x++) {
//            int idx = x * sizeY;
//            for (int y = 0; y < sizeY; y++) {
//                PanelNode n = nodes.get(idx + y);
//                if (x > 0) addConnection(n, -1, 0);
//                if (y > 0) addConnection(n, 0, -1);
//                if (x < sizeX - 1) addConnection(n, 1, 0);
//                if (y < sizeY - 1) addConnection(n, 0, 1);
//            }
//        }
    }

    //sets a graph of PanelNodes unit can move to
    public void setUnitMoveGraph(Locations.PositionData pos){
        int oriX = pos.positions.get(0)[0];
        int oriY = pos.positions.get(1)[1];

        startNode = getNode(oriX, oriY);
        int range = pos.unitRange;

        moveNodes = new Array<PanelNode>(sizeX*sizeY);

//        boolean checkedX = false; //checked in all X directions
//        boolean checkedY = false; //checked all Y directions
//        int x = oriX;
//        int y = oriY;
        //adds nodes to moveNodes
        for (int x = oriX - range; x < oriX + range; x++){
            if (x > 0 && x < sizeX){
                int idx = x * pos.unitRange;
                for (int y = oriY - range; y < oriY + range; y++){
                    if (y > 0 && y < sizeY){
                        if (x > oriX) addConnection(idx, -1, 0, pos);
                        if (y > oriY) addConnection(idx, -1, 0, pos);
                        if (x < sizeX - 1 ) addConnection(idx, -1, 0, pos);
                        if (y < sizeY - 1) addConnection(idx, -1, 0, pos);
                    }
                }
            }

        }


    }

    //returns node based on {x,y} GRID coordinates
    public PanelNode getNode(int x, int y){
        return nodes.get(x * sizeY + y);
    }


    /** Updates node(s) based on what Panels Unit occupies
     *  Resets no longer occupied nodes as well, changing to original "terrainType"
     *
     * @param positions : position(s) Unit occupies in Graph
     */
    public void updateOccupiedNodes(Array<int[]> positions){
        for (int[] pos: positions){
            getNode(pos[0], pos[1]).updateNodeType();
        }
    }

    //returns node based on index
    public PanelNode getNode(int index){
        return nodes.get(index);
    }


    //adds connection with x & y offset
    private void addConnection(PanelNode n, int xOffset, int yOffset){
        PanelNode node = getNode(n.x + xOffset, n.y + yOffset);

        node.addConnection(new PanelConnection(n, node, this));
    }


    /** Adds connection based on unit type
     *
     * @param nodeIndex : node index
     * @param xOffset : offset x
     * @param yOffset : offset y
     * @param pos : data related to Unit obstacle
     */
    private void addConnection(int nodeIndex, int xOffset, int yOffset, Locations.PositionData pos){
        PanelNode n = nodes.get(nodeIndex);
        PanelNode node = getNode(n.x + xOffset, n.y + yOffset);

        //check if node in front of this node is occupied
        if (!node.isNodeOccupied()){
            //if it is not, check if larger size unit will fit
            if (pos.unitSize == Unit.LARGE){
                if (getNode(n.x+xOffset+xOffset, n.y+yOffset+yOffset).isNodeOccupied()){
                    if (node.isNodePassable(pos.type)) {
                        n.addConnection(new PanelConnection(n, node, this));
                        moveNodes.add(n);
                    }
                }
            }
            else if (pos.unitSize == Unit.MEDIUM){
                if (getNode(n.x+xOffset+xOffset, n.y+yOffset).isNodeOccupied()){
                    if (node.isNodePassable(pos.type)) {
                        n.addConnection(new PanelConnection(n, node, this));
                        moveNodes.add(n);
                    }
                }
            }
            else{
                if (node.isNodePassable(pos.type)){
                    n.addConnection(new PanelConnection(n, node, this));
                    moveNodes.add(n);
                }
            }
        }

    }

    /** Checks to see if PanelNode is startNode
     *
     * @param n : node being checked
     * @return : true if it is, false if not
     */
    public boolean isNodeStart(PanelNode n){
        return n.x==startNode.x && n.y==startNode.y;
    }


    public Locations.PositionData getCurrPos(){
        return currPos;
    }


    @Override
    public int getNodeCount() {
        return nodes.size;
    }


}
