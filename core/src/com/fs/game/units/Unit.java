package com.fs.game.units;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.fs.game.MainGame;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.data.GameData;
import com.fs.game.data.UnitData;
import com.fs.game.map.Locations;
import com.fs.game.map.Panel;
import com.fs.game.map.PanelState;
import com.fs.game.stages.GameStage;
import com.fs.game.utils.UnitUtils;

// so that Actions do not need reference to Actions
// static class, static import it

////NOTE: USE NON- STATIC IMPORTS FOR ACTIONS

/** Unit Actor - main actor (or characters) for players to control
 *  Represents Units on the board, gets put into GameStage, its location based on panel it is on
 *  Includes states, animations, damages, health, etc.
 *
 * TODO: restructure animation access
 *
 * @author Allen
 *
 */
public class Unit extends Actor {

	final String LOG = "UNIT ACTOR LOG: "; //for log message

    //sizes of units - final static values
    // this is how they are represnted in JSON
    public static final String SMALL = "32x32";
    public static final String MEDIUM = "64x32";
    public static final String LARGE = "64x64";

    //type of unit: land, water, air
    public static final int LAND = 1;
    public static final int WATER = 2;
    public static final int AIR = 3;
    public int unitType;

    //which side unit is on
    public static final int LEFT_SIDE = 1;
    public static final int RIGHT_SIDE = 2;
    public int unitSide; //the unit's side it is on


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
    Animation stillAlt; //alternate still anim; if Unit switches sides to move to
      
	float timeInterval = 0.1f; //stores delta time for animations
	float aniTime = .1f; //stores data related to animation between panels
	float attackTime = 0f; //time spent in UNDER_ATTACK state

    Texture attackFrame; //frame for when unit can be attacked
    public BitmapFont damageText;

    ShapeRenderer renderer; //render box indicating enemy.ally around unit

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
 	
	public int health = 400; //each unit has 400 health
	public Texture healthBar; // the texture which is drawn with the unit
	public int damage = 0; //the damage this unit IS dealt
    ObjectMap<String, Unit> enemyMap; //enemy map
    boolean attackDone = false; //if false, then will have attacked already

 	//different states the unit can be in : attack, moving, etc
    public UnitState state; //actual active state of Unit
	public AnimState animState; //for drawing the unit

 	public int clickCount = 0; //if unit selected or not still

    public boolean isSpawn; //means Unit was spawned (mainly for setting a different name for id purposes)
 	//related to unit moving on board
	private int maxMoves; //maximum move distance of unit actor
	private String unitSize; //size of unit in dimensions (can be 32x32, 64x32 or 64x64)

	//for unit movements
	Panel targetPan; //the target panel unit moves to

    private Locations.PositionData posData; //unit position data

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
	public Unit(UnitInfo unitInfo, float posX, float posY, int player, String owner) {
        //set texture, coordinates & other info
        //this.texture = UnitUtils.Setup.getUnitStill(unitInfo, player); //main unit texture for still animation
        this.unitInfo = unitInfo;
        this.player = player; //sets the player
        this.owner = owner; //set name of player that controls unit

        setupUnit(posX, posY);
        setupUnitTextures();
        setupUnitData();

        addListener(UnitUtils.Listeners.unitClickListener()); //add listener

        panelArray = new Array<Panel>();

        //set Unit initial state
        state = player == 1 ? UnitState.STANDING : UnitState.DONE;
        //create shape renderer
        renderer = new ShapeRenderer();

        if (MainGame.isMultiGame() && getOwner() != GameData.playerName)
            setTouchable(Touchable.disabled);
    }


    //sets up most of the unit attributes from actor class
	public void setupUnit(float x, float y){
        setSize(unitInfo.getWidth(), unitInfo.getHeight());

//        //aligns position to right if right side
//        if (player == 1){
//            setPosition(x, y);
//        }
//        else{
//            setPosition(x, y, Align.right);
//        }

        setPosition(x, y);

        setOrigin(x, y); //original positions for determining where unit has moved
        setName(unitInfo.getUnit()); //sets unit name

		faction = unitInfo.getFaction(); //sets faction
		unitID = unitInfo.getId(); //sets unit ID
		damageList = unitInfo.damageList; //sets the damage list
		unitSize = unitInfo.getSize();
        maxMoves = unitInfo.getMaxMoves();
        unitSide = player; //side based on which player unit belongs to

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
        unitData.setState(UnitData.IS_STANDING);
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

		// POSSIBLE temporary draw setup for healthbar
        // displays a small bar overhead
		float healthBarWidth = healthBar.getWidth() * (health/400f);
  		batch.draw(healthBar, this.getX(), this.getY(), healthBarWidth, healthBar.getHeight());

        if (state == UnitState.DONE && !isPlayerUnit()){
            // Display currUnit damage to this unit & also set it
            if (UnitController.getInstance().currUnit != null){
                damage = UnitController.getInstance().getEnemyDamage(this);
                this.damageText.draw(batch, Integer.toString(-damage) + "", getX()+getWidth()-10f, getY()+getHeight
                        ()-10f);
            }
        }

        renderBoxVisual(batch);
 	}

