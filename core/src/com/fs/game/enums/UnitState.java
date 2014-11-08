package com.fs.game.enums;

import com.badlogic.gdx.math.Vector2;

/** enums for unit states
 * 
 * @author Allen
 *
 */

public enum UnitState {
 
	//which direction unit is facing
	STILL,
	STILL_LEFT,
	STILL_RIGHT, 
 
	//which direction moving 
 	MOVE_RIGHT,
	MOVE_LEFT,
	MOVE_UP,
	MOVE_DOWN,
	
	//direction attacking
	ATTACK_RIGHT,
	ATTACK_LEFT,
	ATTACK_UP,
	ATTACK_DOWN,

    //unit is dead
    DEAD,
	
 }
