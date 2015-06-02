package com.fs.game.ai;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.fs.game.ai.fsm.UnitAgent;

/** Manages BehaviorTree
 *
 * Created by Allen on 5/11/15.
 */
public class TreeManager {

    private static TreeManager instance;

    //BehaviorTree which influences UnitAgent decisions
    BehaviorTree<UnitAgent> tree;


    public TreeManager(){
//        tree = new BehaviorTree<>();


    }

    public static TreeManager getInstance(){
        if (instance==null)
            instance = new TreeManager();

        return instance;
    }

}
