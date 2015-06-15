package com.fs.game.ai.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Metadata;
import com.badlogic.gdx.ai.btree.Task;
import com.fs.game.ai.fsm.UnitAgent;

/** Checks Unit positions of opponent (player) and damages to/from current unit, as well as health
 *  Uses heuristics to make decision to move to panel.
 *
 *
 * Created by Allen on 5/11/15
 */
public class PositionTask extends LeafTask<UnitAgent> {

    public static final Metadata METADATA = new Metadata("offsetArray");

    @Override
    public void run(UnitAgent object) {

    }

    @Override
    protected Task<UnitAgent> copyTo(Task<UnitAgent> task) {
        return null;
    }
}
