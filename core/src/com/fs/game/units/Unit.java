package com.fs.game.units;

 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
////NOTE: USE NON- STATIC IMPORTS FOR ACTIONS
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.fs.game.data.GameData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.Panel;
import com.fs.game.assets.Constants;
import com.fs.game.assets.GameManager;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.UnitUtils;
import com.fs.game.utils.pathfinder.PathGenerator;

public class Unit extends Actor {

	final String LOG = "UNIT ACTOR LOG: "; //for log message

	//for unit movements, destination & origin of actor
 	float destX, destY; //position unit will go on board next ON SCREEN
 	public int gridPosX, gridPosY; //origin of actor on the stage based on tile number
	private Vector2 gridLocation; //where the unit is on the grid (or tile in map)

	public Rectangle unitBox;
	public Array<Unit> otherUnits; //an array storing info about other units on board
	public Array<Unit> enemyUnits; //an array that stores enemy unit info
	
	//arrays for panels & other units on stage	
	public Array<Panel> panelArray; //created from panel position
	public Array<Vector2> panelPath; //the actual path unit takes when moving 
  
	/**ANIMATION OBJECTS**/
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
	Animation deathAnim;	//death animation
      
	float timeInterval = 0.1f; //stores delta time for animations
	float aniTime = .1f; //stores data related to animation between panels
	float attackTime = 0f;
	public int actualMoves = 0; //the actual moves unit takes (may not be max necessarily)

	//unit information
 	public UnitInfo unitInfo; //sets unit's information
 	public int[] damageList;  //lists how much damage done to or recieved by opponent
	protected int unitID;
    public BitmapFont damageText;
    public Label damageLabel;
    public String damageString; //a small label shows damage unit recieves from selected unit
    public Texture texture; //unit main texture (framesheet for animation)
	//store player & faction info
	public int player = 0;
	String faction; //
	public boolean crossWater = false;
	public boolean crossLand = false; 
 	
	public float health = 4; //each unit has 4 health
	public Pixmap pixHealthBar;		//the pixmap which changes based on damage taken
	public Texture healthBar; // the texture which is drawn with the unit
	public float damage = 0; //the damage this unit IS dealt

 	//different states the unit can be in : attack, moving, etc
	public UnitState state;
 	public boolean moving = false;
	public boolean standing = true; //whether the unit is still or not
	public boolean lock = false; //this value is for 
	public boolean chosen = false; //if unit is chosen
    public boolean underattack = false;
	public boolean done = false; //unit has finished moving/attacking
	boolean panelsFound = false;
	
	public int attackCount = 0; //number of times units have attacked each other
 	public int clickCount = 0; //if unit selected or not still

 	//related to unit moving on board
	private float moveTime = 8f; //gets the move duration (shorter the further unit can move)
	private int maxMoves; //maximum move distance of unit actor
	private String unitSize; //size of unit in dimensions (can be 32x32, 64x32 or 64x64)

	//for unit movements
	Panel targetPan; //the target panel unit moves to
	public PathGenerator pathGen; //class which generates the unit's possible move movements
	public SequenceAction moveSequence;
    public ParallelAction attackAction;

    //Color Manipulation object for when attacked
    Color flashAttack;

	//place to recycle allocated move actions (max of 16)
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
		this.pixHealthBar = GameManager.createPixmap(Constants.HLTH_W, Constants.HLTH_H, Color.YELLOW);
		this.healthBar = new Texture(pixHealthBar);
 		this.otherUnits = new Array<Unit>(13); //all other units except this one
		this.enemyUnits = new Array<Unit>(7);	//all enemy units
		
 		//original positions for determining where unit has moved
		this.setOrigin(getX(), getY());
		this.pathGen = new PathGenerator(this, getOriginX(), getOriginY());
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
        setMaxMoves(unitInfo.getMaxMoves());
		setWidth(unitInfo.getWidth());
        setHeight(unitInfo.getHeight());

