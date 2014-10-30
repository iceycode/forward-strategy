package com.fs.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.fs.game.enums.UnitState;
import com.fs.game.utils.Constants;
import com.fs.game.utils.MapUtils;
import com.fs.game.utils.UnitUtils;

public class UnitListeners {
	
	final static private String LOG = Constants.LOG_MAIN;
	
	
	
	/* some listeners which are or are not being used
     * - currently (07/29) using only unitListener
     * 
     */
   	public static ActorGestureListener actorGestureListener = new ActorGestureListener() {
		@Override
		public void touchDown(InputEvent event, float x, float y, int pointer, int button){
			Unit currUnit = ((Unit)event.getTarget());
			currUnit.clickCount++;
			
			Gdx.app.log(LOG, " unit rectangle position is: " + 
						"(" + currUnit.getUnitBox().x + ", " + currUnit.getUnitBox().y + ")");
 

			//some info about listeners
			if (currUnit.clickCount < 2){
				if (currUnit.otherUnits!=null)
					UnitUtils.deselectUnits(currUnit.otherUnits);

				currUnit.chosen = true;
				Gdx.app.log(LOG, Constants.UNIT_CHOSE);
 			}
 		}
		
		@Override
		public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
			Unit currUnit = ((Unit)event.getTarget());

			if (currUnit.clickCount == 2 && !currUnit.lock) {
 				currUnit.chosen = false;
				currUnit.hideMoves();
				Gdx.app.log(LOG, Constants.UNIT_DESELECT);
				currUnit.clickCount = 0; //reset clickCount
			}
			
		}
		
		
 
		
	};
	
		
	/*******CHANGELISTENER********
	 * listens to changes in unit
	 * changes values based on movement
	 */
	public static final EventListener unitChangeListener = new EventListener() {
		@Override
		public boolean handle(Event event) {
			Unit uni = ((Unit)event.getTarget());	
			
			
			if (!uni.chosen){
				uni.hideMoves();
			}
			
			if (uni.getX() != uni.getOriginX() || uni.getY() != uni.getOriginY()) {
				Gdx.app.log("UNIT log: ", " current pos : (" + uni.getX() + ", " + uni.getY() + ")");
 				uni.updatePosition(); //updates unit position on board & stage
				
 				for (Unit u: uni.otherUnits){
 					u.updateUnitDataArrays( uni.getStage().getActors());
 				}
				uni.clickCount = 0; //reset clickCount 
				uni.chosen = false; //set unit as not chosen
				uni.hideMoves();
 			}
			
			return true;
		}
	};

	/** another unit actor gesture listener
	 * 
	 */
   	public static ActorGestureListener unitListener1 = new ActorGestureListener() {
   			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, int button){
   				Unit currUnit = ((Unit)event.getTarget());
   				currUnit.clickCount++;
 
   				//some info about listeners
   				if (currUnit.clickCount == 1){
   					currUnit.chosen = true;
   					Gdx.app.log(Constants.LOG_MAIN, Constants.UNIT_CHOSE);
   					
   				}
   				else {
   					currUnit.chosen = false;
   					Gdx.app.log(Constants.LOG_MAIN, Constants.UNIT_DESELECT);
   					currUnit.clickCount = 0; //reset clickCount
   				}
   			}
   			
   			@Override
   			public boolean handle(Event event) {
   				Unit uni = ((Unit)event.getTarget());	
   				
   				
   				if (!uni.chosen){
   					uni.hideMoves();
   				}
   				
   				if (uni.getX() != uni.getOriginX() || uni.getY() != uni.getOriginY()) {
   					Gdx.app.log("UNIT log: ", " current pos : (" + uni.getX() + ", " + uni.getY() + ")");
   	 				uni.updatePosition(); //updates unit position on board & stage
   					
   	 				for (Unit u: uni.otherUnits){
   	 					u.updateUnitDataArrays( uni.getStage().getActors());
   	 				}
   					uni.clickCount = 0; //reset clickCount 
   					uni.chosen = false; //set unit as not chosen
   					uni.hideMoves();
   	 			}
   				
   				return true;
   			}
	};
	

	
	
	/** -----InputListener for unit
	 * 
	 * - not currently used
	 * 
	 *  InputListener for unit
	 */
	public static final InputListener unitInputListener = new InputListener(){
				@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Unit uni = (Unit)event.getTarget(); //sets this as Unit which recieved this event
				
				//UnitUtils.checkBoard(uni); //checks board to see whether other units selected, if so, resets them
				
				uni.clickCount++; //for tracking user interaction
 				System.out.println("selected unit");
				uni.chosen = true; //sets unit as not chosen, seen in act method as false

				return true;
			}// 
	
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Unit uni = (Unit)event.getTarget(); //sets this as Unit which recieved this event
	
				if (uni.clickCount == 2) {
					System.out.println("deselected unit");
					uni.chosen = false; //sets unit as not chosen, seen in act method as false
					uni.clickCount = 0; //reset clickCount
				}
			}
			 
	};
}
