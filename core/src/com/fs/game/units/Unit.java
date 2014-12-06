package com.fs.game.units;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.enums.UnitState;
import com.fs.game.maps.Panel;
import com.fs.game.utils.GameUtils;
import com.fs.game.utils.UnitUtils;
import com.fs.game.utils.pathfinder.PathGenerator;

////NOTE: USE NON- STATIC IMPORTS FOR ACTIONS

public class Unit extends Actor {

	final String LOG = "UNIT ACTOR LOG: "; //for log message

	//for unit movements, destination & origin of actor
 	public float destX, destY; //position unit will go on board next ON SCREEN
	private Vector2 gridLocation; //where the unit is on the grid (or tile in map)
    public Batch unitBatch;

	public Rectangle unitBox;
	public Array<Unit> otherUnits; //an array storing info about other units on board
	public Array<Unit> enemyUnits; //an array that stores enemy unit info
	
	//arrays for panels & other units on stage	
	public Array<Panel> panelArray; //created from panel position
	public Array<Vector2> panelPath; //the actual path unit takes when moving 
  
	/**ANIMATION OBJECTS**/
    Array<Animation> animations; //stores all animations
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

	//unit information
 	public UnitInfo unitInfo; //sets unit's information
 	public int[] damageList;  //lists how much damage done to or recieved by opponent
	protected int unitID;
    public BitmapFont damageText;
    public Texture texture; //unit main texture (framesheet for animation)
	//store player & faction info
	public int player = 0;
	String faction; //
	public boolean crossWater = false;
	public boolean crossLand = false; 
 	
	public float health = 4; //each unit has 4 health
	public Texture healthBar; // the texture which is drawn with the unit
	public int damage = 0; //the damage this unit IS dealt

 	//different states the unit can be in : attack, moving, etc
	public UnitState state; //for drawing the unit
 	public boolean moving = false;
	public boolean standing = true; //whether the unit is still or not
	public boolean lock = false; //this value is for 
	public boolean chosen = false; //if unit is chosen
    public boolean underattack = false;
	public boolean done = false; //unit has finished moving/attacking
	public boolean panelsFound = false;
    public boolean damageSet = false;
	
 	public int clickCount = 0; //if unit selected or not still

 	//related to unit moving on board
	private int maxMoves; //maximum move distance of unit actor
	private String unitSize; //size of unit in dimensions (can be 32x32, 64x32 or 64x64)

	//for unit movements
	Panel targetPan; //the target panel unit moves to
	public PathGenerator pathGen; //class which generates the unit's possible move movements
	public SequenceAction moveSequence;
    public ParallelAction attackAction;


    //for online multiplayer data
    public UnitData unitData;
    public String owner;

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
	
	/** The NEW main constructor
	 */
	public Unit(UnitInfo unitInfo, float posX, float posY, int player) {
        //set texture, coordinates & other info
        this.texture = UnitUtils.Setup.getUnitStill(unitInfo, player); //main unit texture for still animation
        this.unitInfo = unitInfo;
        this.player = player; //sets the player

        setupUnit(posX, posY);
        this.animations = UnitUtils.Setup.setupAnimations(this, aniTime);

        this.damageText = Assets.uiSkin.getFont("retro1");
        this.damageText.setColor(Color.YELLOW);

        this.unitBox = new Rectangle(getX(), getY(), this.getWidth(), this.getHeight()); //for collision detection
        this.healthBar = Assets.uiSkin.get("healthBar", Texture.class);

        this.state = UnitState.STILL;

        this.addListener(UnitUtils.Listeners.actorGestureListener);


        this.otherUnits = new Array<Unit>(13); //all other units except this one
        this.enemyUnits = new Array<Unit>(7);	//all enemy units

        this.pathGen = new PathGenerator(this, getOriginX(), getOriginY());


        this.moveSequence = new SequenceAction();
        this.panelArray = new Array<Panel>();
 	}


    //sets up most of the unit attributes from actor class
	public void setupUnit(float x, float y){
		setUnitInfo(unitInfo);
        setWidth(unitInfo.getWidth());
        setHeight(unitInfo.getHeight());
        setBounds(x, y, getWidth(), getHeight());

		setFaction(unitInfo.getFaction()); //sets faction
		setUnitID(unitInfo.getId()); //sets unit ID
		setName(unitInfo.getUnit()); //sets unit name

		setDamageList(unitInfo.damageList); //sets the damage list
		setUnitSize(unitInfo.size);
        setMaxMoves(unitInfo.getMaxMoves());

        setOrigin(getX(), getY()); //original positions for determining where unit has moved

        if (player == 1)
            this.state = UnitState.STILL_RIGHT;
        else
            this.state = UnitState.STILL_LEFT;

        if (this.getUnitInfo().isCrossLandObst().equals("Yes")){
			this.crossLand = true;
		}
		if (this.getUnitInfo().isCrossWater().equals("Yes")){
			this.crossWater = true;
		}
	}
 
//	/**initially set to .01 (slow)
//	 * sets up the animations for this unit
//	 */
// 	public void setupAnimations(){
//   		this.stillAnim = UnitUtils.Setup.createAnimation(aniTime, texture, getWidth(), getHeight());
//
//		//gets textures from assetmanager
//		for(String fs : unitInfo.getTexPaths()){
//
////            Gdx.app.log(LOG, "unit name : " + getName() + " , framesheet is " + fs);
//            Texture tex = Assets.assetManager.get(fs, Texture.class);
//
//            if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_RIGHT)){
//                moveRightAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_LEFT)){
//                moveLeftAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_UP)){
//                moveUpAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_MOVE_DOWN)){
//                moveDownAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath()+ Constants.UNIT_ATTACK_RIGHT)){
//                attackRightAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_LEFT)){
//                attackLeftAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)){
//                attackUpAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//            else if (fs.equals(unitInfo.getUnitPath() + Constants.UNIT_ATTACK_UP)) {
//                attackUpAnim = UnitUtils.Setup.createAnimation(aniTime, tex, getWidth(), getHeight());
//            }
//        }
//
//        if (this.moveUpAnim==null || this.moveDownAnim == null){
//            moveUpAnim = moveRightAnim;
//            moveDownAnim = moveLeftAnim;
//        }
//
//
//	}
//