		if (this.getUnitInfo().isCrossLandObst().equals("Yes")){
			crossLand = true;
		}
		if (this.getUnitInfo().isCrossWater().equals("Yes")){
			crossWater = true;
		}
	}
 
	/**initially set to .01 (slow) TODO: get all animations
	 * sets up the animations for this unit
	 */
 	public void setupAnimations(){
   		this.stillAnim = UnitUtils.createAnimation(aniTime, texture, getWidth(), getHeight());

		//gets textures from assetmanager
		for(String fs : unitInfo.getTexPaths()){
 				
            Gdx.app.log(LOG, "unit name : " + getName() + " , framesheet is " + fs);
            Texture tex = GameManager.assetManager.get(fs, Texture.class);

            if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_RIGHT)){
                moveRightAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_LEFT)){
                moveLeftAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_UP)){
                moveUpAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_DOWN)){
                moveDownAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath()+ Constants.UNIT_ATTACK_RIGHT)){
                attackRightAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_LEFT)){
                attackLeftAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)){
                attackUpAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)) {
                attackUpAnim = UnitUtils.createAnimation(aniTime, tex, getWidth(), getHeight());
            }
        }

        if (this.moveUpAnim==null || this.moveDownAnim == null){

//            Texture moveRightFS = new Texture(Gdx.files.internal(this.unitInfo.getUnitPath() + Constants.UNIT_MOVE_RIGHT));
//            this.moveRightAnim = UnitUtils.createAnimation(aniTime, moveRightFS, this);

            moveUpAnim = moveRightAnim;
            moveDownAnim = moveLeftAnim;
        }

        flashAttack = new Color(1,0,0,.02f); //a transparent red color (

	}


	@Override
	public void draw(Batch batch, float alpha) {

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
				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
            //TODO: get (or make, most likely) animations for this
            case ATTACK_RIGHT:
                batch.draw(attackRightAnim.getKeyFrame(timeInterval, true), getX(), getY());
                break;
			case ATTACK_LEFT:
                batch.draw(attackLeftAnim.getKeyFrame(timeInterval, true), getX(), getY());
                break;
			case ATTACK_UP:
                batch.draw(attackUpAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
			case ATTACK_DOWN:
                batch.draw(attackDownAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
            case DEAD:
                this.remove();
                this.clear();
                break;
            default:
				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
				break;
		}


		//POSSIBLE temporary draw setup for healthbar
		float healthBarWidth = healthBar.getWidth() * (health/4f); 
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

 	}

	/** overridden act method
	 * - holds the actions of the actor child Unit
	 */
	@Override
	public void act(float delta){
        timeInterval += delta; //get time for animations in draw method
        // attackTime += delta; //for attack animations (could differ) <---need to get attack anims first

        super.act(delta);
        unitActs();    //unit act method

	}
 

	/***** main acting method for units
	 * - the main acting method for units
	 * 
	 */
	public void unitActs() {
		/*
		 * update all the units Arrays which store info about current game state
		 * important for Unit's knowledge of ohter units/objects
		 */
        if (this.getStage()!=null) //in case unit is last actor on board
            updateUnitDataArrays(this.getStage().getActors());

        /*
 		 * if the unit finds nearby enemy, they attack; ONLY ON THEIR TURN
 		 * - while attack is true unit is damaged (constant damage)
 		 */
        if (lock && !underattack) {
            findAttackers();
        }

		/*the series of actions unit takes
		 * if clicked on, then chosen
		 *  then, if a panel is clicked while highlighted, unit moves there
		 *  if there is a unit there, it attacks 
		 */
		if (chosen && !done && !lock) {
			//other units deselected
 		 	UnitUtils.deselectUnits(otherUnits);
            updateGameData(chosen);

            //only gets panel array doesn't contain any panels
 		 	if (!panelsFound){
                Gdx.app.log(LOG, "actor at {" + getX() + ", " + getY() + "}");
 		 		panelArray = pathGen.findPaths();
 		 		panelsFound = true;
 		 	}

 			showMoves(); //shows possible moves
			checkTargetPanel(); 
			
		}

        if (!chosen && panelsFound){
            updateGameData(chosen);
            hideMoves();
        }

		
		/* if a panel was selected while unit chosen
		 * unit now moves to destination
		 */
		if (moving) {
 			if (moveSequence.getActions().size != panelPath.size) {
                for (Vector2 pos : panelPath) {
                    MoveToAction moveAction = Actions.moveTo(pos.x, pos.y, 5f);
                    moveSequence.addAction(moveAction);
                }
            }
			addAction(moveSequence);

			UnitUtils.unitDirection(this, getX(), getY()); //sets the unit state
            Gdx.app.log(LOG, "unit is moving right, left, up, down (enums): " + state.name().toString());
			//updatePosition();
			
			if (getX()==targetPan.getX() && getY()==targetPan.getY())
				updateUnit();
 
 		}
		
		/* the state unit is in when no longer chosen/moving
		 * 
		 */
		if (standing){
			state = UnitState.STILL;
		}
		
		 
	}


    public void updateGameData(boolean chosen){
        if (chosen) {
            GameData.unitDetails = new Array<String>();
            GameData.unitDetails.add(UnitUtils.unitDetails(this));
            GameData.unitDetails.add(UnitUtils.unitDamageList(this));
            GameData.enemyUnits = GameUtils.findEnemyUnits(this, getStage());
            GameData.chosenUnit = this;
            GameData.unitIsChosen = true;
        }
        else{
            GameData.unitIsChosen = false;
        }
    }
	
	
	/** finds any and all attackers 
	 * 
	 */
	public void findAttackers(){
		for (Unit u : enemyUnits){
            if (UnitUtils.unitAdjacent(this, u)){
                underattack = true;
                this.damage = UnitUtils.getUnitDamage(u);
                unitAttacked();
            }
		}
	}
 
	/** when unit is attacked, damage dealt
     * if unit health < 4, it disappears
     *
	 *
	 */
	public void unitAttacked(){
		//attackTime += timeInterval; //for attack animations

 		/*if (standing || lock){
 			this.attacking = false;

			attackTime = 0;
   		}*/
        if (lock && standing) {

            Gdx.app.log(LOG, "Unit " + getName() + " health is at " + health);

            if (health <= 0) {
                addAction(Actions.alpha(0f, 2f));
                this.state = UnitState.DEAD;
//                this.remove(); //removed from stage
//                this.clear();
            }
            else {
                health += damage;
                attackAction = Actions.parallel(Actions.color(Color.RED), Actions.alpha(.2f));
                addAction(attackAction);
            }
        }
 	}
 
	

	/**
     * sets duration based on moves actually taken
	 * TODO: tweak times when all animation put in
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
 		setOrigin(getX(), getY());
		updatePosition();
        clickCount = 0;
		moving = false;
		standing = true;
		
		this.state = UnitState.STILL;
		//lock = true;
		done = true;

	 	pathGen.getOriginPanel(getX(), getY());
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
		if (this.getX() == targetPan.getX() && this.getY() == targetPan.getY()) {
            this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box
        }
	}
	
	/** updates all other units this unit sees on board
	 * takes in this.getStage().getActors() to see everything on board
	 * 
	 * @param  allActors
	 */
	public void updateUnitDataArrays(Array<Actor> allActors){
		//arrays which update the other units on stage so this unit knows about them
		Array<Unit> allUnits = GameUtils.findAllUnits(allActors);
		this.otherUnits = GameUtils.otherUnits(allUnits, this);

		//updates the enemies on the board based on player
		if (this.player == 1){
			this.enemyUnits = GameUtils.findPlayerUnits(allUnits, 2);
		}
		else{
			this.enemyUnits = GameUtils.findPlayerUnits(allUnits, 1);
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
//
//				//sets position in grid (so that other units won't go over or collide)
//				gridPosX = targetPan.gridPosX;
//				gridPosY = targetPan.gridPosY;

				//sets unit to new position
				destX = targetPan.getX(); 
				destY = targetPan.getY();

				//gets number of moves player actual decided to take
				actualMoves = (int) (maxMoves - Math.abs((double)(targetPan.getMatrixPosX() - gridPosX)));
 				moveTime = setDuration(maxMoves, actualMoves);
				
				panelPath = UnitUtils.getMovePath(this, targetPan);

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
	 * @param maxMoves the maxmoves to set
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
 