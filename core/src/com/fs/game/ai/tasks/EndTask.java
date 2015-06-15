package com.fs.game.ai.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fs.game.units.UnitController;

/** StayTask - Unit stays in the same location
 *  This is for when a Unit is attacking another Unit and does not want to move.
 *
 *
 * Created by Allen on 5/11/15.
 */
public class EndTask extends LeafTask<UnitController> {


    public EndTask(){
        
    }

    @Override
    public void run(UnitController object) {

    }

    @Override
    protected Task<UnitController> copyTo(Task<UnitController> task) {
        return null;
    }
}
