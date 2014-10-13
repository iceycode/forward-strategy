/**
 * 
 */
package com.fs.game.maps;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.fs.game.data.GameData;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;

/** DEPRECATED ... replaced by methods in MapUtils 
 * 
 * GameBoard.java
 * - creates a stage out of Textures
 * 
 * @author Allen Jagoda
 *
 */
public class GameBoard {
	
	int rows = 12;
	int columns = 12;
	int width = 32;
	int height = 32;
	int gridOriX = 800/2 - 384/2;
	int gridOriY = 100;

	//table which holds the grids
 	public Table table;
 	//protected Array<Cell> cellsArray;
	public Array<Panel> panelsOnStage;
	protected Panel panelActor;
 	
	//map actors which coincide with panels
	//also for adding terrain type to panel
 	public MapActor[][] mapActMatrix; //stores based on grid cell
	public Panel[][] panelMatrix;

 
	/**
	 * default constructor creates table
	 */
	public GameBoard() {
 
		createGrid();

 
	}//default constructor
	
	/** initialize the game board WITH TiledMap tiles as actors
	 * - needs MapFactory class to get the MapActors
	 */
	public void initialize(MapActor[][] mapActors) {
		
		this.mapActMatrix = mapActors; 
		
 	}
	
	/*****Sets all the panels positions in matrix
	 * 
	 */
	public void createGrid() {
		Array<Panel> panelsOnStage = new Array<Panel>(Constants.ROWS*Constants.COLS); //<----not using now  
		Panel[][] panelMatrix = new Panel[rows][columns];
		
		
		for (int x = 0; x < rows; x ++) 	{
			String panelName = "x"+x;
			for (int y = 0; y < columns; y++) 	 {
				float stagePosX = x*width + gridOriX;
				float stagePosY = y*height + gridOriY;
				
				Panel panelActor = new Panel(stagePosX, stagePosY);
				panelActor.setName(panelName.concat("y"+y)); //used for id
				panelActor.setMatrixPosX(x);
				panelActor.setMatrixPosY(y);
				
				//this simply gets the map terrain & sets terraintype field in
				//panel to this terrain property 
//				MapActor ma = mapActMatrix[x][y]; // <----these are seen in MapStage's TiledMap
// 				panelActor.setTerrainType(ma.terrainType);
 			
				//panelActor.toFront();
				panelMatrix[x][y] = panelActor; //store in position matrix
				panelsOnStage.add(panelActor);
			}
		}
		
		//set the elements which will be used on stage & by Unit actors
		this.panelsOnStage = panelsOnStage;
		this.panelMatrix = panelMatrix;
		this.table = createGridTable(panelMatrix); //places panels into table grid

		//this is for units
 		GameData.gridBoard = panelsOnStage;
		GameData.gridMatrix = panelMatrix;
		
 	}
	
	/*************TABLE GRID******w************
	 *  
	 *  makes a Table which can be added to stage
	 */
	public Table createGridTable(Panel[][] panelMatrix) {
		Table table = new Table();	
		table.setFillParent(false);
		
		for(int x = 0; x < rows; x++) 	{
			for (int y = 0; y < columns; y++ ) {
				// final Panel p = new Panel(stage, tiles, x, y);
				Panel panelActor = panelMatrix[x][y];
				table.add(panelActor).width(width).height(height);
				table.addActor(panelActor);
			}
			table.row(); //creates a row out of the actors
		}	
		
		return table;
 	}
	
	/**
	 * @return the panelMatrix
	 */
	public Panel[][] getPanelMatrix() {
		return panelMatrix;
	}

	/**
	 * @param panelMatrix the panelMatrix to set
	 */
	public void setPanelMatrix(Panel[][] panelArr) {
		this.panelMatrix = panelArr;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}
 
	/**
	 * @return the panelsOnStage
	 */
	public Array<Panel> getPanelsOnStage() {
		return panelsOnStage;
	}

	/**
	 * @param panelsOnStage the panelsOnStage to set
	 */
	public void setPanelsOnStage(Array<Panel> panelsOnStage) {
		this.panelsOnStage = panelsOnStage;
	}

	/**
	 * @return the panelActor
	 */
	public Panel getPanelActor() {
		return panelActor;
	}

	/**
	 * @param panelActor the panelActor to set
	 */
	public void setPanelActor(Panel panelActor) {
		this.panelActor = panelActor;
	}
 
	/**
	 * @return the mapActMatrix
	 */
	public MapActor[][] getMapActMatrix() {
		return mapActMatrix;
	}

	/**
	 * @param mapActMatrix the mapActMatrix to set
	 */
	public void setMapActMatrix(MapActor[][] mapActMatrix) {
		this.mapActMatrix = mapActMatrix;
	}
}
