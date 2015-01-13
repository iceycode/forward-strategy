package com.fs.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.fs.game.actors.Unit;
import com.fs.game.assets.Assets;
import com.fs.game.actors.Panel;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.UnitUtils;

import java.util.Random;

/** Game Artificial Intelligence module
 * Notes on difficulty:
 * - on 0, AI does not even look at players moves, moves randomly
 * - at 1, AI does look at player moves, aiming to kill player's units, disregarding damage that can be done
 * - at 2, AI actually analyzes best way to both attack & defend own units (takes into account damage)
 *
 * Created by Allen on 12/22/14.
 */
public class GameAI {

    Random rand;

    Array<Panel> panelArray;
    Array<Unit> units;
    Array.ArrayIterator<Unit> iterator;

    Unit chosenUnit;
    Moves moves;

    GameStage stage; //the stage

    //0 = easy, 1 = medium, 2 = hard
    // with easy, a random move is ma
    int difficulty = 0; //set to easy, TODO: figure out how to make difficulty harder

    public GameAI(GameStage stage, int difficulty){
        this.difficulty = difficulty;
        this.moves = new Moves();
        this.stage = stage;
    }

    public void update(float delta){
        iterator = stage.getUnitIter();

        while (iterator.hasNext()){
            Unit u = iterator.next();

            if (!u.done){
                chosenUnit = chooseRandomUnit();
                moves.moveUnit(chosenUnit.panelArray);
            }
        }

    }


    public Unit chooseRandomUnit(){
        int randUnitIndex = rand.nextInt(units.size);
        Unit chosenUnit = units.get(randUnitIndex);
        chosenUnit.chosen = true;

        return chosenUnit;
    }


    /** For AI movement & learning from movements
     * - stores information about previous moves, next ones
     *
     */

    public class Moves{
        OrderedMap<Unit, Vector2> aiPositions; //map containing moves made by ai units
        OrderedMap<Unit, Vector2> playerPositions; //moves made by player

        public Moves(){
            aiPositions = new OrderedMap<Unit, Vector2>(7);
            playerPositions = new OrderedMap<Unit, Vector2>(7);
        }


        public void moveUnit(Array<Panel> panels){
            Panel target = nextMove(panels);
            target.selected = true;
        }

        /** moves unit
         *  movement based on difficulty as well
         *  - if difficulty set to 0, then moves to first found unit
         *  - if set at 1, then moves to unit that it will do most damage to
         *
         * @param panels
         * @return
         */
        public Panel nextMove(Array<Panel> panels){
            int randPanelIndex = rand.nextInt(panels.size);
            Panel target = panels.get(randPanelIndex);

            int damage = 1; //damage unit can do if in range

            Array.ArrayIterator<Panel> panelIterator = new Array.ArrayIterator<Panel>(panels);
            while (panelIterator.hasNext()){
                Panel panel = panelIterator.next();

                Array.ArrayIterator<Unit> unitIterator = stage.getUnitIter();

                while (unitIterator.hasNext()){
                    Unit u = unitIterator.next();
                    if (UnitUtils.Attack.isEnemy(u) && UnitUtils.Attack.panelAdjacent(panel, u)){

                        if (difficulty == 0) {
                            target = panel;
                        }
                        else if (difficulty == 1){
                            if (checkDamageTo(damage, u)){
                                target = panel;
                            }
                        }
                    }
                }
            }

            return target;
        }



        private boolean checkDamageTo(int currDamage, Unit unit){
            int unitDamage = Assets.damageListArray.get(chosenUnit.getUnitID()-1)[unit.getUnitID()-1];

            if (unitDamage < currDamage){
                return true;
            }
            return false;
        }

        private void checkDamageFrom(){

        }

    }

}
