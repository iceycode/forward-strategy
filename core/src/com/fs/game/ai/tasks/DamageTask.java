package com.fs.game.ai.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fs.game.ai.fsm.UnitAgent;

/** Checks damage unit does to opponent units and
 *   damage opponents do to unit
 *
 * Created by Allen on 5/11/15.
 */
public class DamageTask extends LeafTask<UnitAgent>{

    @Override
    public void run(UnitAgent unitAgent) {

    }

    @Override
    protected Task<UnitAgent> copyTo(Task<UnitAgent> task) {
        return null;
    }
}
