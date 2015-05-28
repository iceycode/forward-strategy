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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SerializationException;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.data.UserData;
import com.fs.game.map.Locations;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;
import com.fs.game.screens.GameState;
import com.fs.game.screens.MainScreen;
import com.fs.game.tests.TestScreen;
import com.fs.game.utils.UnitUtils;

// so that Actions do not need reference to Actions
// static class, static import it

////NOTE: USE NON- STATIC IMPORTS FOR ACTIONS

public class Unit extends Actor {

	final String LOG = "UNIT ACTOR LOG: "; //for log message

    //sizes of units - final static values
    // this is how they are represnted in JSON
    public static final String SMALL = "32x32";
    public static final String MEDIUM = "64x32";
    public static final String LARGE = "64x64";

    //type of unit: land, water, air
    public static final int LAND = 0;
    public static final int WATER = 1;
    public static final int AIR = 2;
    public int unitType;

    //which side unit is on
    public static final int LEFT_SIDE = 0;
    public static final int RIGHT_SIDE = 1;
    public int unitSide; //the unit's side it is on


	public Rectangle unitBox;
	public Array<Unit> otherUnits; //an array storing info about other units on board
	public Array<Unit> enemyUnits; //an array that stores enemy unit info
	
	//arrays for panels & other units on stage	
	public Array<Panel> panelArray; //created from panel position
    public Array<Unit> enemiesInRange; //enemies in range
    public Unit enemyUnit;
	public Array<Vector2> panelPath; //the actual path unit takes when moving 
  
	/**ANIMATION OBJECTS**/
    //Array stores all animations adsfas
    // 0=Still; 1=MoveRight; 2=MoveLeft; 3=MoveUp; 4=MoveDown
    // 5=AttackRight; 6=AttackLeft; 7=AttackUp; 8=AttackDown
    Array<Animation> animations; //stores all animations
      
	float timeInterval = 0.1f; //stores delta time for animations
	float aniTime = .1f; //stores data related to animation between panels
	float attackTime = 0f; //time spent in UNDER_ATTACK state

    Texture attackFrame; //frame for when unit can be attacked
    public BitmapFont damageText;

	//unit information
 	public UnitInfo unitInfo; //sets unit's information
 	public int[] damageList;  //lists how much damage done to or recieved by opponent
	protected int unitID;

    //public Texture texture; //unit main texture (framesheet for animation)
	//store player & faction info
	public int player = 0; //1 is on left side, 2 is on right
	String faction; //faction of unit
	public boolean crossWater = false;
	public boolean crossLand = false; 
 	
	public int health = 400; //each unit has 400 health FIXED: now set to 400 instead of 4
	public Texture healthBar; // the texture which is drawn with the unit
	public int damage = 0; //the damage this unit IS dealt

 	//different states the unit can be in : attack, moving, etc
    public UnitState state; //actual active state of Unit
	public AnimState animState; //for drawing the unit

    //FIXME: use UnitState instead of boolean values
	public boolean lock = false; //this value is for 
	public boolean chosen = false; //if unit is chosen

    //for attacking
    public boolean underattack = false; //unit is under attack

    public boolean done = false; //unit has finished moving/attacking
    public boolean isEnemyUnit = false; //whether this is enemy unit or not

 	public int clickCount = 0; //if unit selected or not still

 	//related to unit moving on board
	private int maxMoves; //maximum move distance of unit actor
	private String unitSize; //size of unit in dimensions (can be 32x32, 64x32 or 64x64)

	//for unit movements
	Panel targetPan; //the target panel unit moves to
    protected SequenceAction moveSequence;
//    public ParallelAction attackAction;

    Locations.PositionData posData; //unit position data

    //for online multiplayer data
    private UnitData unitData;
    private String owner;


	//place to recycle allocated move actions (max of 16)
	public Pool<SequenceAction> actionPool = new Pool<SequenceAction>(){
		@Override
		protected SequenceAction newObject(){
			return new SequenceAction();
		}
	};

//    PanelUpdater updater; //updates panels state to indicate whether unit is ally/enemy/selected/attacking
//
//    public interface PanelUpdater{
//        void setPanelState(boolean isPlayer); //sets panel as moveable
//    }

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

