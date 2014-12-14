package appwarp;

import com.shephertz.app42.gaming.multiplayer.client.events.*;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;

import java.util.HashMap;

public class NotificationListener implements NotifyListener{

	
	private WarpController callBack;
	
	public NotificationListener(WarpController callBack) {
		this.callBack = callBack;
	}
	
	@Override
	public void onChatReceived(ChatEvent event) {
		
	}

	@Override
	public void onRoomCreated(RoomData arg0) {
		
	}

	@Override
	public void onRoomDestroyed(RoomData arg0) {
		
	}

	@Override
	public void onUpdatePeersReceived(UpdateEvent event) {
		callBack.onGameUpdateReceived(new String(event.getUpdate()));
	}

	@Override
	public void onUserJoinedLobby(LobbyData arg0, String arg1) {
		
	}

	@Override
	public void onUserJoinedRoom(RoomData data, String username) {
		callBack.onUserJoinedRoom(data.getId(), username);
	}

	@Override
	public void onUserLeftLobby(LobbyData arg0, String arg1) {
		
	}

	@Override
	public void onUserLeftRoom(RoomData roomData, String userName) {
		callBack.onUserLeftRoom(roomData.getId(), userName);
	}

	@Override
	public void onGameStarted (String arg0, String arg1, String arg2) {
		
	}
	
	@Override
	public void onGameStopped (String arg0, String arg1) {
		
	}

	@Override
	public void onMoveCompleted (MoveEvent me) {
		
	}

	@Override
	public void onPrivateChatReceived (String arg0, String arg1) {
		
	}

    @Override
    public void onPrivateUpdateReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
	public void onUserChangeRoomProperty (RoomData roomData, String userName, HashMap<String, Object> properties, HashMap<String, String> lockProperties) {
		int code = Integer.parseInt(properties.get("result").toString());
		callBack.onResultUpdateReceived(userName, code);
	}

	@Override
	public void onUserPaused (String arg0, boolean arg1, String arg2) {
		
	}

	@Override
	public void onUserResumed (String arg0, boolean arg1, String arg2) {
		
	}

    //@Override
    private void onNextTurnRequest(String s) {

    }

}
