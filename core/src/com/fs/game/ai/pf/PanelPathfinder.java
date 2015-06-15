package com.fs.game.ai.pf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.fs.game.map.Locations;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitController;

/** Utils for pathfinding used by AI & game
 * - implements Index A* pathfinding algoirthm in gdx-ai
 *
 * unitPaths holds onto shortest paths b/w potential panels
 *  each unit has multiple path choices
 *  resultPath contains shortest path to panel AI/player hit
 *
 * TODO: create a UML activity for the flow of PathFinding activity as it relates to AI & game movements
 *
 * Created by Allen on 5/7/15.
 */
public class PanelPathfinder implements Telegraph {

    private static PanelPathfinder instance;

    final static float WIDTH = 32; //width of tile == height

    //flags for request or response to/from IndexedAStarPathFinder
    public final static int AGENT_PF_REQUEST = 0; //request sent from UnitAgent
    public final static int PLAYER_REQUEST = 1; //
    public final static int AI_RESPONSE = 2;

    //for Pathfinder handling UnitController requests
    public final static int SHOW_ALL_PATHS = 0; //request to show moveable paths
    public final static int FIND_PATH = 1; //request to find path
    public final static int FOUND_ALL_PATHS = 2;
    public final static int FOUND_PATH = 3; //a path has been found
    public final static int HIDE_PATHS = 4;
    public final static int NO_PATH_FOUND = -1;

    Locations locations; //locations contain info related to unit-panel position relationships

    Array<PanelNode> moveNodes; //moves nodes
    PanelNode startNode; //starting node
    PanelNode endNode; //target node - Panel selected
    PanelGraph gameMap; //Graph containing PanelNode Array

    DefaultGraphPath<PanelNode> resultPath; //resulting path for unit
    IntMap<DefaultGraphPath<PanelNode>> allPaths; //all paths unit can move to

    PanelHeuristic heuristic; //heuristic for finding path to target
    IndexedAStarPathFinder<PanelNode> pathFinder; //pathfinder from gdx-ai
    float startTime; //start time for pathfinder



    public PanelPathfinder(){
        locations = Locations.getLocations(); //gets location singleton
        gameMap = locations.getPanelGraph(); //set the panelgraph

        initPathfinder(); //initialize path finder
    }

    public static PanelPathfinder getInstance(){
        if (instance == null)
            instance = new PanelPathfinder();

        return instance;
    }



    public void initPathfinder(){

        resultPath = new DefaultGraphPath<PanelNode>(); //the path agent/player chose
        pathFinder = new IndexedAStarPathFinder<PanelNode>(gameMap, true); //if true, calculates metrics

    }

    /** This sets startNode positions based on where unit is
     *
     * @param unit : unit that is chosen
     */
    public void showMovePaths(Unit unit){
        Locations.PositionData data = unit.getPosData();
        heuristic = new PanelHeuristic(); //heuristic used by pathfinder

        setUnitMovePanels(data); //set moveNodes
    }

    /** Sets unit movement graph, copying the main game graph nodes unit can move to
     *
     * @param pos : position of unit
     */
    public void setUnitMovePanels(Locations.PositionData pos){
        moveNodes = new Array<PanelNode>(); //new array for movenodes
        allPaths = new IntMap<DefaultGraphPath<PanelNode>>();

        int[] originPos = gameMap.getStartNodePosition(pos); //get origin position
        startNode = gameMap.getNode(originPos[0], originPos[1]); //set start node
        gameMap.startNode = startNode; //for reference in connections
        gameMap.data = pos; //for pathfinder costs

        log("Start finding panels within range that are not blocked for unit: " + pos.name + "\n  position: (" +
                originPos[0] + ", " + originPos[1] + ")" + "\n  size : " + pos.size +  "\n  type (terrain): "
                + pos.type + "\n  range: " + pos.range + "\n  steps: x=" + pos.steps[0] + ", y=" + pos.steps[1]);

        //set the x, y boundaries in graph
        int right = originPos[0] + (pos.range * pos.steps[0]);
        int left = originPos[0] - (pos.range * pos.steps[0]);
        int upper = originPos[1] + (pos.range * pos.steps[1]);
        int lower = originPos[1] - (pos.range * pos.steps[1]);

        //for loop adds nodes to moveNodes Array
        for (int x = left; x <= right; x++){
            for (int y = lower; y <= upper; y++){
                if (gameMap.isInBounds(x, y)){
                    PanelNode node = gameMap.getNode(x, y);

                    if (gameMap.isWithinRange(node, pos.range)){
                        log("Checking node at " + x + ", " + y + ", of type: " + node.type);
                        addMoveNode(node, pos); //adds node with method 1
                    }
                }
            }
        }

    }


