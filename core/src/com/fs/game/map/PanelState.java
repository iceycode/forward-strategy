package com.fs.game.map;

/** Panel State is state of Panel/Grid/Tile actors
 *  For Panel texture states. Similar to AnimState for Unit.
 *
 *  NOTE: In TiledMap layers 1-7 contain Tiles with property name "PanelState" and value
 *   related to enum values.
 *
 * Created by Allen on 5/24/15.
 */
public enum PanelState {
//    VIEW(5), //<--May or may not use this in future
    MOVEABLE(-1), //is negative since is animation

    //all int value represent index
    NONE(0),
    SELECTED(1),

    //tells whether ally or enemy is on top of Panel
    ALLY(2),
    ENEMY(3),

    ATTACK(4);


    private int index;

    PanelState(int val){
        this.index = val;
    }

    public int getIndex(){
        return index;
    }


}
