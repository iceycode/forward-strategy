package com.fs.game.units;

 
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
////NOTE: USE NON- STATIC IMPORTS FOR ACTIONS
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.Panel;
import com.fs.game.stages.MapStage;
import com.fs.game.unused_old_classes.TextureUtils;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;
import com.fs.game.utils.MapUtils;
import com.fs.game.utils.UnitUtils;
import com.fs.game.utils.pathfinder.PathGenerator;

public class Unit extends Actor {

	final String LOG = "UNIT ACTOR LOG: "; //for log message
	float screenWidth = 800; //the screen width 
	float screenHeight = 500;
	float gridSide = 384; //how big the grid is if 32x32 images
	float gridOriX = Constants.GRID_X, gridOriY = Constants.GRID_Y;
	//for unit movements, destination & origin of actor
 	float destX, destY, oriX, oriY; //position unit will go on board next ON SCREEN
 	public int gridPosX = 0;//origin of actor on the stage matrix array
	public int gridPosY = 0;
	private Vector2 gridLocation; //where the unit is on the grid (or tile in map)
	public MapStage unitStage;

	public Rectangle unitBox;
	public Array<Unit> otherUnits; //an array storing info about other units on board
	public Array<Unit> enemyUnits; //an array that stores enemy unit info
	
	//arrays for panels & other units on stage	
	public Array<Panel> panelArray; //created from panel position
	public Array<Vector2> panelPath; //the actual path unit takes when moving 
  
	/**ANIMATION OBJECTS**/
	Array<Texture> animationTextures; //temporarily stores the   textures
	
	
	Animation moveAnimation; //a generic move Animation object
	Animation stillAnim;
 
	//move animations
	Animation moveLeftAnim;	
	Animation moveRightAnim;
	Animation moveUpAnim;
	Animation moveDownAnim;
	
	//attack animations
	Animation attackLeftAnim;
	Animation attackRightAnim;
	Animation attackUpAnim;
	Animation attackDownAnim;

	Animation deathAnim;	//death sequence
      
	float timeInterval = 0.1f; //stores delta time for animations
	float aniTime = .1f; //stores data related to animation between panels
	float attackTime = 0f;
	public int actualMoves = 0; //the actual moves unit takes (may not be max necessarily)
	int moveCounter = 0; //counts the number of moves
	
	//unit information
 	public UnitInfo unitInfo; //sets unit's information
 	public int[] damageList;  //lists how much damage done to or recieved by opponent
	protected int unitID;
	public Texture texture; //unit texture
	//store player & faction info
	public int player = 0;
	String faction; //
	public boolean crossWater = false;
	public boolean crossLand = false; 
 	
	public float health = 4; //each unit has 4 health
	public Pixmap pixHealthBar;		//the pixmap which changes based on damage taken
	public Texture healthBar; // the texture which is drawn with the unit
	public float damage = 0; //the damage this unit deals OR takes
	
 	//different states the unit can be in : attack, moving, etc
	public UnitState state;
 	public boolean moving = false;
	public boolean standing = true; //whether the unit is still or not
	public boolean lock = false; //this value is for 
	public boolean chosen = false; //if unit is chosen
 	public boolean attacking = false; //determines if attack animation necessary
	public boolean done = false; //unit has finished moving/attacking
	boolean panelsFound = false;

	public boolean rightSide = false;
	public boolean leftSide = false;
	
	public int attackCount = 0; //number of times units have attacked each other
 	public int clickCount = 0; //if unit selected or not still

 	//related to unit moving on board
 	public SequenceAction moveUnit;	//actions performed by units in sequence
	private float moveTime = 8f; //gets the move duration (shorter the further unit can move)
	private int maxMoves; //maximum move distance of unit actor
	private String unitSize; //size of unit in dimensions (can be 32x32, 64x32 or 64x64)

	//for unit movements
	Panel[][] panelsPos; //matrix for panel position
	//NOTE: trying something new with coordinate system instead of panel array
	double[][] panelCoords = Constants.GRID_SCREEN_VECTORS; //coords of panels
	Panel targetPan; //the target panel unit moves to
	public PathGenerator pathGen; //class which generates the unit's possible move movements
	public Array<Panel> movePath; 
	public SequenceAction moveSequence;

	//place to recycle allocated actions (max of 16) 
	public Pool<SequenceAction> actionPool = new Pool<SequenceAction>(){
		@Override
		protected SequenceAction newObject(){
			return new SequenceAction();
		}
	};
	