	@Override
	public void draw(Batch batch, float alpha) {

 		super.draw(batch, alpha);


        batch.draw(animations.get(state.getValue()).getKeyFrame(timeInterval, true), getX(), getY());


//		switch(state){
//			case MOVE_RIGHT:
//				batch.draw(moveRightAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//			case MOVE_LEFT:
//				batch.draw(moveLeftAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//			case MOVE_UP:
//				batch.draw(moveUpAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//			case MOVE_DOWN:
//				batch.draw(moveDownAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//			case STILL:
//				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//
//            case ATTACK_RIGHT:
//                batch.draw(attackRightAnim.getKeyFrame(timeInterval, true), getX(), getY());
//                break;
//			case ATTACK_LEFT:
//                batch.draw(attackLeftAnim.getKeyFrame(timeInterval, true), getX(), getY());
//                break;
//			case ATTACK_UP:
//                batch.draw(attackUpAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//			case ATTACK_DOWN:
//                batch.draw(attackDownAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//            case DEAD:
//                this.remove();
//                this.clear();
//                break;
//            default:
//				batch.draw(stillAnim.getKeyFrame(timeInterval, true), getX(), getY());
//				break;
//		}


		//POSSIBLE temporary draw setup for healthbar
		float healthBarWidth = healthBar.getWidth() * (health/4f); 
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

        if (lock){
            UnitUtils.Attack.setUnitDamage(this);
            this.damageText.draw(batch, Integer.toString(-damage) + "", getX()+getWidth()-10f, getY()+getHeight()-10f);
        }

 	}

	/** overridden act method
	 * - holds the actions of the actor child Unit
	 */
	@Override
	public void act(float delta){
        timeInterval += delta; //get time for animations in draw method
        // attackTime += delta; //for attack animations (could differ) <---need to get attack anims first
//
//        if (this.getStage()!=null) //in case unit is last actor on board
//            updateUnitDataArrays(this.getStage().getActors());

        unitActs();

        super.act(delta);
	}


	/***** main acting method for units
	 * - the main acting method for units
	 * 
	 */
	public void unitActs() {

        updateUnitDataArrays(this.getStage().getActors());

        if (lock) {
            UnitUtils.Attack.findAttackers(this);
        }
		else{
            if (chosen){
                //other units deselected
// 		 	GameUtils.StageUtils.deselectUnits(otherUnits); //in listener
                updateGameData();

                //only gets panel array doesn't contain any panels
                if (!panelsFound){
                    panelArray = pathGen.findPaths();
                    panelsFound = true;
                }

                UnitUtils.Movement.showMoves(this); //shows possible moves
                UnitUtils.Movement.checkTargetPanel(this);
            }
            if (moving) {
                addAction(UnitUtils.Movement.createMoveAction(moveSequence, this));
                state = UnitUtils.Movement.unitDirection(this, getX(), getY()); //sets the unit state
                //updateRectangle();

                if (getX()==targetPan.getX() && getY()==targetPan.getY())
                    updateUnit();

            }
            else {
                state = UnitState.STILL;
            }
			
		}


		
		 
	}

    //this will get sent into a json object via appwarp api
    public UnitData updateUnitData(){

        return unitData;
    }




    public void updateGameData(){

        if (chosen) {
            String[] unitDetails = {UnitUtils.Info.unitDetails(this), UnitUtils.Info.unitDamageList(this)};
            GameData.unitDetails = unitDetails;
            GameData.chosenUnit = this;
            GameData.isChosen = true;
        }
        else{
            GameData.isChosen = false;
        }
    }

	
	/** contains methods which updates unit positions, state &
	 * arrays which contain ally/enemy unit info
	 *  
	 */
	public void updateUnit(){
        //update unit position, animation & selection information
 		setOrigin(getX(), getY());
		updateRectangle();

		moving = false;
		standing = true;
        done = true;

		this.state = UnitState.STILL;


        //reset path finder variables
	 	pathGen.getOriginPanel(getX(), getY());
	 	panelsFound = false;
		panelPath.clear();
		panelArray.clear();

        //reset/clear movement actions
		moveSequence.reset(); //NOTE: this is required to reset sequence so it doesn't cause infinite loop
		actionPool.free(moveSequence);
		actionPool.clear();
  	}
	
	/** updates unit position location variables on board
	 * important for finding panels units can move to
	 */
	public void updateRectangle() {
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
		Array<Unit> allUnits = GameUtils.StageUtils.findAllUnits(allActors);
		this.otherUnits = GameUtils.StageUtils.otherUnits(allUnits, this);

		//updates the enemies on the board based on player
		if (this.player == 1){
			this.enemyUnits = GameUtils.StageUtils.findPlayerUnits(allUnits, 2);
		}
		else{
			this.enemyUnits = GameUtils.StageUtils.findPlayerUnits(allUnits, 1);
		}
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

	public int[] getDamageList() {
		return damageList;
	}

	public void setDamageList(int[] damageList) {
		this.damageList = damageList;
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
 