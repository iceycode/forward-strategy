package com.fs.game.units.interfaces;

import com.fs.game.units.Unit;
import com.fs.game.map.Panel;

/** Unit Action Interface
 *  To be used with UnitState for Unit act
 *
 * Created by Allen on 5/23/15.
 */
public interface UnitAction {

    //moves unit to panel
    public void moveUnit(Panel panel);


    /** Selects a Unit
     *
     * @param unit
     */
    public void selectUnit(Unit unit);


    /** Sends message to another Unit
     * Mainly used for attack
     *
     * @param sender : sender of message (eg attacker)
     * @param reciever : reciever of message (eg target Unit)
     */
    public void onAttack(Unit sender, Unit reciever);

}