	/** default empty constructor
	 *  - nearly empty, except for set lock
	 *  - useful for testing pathfinder & various methods in other classes
	 */
	public Unit() { }//empty default constructor
	
	/*****unit constructor for units menu *****
	 * 
	 * TODO: get units chosen on menu onto board
	 * 
	 */
	public Unit(final Texture texture, UnitInfo unitInfo) {
		setTexture(texture);
		setUnitInfo(unitInfo);
		setLock(true);
		
 	}// texture set to an armoredcroc

	/*****UNIT when on board*****
	 * Note: lock is false when actually on board
	 */
	public Unit(Texture texture, float actorX, float actorY, 
			UnitInfo unitInfo) {
		//set texture, coordinates & other info
   		this.texture = texture; //main unit texture for still animation
   		this.unitInfo = unitInfo;
   		setupInfo();
  		
		this.state = UnitState.STILL;
 		
		//this is for collision detection
 		this.setBounds(actorX, actorY, getWidth(), getHeight());
		this.unitBox = new Rectangle(getX(), getY(), this.getWidth(), this.getHeight());	
		//creates the health bar (changes if damaged)
		this.pixHealthBar = UnitUtils.createPixmap(Constants.HLTH_W, Constants.HLTH_H, Color.YELLOW);
		this.healthBar = new Texture(pixHealthBar);
 
 		this.otherUnits = new Array<Unit>(13); //all other units except this one
		this.enemyUnits = new Array<Unit>(7);	//all enemy units
		
 		//original positions for determining where unit has moved
		this.gridLocation = new Vector2(actorX, actorY);  //TODO: this will be used in pathfinder
 		this.oriX = getX();
		this.oriY = getY();
		this.setOrigin(oriX, oriY);
		this.pathGen = new PathGenerator(this, oriX, oriY);
		
		setupAnimations();
		
		this.addListener(UnitListeners.actorGestureListener);
		//this.addListener(UnitListeners.unitInputListener);
		//this.addListener(UnitListeners.unitChangeListener);
		this.moveSequence = new SequenceAction();
		this.panelArray = new Array<Panel>();
  	}
 
	public void setupInfo(){
		setUnitInfo(unitInfo);
		setFaction(unitInfo.getFaction()); //sets faction
		setUnitID(unitInfo.getId()); //sets unit ID
		setName(unitInfo.getUnit()); //sets unit name
		setDamageList(unitInfo.damageList); //sets the damage list
		setUnitSize(unitInfo.size);
		
		if (this.unitSize.equals("32x32")){
			this.setWidth(32);
			this.setHeight(32);
		}
		else if (this.unitSize.equals("64x32")){
			this.setWidth(64);
			this.setHeight(32);
		}
		else{
			this.setWidth(64);
			this.setHeight(64);
		}
  		setMaxMoves(unitInfo.getMaxMoves());
		
		
		if (this.getUnitInfo().isCrossLandObst() == "Yes"){
			this.crossLand = true;
		}
 
		if (this.getUnitInfo().isCrossWater().equals("Yes")){
			this.crossWater = true;
		}
 
		
	}
 
