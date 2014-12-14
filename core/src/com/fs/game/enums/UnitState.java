package com.fs.game.enums;

/** enums for unit states
 * - integer value associated with JSON data for multiplayer
 *
 * @author Allen
 *
 */

public enum UnitState {


    CHOSEN(10), //unit is chosen
    MOVING(20), //unit is moving
    ATTACKING(30), //unit is attacking

	//which direction moving or attacking
    STILL(0),
 	MOVE_RIGHT(1),
	MOVE_LEFT(2),
	MOVE_UP(3),
	MOVE_DOWN(4),
	ATTACK_RIGHT(5),
	ATTACK_LEFT(6),
	ATTACK_UP(7),
	ATTACK_DOWN(8),

    //unit is dead
    DEAD(9);


    private int value;

    UnitState(int value) {
        setValue(value);
    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }


 }
