package appwarp;

import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.TurnBasedRoomListener;

/** An implementation of TurnBasedRoomListener class
 *
 * Created by Allen on 12/3/14.
 */
public class TurnRoomListener implements TurnBasedRoomListener{

    WarpController callBack;

    public TurnRoomListener(WarpController callBack) {
        this.callBack = callBack;
    }


    @Override
    public void onSendMoveDone(byte b) {

    }

    @Override
    public void onStartGameDone(byte b) {

    }

    @Override
    public void onStopGameDone(byte b) {

    }

    @Override
    public void onGetMoveHistoryDone(byte b, MoveEvent[] moveEvents) {

    }
}