	/**initially set to .01 (slow) TODO: get all animations
	 * sets up the animations for this unit
	 */
 	public void setupAnimations(){
   		this.stillAnim = UnitUtils.animateUnit(aniTime, texture, this);

		//gets textures from assetmanager
		if (GameManager.am != null){
 			for(String fs : unitInfo.getTexPaths()){
 				
 				Gdx.app.log(LOG, "unit name : " + getName() + " , framesheet is " + fs);
 				
 				Texture tex = GameManager.am.get(fs, Texture.class);
 
 				if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_RIGHT)){
					moveRightAnim = UnitUtils.animateUnit(aniTime, tex, this); 
				}
				else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_LEFT)){
					moveLeftAnim = UnitUtils.animateUnit(aniTime, tex, this); 
				}
				else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_UP)){
					moveUpAnim = UnitUtils.animateUnit(aniTime, tex, this);
				}
				else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_DOWN)){
					moveDownAnim = UnitUtils.animateUnit(aniTime, tex, this);
				}
  			}
			
 			if (this.moveUpAnim==null || this.moveDownAnim == null || this.moveRightAnim == null){
 
  				Texture moveRightFS = new Texture(Gdx.files.internal(this.unitInfo.getUnitPath() + Constants.UNIT_MOVE_LEFT));
				this.moveRightAnim = UnitUtils.animateUnit(aniTime, moveRightFS, this);;
				
				moveUpAnim = moveRightAnim;
				moveDownAnim = moveLeftAnim;
 			}
  		}
	}

	@Override
	public void draw(Batch batch, float alpha) {
		//TODO: normally this would be here, 
		// but now goes after if..else statement
		//since animation frames are not ready
 		super.draw(batch, alpha);  
  		
		switch(state){
			case MOVE_RIGHT :
				batch.draw(moveRightAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case MOVE_LEFT :
				batch.draw(moveLeftAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case MOVE_UP:
				batch.draw(moveUpAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case MOVE_DOWN:
				batch.draw(moveDownAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case STILL:
				//TODO: put the animations in
				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case ATTACK_LEFT:
				
				break;
			case ATTACK_UP:
				
				break;
			case ATTACK_DOWN:
			
				break;
			default:
				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
		}
 
  	 
 		
		//temporary draw setup for healthbar 
		float healthBarWidth = healthBar.getWidth() * (health/4f); 
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

 	}

	/** overridden act method
	 * - holds the actions of the actor child Unit
	 */
	@Override
	public void act(float delta){
		super.act(delta);

		timeInterval += delta; //get time
		attackTime += delta;
		
		unitActs();	//unit act method
		
	}
 
	
	/**
	 * 
	 */
	
	/***** main acting method for units
	 * - the main acting method for units
	 * 
	 */
	public void unitActs() {
		
		/*
		 * update all the units Arrays which store info about current game state
		 * important for Unit's knowledge of ohter units/objects
		 */
 		updateUnitDataArrays(this.getStage().getActors()); 

 		/*
 		 * if the unit finds nearby enemy, they attack
 		 * - while attack is true unit is damaged (constant damage)
 		 */
 		findAttackers();
 	
		/*the series of actions unit takes
		 * if clicked on, then chosen
		 *  then, if a panel is clicked while highlighted, unit moves there
		 *  if there is a unit there, it attacks 
		 */
		if (chosen && !done && !lock) {
			//other units deselected
 		 	UnitUtils.deselectUnits(otherUnits);

 		 	//only gets panel array doesn't contain any panels
 		 	if (!panelsFound){
 		 		panelArray = pathGen.findPaths();
 		 		panelsFound = true;
 		 	}
 		 	
 			showMoves(); //shows possible moves
 			
			checkTargetPanel(); 
			
		}
	 
		
		/* if a panel was selected while unit chosen
		 * unit now moves to destination if target panel found
		 * TODO: get rid of the panelPath!=null exception & FIX IT!
		 */
		if (moving ) {
 			if (moveSequence.getActions().size != panelPath.size){
				for (Vector2 pos : panelPath){
 					MoveToAction moveAction = Actions.moveTo(pos.x, pos.y, 5f);
					moveSequence.addAction(moveAction);
					
				}
			}
			
			addAction(moveSequence);
				
			Gdx.app.log(LOG, "unit is moving right, left, up, down (enums): " + state.name().toString());
			UnitUtils.unitDirection(this, getX(), getY()); //sets the unit state
			//updatePosition();
			
			if (getX()==targetPan.getX() && getY()==targetPan.getY())
				updateUnit();
 
 		}
//		//NOTE: this is a temporary work-around
//		else if (moving && panelPath==null){
//			this.chosen = false;
//			this.moving = false;
//		}
// 
		
		/* the state unit is in when no longer chosen/moving
		 * 
		 */
		if (standing){
			state = UnitState.STILL;
		}
		
		 
	}
	
	
	/** finds any and all attackers 
	 * 
	 */
	public void findAttackers(){
		for (Unit u : enemyUnits){
			UnitUtils.unitAttacks(this, u);
		}
		
		if (attacking){
			unitAttacked();
			attacking = false;
		}
	}
 
	/** returns true if unit attacks
	 * 
	 * @param uni1
	 * @param uni2
	 * @return
	 */
	public void unitAttacked(){
		attackTime += timeInterval;

 		if (standing && attackTime > 1.5f){
 			this.attacking = false;
 			this.health += damage;
			attackTime = 0;
   		}
		
 		if (health <= 4){
			if (health <= 0){
				UnitUtils.unitDeathAction(this, 2f);
				this.remove(); 
			}
		}		

 	}
 
	
	 
	/** sets duration based on moves actually taken
	 * TODO: figure out times <--mainly these are for animations
	 * 
	 * @param maxMoves
	 * @param actualMoves
	 * @return
	 */
	public float setDuration(int maxMoves, int actualMoves) {
		float duration = 4f;
		int moves = maxMoves - actualMoves;
		
		//based on number of moves unit takes 
		switch (moves) {
			case 2 : duration = 2.5f; break;
			case 3 : duration = 2f; break;
			case 4 : duration = 1f; break;
			case 5 : duration = .5f; break;
			default : duration = 2f; break; //1 moves
		}
		
		return duration;
	}
	
	/** contains methods which updates unit positions, state &
	 * arrays which contain ally/enemy unit info
	 *  
	 */
	public void updateUnit(){
 		
		updatePosition();

		moving = false;
		standing = true;
		
		this.state = UnitState.STILL;
		lock = true;
		done = true;
		
		//setOrigin(destX, destY);
	 	pathGen.getUnitOrigin(getX(), getY());

	 	panelsFound = false;
		panelPath.clear();
		panelArray.clear();
		
		moveSequence.reset(); //NOTE: this is required to reset sequence so it doesn't cause infinite loop
		actionPool.free(moveSequence);
		actionPool.clear();
  	}
	
	/** updates unit position location variables on board
	 * important for finding panels units can move to
	 */
	public void updatePosition() {
 
		//set new unit position within 12x12 grid position matrix
		if (targetPan != null){
			gridPosX = targetPan.gridPosX;
			gridPosY = targetPan.gridPosY;
			//setPosition(targetPan.getX(), targetPan.getY());

		}
		
		if (this.getX() == targetPan.getX() && this.getY() == targetPan.getY())
			this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box

	}
	
	/** updates all other units this unit sees on board
	 * takes in this.getStage().getActors() to see everything on board
	 * 
	 * @param Array<Actor> allActors
	 */
	public void updateUnitDataArrays(Array<Actor> allActors){
		//arrays which update the other units on stage so this unit knows about them
		Array<Unit> allUnits = MapUtils.findAllUnits(allActors);
		this.otherUnits = MapUtils.otherUnits(allUnits, this);
		
		//updates the enemies on the board based on player
		if (this.player == 1){
			this.enemyUnits = MapUtils.findPlayerUnits(allUnits, 2);
		}
		else{
			this.enemyUnits = MapUtils.findPlayerUnits(allUnits, 1);
		}
	}
	 
	/** Get all possible unit movements on board
	 * uses the max moves to show player movements
	 * adds the panels into an array
	 * find relative position of panels to all possible moves
	 * highlights all possible panels on board if unit is selected
	 */
	public void showMoves() {
 		if (panelArray!=null){
	 		for (Panel p : panelArray) {
	 			
				if (!p.moveableTo){
					
					p.moveableTo = true;
				}
			}
 		}
  
	}//sets the associated panels with unit max moves
	
	/**
	 * hides the panels highlighted by showMoves()
	 */
	public void hideMoves(){
		if (panelArray!=null){
			for (Panel p : panelArray) {
				if ((p.moveableTo || p.selected) ) {
					p.selected = false; //in case p was selected 
					p.moveableTo = false;
				}
			}
		}
		panelsFound = false;
	}
	
	/** finds the targetPanel that the user selected 
	 * 	unit to go to
	 *
	 */
	public void checkTargetPanel() {
		for (Panel p : panelArray) {
			if ((p.selected && p.moveableTo)) {
				targetPan = p;
				//movePath = pathGen.findBestPath(targetPan);
								
				moving = true;
				standing = false;
				chosen = false;
				hideMoves(); //moves not shown now

				//sets position in grid (so that other units won't go over or collide)
				gridPosX = targetPan.gridPosX;
				gridPosY = targetPan.gridPosY;

				//sets unit to new position
				destX = targetPan.getX(); 
				destY = targetPan.getY();

				//gets number of moves player actual decided to take
				actualMoves = (int) (maxMoves - Math.abs((double)(targetPan.getMatrixPosX() - gridPosX)));
 				moveTime = setDuration(maxMoves, actualMoves);
				
				panelPath = UnitUtils.getMovePath(panelArray, this, targetPan);
 
				break;
  
			}
 
		}
	}
	
 
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
 		this.texture = texture;
	}
  
	/**
	 * @return the chosen
	 */
	public boolean isChosen() {
		return chosen;
	}

	/**
	 * @param chosen the chosen to set
	 */
	public void setChosen(boolean chosen) {
		this.chosen = chosen;
	}

	/**
	 * @return the maxmoves
	 */
	public int getMaxMoves() {
		return maxMoves;
	}

	/**
	 * @param maxmoves the maxmoves to set
	 */
	public void setMaxMoves(int maxMoves) {
		//need to add extra move to large & medium units
		if (this.getWidth()>32 && this.getHeight()>32) {   
			maxMoves++;
		}
				
		this.maxMoves = maxMoves;
	}
	 
 
	public void setArrayPosition(int arrPosX, int arrPosY) {
		setArrayPosX(arrPosX);
		setArrayPosY(arrPosY);
	}
	
	/**
	 * @return the arrayPosX
	 */
	public int getArrayPosX() {
		return gridPosX;
	}

	/**
	 * @return the unitID
	 */
	public int getUnitID() {
		return unitID;
	}

	/**
	 * @param unitID the unitID to set
	 */
	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	/**
	 * @param arrayPosX the arrayPosX to set
	 */
	public void setArrayPosX(int arrayPosX) {
		this.gridPosX = arrayPosX;
	}

	/**
	 * @return the arrayPosY
	 */
	public int getArrayPosY() {
		return gridPosY;
	}

	/**
	 * @param arrayPosY the arrayPosY to set
	 */
	public void setArrayPosY(int arrayPosY) {
		this.gridPosY = arrayPosY;
	}

	/**
	 * @return the unitInfo
	 */
	public UnitInfo getUnitInfo() {
		return unitInfo;
	}

	/**
	 * @param unitInfo the unitInfo to set
	 */
	public void setUnitInfo(UnitInfo unitInfo) {
		this.unitInfo = unitInfo;
	}

	/**
	 * @return the targetPan
	 */
	public Panel getTargetPan() {
		return targetPan;
	}

	/**
	 * @param targetPan the targetPan to set
	 */
	public void setTargetPan(Panel targetPan) {
		this.targetPan = targetPan;
	}
 
	/**
	 * @return the faction
	 */
	public String getFaction() {
		return faction;
	}

	/**
	 * @param faction the faction to set
	 */
	public void setFaction(String faction) {
		this.faction = faction;
	}

	 
	/** locks the unit so they cannot move
	 * @return the lock
	 */
	public boolean isLock() {
		return lock;
	}

	/**
	 * @param lock the lock to set
	 */
	public void setLock(boolean lock) {
		this.lock = lock;
	}


	public Rectangle getUnitBox() {
		return unitBox;
	}

	public void setUnitBox(Rectangle unitBox) {
		this.unitBox = unitBox;
	}

	public int[] getDamageList() {
		return damageList;
	}

	public void setDamageList(int[] damageList) {
		this.damageList = damageList;
	}

	
	/**
	 * @return the otherUnits
	 */
	public Array<Unit> getOtherUnits() {
		return otherUnits;
	}

	/**
	 * @param otherUnits the otherUnits to set
	 */
	public void setOtherUnits(Array<Unit> otherUnits) {
		this.otherUnits = otherUnits;
	}

	/**
	 * @return the enemyUnits
	 */
	public Array<Unit> getEnemyUnits() {
		return enemyUnits;
	}

	/**
	 * @param enemyUnits the enemyUnits to set
	 */
	public void setEnemyUnits(Array<Unit> enemyUnits) {
		this.enemyUnits = enemyUnits;
	}
 
	/**
	 * @return the player
	 */
	public int getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(int player) {
		this.player = player;
	}

	/**
	 * @return the clickCount
	 */
	public int getClickCount() {
		return clickCount;
	}

	/**
	 * @param clickCount the clickCount to set
	 */
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public UnitState getUnitState() {
		return state;
	}

	public void setUnitState(UnitState unitState) {
		this.state = unitState;
	}

	public Vector2 getGridLocation() {
		return gridLocation;
	}

	public void setGridLocation(Vector2 gridLocation) {
		this.gridLocation = gridLocation;
	}

	public String getUnitSize() {
		return unitSize;
	}

	public void setUnitSize(String unitSize) {
		
		this.unitSize = unitSize;
	}


}
 