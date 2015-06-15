package com.fs.game.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class RoomListener implements RoomRequestListener{

	
	private WarpController callBack;
	
	public RoomListener(WarpController callBack) {
		this.callBack = callBack;
	}
	
	@Override
	public void onGetLiveRoomInfoDone(LiveRoomInfoEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			callBack.onGetLiveRoomInfo(event.getJoinedUsers());
		}else{
			callBack.onGetLiveRoomInfo(null);
		}
	}

	@Override
	public void onJoinRoomDone(RoomEvent event) {
		callBack.onJoinRoomDone(event);
	}

	@Override
	public void onLeaveRoomDone(RoomEvent arg0) {
		
	}

	@Override
	public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
		
	}

	@Override
	public void onSubscribeRoomDone(RoomEvent event) {
		if(event.getResult()==WarpResponseResultCode.SUCCESS){
			callBack.onRoomSubscribed(event.getData().getId());
		}else{
			callBack.onRoomSubscribed(null);
		}
	}

	@Override
	public void onUnSubscribeRoomDone(RoomEvent arg0) {
		
	}

	@Override
	public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
		
	}

	@Override
	public void onLockPropertiesDone (byte result) {
		
	}

	@Override
	public void onUnlockPropertiesDone (byte arg0) {
		
	}

}
