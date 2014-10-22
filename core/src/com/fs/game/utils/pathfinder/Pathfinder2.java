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

public class Pathfinder2 {
	
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
	Array<Panel> closedList = new Array<Panel>(); //the panel array which will be returned; 
	Array<Panel> openList = new Array<Panel>(); //contains panels being inspected
	
	
	public Array<Panel> unitMovePath; //the shortest path unit takes

	/** 
	 * 
	 * @param unit
	 * @param oriX
	 * @param oriY
	 */
	public Pathfinder2(Unit unit, float oriX, float oriY) {
		this.unit = unit;
		this.maxDistance = unit.getMaxMoves()*32;
		this.unitSize = unit.getUnitSize();
		Gdx.app.log(LOG, "unit size = " + unitSize);
		this.crossWater = unit.crossWater;
		this.crossLand = unit.crossLand;
		getUnitOrigin(oriX, oriY);
		
		if (unitSize.equals("32x32")){
			this.incrX = 32;
			this.incrY = 32;
		}
		else if (unitSize.equals("64x32")){
			this.incrX = 64;
			this.incrY = 32;
		}
		else if (unitSize.equals("64x64")){
			this.incrX = 64;
			this.incrY = 64;
		}
		
		
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
		
		closedList = new Array<Panel>(); //the panel array which will be returned
		openList.add(temp); //initialize list
		
		//loop until max distance 
		while (openList.size>0){
			temp = openList.pop(); //pop off first added panel on list

			if (temp.getCostFromStart() <= maxDistance){
				findAdjacentPanels(temp, incrX, incrY); //find all adjacent panels

				Gdx.app.log(LOG, "added panel " + temp.getName()
						+ "at location : (" + temp.getX() + ", " + temp.getY()+ ")");
				
 			}
			
		}
		
		return closedList;
	}
	
	
	public void findAdjacentPanels(Panel currPan, float incrX, float incrY){
		for (Panel pan : allPanels){
			//check for 3 requirements for adding to the open list
			if (isNeighborPanel(currPan, pan, 32, 32)) {
				if (!isObstacle(pan) && !closedList.contains(pan, false)){
					pan.setCostFromStart(calculateCostFromStart(currPan, pan));
					Gdx.app.log(LOG, "Panel, " + pan.getName() + "cost from start = " + pan.getCostFromStart());
					closedList.add(pan);
					openList.add(pan);
				}
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
	private boolean isNeighborPanel(Panel parent, Panel child, float incrX, float incrY){
		
		return (vertNeighbor(parent,child, incrY) || horizNeighbor(parent, child, incrX));// || diagNodeNeighbor(parent, child);
	}
	
	/** checks to see if neighbor left or right
	 * 
	 * @param parent
	 * @param child
	 * @param incrY
	 * @return
	 */
 	public boolean vertNeighbor(Panel parent, Panel child, float incrY){
		//return Math.abs(n1.y-n2.y)==maxY && n1.x==n2.x;
// 		Gdx.app.log(LOG, "parent is at : " + "(" + parent.getX() + ", " + parent.getY() +")");
// 		Gdx.app.log(LOG, "child is at : " + "(" + child.getX() + ", " + child.getY() +")");
 		
		return (parent.getY() == child.getY() + incrY || parent.getY() == child.getY() - incrY) && parent.getX()==child.getX();
	}
	
 	/** checks neighbors above or below
 	 * 
 	 * @param parent
 	 * @param child : next node
 	 * @return
 	 */
	public boolean horizNeighbor(Panel parent, Panel child, float incrX ){
		//return Math.abs(n1.x-n2.x)==maxX && n1.y==n2.y;
 		
		return (parent.getX() == child.getX() + incrX || parent.getX() == child.getX() - incrX) && parent.getY()==child.getY();
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
	public float calculateCostManhattanDistance(Panel current, Panel goal){
		
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
		Array<Unit> allUnits = UnitUtils.findAllUnits(stage.getActors());
		Array<Unit> otherUnits = UnitUtils.findOtherUnits(stage.getActors(), unit);
		
		if((!this.crossWater && pan.terrainType.equals("water")) ||
				(!this.crossLand && pan.terrainType.equals("obstacles"))){
			mapObstacle = true;
		}
		//checks for any untis overlapping panels
		for (Unit u : otherUnits){
				//check to see that another unit is not occupying space
			if (u.unitBox.overlaps(pan.panelBox)){
				unitObstacle = true;
			}
		}
		
		return unitObstacle || mapObstacle;
	}
}
