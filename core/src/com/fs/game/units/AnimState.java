package com.fs.game.units;

/** enums for unit animation states
 * - integer value associated with JSON data for multiplayer
 *
 * @author Allen
 *
 */

public enum AnimState {


    CHOSEN(10), //unit is chosen
    MOVING(20), //unit is moving
    ATTACKING(30), //unit is attacking
    UNDER_ATTACK(40), //unit is being attacked

	//which direction moving or attacking
    STILL(0),
 	MOVE_RIGHT(1),
	MOVE_LEFT(2),
	MOVE_UP(3),
	MOVE_DOWN(4),

	ATTACK_RIGHT(5){
        public void adjustFrame(int offset){
            int indexNew = this.getValue() + offset;
            this.setValue(indexNew);
        }
    },
	ATTACK_LEFT(6),
	ATTACK_UP(7),
	ATTACK_DOWN(8),

    //unit is dead
    DEAD(9);

    private int fc;
    private int value;

    AnimState(int value) {
        setValue(value);
    }

    /** Sets number of animation frames for each Unit. Some Units have less frames
     *  since they only move up and down or left & right
     *
     * @param numFrames
     */
    public void setAnimFrameCount(int numFrames){

    }

    public int getValue(){
        return value;
    }

    public void setValue(int value){
        this.value = value;
    }


 }
