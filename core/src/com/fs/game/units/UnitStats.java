package com.fs.game.units;

/** Unit stats: includes health, damages and distances
 *
 * @author Allen
 * Created by Allen on 6/4/15.
 */
public class UnitStats {

    private static UnitStats instance;




    public static UnitStats getInstance() {
        if (instance == null)
            instance = new UnitStats();

        return instance;
    }




}
