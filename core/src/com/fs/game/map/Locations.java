package com.fs.game.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.fs.game.units.Unit;
import com.fs.game.ai.pf.PanelGraph;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;

/** Map Locations of All Units on Map
 *  Containts methods & data useful for Pathfinding, Panel and Unit utility classes
 * - class for accessing whether spaces are occupied by units or obstacles
 * - helps UnitAgent decide where to move current unit
 *
 * NOTE: although a singleton, this needs to be initialized AFTER Units and Panels are setup
 * FIXME: setup positions so that they are within boundaries of array
 *
 * Created by Allen on 5/10/15.
 */
public class Locations {

    private static Locations locations;

    public PanelGraph panelGraph; //graph of panels
    public Array<PositionData>  allPositions = new Array<PositionData> (); //allunit positions
    private IntMap<ObjectMap<String, PositionData>> uPositionMap = new IntMap<ObjectMap<String, PositionData>>();

    public Locations(){
        log("New locations instance");
    }

    //get singleton object method
    public static Locations getLocations(){
        if (locations == null)
            locations = new Locations();
        return locations;
    }

    /** initializes all Locations
     *  NOTE: for Multiplayer Mode, this is initialized in GameStage
     */
    public void initLocations(){
        log("initializing Locations position data");
        generatePanelGraph(GameData.panelMatrix);
        initPositionMap(1, GameData.playerUnits); //sets positions relative to PanelNode graph
        initPositionMap(2, GameData.enemyUnits);
    }

    //generates PanelGraph, an IndexedGraph implementation
    public void generatePanelGraph(Panel[][] panelMatrix){

        panelGraph = new PanelGraph();
        panelGraph.init(panelMatrix);
    }


    public void initPositionMap(int player, Array<Unit> p1Units){
        uPositionMap.put(player, getUnitPositions(p1Units));


        for (PositionData d : uPositionMap.get(1).values().toArray()){
            log(d.toString());
        }
    }

    /** Gets unit positions as Position object
     *
     * @param units : units for AI or player
     * @return : a Position Array
     */
    protected ObjectMap<String, PositionData> getUnitPositions(Array<Unit> units){
        ObjectMap<String, PositionData> unitPositions = new ObjectMap<String, PositionData>();

        for (Unit unit : units){
            PositionData position = new PositionData();
            setUnitPosition(unit, position); //sets up unit Position object, updating PanelNode types also
            position.unitSide = unit.getUnitSide(); //the side unit is on
            unit.setPosData(position); //set unit position data for updating
            unitPositions.put(unit.getName(), position);

            log("Initial Unit pos: " + position.toString());
        }

        return unitPositions;
    }


    /** Sets unit int list of positions
     *
     * @param unit : unit whose PanelNode positions are obtained
     * @param position : Position object containing Array of lists
     */
    protected void setUnitPosition(Unit unit, PositionData position){
        position.positions = unitGridPositions(unit); //sets positions Unit occupies

        //ternary conditional that sets up step size of Unit in graph
        position.unitSteps = unit.getUnitSize() != Unit.SMALL ? new int[]{1,1}
                : unit.getUnitSize()==Unit.MEDIUM ? new int[]{2,1}
                : new int[]{2,2};

        position.unitRange = unit.getMaxMoves();
        position.type = unit.unitType;
        position.unitSize = unit.getUnitSize();
        position.unitName = unit.getName();
    }


    // after UnitAgent moves unit or when player does, need to update
    // Positions object
    public void updateUnitNodePosition(PositionData data, Unit unit){
//        log(data.toString());
        //need to reset previous positions, so get old positions & reset panelGraph nodes
        Array<int[]> previousPos = data.positions;
        panelGraph.updateOccupiedNodes(previousPos); //update Panels via panelGraph PanelNodes


        //then update the positions
        Array<int[]> positions = unitGridPositions(unit);
        data.positions = positions;
        panelGraph.updateOccupiedNodes(positions);

        //NOTE: log position update here
        logLocationUpdate(previousPos.first(), positions.first());
    }


    /** Takes Panel point on screen & updates to point in gameMap
     *
     * @param screenX : x coordinate
     * @param screenY : y coordinate
     * @return nodePosition array
     */
    public int[] screenToNode(float screenX, float screenY){
        int nodeX = (int)((screenX - Constants.MAP_X)/32);
        int nodeY = (int)((screenY - Constants.MAP_Y)/32);

        return new int[]{nodeX, nodeY};
    }


    /** Converts from node position to screen
     *
     * @param x : node x position
     * @param y : node y position
     * @return: float position
     */
    public float[] nodeToScreen(int x, int y){
        float screenX = x*32 + Constants.MAP_X;
        float screenY = y*32 + Constants.MAP_Y;

        return new float[]{screenX, screenY};
    }


    /** Takes Unit box shape, translates it into an Array of int[] which contain
     *  its occupied space on PanelGraph
     *
     * @param unit : Unit box being occupied
     * @return a Vector2 array
     */
    public Array<int[]> unitGridPositions(Unit unit){
        Array<int[]> unitPositions = new Array<int[]>();
        int[] ori = screenToNode(unit.getX(), unit.getY());
        unitPositions.add(ori);

        if (unit.getWidth() > 32){
            unitPositions.add(new int[]{ori[0]+1, ori[1]});
            if (unit.getHeight()>32){
                unitPositions.add(new int[]{ori[0]+1, ori[1]+1});
                unitPositions.add(new int[]{ori[0], ori[1] + 1});
            }
        }

        return unitPositions;
    }


    public PanelGraph getPanelGraph(){
        return panelGraph;
    }

    /** object representing occupied position of Unit on PanelNode map
     *  as well as what side coming from, range, steps (on node, relates to size) and
     *  type (what obstacles they can cross)
     */
    public static class PositionData {
        //Array holding array since units can occupy more then 1 position!
        public Array<int[]> positions = new Array<int[]>();

        //Information relavent to Unit pathfinding, movement in graph
        public int unitSide = 0; //side of Unit

        public String unitName;
        public String unitSize; //size of unit

        public int unitRange; //number of nodes Unit can move in each direction
        public int[] unitSteps; //step unit takes in each direction

        public int type; //tye of terrain unit can pass

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Name: " + unitName);
            builder.append("\nUnit size: " + unitSize);
            builder.append("\nPositions: ");
            for (int[] p : positions){
                builder.append("["+Integer.toString(p[0]) + ", " + Integer.toString(p[1]) + "]");
            }
            builder.append("\nUnitRange = " + Integer.toString(unitRange));
            builder.append("\nTerrain Type = " + Integer.toString(type));

            return builder.toString();
        }
    }

    /** Simply logs previous and current position when a change occurs
     *
     * @param prev : previous position
     * @param curr : current position
     */
    private void logLocationUpdate(int[] prev, int[] curr){
        if (prev[0] != curr[0] || prev[1] != curr[1]){
            log("Update unit position from [" + prev[0] + ", " + prev[1] + ") to ["+curr[0]+ ", "+ curr[1] + "]");
        }
    }

    private void log(String message){
        Gdx.app.log("Locations LOG", message);
    }
}
