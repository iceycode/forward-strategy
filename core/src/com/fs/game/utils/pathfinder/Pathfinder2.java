package com.fs.game.utils.pathfinder;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.units.Unit;
import com.fs.game.utils.Constants;

public class Pathfinder2 {
	
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
	

	public Pathfinder2() {
		
 	}

}
