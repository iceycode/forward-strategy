package appwarp;

/** skeleton code obtained from https://github.com/SauravGShephertz/libgdxMultiplayerSuperJumper
 *
 */

import com.fs.game.assets.Constants;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

import java.util.HashMap;

//from SJDTutorial; organization: mytutorials; appwarp realtime multiplayer
public class WarpController {

	private static WarpController instance;
//    public static WarpController instance;
	
	private boolean showLog = true;


    private final String apiKey = Constants.App42.API_KEY;
    private final String secretKey = Constants.App42.SECRET_KEY;
    private final String roomA_ID = Constants.App42.ROOM_A;

	private WarpClient warpClient;
	
	private String localUser;

    //Id for RoomA (Owner: admin) = 223097003 (max players=3)
    private String roomId;
	
	private boolean isConnected = false;
	boolean isUDPEnabled = false;
	
	private WarpListener warpListener ;
    private WarpListener turnListener;
	
	private int STATE;

	// Game state constants
	public static final int WAITING = 1;
	public static final int STARTED = 2;
	public static final int COMPLETED = 3;
	public static final int FINISHED = 4;
	
	// Game completed constants
	public static final int GAME_WIN = 5;
	public static final int GAME_LOST = 6;
	public static final int ENEMY_LEFT = 7;
	
	public WarpController() {
		initAppwarp();
		warpClient.addConnectionRequestListener(new ConnectionListener(this));
		warpClient.addChatRequestListener(new ChatListener(this));
		warpClient.addZoneRequestListener(new ZoneListener(this));
		warpClient.addRoomRequestListener(new RoomListener(this));
		warpClient.addNotificationListener(new NotificationListener(this));

//
//        warpClient.addTurnBasedRoomListener(new TurnRoomListener(this));

	}
	
	public static WarpController getInstance(){
		if(instance == null){
			instance = new WarpController();
		}
		return instance;
	}
	
	public void startApp(String localUser){
		this.localUser = localUser;
		warpClient.connectWithUserName(localUser);

	}
	
	public void setListener(WarpListener listener){
		this.warpListener = listener;
	}


	public void stopApp(){
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
		}
		warpClient.disconnect();
	}
	
	private void initAppwarp(){
		try {
			WarpClient.initialize(apiKey, secretKey);
			warpClient = WarpClient.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public void sendGameUpdate(String msg){
		if(isConnected){
			if(isUDPEnabled){
				warpClient.sendUDPUpdatePeers((localUser+"#@"+msg).getBytes());
			}else{
				warpClient.sendUpdatePeers((localUser+"#@"+msg).getBytes());
			}
		}
	}
	
	public void updateResult(int code, String msg){
		if(isConnected){
			STATE = COMPLETED;
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put("result", code);
			warpClient.lockProperties(properties);
		}
	}
	
	public void onConnectDone(boolean status){

		log("onConnectDone: "+status );
		if(status){
			warpClient.initUDP();
			warpClient.joinRoomInRange(1, 1, false);
		}else{
			isConnected = false;
			handleError();
		}
	}
	
	public void onDisconnectDone(boolean status){
		
	}
	
	public void onRoomCreated(String roomId){
		if(roomId!=null){
			warpClient.joinRoom(roomId);
		}else{
			handleError();
		}
	}

    /** creates a turn based room
     *
     * @param event
     */
	public void onJoinRoomDone(RoomEvent event){
		log("onJoinRoomDone: "+event.getResult());
		if(event.getResult()==WarpResponseResultCode.SUCCESS){// success case
			this.roomId = event.getData().getId();
			warpClient.subscribeRoom(roomId);
		}else if(event.getResult()==WarpResponseResultCode.RESOURCE_NOT_FOUND){// no such room found
			HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("result", "");
			warpClient.createRoom("ForwardStrategy Room", "ADMIN", 2, data);
		}else{
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onRoomSubscribed(String roomId){
		log("onSubscribeRoomDone: "+roomId);
		if(roomId!=null){
			isConnected = true;
			warpClient.getLiveRoomInfo(roomId);
		}else{
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onGetLiveRoomInfo(String[] liveUsers){

		log("onGetLiveRoomInfo: ");
		if(liveUsers!=null){
			if(liveUsers.length==2){
				startGame();
			}else{
				waitForOtherUser();
			}
		}else{
			warpClient.disconnect();
			handleError();
		}
	}
	
	public void onUserJoinedRoom(String roomId, String userName){
		/*
		 * if room id is same and username is different then start the game
		 */
		if(localUser.equals(userName)==false){
			startGame();
		}
	}

	public void onSendChatDone(boolean status){
		log("onSendChatDone: "+status);
	}


	int counter = 0;
	public void onGameUpdateReceived(String message){
        log("onMoveUpdateReceived: " + message );

		String userName = message.substring(0, message.indexOf("#@"));
		String data = message.substring(message.indexOf("#@")+2, message.length());
		if(!localUser.equals(userName)){
			warpListener.onGameUpdateReceived(data);
		}
	}
	
	public void onResultUpdateReceived(String userName, int code){
		if(localUser.equals(userName)==false){
			STATE = FINISHED;
			warpListener.onGameFinished(code, true);
		}else{
			warpListener.onGameFinished(code, false);
		}
	}
	
	public void onUserLeftRoom(String roomId, String userName){
		log("onUserLeftRoom "+userName+" in room "+roomId);
		if(STATE==STARTED && !localUser.equals(userName)){// Game Started and other user left the room
			warpListener.onGameFinished(ENEMY_LEFT, true);
		}
	}
	
	public int getState(){
		return this.STATE;
	}
	
	private void log(String message){
		if(showLog){
			System.out.println(message);
		}
	}
	
	private void startGame(){
		STATE = STARTED;
		warpListener.onGameStarted("Start the Game");
	}
	
	private void waitForOtherUser(){
		STATE = WAITING;
		warpListener.onWaitingStarted("Waiting for other user");
	}
	
	private void handleError(){
		if(roomId!=null && roomId.length()>0){
			warpClient.deleteRoom(roomId);
		}
		disconnect();
	}
	
	public void handleLeave(){
		if(isConnected){
			warpClient.unsubscribeRoom(roomId);
			warpClient.leaveRoom(roomId);
			if(STATE!=STARTED){
				warpClient.deleteRoom(roomId);
			}
			warpClient.disconnect();
		}
	}
	
	private void disconnect(){
		warpClient.removeConnectionRequestListener(new ConnectionListener(this));
		warpClient.removeChatRequestListener(new ChatListener(this));
		warpClient.removeZoneRequestListener(new ZoneListener(this));
		warpClient.removeRoomRequestListener(new RoomListener(this));
		warpClient.removeNotificationListener(new NotificationListener(this));
		warpClient.disconnect();
	}
}
