package com.fs.game.ai.pf;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/** Subclass of {@link com.badlogic.gdx.ai.pfa.Connection} in gdx-ai tools.
 *  Connects between two nodes
 *
 * Created by Allen on 5/7/15.
 */
public class PanelConnection extends DefaultConnection<PanelNode>{

    public static final float OBSTACLE_COST = 3f; //obstacle fromNode
    public static final float OCCUPIED_COST = 2f; //occupied toNode cost

    PanelGraph gameMap;


    public PanelConnection(PanelNode fromNode, PanelNode toNode, PanelGraph gameMap) {
        super(fromNode, toNode);
        this.gameMap = gameMap;
    }


    @Override
    public float getCost() {
        if (toNode.isNodeOccupied())
            return OCCUPIED_COST;

        return gameMap.isNodeStart(getToNode()) && getToNode().isNodePassable(gameMap.getCurrPos().type) ? OBSTACLE_COST : 1;
    }

}
