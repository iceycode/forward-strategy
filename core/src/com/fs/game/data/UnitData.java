package com.fs.game.data;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/** Class that stores unit data to be read/written by Json (libgdx)
 *
 *
 *
 * Created by Allen on 11/28/14.
 */
public class UnitData {

    //units ID
    private int unitID;
    private float[] size;
    private Vector2 unitPosition; //this could be null
    private int state; //unit state
    private String owner;

    private ArrayList<Vector2> panelPositions; //panel positions of unit (if selected
    private int damage;
    private int health;

    public UnitData(int id, int state, float[] size, String owner){
        this.unitID = id;
        this.state = state;
        this.size = size;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public float[] getSize() {
        return size;
    }

    public void setSize(float[] size) {
        this.size = size;
    }

    public Vector2 getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(Vector2 unitPosition) {
        this.unitPosition = unitPosition;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<Vector2> getPanelPositions() {
        return panelPositions;
    }

    public void setPanelPositions(ArrayList<Vector2> panelPositions) {
        this.panelPositions = panelPositions;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
