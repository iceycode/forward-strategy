package com.fs.game.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.fs.game.constants.Constants;
import com.fs.game.actors.Unit;
import com.fs.game.stages.GameStage;

import java.util.Random;

/** Player Utils
 * - helps set/manage information about player before game & during game
 * - contains methods for identifying players online as well
 *
 *
 * Created by Allen on 5/7/15.
 */
public class PlayerUtils {

    //sets random player bw 1 & 2
    public static int randPlayer() {
        Random rand = new Random();
        return rand.nextInt(2) + 1;
    }

    /**
     * returns a random Faction
     */
    public static String randomFaction() {
        Random rand = new Random();
        int factInt = rand.nextInt(3); //0-1 (only 1 working factions ATM)
        if (factInt == 0)
            return Constants.HUMAN;

        return Constants.ARTHROID;
    }


    public static int nextPlayer(int player, Button p1Button, Button p2Button, GameStage stageMap) {
        if (player == 1) {

            if (!p1Button.isChecked())
                p1Button.toggle(); //toggle

            GameMapUtils.lockPlayerUnits(player, stageMap);  //lock these player units
            player = 2; //next player
            GameMapUtils.unlockPlayerUnits(player, stageMap);    //unlock player units
            p2Button.toggle();    //toggle checked animState p2
        } //player 2 goes
        else if (player == 2) {

            if (!p2Button.isChecked())
                p2Button.toggle(); //if it is not checked

            GameMapUtils.lockPlayerUnits(player, stageMap);
            player = 1; //next player
            GameMapUtils.unlockPlayerUnits(player, stageMap);    //unlock player units
            p1Button.toggle();
        } //player 1 goes


        return player;
    }


    /**
     * updates current game score
     *
     * @param currScore
     * @param unit
     * @return
     */
    public static int updateScore(int currScore, Unit unit) {

        if (unit.unitInfo.getSize().equals("32x32")) {
            currScore += 10;
        } else if (unit.unitInfo.getSize().equals("64x32")) {
            currScore += 20;
        } else
            currScore += 30;


        return currScore;
    }

    /**
     * for setting up which side player is on
     * max value needs to be high enough so that there
     * is virtually no chance 2 random numbers in range
     * of 1000 multiplied by each other will
     * be the same (not even worth calculating probability)
     *
     * @return the int random number
     */
    public static int randomLengthPlayerID() {
        Random rand = new Random();
        int id = rand.nextInt(1000) * rand.nextInt(1000);

        return id;
    }

    public static String setupUsername() {
        Random rand = new Random();
        int idLength = rand.nextInt(6) + 1;

        String uniqueID = getRandomHexString(idLength);

        return "tester" + uniqueID;
    }

    private static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numchars);
    }
}
