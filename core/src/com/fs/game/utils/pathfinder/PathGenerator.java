package com.fs.game.utils.pathfinder;

/** an alternative path finding class
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
 * 		|| 									\\
 * 		||									 \\
 *      ||									  \\
 *      \/									   \\
 *  Add to list, count distance from unit		\\
 *   - also, calculate F-score					||
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.units.Unit;
import com.fs.game.utils.Constants;
import com.fs.game.utils.UnitUtils;

public class PathGenerator {
	
	final String LOG = "PathFinder2 LOG : ";
	
	Unit unit; //the unit that is looking for possible move paths
 
	String unitSize; //size of the Unit needed to determine 1 unit of distance
	float maxDistance; //maximum distance Unit can travel from origin in any direction
	float incrX = 32; //measure of distance in X direction for this unit
	float incrY = 32; //measure of distance in Y direction
	boolean crossWater;
	boolean crossLand;

	
	Panel origin; //the origin panel where Unit is positioned at
	
	double[][] PANEL_COORD_GRAPH = Constants.GRID_SCREEN_VECTORS;
	Array<Panel> allPanels = GameData.gamePanels;
	Array<Panel> possibleMoves = new Array<Panel>(); //the panel array of POSSIBLE moves; 
	Array<Panel> openList = new Array<Panel>(); //contains panels being inspected
	Array<Panel> movePath = new Array<Panel>(); //the move panels 
	
	public Array<Panel> unitMovePath; //the shortest path unit takes

	/** 
	 * 
	 * @param unit
	 * @param oriX
	 * @param oriY
	 */
	public PathGenerator(Unit unit, float oriX, float oriY) {
		this.unit = unit;
		this.maxDistance = unit.getMaxMoves()*32;
		this.unitSize = unit.getUnitSize();
		Gdx.app.log(LOG, "unit size = " + unitSize);
		this.crossWater = unit.crossWater;
		this.crossLand = unit.crossLand;
		getUnitOrigin(oriX, oriY);
		
//		if (unitSize.equals("32x32")){
//			this.incrX = 32;
//			this.incrY = 32;
//			
//		}
//		else if (unitSize.equals("64x32")){
//			this.incrX = 64;
//			this.incrY = 32;
//		}
//		else if (unitSize.equals("64x64")){
//			this.incrX = 64;
//			this.incrY = 64;
//		}
//		
//		
 	}
	
	/** finds the panel the unit is standing on
	 * 
	 * @param panX
	 * @param panY
	 */
	public void getUnitOrigin(float panX, float panY){
		for (Panel p : allPanels){
			if (p.getX() == panX && p.getY() == panY){
				this.origin = p;
				break;
			}
		}
	}
	
	
	/** returns all eligible paths b
	 * 
	 * @param incrX : 1 unit of movement in X direction
	 * @param incrY : 1 unit of movement in Y direction
	 * @return
	 */
	public Array<Panel> findPaths(){
		origin.setCostFromStart(0);
		Panel temp = origin; //temporary holder for panel being checked initialized to origin
		
		possibleMoves = new Array<Panel>(); //the panel array which will be returned
		openList.add(temp); //initialize list which stores potential neighbors
		
		//loop until max distance 
		while (openList.size>0){
			temp = openList.pop(); //pop off first added panel on list

			if (temp.getCostFromStart() <= maxDistance){
				findAdjacentPanels(temp); //find all adjacent panels
 	
				
				Gdx.app.log(LOG, "added panel " + temp.getName()
						+ "at location : (" + temp.getX() + ", " + temp.getY()+ ")");
				
 			}
			
		}
		
		checkForSpace(); //for larger units
		
		
		return possibleMoves;
	}
	
	
	public void findAdjacentPanels(Panel currPan){
		for (Panel pan : allPanels){
			
			
			Gdx.app.log(LOG, "Panel name is " + pan.getName() + "panel terrain type is " + pan.getTerrainType());
			
			//check for 3 requirements for adding to the open list
			if (isNeighborPanel(currPan, pan) && !isObstacle(pan) && !possibleMoves.contains(pan, false)) {
				pan.setCostFromStart(calculateCostFromStart(currPan, pan));
 
				

				Gdx.app.log(LOG, "Panel, " + pan.getName() + "cost from start = " + pan.getCostFromStart());
				
				openList.add(pan); //added to openList to find new nodes
				possibleMoves.add(pan);
				
			}
 
		}
	}
 	
	
	/** Makes sure larger units have enough room to move
	 * 
	 * 
	 */
	private void checkForSpace(){
		float tempValue = 0f;
		for (Panel p : openList){
			
			if (unitSize.equals("64x32")){
				if (p.panelLeft==null || p.panelRight ==null){
					possibleMoves.removeValue(p, false);
					tempValue = p.getCostFromStart();
				}
				
				if (tempValue>=p.getCostFromStart() || tempValue<=p.getCostFromStart()){
					possibleMoves.removeValue(p, false);
				}
				
			}
			else if (unitSize.equals("64x64")){
				possibleMoves.removeValue(p, false);
 			}
			else{
				possibleMoves.add(p); //move options that will be returns	
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
	 * @param incrY
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
	
	/** returns a Manhattan distance Heuristic  
	 *   H = Math.abs(start.x-destination.x) + Math.abs(start.y-destination.y));
	 *   
	 * @parent : current node in queue
	 * @neighbor : neighbor node whose Heuristic is being calculated
	 * @return
	 */
	public float calculateHeuristic(Panel current, Panel goal){
		
		float x = Math.abs(current.getX() - goal.getX());
		float y = Math.abs(current.getY() - goal.getY());
	
		return x + y;
	}
	
	/** ----------Method that checks for obstacles & other units in the way------------------
	 * TODO: Figure out what to do about the panels which are neighbors of 
	 * 
	 * @param pan : Panel
	 * @return obstacle : boolean
	 */
	public boolean isObstacle(Panel pan){
		boolean unitObstacle = false; //whether this is a unit obstacle
		boolean mapObstacle = false;  //whether this is a map obstacle 
		
		MapStage stage = (MapStage)this.unit.getStage();
		Array<Unit> otherUnits = UnitUtils.findOtherUnits(stage.getActors(), unit);
		
		if(!this.crossWater && pan.terrainType.equals("water") ||
				!this.crossLand && pan.terrainType.equals("obstacles")){
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
	
	/** method for finding the shortest path
	 * - uses A* algorithm to find best path 
	 * - since distance from start is already set, heuristic only set
	 * 
	 * @param moveOptions
	 * @param goal
	 * @param start
	 * @return
	 */
	public Array<Panel> findBestPath(Panel goal){
		movePath = new Array<Panel>(); //stores the panels unit will move along
		Panel parent = origin; //set panel to origin
		
		openList.add(parent); //initialize the open list
		movePath.add(parent);
		
		//calculate the Heuristics for all Panels in openList
		for (Panel pan : openList){
			pan.setHeuristic(calculateHeuristic(pan, goal));
		}
		
		while (openList.size > 0){
			
			addNeighborPanels(parent, goal);
 			
			parent = findBestPanel(parent);
			movePath.add(parent);
			
			if (goal.getX() == parent.getX() && goal.getY() == parent.getY()){    
 				return movePath;
			}
			
		}
		
		return null;
	}
	
	
	private void addNeighborPanels(Panel current, Panel goal){
		
 		//add neighbor nodes to list
		for (Panel pan : possibleMoves){
 			//checks see if blocks are neighbors or obstacles, units, etc
 			if (isNeighborPanel(current, pan)){
 				//if not in closed list already & cost of totalCost (F) of parent is greater then cost of node
				if (!movePath.contains(pan, false) && current.getTotalCost() > pan.getTotalCost()){
					pan.neighbor = current;			
 					openList.add(pan);
					
					Gdx.app.log(LOG, "Open list contains values : " + openList.toString(", "));
 				}
				else if (!movePath.contains(pan, false) && !openList.contains(pan, false)){
					pan.neighbor = current;
 
					
					openList.add(pan);
				}
  			}
 		}
 	}
	
	public Panel findBestPanel(Panel parent){
		int lowestCostIndex = 0;
		float cost = parent.getTotalCost(); //current parent
		
		for(int index = 1; index < openList.size; index++){
			Panel pan = openList.get(index);
			if(pan.getTotalCost() < cost){
				cost = pan.getTotalCost();
				lowestCostIndex = index;
			}	
 
		}
		
		Panel next = openList.removeIndex(lowestCostIndex); //remove from openList
		
		
		return next;
	}
}
