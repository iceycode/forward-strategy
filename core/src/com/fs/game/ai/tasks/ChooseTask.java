package com.fs.game.ai.tasks;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fs.game.ai.AgentManager;

/** Chooses which Unit to move
 *
 * Created by Allen on 5/11/15.
 */
public class ChooseTask extends LeafTask<AgentManager>{


    @Override
    public void start(AgentManager uc) {
        super.start(uc);


    }

    @Override
    public void run(AgentManager uc) {

    }

    @Override
    protected Task<AgentManager> copyTo(Task<AgentManager> task) {
        return null;
    }
}
