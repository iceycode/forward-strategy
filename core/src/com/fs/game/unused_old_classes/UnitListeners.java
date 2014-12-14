package com.fs.game.unused_old_classes;

/**
 * @deprecated
 */
public class UnitListeners {
	
	//final static private String LOG = Constants.LOG_MAIN;
	
	
	


//	/** another unit actor gesture listener
//	 *
//	 */
//   	public static ActorGestureListener unitListener1 = new ActorGestureListener() {
//   			@Override
//			public void touchDown(InputEvent event, float x, float y, int pointer, int button){
//   				Unit currUnit = ((Unit)event.getTarget());
//   				currUnit.clickCount++;
//
//   				//some info about listeners
//   				if (currUnit.clickCount == 1){
//   					currUnit.chosen = true;
//   					Gdx.app.log(Constants.LOG_MAIN, Constants.UNIT_CHOSEN);
//
//   				}
//   				else {
//   					currUnit.chosen = false;
//   					Gdx.app.log(Constants.LOG_MAIN, Constants.UNIT_DESELECT);
//   					currUnit.clickCount = 0; //reset clickCount
//   				}
//   			}
//
//   			@Override
//   			public boolean handle(Event event) {
//   				Unit uni = ((Unit)event.getTarget());
//
//
//   				if (!uni.chosen){
//   					hideMoves();
//   				}
//
//   				if (uni.getX() != uni.getOriginX() || uni.getY() != uni.getOriginY()) {
//   					Gdx.app.log("UNIT log: ", " current pos : (" + uni.getX() + ", " + uni.getY() + ")");
//   	 				uni.updateRectangle(); //updates unit position on board & stage
//
//   	 				for (Unit u: uni.findOtherUnits){
//   	 					u.updateUnitDataArrays( uni.getStage().getActors());
//   	 				}
//   					uni.clickCount = 0; //reset clickCount
//   					uni.chosen = false; //set unit as not chosen
//   					uni.hideMoves();
//   	 			}
//
//   				return true;
//   			}
//	};



// NOT currently used - may come in handy later
//    /*******CHANGELISTENER********
//     * listens to changes in unit
//     * changes values based on movement
//     */
//    public static final EventListener unitChangeListener = new EventListener() {
//        @Override
//        public boolean handle(Event event) {
//            Unit uni = ((Unit)event.getTarget());
//
//
//            if (!uni.chosen){
//                uni.hideMoves();
//            }
//
//            if (uni.getX() != uni.getOriginX() || uni.getY() != uni.getOriginY()) {
//                Gdx.app.log("UNIT log: ", " current pos : (" + uni.getX() + ", " + uni.getY() + ")");
//                uni.updateRectangle(); //updates unit position on board & stage
//
//                for (Unit u: uni.findOtherUnits){
//                    u.updateUnitDataArrays( uni.getStage().getActors());
//                }
//                uni.clickCount = 0; //reset clickCount
//                uni.chosen = false; //set unit as not chosen
//                uni.hideMoves();
//            }
//
//            return true;
//        }
//    };


//
//
//	/** -----InputListener for unit
//	 *
//	 * - not currently used
//	 *
//	 *  InputListener for unit
//	 */
//	public static final InputListener unitInputListener = new InputListener(){
//				@Override
//			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//				Unit uni = (Unit)event.getTarget(); //sets this as Unit which recieved this event
//
//				//UnitUtils.checkBoard(uni); //checks board to see whether other units selected, if so, resets them
//
//				uni.clickCount++; //for tracking user interaction
//				System.out.println("selected unit");
//				uni.chosen = true; //sets unit as not chosen, seen in act method as false
//
//				return true;
//			}//
//
//			@Override
//			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//				Unit uni = (Unit)event.getTarget(); //sets this as Unit which recieved this event
//
//				if (uni.clickCount == 2) {
//					System.out.println("deselected unit");
//					uni.chosen = false; //sets unit as not chosen, seen in act method as false
//					uni.clickCount = 0; //reset clickCount
//				}
//			}
//
//
//
//	};
}
