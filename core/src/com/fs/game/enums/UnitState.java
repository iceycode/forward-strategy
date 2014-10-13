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
  	
	//unit movement/attack state info
	
//	
//	STILL_RIGHT(0,0),
//	STILL_LEFT(0,0),
//	
// 	MOVE_RIGHT(1,0),
//	MOVE_LEFT(-1,0),
//	MOVE_UP(0,1),
//	MOVE_DOWN(0,-1),
//	
//	ATTACK_RIGHT(1,0),
//	ATTACK_LEFT(-1,0),
//	ATTACK_UP(0,1),
//	ATTACK_DOWN(0,-1);
//	
//	private int x;
//	private int y;
//	
//	private UnitState(int x, int y){
//		
//		//finds x direction
//		if (x < 0)
//			this.x = -1;
//		else if (x > 0)
//			this.x = 1;
//		else
//			this.x = 0;
//		
//		//finds y direction
//		if (y < 0)
//			this.y = -1;
//		else if (y > 0)
//			this.y = 1;
//		else 
//			this.y = 0;
//		 
//	}
//	
//	public Vector2 getDirection(){
//		return new Vector2(x,y);
//	}
	
 }
