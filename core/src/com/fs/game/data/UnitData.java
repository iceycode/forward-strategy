package com.fs.game.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fs.game.ai.fsm.AgentUtils;
import com.fs.game.map.Locations;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitState;

/** Class that stores unit data to be read/written by Json (libgdx) for Multiplayer
 *  Appwarp client send/request protocol
 *
 *  For AgentManager, this represents updates {@link Locations} and
 *  {@link AgentUtils}.
 *
 * Created by Allen on 11/28/14.
 */
public class UnitData {

    //states a players opponent's Unit can be in when sending info
    public static final int IS_STANDING = -1; //just standing
    public static final int IS_CHOSEN = 0; //unit was chosen
    public static final int IS_MOVING = 1; //unit is now moving
    public static final int IS_ATTACKING = 2; //unit just attacked
    public static final int IS_DAMAGED = 3; //unit was just dsamaged

    private int unitID;

    private String owner;
    private String size;
    private Vector2 unitPosition; //this could be null
    private Locations.PositionData posData; //updating position data (for panel states)
    private int state; //unitData update state - eg, Unit was IS_CHOSEN or IS_MOVING
    private int damage; //units current damage
    private int health; //units current health


    //NEW VARIABLES FOR NEW SETUP
    private String name;
    private UnitState unitState; //if Unit chosen
    private int[] target; //target panel graph position
    private Array<String> enemies; //enemy units by name


    public UnitData(){
        
    }


    public void updateData(Unit unit, Vector2 pos){
        setName(unit.getName());
        setOwner(unit.getOwner());
        setState(unit.animState.getValue());
        setDamage(unit.damage);
        setHealth(unit.health);
        setUnitPosition(pos);
        setPosData(unit.getPosData());

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public void setSize(String size) {
        this.size = size;
    }

    public Vector2 getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(Vector2 unitPosition) {
        this.unitPosition = unitPosition;
    }

    public void setUnitState(UnitState state){
        this.unitState = state;
    }

    public UnitState getUnitState(){
        return unitState;
    }

    public void setEnemies(Array<String> enemies){
        this.enemies = enemies;
    }

    public Array<String> getEnemies(){
        return enemies;
    }

    public void setTarget(int[] target){
        this.target = target;
    }

    public int[] getTarget(){
        return target;
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


    public Locations.PositionData getPosData() {
        return posData;
    }

    public void setPosData(Locations.PositionData posData) {
        this.posData = posData;
    }

}