    /** Adds a Panel to moveNodes by finding PanelNode that is in range of Unit's max move range and is passable and
     *  not occupied by unit. The requirements for adding a node/making it moveable differ based on size of unit.
     *  eg. large unit needs 3 extra spaces to fit, so any node in range needs to check whether neighbor nodes are not
     *  blocked in any way so that all of large unit can fit in space.
     *
     * @param node : node being checked
     * @param pos : position data of Unit
     */
    public void addMoveNode(PanelNode node, Locations.PositionData pos){
        //&& !isStartNode (node) // note: used to be here
        if (node.isPassable(pos.type) && !node.isNodeOccupied(pos.positions)) {

            if (pos.size.equals(Unit.LARGE)){
                int offset1L = pos.side == Unit.LEFT_SIDE ? 1 : -2; //for diagonal & x offset
                int offsetY = 1; //same for both sides

                if (gameMap.canUnitMoveTo_Large(node, offset1L, offsetY, pos.type)){
                    log("Unit of size, " + pos.size + ", had extra room");
                    log("Added node: (" + node.x + ", " + node.y + ")");
                    moveNodes.add(node);
                    node.setPanelMoveState(true);
                }
            }

            if (pos.size.equals(Unit.MEDIUM)){
                int offset = pos.side == Unit.LEFT_SIDE ? 1 : -1;
                if (gameMap.canUnitMoveTo_Medium(node, offset, pos.type)){
                    log("Added node: (" + node.x + ", " + node.y + ")");
                    moveNodes.add(node);
                    node.setPanelMoveState(true);
                }
            }

            if (pos.size.equals(Unit.SMALL)){
                log("Added node: (" + node.x + ", " + node.y + ")");
                moveNodes.add(node);
                node.setPanelMoveState(true);
            }

        }
    }


    /** Resets move nodes by updating eawch panelNode panel's PanelState.
     *
     */
    public void resetMoveNodes(){
        if (moveNodes.size > 0){
            for (PanelNode p : moveNodes){
                p.setPanelMoveState(false);
            }
        }
    }

//    public boolean isStartNode(PanelNode node){
//        return node.x != startNode.x && node.y != startNode.y;
//    }

    /** Finds shortest path between start and end (target) node
     *
     * @param endPos : end node position
     */
    public void findPath(int[] endPos){

        resetMoveNodes(); //hide move panels (nodes)
        endNode = gameMap.getNode(endPos[0], endPos[1]); //set end node

        Array<Vector2> vectorPath = new Array<Vector2>(); //Array of Vector2 objects for adding Unit actions
        resultPath = new DefaultGraphPath<PanelNode>(); //capacity of 10
        startTime = pathFinder.metrics == null ? 0 : TimeUtils.nanoTime(); //record start time in nanoseconds

        pathFinder.searchNodePath(startNode, endNode, heuristic, resultPath); //search path

        logMetrics();

        if (resultPath.getCount() > 0){

            //add to vectorPath
            for (PanelNode p : resultPath) {
                vectorPath.add(p.getScreenPosition());
            }

            //send message with code & extra info in pfRequest
            MessageManager.getInstance().dispatchMessage(this, UnitController.getInstance(), FOUND_PATH, vectorPath);
        }
        else{
            //send message with code & extra info in pfRequest
            MessageManager.getInstance().dispatchMessage(this, UnitController.getInstance(), NO_PATH_FOUND);
        }
    }




    /** Handles the messages to/from MessageDispatcher instance
     *
     * @param msg : message to be sent
     * @return : t/f depending on animState
     */
    @Override
    public boolean handleMessage(Telegram msg) {

        switch (msg.message) {
            case FIND_PATH:
                findPath((int[]) msg.extraInfo);
                break;
            case SHOW_ALL_PATHS:
                showMovePaths((Unit) msg.extraInfo);
                break;
            case HIDE_PATHS:
                resetMoveNodes();
                break;

        }
        return true;
    }

    //Panel Heuristic - Manhattan Distance
    public class PanelHeuristic implements Heuristic<PanelNode>{

        @Override
        public float estimate(PanelNode node, PanelNode endNode) {
            float x = Math.abs(node.x - endNode.x);
            float y = Math.abs(node.y - endNode.y);

            log("Estimated heuristic: " + Float.toString(x+y));

            return x + y;
        }
    }


    private void logPath(){
        StringBuilder builder = new StringBuilder(); //for logging path info
        builder.append("Path found, in graph,: \n {");
        for (PanelNode p : resultPath){

            builder.append("(" + p.x + ", " + p.y + "), ");
        }
        builder.append("}");
        log(builder.toString());
    }


    /** Prints out metrics for {@link IndexedAStarPathFinder}
     *
     */
    private void logMetrics(){
        if (pathFinder.metrics != null) {
            //convert elapsed time to seconds
            float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
            System.out.println("----------------- Indexed A* Path Finder Metrics -----------------");
            System.out.println("Visited nodes................... = " + pathFinder.metrics.visitedNodes);
            System.out.println("Open list additions............. = " + pathFinder.metrics.openListAdditions);
            System.out.println("Open list peak.................. = " + pathFinder.metrics.openListPeak);
            System.out.println("Path finding elapsed time (ms).. = " + elapsed);

            logPath();
        }
    }

    private void log(String message){
        Gdx.app.log("PanelPathFinder LOG", message);
    }
}
