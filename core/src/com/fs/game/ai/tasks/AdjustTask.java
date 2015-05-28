package com.fs.game.ai.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fs.game.actors.Unit;

/** Adjusts how UnitAgents act based on tree task outcomes
 *  Issued after selecting running Damage & Position Tasks in parallel
 *
 *
 * Created by Allen on 5/11/15.
 */
public class AdjustTask extends LeafTask<Unit> {


    public AdjustTask(){
        
    }

    @Override
    public void run(Unit object) {

    }

    @Override
    protected Task<Unit> copyTo(Task<Unit> task) {
        return null;
    }
}
