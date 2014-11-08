package com.fs.game.utils.pathfinder;

/** all heuristic metrics are set to only two digits for efficiency/speed 
 * 
 * euclidian distance for medium units is 71.554 which is about 72
 * 				for large is 90.509 which is about 91
 * 				for small it is 45
 * 
 * 
 * @author Allen 
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class GridNode extends Vector2 {
	
	final String LOG = "GridNode LOG: ";
	
	public Vector2 location; //location in grid using int values
	
	public GridNode parent; //the parent of this grid
	public GridNode goal; //the goal
 
	//score costs 
	public float costFromStart; //the G value
	public float costToGoal;		//the H value
	public float totalCost; //the F value
	
	public boolean blocked = false; //if this panel is blocked (not moveableTo or selected)
	
	//distance b/w target & current
	private float gridDistX = 32; //horizontal distance b/w squares ... larger is 64
	private float gridDistY = 32; //vertical distance b/w nodes
  	private float euclidDist = 45; //normal euclidan distance ... larget is 91
 	
	public GridNode(Vector2 pos, GridNode parent, GridNode goal) {
		
		super(pos);
		
 		this.location = pos;
		this.parent = parent;
		this.goal = goal;
 
		//for calculating G, or move cost from start
 		if (parent!= null){
			this.costFromStart = parent.getCostFromStart() + calculateCostFromStart();
 			//Gdx.app.log("LOG", " initial G (movement) " + this + " is " + costFromStart);

		}
 		else 
 			this.costFromStart =  0;  
 		 
 		//for calculating H, or heuristic
 		if (goal!=null){
 			this.costToGoal = calculateCostToGoal(); //this gets more accurate costs\
 			//Gdx.app.log("LOG", " initial heuristic (H) of node " + this + " is " + costToGoal);
		}
 		else 
 			this.costToGoal =  0;
	}
	
 
	
	/** returns G, or movement cost from one location to another
	 * 
	 * @param currX
	 * @param currY
	 * @param parentX
	 * @param parentY
	 * @return total cost in int (faster processing if int)s
	 */
	public float calculateCostFromStart(){
 
		float x = Math.abs(this.x - parent.x);
		float y = Math.abs(this.y - parent.y);
 
		return parent.getCostFromStart() + (x + y);
		//return parent.getCostFromStart() + diagDist;
	}
	
	
	/** returns the manhattan distance
	 *   H = Math.abs(start.x-destination.x) + Math.abs(start.y-destination.y));
	 *   
	 * @return
	 */
	public float calculateCostManhattanDistance(){
		
		float x = Math.abs(this.location.x - goal.location.x);
		float y = Math.abs(this.location.y - goal.location.y);
		
		float dist = 32*(x + y);
 		 
		return dist;
	}
	
	
	/** returns the heuristic cost (Diagonal Shortcut Heuristic)
	 * 
	 * @param goal
	 * @return
	 */
 	public float calculateCostToGoal(){
		float xDist = Math.abs(this.x - goal.x);
		float yDist = Math.abs(this.y - goal.y);
		
 		if (xDist > yDist)
			return euclidDist*yDist + gridDistY*(xDist - yDist);
		
		return euclidDist*xDist + gridDistX*(yDist - xDist);
	}

	
 	/**recalculates the costs based on a new parent
 	 * 
 	 */
 	public void calculateTotalCosts(){
		this.costFromStart = calculateCostFromStart();
		this.costToGoal = calculateCostToGoal();
 	}
	
	/** returns total score F = G + H
	 * 
	 * @return
	 */
	public float getTotalCost(){
		return costToGoal + costFromStart;
	}
	
	 
	
	/**
	 * 
	 * @return
	 */
	public void setTotalCost(int cost){
		
		this.totalCost = cost;
	}
	public GridNode getParent() {
		return parent;
	}

	public void setParent(GridNode parent) {
		this.parent = parent;
	}

	public float getCostFromStart() {
		return costFromStart;
	}
  
	public void setCostToGoal(int costToGoal) {
		this.costToGoal = costToGoal;
	}
 

	public GridNode getGoal() {
		return goal;
	}



	public void setGoal(GridNode goal) {
		this.goal = goal;
	}



	public float getCostToGoal() {
		// TODO Auto-generated method stub
		return costToGoal;
	}



	public float getGridDistX() {
		return gridDistX;
	}



	public void setGridDistX(float gridDistX) {
		this.gridDistX = gridDistX;
	}



	public float getGridDistY() {
		return gridDistY;
	}



	public void setGridDistY(float gridDistY) {
		this.gridDistY = gridDistY;
	}



	public float getEuclidDist() {
		return euclidDist;
	}



	public void setEuclidDist(float euclidDist) {
		this.euclidDist = euclidDist;
	}
}
