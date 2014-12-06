package com.fs.game.enums;

/** enums for unit states
 * - integer value associated with JSON data for multiplayer
 *
 * @author Allen
 *
 */

public enum UnitState {

	//which direction unit is facing
	STILL(0),
	STILL_LEFT(0),
	STILL_RIGHT(0),

    //unit is chosen
    CHOSEN(10),
    MOVING(20),
    ATTACKING(30),

	//which direction moving
 	MOVE_RIGHT(1),
	MOVE_LEFT(2),
	MOVE_UP(3),
	MOVE_DOWN(4),

	//direction attacking
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
