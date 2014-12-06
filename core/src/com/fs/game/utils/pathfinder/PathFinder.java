package com.fs.game.utils.pathfinder;

/** PathFinder.java
 * an A* search algorithm 
 * Steps:
 *  1) add start to open list, set as parent
 *  2) add adjacent panels to open list
 *  3) find the panel with lowest score (F = G+H)
 *    - ignore if not walkable (don't need to do this)
 *  4) remove from open list
 *  	add to closed list
 *  5) check adjacent squares (ignore units & terrain)
 *  	add to open list if they are not in it
 *  	make current target the parent
 *  6) Check to see if adjacent square is lower then current 
 *  	if current > adjacent
 *  		add adjacent to list, recalculate G
 *  	else ignore
 * 
 * 
 REF http://web.mit.edu/eranki/www/tutorials
// A* Pseudocode 
initialize the open list
initialize the closed list
put the starting node on the open list (you can leave its f at zero)

while the open list is not empty
    find the node with the least f on the open list, call it "q"
    pop q off the open list
    generate q's 8 successors and set their parents to q
    for each successor
    	if successor is the goal, stop the search
        successor.g = q.g + distance between successor and q
        successor.h = distance from goal to successor
        successor.f = successor.g + successor.h

        if a node with the same position as successor is in the OPEN list \
            which has a lower f than successor, skip this successor
        if a node with the same position as successor is in the CLOSED list \ 
            which has a lower f than successor, skip this successor
        otherwise, add the node to the open list
    end
    push q on the closed list
end

 * 
 * @author Allen Jagoda
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.assets.Constants;

public class PathFinder {
	
	final String LOG = "PathFinder LOG : ";
	
	//unit from and to info
	Unit unit;			//unit moving
	int maxX = 32; //max distance traveled to each square (more for larger units)
	int maxY = 32; //max vertical distance a unit can travel
	Array<Vector2> unitMovePath; //the shortest path unit takes
 	
	Array<GridNode> openList;	//the list into which grid adjacent grid nodes go into
	Array<GridNode> closedList; //the list of nodes within graph
  	
	Array<Vector2> gridVectors; //the set of nodes (directed graph)
	Array<GridNode> gridNodeGraph; 
	
	double[][] graph = Constants.GRID_SCREEN_VECTORS; //all grid vectors from columns 1 to 12
	
	public PathFinder(Unit uni, Panel target ) {
		this.unit = uni;
 
 		openList = new Array<GridNode>();
		closedList = new Array<GridNode>();


		Vector2 startVec = new Vector2(unit.getX(), unit.getY());
		Vector2 targetVec = target.getLocation();

		GridNode end = new GridNode(targetVec, null, null);
		GridNode start = new GridNode(startVec, null , end);

		Gdx.app.log(LOG, " START is at : " + NodeUtils.getBoardPositions(start) +
				", TARGET is at : " + NodeUtils.getBoardPositions(end));

		//create the map of nodes unit needs to navigate
		gridNodeGraph = NodeUtils.createNodeMap(uni, start, end);
		
		//initialize open & closed lists
		openList.add(start);	//add parent to open list
		closedList.add(start);

		unitMovePath = findNodePath(start, end);

  	}
 
	public Array<Vector2> findNodePath(GridNode parent, GridNode end) {
		//while list is not empty
		while(openList.size > 0){
 
			if ((parent.x == end.x && parent.y == end.y)){    
 				return shortestPath();
			}

  			addNeighborNodes(parent, end); //adds successors if they exist

  			Gdx.app.log(LOG, " parent is currently at " + NodeUtils.getBoardPositions(parent) 
				+ ", G " + parent.getCostFromStart() + " + H  "
				+ parent.costToGoal + " = F  " + parent.getTotalCost());
  
  			//parent = findBestNode2(parent);
   			parent = findBestNode(parent);
			closedList.add(parent);  


			Gdx.app.log(LOG, " node, now PARENT, removed from OPEN LIST & added to CLOSED LIST : " 
					+ NodeUtils.getBoardPositions(parent) 
					+ ", G " + parent.getCostFromStart() + " + H  "
					+ parent.costToGoal + " = F  " + parent.getTotalCost());
 		}
		
		Gdx.app.log(LOG, "path was not completed!!!!!! currently contains " + shortestPath());
 		return shortestPath(); 
 	}
	
 
	/**  returns the next node to be parent
	 * 
	 * @return
	 */
	private GridNode findBestNode(GridNode parent){
		 
		int lowestCostIndex = 0;
		float cost = parent.getTotalCost(); //current parent
		
		for(int index = 1; index < openList.size; index++){
			GridNode node = openList.get(index);
			if(node.getTotalCost() < cost){
 
				cost = node.getTotalCost();
				lowestCostIndex = index;
			}	
 
		}
		
		GridNode node = openList.removeIndex(lowestCostIndex); //remove from openList
		
		
		Gdx.app.log(LOG, " node removed and new parent node is " + NodeUtils.getBoardPositions(node));
 		
		return node;
	}
	 
	private void addNeighborNodes(GridNode parent, GridNode target){
		
 		//add neighbor nodes to list
		for (GridNode node : gridNodeGraph){
 			//checks see if blocks are neighbors or obstacles, units, etc
 			if (isNeighborNode(parent, node) ){
 				//if not in closed list already & cost of totalCost (F) of parent is greater then cost of node
				if (!closedList.contains(node, false) && parent.getTotalCost() > node.getTotalCost()){
					node.setParent(parent);
					node.setGoal(target);
					node.calculateTotalCosts();					
					
 					openList.add(node);
 					
					Gdx.app.log(LOG, " node added to OPEN LIST : " + NodeUtils.getBoardPositions(parent) + 
							", G " + node.getCostFromStart() + " + H  "
							+ node.costToGoal + " = F  " + node.getTotalCost());
					
					
					Gdx.app.log(LOG, "Open list contains values : " + openList.toString(", "));
 				}
				else if (openList.contains(node, false) && node.getCostFromStart() < parent.getCostFromStart() ){
					node.setParent(parent);
					node.calculateCostFromStart();
					node.calculateCostToGoal();

					//openList.add(node);
					Gdx.app.log(LOG, " new cost (G) of node in open list is "+ node.getCostFromStart());
				}
				else if (!closedList.contains(node, false) && !openList.contains(node, false)){
					node.setParent(parent);
					node.calculateCostFromStart();
					node.calculateCostToGoal();
					
					openList.add(node);
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
	private boolean isNeighborNode(GridNode parent, GridNode child){
			return (vertNodeNeighbor(parent,child) || horizNodeNeighbor(parent,child));// || diagNodeNeighbor(parent, child);
	}
	
	/** checks to see if neighbor left or right
	 * 
	 * @param parent
	 * @param node
	 * @return
	 */
 	public boolean vertNodeNeighbor(GridNode parent, GridNode node){
		//return Math.abs(n1.y-n2.y)==maxY && n1.x==n2.x;
 		
 		return (parent.y == node.y + maxY || parent.y == node.y - maxY) && parent.x==node.x;
	}
	
 	/** checks neighbors above or below
 	 * 
 	 * @param parent
 	 * @param node
 	 * @return
 	 */
	public boolean horizNodeNeighbor(GridNode parent, GridNode node ){
  		
		return (parent.x == node.x + maxX || parent.x == node.x - maxX) && parent.y==node.y;
	}
	
	/** checks neighbors that are diagonal from n1
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public boolean diagNodeNeighbor(GridNode n1, GridNode n2){
		return (n1.x == n2.x + maxX || n1.x == n2.x - maxX) && (n1.y == n2.y + maxY || n1.y == n2.y - maxY);
	}
	
	
	/** returns vectors which actor uses to move from grid to grid
	 * these are all positions unit will be moving to
	 * also, these need to be reversed
	 * 
	 * 
	 * @return unitMovePath : the shortest path on the grid
	 */
	public Array<Vector2> shortestPath(){
		
		Array<Vector2> unitMovePath = new Array<Vector2>();
		
		for (GridNode node : closedList){
			unitMovePath.add(node.location);
			Gdx.app.log(LOG, "this node is on path: " + NodeUtils.getBoardPositions(node) );
		}
 
		
		Gdx.app.log(LOG, " unit move path is : " + unitMovePath.toString(","));
		
		return unitMovePath;
	}


	public Array<Vector2> getUnitMovePath() {
		return unitMovePath;
	}


	public void setUnitMovePath(Array<Vector2> unitMovePath) {
		this.unitMovePath = unitMovePath;
	}
 
}