    /** Renders the box around the unit
     *
     * @param batch
     */
    public void renderBoxVisual(Batch batch){
        batch.end();

        //transform & projection matrices used for renderer
//        renderer.setProjectionMatrix(batch.getProjectionMatrix());
//        renderer.setTransformMatrix(batch.getTransformMatrix());
//        renderer.translate(getX(), getY(), 0);

        renderer.begin(ShapeRenderer.ShapeType.Line); //just an outline

        Color color = isPlayerUnit() ? Color.GREEN : Color.RED;

        renderer.setColor(color);
        renderer.rect(getX(), getY(), getWidth(), getHeight());
        renderer.end();

        //start batch again
        batch.begin();
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

        switch(state){
            case STANDING:
                //NOTHING HERE...waiting to be selected
                break;
            case CHOSEN: //chosen but not moving
                ((GameStage)getStage()).setCurrentUnit(this);
                break;
            case MOVING: //chosen & moving
                updatePosition();
                break;
            case ATTACKING: //attacking
//                enemyUnit.damageUnit(); //attacks enemy Unit set by Controller
//                UnitController.getInstance().onTurnFinish(this);
                break;
            case UNDER_ATTACK:
                attackTime += Gdx.graphics.getDeltaTime();
                setUnitPanelState(PanelState.ATTACK);
                //show attack animation/panel for 2 seconds, then set to done
                if (attackTime > 2.5f){
                    state = UnitState.DONE;
                    attackTime = 0; //reset attackTime
                }
                break;
            case AT_ENEMY_BORDER:
                //TODO: finish this state case
                break;
            case DONE:
                break;
            case DEAD:
                //NOTHING DONE HERE YET...TODO: setup an animation of some sort
                break;
        }
    }


    /** Units is attacked
     *  NOTE: health is currently at 400 per Unit, damage is multiplied by 15
     */
    public void damageUnit(){
        health += damage ; //damage set by other Unit

        if (health > 0)
            state = UnitState.UNDER_ATTACK; //set state as under attack
        else{
            state = UnitState.DEAD;
            UnitController.getInstance().unitDeathUpdate(this); //updates score, stage & it multi, other player unit
        }
    }

    //signals to player whether unit is enemy or ally

    /** Sets the Panel(s) unit occupies to a specific state
     *
     * @param state : state of panel
     */
    public void setUnitPanelState(PanelState state){
        Array.ArrayIterator<int[]> posIter = posData.getPosIterator();

        while (posIter.hasNext()){
            int[] pos = posIter.next();
            GameData.panelMatrix[pos[0]][pos[1]].setPanelState(state);
        }
    }


	/** updates unit position location variables on board
	 * important for finding panels units can move to
     *
	 */
	public void updatePosition() {
        if (this.getX() == targetPan.getX() && this.getY() == targetPan.getY()) {
            Locations.getLocations().updateUnitNodePosition(posData, this);

            setOrigin(getX(), getY()); //update origin
            setPosition(getX(), getY()); //set position
            this.unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box
            animState = AnimState.STILL;

            panelPath.clear();
            panelArray.clear();

            clickCount = 0;

            UnitController.getInstance().onMoveFinish(this);
        }
	}



	/**
	 * @return the maxmoves
	 */
	public int getMaxMoves() {
		return maxMoves;
	}


    /** Returns unit origin on graph.
     *  For larger units on right side, their panel graph (or game board) origin will be the second position (origin
     *  + 1 tile) since the actual actor origin is always on the bottom left even if units face different direction.
     *
     * @return : the graph origin as int array
     */
    public int[] getGraphOrigin(){
        if (player == 2 && getWidth() > 32)
            return posData.positions.get(1);

        return posData.positions.get(0);
    }


    /** Check to see if this is a Unit owned by actual player
     *
     * @return : true if it is player's Unit
     */
    public boolean isPlayerUnit(){

        if (MainGame.isMultiGame())
             return player == ((GameStage)getStage()).getPlayer() ? true : false;

        return player == ((GameStage)getStage()).getCurrPlayer() ? true : false;
    }


	/**
	 * @return the unitID
	 */
	public int getUnitID() {
		return unitID;
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

    public int getUnitSide(){
        return unitSide;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    private void log(String message){
    Gdx.app.log(LOG, message);
}
}
//OLD AppWarp Unit update methods


//only updates if a Multiplayer Game
//    public void selfUpdateData(){
//
//        unitData.updateData(this, new Vector2(getX(), getY()));
//
//        AppWarpAPI.getInstance().sendUnitUpdate(unitData);
//    }

//    /** For updating Unit for Multiplayer
//     *  Previous method, selfUpdateData(), sends out the UserData with UnitData in it
//     * @param unitData : unitData is data sent from AppWarp
//     */
//    public void updateUnit(UnitData unitData){
//        setX(unitData.getUnitPosition().x);
//        setY(unitData.getUnitPosition().y);
//
//        posData = unitData.getPosData();
//        Locations.getLocations().updateUnitNodePosition(posData, this);
////        setUnitPanelState(PanelState.ENEMY);
//        unitBox.set(getX(), getY(), this.getWidth(), this.getHeight()); //update the unit box
//
//        //animState.setValue(unitData.getState());
//        health = unitData.getHealth();
//        damage = unitData.getDamage();
//    }

