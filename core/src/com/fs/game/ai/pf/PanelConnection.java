package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/** Subclass of {@link com.badlogic.gdx.ai.pfa.Connection} in gdx-ai tools.
 *  Connects between two nodes
 *
 * Created by Allen on 5/7/15.
 */
public class PanelConnection extends DefaultConnection<PanelNode>{

    //obstacle and occupied costs are high so that there is no chance of using them
    public static final float REGULAR_COST = (float)Math.sqrt(2); //as if no obstacle was there

    PanelGraph gameMap;


    public PanelConnection(PanelNode fromNode, PanelNode toNode, PanelGraph gameMap) {
        super(fromNode, toNode);
        this.gameMap = gameMap;
    }

    @Override
    public float getCost() {
//        return gameMap.startNode.x == getToNode().x && gameMap.startNode.y == getToNode().y ? 1
//                :getToNode().getTypeCost() ;

        if (gameMap.isNodeUnitPosition(getToNode()))
            return 0;

        return Math.abs(gameMap.data.type - getToNode().getTypeCost());
    }

}
