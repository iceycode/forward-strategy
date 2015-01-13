package com.fs.game.utils.pathfinder;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.actors.Panel;
import com.fs.game.actors.Unit;
import com.fs.game.assets.Constants;

public class NodeUtils {


	/** returns an ordered graph of the nodes which can or cannot be moved to
	 *  -ordered by columns or y
	 * TODO: figure out what this will be used for
	 * 
	 * @param panelArray
	 * @param target
	 * @param target
	 * @return Array<GridNode> nodeGraph
	 */
	public static Array<GridNode> createOrderedGraph(Array<Vector2> panelArray, GridNode target, GridNode start){
		
		double[][] orderedNodeGraph = Constants.GRID_SCREEN_VECTORS;
 
		Array<GridNode> nodeGraph = new Array<GridNode>();
		
 		for (int x = 0; x < orderedNodeGraph.length; x++){
 			for (int y = 0; x < orderedNodeGraph.length; y++){
 	 			GridNode node = new GridNode(new Vector2(x,y), start, target);
 	 			nodeGraph.add(node);
  			}
  		}
 		
 		return nodeGraph;
	}
	
	/** creates a map out of positions unit can move to
	 * NOTE: already filtered out other units
	 * 
	 * @param unit
	 * @param end
	 * @param start
	 * @return
	 */
	public static Array<GridNode> createNodeMap(Unit unit, GridNode start, GridNode end) {
		Array<GridNode > gridNodeGraph = new Array<GridNode>();
		Array<Panel> panelArray = unit.panelArray;
		
		for (Panel p : panelArray){
 			GridNode node = new GridNode(p.location, start, end);
			gridNodeGraph.add(node);
		}
		
		
		return gridNodeGraph;
	}


	
	/** this method turns unit panelArray into a graph with grid nodes
	 * 
	 * @param panelArray
	 */
	public static Array<Vector2> getVectorMap(Array<Panel> panelArray){
		Array<Vector2> gridVectors = new Array<Vector2>();
  		
  		for (Panel p : panelArray){
			gridVectors.add(p.location);
		}

  		return gridVectors;
	}
	
 	
	public static void calculateTotalCost(GridNode node, GridNode parent, GridNode target){
 
		node.costFromStart = parent.costFromStart + node.calculateCostFromStart();
		node.costToGoal = node.calculateCostToGoal(); //diagnol heuristic
		//node.costToGoal = node.calculateCostManhattanDistance(); //a different heuristic

	}
	
	public static void	calculateCostFromParent(GridNode node, GridNode parent){
 		node.costFromStart = node.calculateCostFromStart();
 	}
	
	
	public static void calculateCostToGoal(GridNode node, GridNode parent){
		node.setParent(parent);
		node.costFromStart = node.calculateCostFromStart();
		
	}
	

	public static Vector2 getBoardPositions(GridNode node){
		float x = (node.x - 208)/32;
		float y = (node.y - 100)/32;
		
		return new Vector2(x,y);
	}
	
	
/*-----------Positions of nodes------------
 * 
 * - method finds position of node relative to parent
 * 
 * 
 * 
 */
	/** if this is right of successor
	 * 
	 * @param curr
	 * @param dest
	 * @return
	 */
	public static boolean isRight(GridNode curr, GridNode dest){
		return curr.x < dest.x;
	}
	
	/** if it is left of successor 
	 * 
	 * @param curr
	 * @param dest
	 * @return
	 */
	public static boolean isLeft(GridNode curr, GridNode dest){
		return curr.x > dest.x;
	}
 	
	/** if it is above successor 
	 * 
	 * @param curr
	 * @param dest
	 * @return
	 */
	public static boolean isUp(GridNode curr, GridNode dest){
		return curr.y > dest.y;
	}
	
	/** if it is below of successor 
	 * 
	 * @param curr
	 * @param dest
	 * @return
	 */
	public static boolean isDown(GridNode curr, GridNode dest){
		return curr.y < dest.y;
	}
}
