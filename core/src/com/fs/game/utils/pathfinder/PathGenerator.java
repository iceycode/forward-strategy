package com.fs.game.utils.pathfinder;

/**TODO: consolidate methods into 1 class
 *  an alternative path finding class
 * - Unit uses this instead of own methods
 * - finds all possible paths here
 * - algorithm creates an ordered graph + A* scores for each calculated
 * - Heuristic is based on Manhattan distance
 * 		- NOTE: euclidean is not used since 
 * NOTES:
 * 	conversion from single digit distance occurs here
 * 
 * Algorithm:
 * 	Find adjacent panel	<=================\\
 * 		|| 								   \\
 * 		||									\\
 *      ||									 \\
 *      \/									  \\
 *  Add to list, count distance from unit	   \\
 *   - also, calculate F-score				    ||
 * 		|| 										 \\
 * 		||										  \\
 *      ||										   ||
 *      \/										   ||
 *  Check if max distance (every direction) 	   ||
 *  	&&										   ||
 *  If all directions maxed out ========NO=========||
 *  	||
 *  	|| YES
 *  	||
 *      \/
 *   PROCEED 
 *  -Scores already calculated, so that shortest path is taken if target panel selected
 *     	
 * @author Allen
 * 
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.map.Panel;
import com.fs.game.stages.GameStage;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameMapUtils;

public class PathGenerator {
	
	final String LOG = "PathGenerator LOG : ";
	private static PathGenerator instance;
	
	Unit unit; //the unit that is looking for possible move paths
 
	String unitSize; //size of the Unit needed to determine 1 unit of distance
	float maxDistance; //maximum distance Unit can travel from origin in any direction
	float incrX = 32; //measure of distance in X direction for this unit
	float incrY = 32; //measure of distance in Y direction
	boolean crossWater;
	boolean crossLand;

	
	Panel origin; //the origin panel where Unit is positioned at
	
	Array<Panel> allPanels = GameData.gamePanels;
	Array<Panel> possibleMoves = new Array<Panel>(); //the panel array of POSSIBLE moves; 
	Array<Panel> openList = new Array<Panel>(); //contains panels being inspected
	Array<Panel> movePath = new Array<Panel>(); //the move panels 
	

	//empty constructor
	public PathGenerator() {
		instance = this;
 	}


	public static PathGenerator getPG(){
		if (instance == null)
			instance = new PathGenerator();
		return instance;
	}


	/**
	 *
	 * @param unit : unit whose path is being generated
	 * @param oriX : origin of that unit on X axis (starting position)
	 * @param oriY: origin of unit on Y axis
	 */
	public void update(Unit unit, float oriX, float oriY){
		this.unit = unit;
		this.maxDistance = unit.getMaxMoves()*32;
		this.unitSize = unit.getUnitSize();
//		Gdx.app.log(LOG, "unit size = " + unitSize);
		this.crossWater = unit.crossWater;
		this.crossLand = unit.crossLand;
		setOriginPanel(oriX, oriY);
	}
	
	/** finds the panel the unit is standing on
	 * 
	 * @param x : screen x of unit
	 * @param y : screen y of unit
	 */
	public void setOriginPanel(float x, float y){
		int[] gridPos = GameMapUtils.getGridPosition(x, y);

		origin = GameData.panelMatrix[gridPos[0]][gridPos[1]];
	}
	
	
	/** returns all eligible paths b
	 *
	 * @return possibleMoves
	 */
	public Array<Panel> findPaths(){
//        setOriginPanel(unit.getX(), unit.getY());
		origin.setCostFromStart(0);
		Panel temp = origin; //temporary holder for panel being checked initialized to origin
		
		possibleMoves = new Array<Panel>(); //the panel array which will be returned
		openList.add(temp); //initialize list which stores potential neighbors
		
		//loop until max distance 
		while (openList.size > 0){
			temp = openList.pop(); //pop off first added panel on list

			if (temp.getCostFromStart() <= maxDistance){
				findAdjacentPanels(temp); //find all adjacent panels
				
				//Gdx.app.log(LOG, "added panel " + temp.getName() + "; pos: (" + temp.getX() + ", " + temp.getY()+ ")");
				
 			}
			
		}
		
		if (unit.getUnitSize().equals("64x32")||unit.getUnitSize().equals("64x64"))
            checkForSpace(); //for larger units

		Gdx.app.log("Possible moves  ", possibleMoves.toString(", "));
		
		return possibleMoves;
	}


	/** Finds adjacent panels
	 *
	 * @param currPan : current panel
	 */
	public void findAdjacentPanels(Panel currPan){
		for (Panel pan : allPanels){
			//Gdx.app.log(LOG, "Panel name is " + pan.getName() + "panel terrain type is " + pan.getTerrainName());
			
			//check for 3 requirements for adding to the open list
			if (isNeighborPanel(currPan, pan) && !isObstacle(pan) && !possibleMoves.contains(pan, false)) {
				pan.setCostFromStart(calculateCostFromStart(currPan, pan));

				//Gdx.app.log(LOG, "Panel, " + pan.getName() + "cost from start = " + pan.getCostFromStart());
				
				openList.add(pan); //added to openList to find new nodes
				possibleMoves.add(pan);
			}
 
		}
	}
 	
	
	/** Makes sure larger units have enough room to move
	 *   by adding width/height to costs to account for size
	 */
	private void checkForSpace(){
		for (Panel p : possibleMoves) {
			if (unitObstruction(p, unit)) {
				possibleMoves.removeValue(p, false);
			}
		}
	}
	
	
	/** compares nodes to see if there are neighbors horiz, vertical & diagnolly
	 * 
	 * @param parent
	 * @param child
	 * 
 	 * @return
	 */
	private boolean isNeighborPanel(Panel parent, Panel child){
		
		return (vertNeighbor(parent,child) || horizNeighbor(parent, child));// || diagNodeNeighbor(parent, child);
	}
	
	/** checks to see if neighbor left or right
	 * - doubly links together parent & child
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
 	public boolean vertNeighbor(Panel parent, Panel child){
		//return Math.abs(n1.y-n2.y)==maxY && n1.x==n2.x;
// 		Gdx.app.log(LOG, "parent is at : " + "(" + parent.getX() + ", " + parent.getY() +")");
// 		Gdx.app.log(LOG, "child is at : " + "(" + child.getX() + ", " + child.getY() +")");
 		
 		boolean verticalNeighbor = false;
 		
 		if ((parent.getY() == child.getY() + incrY)  && parent.getX()==child.getX() && !isObstacle(child)) {
 			child.panelAbove = parent;
 			parent.panelBelow = child;
 			parent.neighbor = child;
 			child.neighbor = parent;
 			
 			verticalNeighbor = true;
 		}
 		else if ((parent.getY() == child.getY() - incrY) && parent.getX()==child.getX() && !isObstacle(child)){
 			parent.panelAbove = child;
 			child.panelBelow = parent;
 			parent.neighbor = child;
 			child.neighbor = parent;
 			
 			verticalNeighbor = true;
 		}
 		
 		
		return verticalNeighbor;
	}
	
 	/** checks neighbors above or below
 	 * 
 	 * @param parent
 	 * @param child : next node
 	 * @return
 	 */
	public boolean horizNeighbor(Panel parent, Panel child){
		boolean horizNeighbor = false;
		
		if ((parent.getX() == child.getX() + incrX) && parent.getY()==child.getY() && !isObstacle(child)){
			child.panelLeft = parent;
			parent.panelRight = child;
			parent.neighbor = child;
			child.neighbor = parent;
			
			horizNeighbor = true;
		}
		else if ((parent.getX() == child.getX() - incrX) && parent.getY()==child.getY() && !isObstacle(child)){
			child.panelRight = parent;
			parent.panelLeft = child;
			child.neighbor = parent;
			parent.neighbor = child;
			
			horizNeighbor = true;
		}
		
		
		return horizNeighbor;
	}
	
	
	
	public float calculateCostFromStart(Panel parent, Panel current){
		 
		float x = Math.abs(current.getX() - parent.getX());
		float y = Math.abs(current.getY() - parent.getY());
 
		return parent.getCostFromStart() + (x + y);
 	}
	

	
	/** Method that checks for obstacles & other units in the way for all units
	 * 
	 * @param pan : Panel
	 * @return obstacle : boolean
	 */
	public boolean isObstacle(Panel pan){
		boolean unitObstacle = false; //whether this is a unit obstacle
		boolean mapObstacle = false;  //whether this is a map obstacle 
		
		GameStage stage = (GameStage)this.unit.getStage();
        Array<Unit> allUnits = GameMapUtils.findAllUnits(stage.getActors());
		Array<Unit> otherUnits = GameMapUtils.findOtherUnits(allUnits, unit);
		
		if(!crossWater && pan.terrainName.equals("water") ||
				!crossLand && pan.terrainName.equals("obstacles")){
			mapObstacle = true;
		}
		
		//checks for any units are overlapping panels
		for (Unit u : otherUnits){
			//check to see that another unit is not occupying space
			if (u.unitBox.overlaps(pan.panelBox)){
				unitObstacle = true;
			}
		}
		
		return unitObstacle || mapObstacle;
	}

    //checks around to make sure larger units cannot overalap others
    public boolean unitObstruction(Panel panel, Unit unit){
        GameStage stage = (GameStage)this.unit.getStage();
        Array<Unit> allUnits = GameMapUtils.findAllUnits(stage.getActors());
        Array<Unit> otherUnits = GameMapUtils.findOtherUnits(allUnits, unit);
        Array.ArrayIterator<Unit> unitIter = new Array.ArrayIterator<Unit>(otherUnits);

        //creates a hypothetical rectangle of where the chosen unit
        // would be on panel in order to check for overlap against other units
        while(unitIter.hasNext()){
            Rectangle r = new Rectangle(panel.getX(), panel.getY(), unit.getWidth(), unit.getHeight());
            Unit u = unitIter.next();
            if (r.overlaps(u.unitBox)){
                return true;
            }
        }

        return false;
    }

}
