package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
import com.fs.game.actors.Unit;
import com.fs.game.actors.UnitController;
import com.fs.game.ai.AgentManager;
import com.fs.game.data.GameData;
import com.fs.game.map.Locations;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;

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
    public final static int PF_SHOW_MOVES = 0;
    public final static int PF_FIND_PATH = 1;

    Locations locations;

    //start & end node positions in graph
    int startNodeX = 1; //where start node is
    int startNodeY = 1;
    int lastEndNodeX = -1; //where end node is
    int lastEndNodeY = -1;

    //unit type array - size & whether air, land or water
    int[] unitType = {0,0};  //default is land & small

    PanelGraph gameMap; //Graph containing PanelNode Array
    Array<DefaultGraphPath<PanelNode>> unitPaths = new Array<DefaultGraphPath<PanelNode>>();
    DefaultGraphPath<PanelNode> resultPath; //resulting path for unit
    PanelHeuristic heuristic;
    IndexedAStarPathFinder<PanelNode> pathFinder; //pathfinder from gdx-ai


    PanelPathUpdater pathPanelUpdater; //used by Unit to update on screen paths

    //sets whether panels in range that can be moved to
    // and the panelPath
    public interface PanelPathUpdater {
        void setPanelsInRange(Array<Panel> panelsInRange);

        void setPath(Array<Panel> panelPath);
    }


    public PanelPathfinder(){
        locations = Locations.getLocations(); //gets location singleton
        gameMap = locations.panelGraph; //set the panelgraph

        initPathfinder(); //initialize path finder
    }

    public static PanelPathfinder getInstance(){
        if (instance == null)
            instance = new PanelPathfinder();

        return instance;
    }



    public void initPathfinder(){

        resultPath = new DefaultGraphPath<PanelNode>(); //the path agent/player chose
        heuristic = new PanelHeuristic(); //heuristic used by pathfinder
        pathFinder = new IndexedAStarPathFinder<PanelNode>(gameMap, true); //if true, calculates metrics

        //register listeners
        MessageManager.getInstance().addListener(UnitController.getInstance(), PF_FIND_PATH);
        MessageManager.getInstance().addListener(UnitController.getInstance(), PF_SHOW_MOVES);
//        PathFinderQueue<PanelNode> pathFinderQueue = new PathFinderQueue<PanelNode>(pathFinder); //queue for pathfinder
//        MessageManager.getInstance().addListener(pathFinderQueue, PLAYER_REQUEST);
    }


    //method updates move path using info about PanelNode position
    public void updatePath(int nodeY, int nodeX){
        //set the current nodes to operate on
        PanelNode[] nodes = getCurrentNodes(nodeX, nodeY);

        pathFinder.searchNodePath(nodes[0], nodes[1], heuristic, resultPath);

        //get current request & set variables
//        AgentPathFinderRequest pfRequest = new AgentPathFinderRequest();
//        pfRequest.startNode = nodes[0];
//        pfRequest.endNode = nodes[1];
//        pfRequest.heuristic = heuristic;
//        pfRequest.resultPath = resultPath;
//        pfRequest.responseMessageCode = AI_RESPONSE;



        //send message with code & extra info in pfRequest
//        MessageManager.getInstance().dispatchMessage(this, PLAYER_REQUEST, pfRequest);
    }




    //where unit is
    protected PanelNode[] getCurrentNodes(int nodeX, int nodeY){
        PanelNode startNode = gameMap.getNode(startNodeX, startNodeY); //startNode
        PanelNode endNode = gameMap.getNode(nodeX, nodeY); //end node

        if (endNode.type == Panel.LAND){
            lastEndNodeX = nodeX; //set lastEndNode to it
            lastEndNodeY = nodeY;
        }
        else if (endNode.type == Panel.OBSTACLE){
            if (unitType[0] == AgentManager.AIR_UNIT){
                lastEndNodeX = nodeX; //set lastEndNode to it
                lastEndNodeY = nodeY;
            }
            else{
                endNode = gameMap.getNode(lastEndNodeX, lastEndNodeY);
            }
        }
        else if (endNode.type == Panel.WATER){
            if (unitType[0] == AgentManager.LAND_UNIT){
                endNode = gameMap.getNode(lastEndNodeX, lastEndNodeY);
            }
            else{
                lastEndNodeX = nodeX; //set lastEndNode to it
                lastEndNodeY = nodeY;
            }
        }

        return new PanelNode[]{startNode, endNode};
    }


    /** This sets startNode positions based on where unit is
     *
     * @param unit :
     */
    public void setPanelsInRange(Unit unit){
        Locations.PositionData data = locations.getUnitPosition(unit);

        int[] ori = data.positions.get(0);

        gameMap.getNode(ori[0], ori[1]);
        gameMap.setUnitMoveGraph(data);


        Array<PanelNode> moveNodes = gameMap.moveNodes;
        for (PanelNode node : moveNodes){
            GameData.panelMatrix[node.x][node.y].setPanelState(PanelState.MOVEABLE);
        }
    }

    public void setSearchRange(int[] origin, int range){

    }



    //Panel Heuristic - Manhattan Distance
    class PanelHeuristic implements Heuristic<PanelNode>{

        @Override
        public float estimate(PanelNode node, PanelNode endNode) {
            float x = Math.abs(node.x - endNode.x);
            float y = Math.abs(node.y - endNode.y);
            return x+y;
        }
    }


    /** Handles the messages to/from MessageDispatcher instance
     *
     * @param msg : message to be sent
     * @return : t/f depending on animState
     */
    @Override
    public boolean handleMessage(Telegram msg) {
        //TODO: figure this out for UnitAgent
        switch (msg.message) {
            case AI_RESPONSE:

            case PLAYER_REQUEST:

                break;

        }
        return true;
    }


    /** A UnitAgent Pathfinder request which implements Telegraph
     *  Allows for agent to get messages back about paths
     */
    class AgentPathFinderRequest extends PathFinderRequest<PanelNode> implements Telegraph{

        public AgentPathFinderRequest() {
        }

//        public AgentPathFinderRequest(PanelNode startNode, PanelNode endNode, Heuristic<PanelNode> heuristic, GraphPath<PanelNode> resultPath) {
//            super(startNode, endNode, heuristic, resultPath);
//        }
//
//
//        public AgentPathFinderRequest(PanelNode startNode, PanelNode endNode, Heuristic<PanelNode> heuristic, GraphPath<PanelNode> resultPath, MessageDispatcher dispatcher) {
//            super(startNode, endNode, heuristic, resultPath, dispatcher);
//        }

        @Override
        public void changeStatus(int newStatus) {
            super.changeStatus(newStatus);
        }

        @Override
        public boolean initializeSearch(long timeToRun) {
            resultPath.clear();
            gameMap.startNode = startNode;
            return true;
        }

        @Override
        public boolean search(PathFinder<PanelNode> pathFinder, long timeToRun) {
            if (pathFound){
                return true;
            }

            return false;
        }

        @Override
        public boolean finalizeSearch(long timeToRun) {
            return true;
        }

        @Override
        public boolean handleMessage(Telegram msg) {
            return false;
        }
    }


}
