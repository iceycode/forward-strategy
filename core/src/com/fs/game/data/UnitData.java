package com.fs.game.data;

import com.badlogic.gdx.math.Vector2;
import com.fs.game.map.Locations;

/** Class that stores unit data to be read/written by Json (libgdx) for Multiplayer
 *  Appwarp client send/request protocol
 *
 *  For AgentManager, this represents updates {@link Locations} and
 *  {@link com.fs.game.ai.fsm.RiskFactors}.
 *
 * Created by Allen on 11/28/14.
 */
public class UnitData {

    private int unitID;
    private String owner;
    private String size;
    private Vector2 unitPosition; //this could be null
    private int state; //unit animState
    private int damage;
    private int health;


    public UnitData(){
        
    }


    public void updateData(String owner, int stateVal, int damage, int health, Vector2 pos){
        setOwner(owner);
        setState(stateVal);
        setDamage(damage);
        setHealth(health);
        setUnitPosition(pos);
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

    public void updateNodePosition(float screenX, float sceenY){

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

}
