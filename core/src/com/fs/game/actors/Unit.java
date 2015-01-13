package com.fs.game.actors;


import appwarp.WarpController;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.fs.game.enums.UnitState;
import com.fs.game.screens.MultiplayerScreen;
import com.fs.game.stages.GameStage;
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
    //Array stores all animations adsfas
    // 0=Still; 1=MoveRight; 2=MoveLeft; 3=MoveUp; 4=MoveDown
    // 5=AttackRight; 6=AttackLeft; 7=AttackUp; 8=AttackDown
    Array<Animation> animations; //stores all animations
      
	float timeInterval = 0.1f; //stores delta time for animations
	float aniTime = .1f; //stores data related to animation between panels
	float attackTime = 0.2f;

    Texture attackFrame; //frame for when unit can be attacked

	//unit information
 	public UnitInfo unitInfo; //sets unit's information
 	public int[] damageList;  //lists how much damage done to or recieved by opponent
	protected int unitID;
    public BitmapFont damageText;
    //public Texture texture; //unit main texture (framesheet for animation)
	//store player & faction info
	public int player = 0;
	String faction; //
	public boolean crossWater = false;
	public boolean crossLand = false; 
 	
	public int health = 4; //each unit has 4 health
	public Texture healthBar; // the texture which is drawn with the unit
	public int damage = 0; //the damage this unit IS dealt

 	//different states the unit can be in : attack, moving, etc
	public UnitState state; //for drawing the unit
 	public boolean moving = false;
	public boolean standing = true; //whether the unit is still or not
	public boolean lock = false; //this value is for 
	public boolean chosen = false; //if unit is chosen

    //for attacking
    public boolean isAttackable = false; //unit is attackable
    public boolean underattack = false; //unit is under attack
    public boolean isAttacking = false; //unit can attack
    public boolean attacked = false; //unit has attacked another


    public boolean done = false; //unit has finished moving/attacking
	public boolean panelsFound = false;
    public boolean enemyUnit = false; //whether this is enemy unit or not

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
    private UnitData unitData;
    private String owner;
    private int updateState; //what kind of update should be done


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
        //this.texture = UnitUtils.Setup.getUnitStill(unitInfo, player); //main unit texture for still animation
        this.unitInfo = unitInfo;
        this.player = player; //sets the player

        setupUnit(posX, posY);
        setupUnitTextures();
        setupUnitData();

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

        setPosition(getX(), getY());
        setOrigin(getX(), getY()); //original positions for determining where unit has moved

        if (this.getUnitInfo().isCrossLandObst().equals("Yes")){
			this.crossLand = true;
		}
		if (this.getUnitInfo().isCrossWater().equals("Yes")){
			this.crossWater = true;
		}
	}

    public void setupUnitTextures(){
        this.animations = UnitUtils.Setup.setupAnimations(this, aniTime);

        if (unitSize.equals("32x32"))
            this.attackFrame = Assets.assetManager.get(Constants.ATTACK_FRAME_SMALL, Texture.class);
        else if (unitSize.equals("64x32"))
            this.attackFrame = Assets.assetManager.get(Constants.ATTACK_FRAME_MED, Texture.class);
        else
            this.attackFrame = Assets.assetManager.get(Constants.ATTACK_FRAME_LARGE, Texture.class);

        this.damageText = Assets.uiSkin.getFont("retro1");
        this.damageText.setColor(Color.YELLOW);

        this.unitBox = new Rectangle(getX(), getY(), this.getWidth(), this.getHeight()); //for collision detection
        this.healthBar = Assets.uiSkin.get("healthBar", Texture.class);

        this.state = UnitState.STILL;
    }

    public void setupUnitData(){
        this.unitData = new UnitData();
        unitData.setUnitID(unitID);
        unitData.setSize(unitSize);
        unitData.setState(state.getValue());
        unitData.setDamage(damage);
        unitData.setHealth(health);
        unitData.setUnitPosition(new Vector2(getX(), getY()));
    }


	@Override
	public void draw(Batch batch, float alpha) {

 		super.draw(batch, alpha);

        int aniIndex;
        state = UnitUtils.Movement.unitDirection(this, getX(), getY()); //sets the unit state if moving
        aniIndex = state.getValue();

        if (aniIndex > animations.size)
            aniIndex = 0;


        batch.draw(animations.get(aniIndex).getKeyFrame(timeInterval, true), getX(), getY());

		//POSSIBLE temporary draw setup for healthbar
		float healthBarWidth = healthBar.getWidth() * (health/4f); 
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

        if (lock){
            Unit chosenUnit = ((GameStage)getStage()).getChosenUnit();
            if (chosenUnit != null){
                damage = Assets.damageListArray.get(chosenUnit.getUnitID()-1)[getUnitID()-1];
                this.damageText.draw(batch, Integer.toString(-damage) + "", getX()+getWidth()-10f, getY()+getHeight()-10f);
            }

            if (isAttackable){
                batch.draw(attackFrame, getX(), getY());
            }

        }
 	}

	/** overridden act method
	 * - holds the actions of the actor child Unit
	 */
	@Override
	public void act(float delta){
        timeInterval += delta; //get time for animations in draw method
        // attackTime += delta; //for attack animations (could differ) <---need to get attack anims first

        unitActs();

        super.act(delta);
	}


	/***** main acting method for units
	 * - the main acting method for units
	 * 
	 */
	public void unitActs() {
        if (owner!=null && !owner.equals(GameData.getInstance().playerName)){
            lock = true; //always keep it locked
            enemyUnit = true;
        }

        if (underattack){
            unitDamaged(damage);
            underattack = false;
        }

        if (chosen){
            if (((GameStage)getStage()).getChosenUnit()!=this){
                chosen = false;
                UnitUtils.Movement.hideMoves(this);
            }
            else{
                //other units deselected
                updateStage();

                //only gets panel array if doesn't contain any panels
                if (!panelsFound) {
                    panelArray = pathGen.findPaths();
                    panelsFound = true;
                }

                UnitUtils.Movement.showMoves(this); //shows possible moves
                UnitUtils.Movement.checkTargetPanel(this);
            }
        }

        if (done && !isAttacking){
            UnitUtils.Attack.attackNearbyUnits(this, ((GameStage)getStage()));
            isAttacking = true;
        }


        if (moving) {
            addAction(UnitUtils.Movement.createMoveAction(moveSequence, this));
            updateRectangle();
            selfUpdateData();

            if (getX()==targetPan.getX() && getY()==targetPan.getY())
                updateAfterMoving();
        }
        else if (!moving && MultiplayerScreen.getInstance().currPlayer != player){
            lock = true;
            standing = true;
            clickCount = 0;
            chosen = false;
            attacked = false;
            underattack = false;
        }
        else {
            state = UnitState.STILL;
        }
	}

    public void unitDamaged(int damageDone){
        health += damageDone;
        selfUpdateData();
    }

    public void selfUpdateData(){

        unitData.setOwner(owner);
        unitData.setState(state.getValue());
        unitData.setDamage(damage);
        unitData.setHealth(health);
        unitData.setUnitPosition(new Vector2(getX(), getY()));

        UserData userData = new UserData();
        userData.setUnitData(unitData);
        userData.setUpdateState(1);

        try{
            Json json = new Json();
            String data = json.toJson(userData, UserData.class);
            WarpController.getInstance().sendGameUpdate(data);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateUnit(UnitData unitData){
        setX(unitData.getUnitPosition().x);
        setY(unitData.getUnitPosition().y);
        this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box

        //state.setValue(unitData.getState());
        health = unitData.getHealth();
        damage = unitData.getDamage();
    }

    public void updateStage(){

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
	public void updateAfterMoving(){
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

        clickCount = 0;

        //reset/clear movement actions
		moveSequence.reset(); //NOTE: this is required to reset sequence so it doesn't cause infinite loop
		actionPool.free(moveSequence);
		actionPool.clear();

        isAtEnemyBorder();
  	}


    public void isAtEnemyBorder(){
        if (getX() == Constants.GRID_TOP_RIGHT[0] && player == 1 ){
            UnitUtils.Setup.cloneUnit(this);
        }
        else if (getX() == Constants.GRID_BTM_LEFT[0] && player == 2){
            UnitUtils.Setup.cloneUnit(this);
        }
    }


	/** updates unit position location variables on board
	 * important for finding panels units can move to
	 */
	public void updateRectangle() {
		if (this.getX() == targetPan.getX() && this.getY() == targetPan.getY()) {
            this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box
        }
	}

    private void log(String message){
        Gdx.app.log(LOG, message);
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

	public String getUnitSize() {
		return unitSize;
	}

	public void setUnitSize(String unitSize) {
		
		this.unitSize = unitSize;
	}

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
 