/**
 * 
 */
package com.fs.game.units;

import com.badlogic.gdx.utils.Array;

/** UnitInfo.java class
 * stores unit stats from json file
 * - NOTE: json must have exact matching string
 * with that in this class's field
 * 
 * 
 * NOTE:
 *  - missing pics:
 *      troops, pathmaker
 *  - missing frame sheets:
 * 
 * 
 * @author Allen
 */
public class UnitInfo {

	protected int id;
	protected String faction;
	protected String unit;
	protected String type; 
	protected String anti;   //unit counter ability
	protected String size; //32x32 ^ 32x64 ^ 64x64 ^ 64x32
	public String crossWater; //Yes or No
	public String crossLand; //Yes or No
	protected int maxMoves;      //range between 1 and 5
	protected String unitPath;	//unit pictures within assets folder
	protected int[] damageList;
	
	//width & height are set to small units by default
	protected float width;
	protected float height;
	
	protected Array<String> texPaths;
	
	public UnitInfo() { }//empty constructor
 	
	/**
	 * @return the faction
	 */
	public String getFaction() {
		return faction;
	}
	/**
	 * @param faction the faction to set
	 */
	public void setFaction(String faction) {
		this.faction = faction;
	}
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		 
		this.size = size;
	}
	/**
	 * @return the crossrivers
	 */
	public String isCrossWater() {
		return crossWater;
	}
	/**
	 * @param crossrivers the crossrivers to set
	 */
	public void setCrossWater(String crosswater) {
		this.crossWater = crosswater;
	}
	/**
	 * @return the crossLandObst
	 */
	public String isCrossLandObst() {
		return crossLand;
	}
	/**
	 * @param crossLandObst the crossLandObst to set
	 */
	public void setCrossLandObst(String crossLandObst) {
		this.crossLand = crossLandObst;
	}
	/**
	 * @return the maxmoves
	 */
	public int getMaxMoves() {
		return maxMoves;
	}
	/**
	 * @param maxmoves the maxmoves to set
	 */
	public void setMaxMoves(int maxMoves) {
		this.maxMoves = maxMoves;
	}
	/**
	 * @return the unitAnti
	 */
	public String getUnitAnti() {
		return anti;
	}
	/**
	 * @param unitAnti the unitAnti to set
	 */
	public void setUnitAnti(String unitAnti) {
		this.anti = unitAnti;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the unitpics
	 */
	public String getUnitPath() {
		return "units/"+unitPath;
	}
	/**
	 * @param unitpics the unitpics to set
	 */
	public void setUnitPath(String unitPath) {
		this.unitPath = unitPath;
	}
	
	/**
	 * @return the damageList
	 */
	public int[] getDamageList() {
		return damageList;
	}
	/**
	 * @param damageList the damageList to set
	 */
	public void setDamageList(int[] damageList) {
		this.damageList = damageList;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Array<String> getTexPaths() {
		return texPaths;
	}

	public void setTexPaths(Array<String> texPaths) {
		this.texPaths = texPaths;
	}
}