        addListener(UnitUtils.Listeners.actorGestureListener);
        otherUnits = new Array<Unit>(13); //all other units except this one
        enemyUnits = new Array<Unit>(7);	//all enemy units

        //a new SequenceAction is obtained from the pool
        moveSequence = actionPool.obtain();
        panelArray = new Array<Panel>();

        //set Unit initial state
        state = player == 1 ? UnitState.STANDING : UnitState.DONE;


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
            this.unitType = AIR;
		}
		else if (this.getUnitInfo().isCrossWater().equals("Yes")){
			this.crossWater = true;
            this.unitType = WATER;
		}
        else{
            this.unitType = LAND;
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

        this.animState = AnimState.STILL;
    }

    public void setupUnitData(){
        this.unitData = new UnitData();
        unitData.setUnitID(unitID);
        unitData.setSize(unitSize);
        unitData.setState(animState.getValue());
        unitData.setDamage(damage);
        unitData.setHealth(health);
        unitData.setUnitPosition(new Vector2(getX(), getY()));
    }


	@Override
	public void draw(Batch batch, float alpha) {

 		super.draw(batch, alpha);

        int aniIndex;
        animState = UnitUtils.Movement.unitDirection(this, getX(), getY()); //sets the unit animState if moving
        aniIndex = animState.getValue();

        if (aniIndex > animations.size)
            aniIndex = 0;


        batch.draw(animations.get(aniIndex).getKeyFrame(timeInterval, true), getX(), getY());

		//POSSIBLE temporary draw setup for healthbar
		float healthBarWidth = healthBar.getWidth() * (health/400f);
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

        if (lock){

            //displays a small bar overhead
            if (UnitController.getInstance().currUnit != null){
                damage = UnitController.getInstance().getEnemyDamage(this);
                this.damageText.draw(batch, Integer.toString(-damage) + "", getX()+getWidth()-10f, getY()+getHeight()-10f);
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


    //new unit act method
    public void unitActs(){
        if (!isPlayerUnit()){
            lock = true; //always keep it locked
        }

        switch(state){
            case STANDING:
                if (lock==true) {
                    lock = false;
                    PanelState stateSTILL = isPlayerUnit() ? PanelState.ALLY : PanelState.ENEMY;
                    setUnitPanelState(stateSTILL);
                }
                break;
            case CHOSEN: //chosen but not moving
                //setUnitPanelState(PanelState.SELECTED);
                break;
            case MOVING: //chosen & moving
                //moveUnit();
                updatePosition();

//                if (getX()==targetPan.getX() && getY()==targetPan.getY())
//                    updateAfterMoving();
                break;
            case DONE_MOVING:
                UnitController.getInstance().onTurnFinish();
                break;
            case ATTACKING: //attacking
                enemyUnit.unitAttacked(); //attacks enemy Unit set by Controller
                UnitController.getInstance().onTurnFinish(); //TODO: move this once attack animations done
                break;
            case UNDER_ATTACK:
                attackTime += Gdx.graphics.getDeltaTime();
                setUnitPanelState(PanelState.ATTACK);
                if (attackTime > 2f)
                    state = UnitState.DONE;

                break;
            case AT_ENEMY_BORDER:
                //TODO: finish this state case
                break;
            case DONE:
                if (lock==false){
                    lock = true; //so unit cannot be selected

                    PanelState stateDONE = isPlayerUnit() ? PanelState.ALLY : PanelState.ENEMY;
                    setUnitPanelState(stateDONE);
                }
                clickCount = 0; //so Unit clickCount reset

                break;
        }
    }


    //starts or finishes unit turn
    public void turnSwitch(){
        lock = lock == true ? false : true;

        PanelState state = isPlayerUnit() ? PanelState.ALLY : PanelState.ENEMY;
        setUnitPanelState(state);
    }

//    public void moveUnit(){
////        moveSequence = UnitUtils.Movement.createMoveAction(actionPool.obtain(), this);
////        moveSequence = UnitUtils.Movement.createMoveAction(this);
//
//        addAction(sequence());
//        addAction(UnitUtils.Movement.createMoveAction(moveSequence, this));
//
//    }

    public void unitAttacked(){
        health += damage; //damage set by other Unit

        state = UnitState.UNDER_ATTACK; //set state as under attack

        if (unitData!=null)
            selfUpdateData();
    }

    //only updates if a Multiplayer Game
    public void selfUpdateData(){
        if (TestScreen.gameState == GameState.MULTIPLAYER || MainScreen.gameState == GameState.MULTIPLAYER){
            unitData.updateData(owner, animState.getValue(), damage, health, new Vector2(getX(), getY()));

            UserData userData = new UserData();
            userData.setUnitData(unitData);
            userData.setUpdateState(1);

            try{
                Json json = new Json();
                String data = json.toJson(userData, UserData.class);
                WarpController.getInstance().sendGameUpdate(data);
            }
            catch(SerializationException e){
                e.printStackTrace();
            }
        }

    }

    public void updateUnit(UnitData unitData){
        setX(unitData.getUnitPosition().x);
        setY(unitData.getUnitPosition().y);
        this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box

        //animState.setValue(unitData.getState());
        health = unitData.getHealth();
        damage = unitData.getDamage();
    }

    boolean panelStateSet = false; //whether state set
    //signals to player whether unit is enemy or ally
    public void setUnitPanelState(PanelState state){
        for (int[] pos : posData.positions){
            GameData.panelMatrix[pos[0]][pos[1]].setPanelState(state);
        }
    }

	
	/** contains methods which updates unit positions, animState &
	 * arrays which contain ally/enemy unit info
	 *  
	 */
	public void updateAfterMoving() {
        //update unit position, animation & selection information

		updatePosition();

//        state = UnitState.DONE_MOVING;
        animState = AnimState.STILL;

		panelPath.clear();
		panelArray.clear();

        clickCount = 0;

        //reset/clear movement actions
//		moveSequence.reset(); //NOTE: this is required to reset sequence so it doesn't cause infinite loop
//		actionPool.free(moveSequence);
//		actionPool.clear();


        UnitController.getInstance().onMoveFinish();
//        isAtEnemyBorder(); //clones enemy & resets Unit position

  	}

	/** updates unit position location variables on board
	 * important for finding panels units can move to
	 */
	public void updatePosition() {
        //update node positions as well, but only if on 32x32 region
        if (getX()%32==0 && getY()%32==0)
            Locations.getLocations().updateUnitNodePosition(this);

        //update object that is sent via AppWarp to other player
        selfUpdateData();

        if (this.getX() == targetPan.getX() && this.getY() == targetPan.getY()) {
            setOrigin(getX(), getY()); //update origin
            this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box
            //        state = UnitState.DONE_MOVING;
            animState = AnimState.STILL;

            panelPath.clear();
            panelArray.clear();

            clickCount = 0;

    //		moveSequence.reset(); //NOTE: this is required to reset sequence so it doesn't cause infinite loop
    //		actionPool.free(moveSequence);
    //		actionPool.clear();

            UnitController.getInstance().onMoveFinish();
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


    /** Check to see if this is a Unit owned by actual player
     *
     * @return : true if it is player's Unit
     */
    public boolean isPlayerUnit(){
        return owner == GameData.playerName ? true : false;
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

    /** Sets Unit position data
     *
     * @param data : data from Locations.PositionData
     */
    public void setPosData(Locations.PositionData data){
        this.posData = data;
    }

    public Locations.PositionData getPosData(){
        return posData;
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

//    //sets side based on x-axis position & size
//    public void setUnitSide(){
//        if (getX()==Constants.MAP_TOP_RIGHT[0] - 32 || getX()==Constants.MAP_TOP_RIGHT[0]-64)
//            unitSide = RIGHT_SIDE;
//        else
//            unitSide = LEFT_SIDE;
//    }

    public int getUnitSide(){
        return unitSide;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

//    @Override
//    public void setSelectedPanel(Panel panel) {
//        this.targetPan = panel;
//        state = UnitState.MOVING; //set state to MOVING
//
//        UnitUtils.Movement.hideMoves(this);  //hide panels
//        moveSequence = UnitUtils.Movement.createMoveAction(this); //create moveSequence
//        updatePosition();
//    }

    private void log(String message){
    Gdx.app.log(LOG, message);
}
}
 