package com.fs.game.data;

import com.badlogic.gdx.math.Vector2;

/** Class that stores unit data to be read/written by Json (libgdx)
 *
 *
 *
 * Created by Allen on 11/28/14.
 */
public class UnitData {

    //units ID
    private int unitID;
    private String owner;
    private String size;
    private Vector2 unitPosition; //this could be null
    private int state; //unit state
    private int damage;
    private int health;


    public UnitData(){

    }

    public int getUnitID() {
        return unitID;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
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

//    @Override
//    public void write(Json json) {
//        json.writeValue("unitID", unitID);
//        json.writeValue("owner", owner);
//        json.writeValue("size", size);
//        json.writeValue("state", state);
//        json.writeValue("unitPosition", unitPosition);
//        json.writeValue("damage", damage);
//        json.writeValue("health", health);
//    }
//
//    @Override
//    public void read(Json json, JsonValue jsonData) {
//
//    }
}
