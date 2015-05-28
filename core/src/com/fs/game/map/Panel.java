package com.fs.game.map;
/** Panel class
 * a gameboard panel component that lies under the tiled map 
 * guides the Unit's movements & lights up path it can take
 * 
 * @author Allen
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.fs.game.actors.Unit;
import com.fs.game.actors.UnitController;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;


public class Panel extends Actor {

	final String LOG = "panel actor log : ";

	//tiled map types
	public static int PASSABLE = 0;
	public static int LAND = 1; //this can be grass, desert, dirt, etc
	public static int WATER = 2;
	public static int OBSTACLE = 3; //any obstacle; eg, a large boulder
	public static int OCCUPIED = 4; //panel is occupied by a Unit
	
	//A* scores for pathfinding algorithm
 	public float costFromStart;//distance from start panel to current panel

	public Panel neighbor;
	public Panel panelAbove;
	public Panel panelBelow;
	public Panel panelRight;
	public Panel panelLeft;

	TiledMap map; //tiled map, which Panel actor sets animation for
	Array<StaticTiledMapTile> panelTiles = new Array<StaticTiledMapTile>(); //panels tiles in layer

	OrderedMap<String, Texture> textureTileMap; //map of tile textures from Panel tileSet
	Array<TextureRegion> moveTextures = new Array<TextureRegion>(2); //move regions for animation
	TextureRegion gridTexture; //main grid texture - just a black 32x32 box
	TextureRegion terrainTex; //terrain texture

	TextureRegion gridTex; //grid texture
	Array<TextureRegion> textures = new Array<TextureRegion>(); //textures of other states
	Animation moveAnim; //move animation
	float stateTime = 0; //time for animation (time in seconds)

	AnimatedTiledMapTile animTile;


	public Vector2 location; //the location on the grid from (0,0) to (11,11)
	
 	public String terrainName;
	public int terrainType = -1; //the type of panel - either terrain or position (at edge of map)

	public Rectangle panelBox;

	//position in a PanelGraph
	public int gridPosX = 0; 
	public int gridPosY = 0;

	public PanelState state; //current panel state
	public boolean selected = false;
	public boolean moveableTo = false; 
	public boolean blocked = false; //either an obstacle or unit on it
	public boolean viewing = false;//if player wants to see info about map tile
	public int clickCount = 0;
	float selectTime = 0; //time a selected panel is showing

	UnitUpdater unitUpdater;

	//interface which indicates to unit that panel was clicked on
	public interface UnitUpdater {
		//for adding Panel to Unit panelArray (panels in move range)
		void setSelectedPanel(Panel panel);
	}

	
	public Panel(float actorX, float actorY) {
		setTileTextures();

		location = new Vector2(actorX, actorY);
		state = PanelState.NONE; //set to none initially

		setBounds(actorX, actorY, 32, 32);

		//this is for collision detection
		panelBox = new Rectangle(actorX, actorY, this.getWidth(), this.getHeight());

		setInputListener(); //sets input listener
   	}

	@Override
	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);

		//draw terrain TextureRegion from TiledMapTile Cell
		batch.draw(terrainTex, getX(), getY(), getWidth(), getHeight());
		//draw grid texture region
		batch.draw(gridTexture, getX(), getY(), getWidth(), getHeight());

		//draw other type of texture over terrainTex and main gridTexture
		if (state.getIndex() > 0){
			batch.draw(textures.get(state.getIndex()), getX(), getY(), getWidth(), getHeight());
		}

		if (state == PanelState.MOVEABLE){
			stateTime += Gdx.graphics.getDeltaTime();
			batch.draw(moveAnim.getKeyFrame(stateTime, true), getX(), getY(), getWidth(), getHeight());
		}


	}

	@Override
	public void act(float delta){
		super.act(delta);

		//if Panel is selected, then becomes deselected after a bit of time
		if (state == PanelState.SELECTED){
			selectTime += delta;
			if (selectTime > 1.5f){
				selectTime = 0;
				setPanelState(PanelState.NONE);
			}
		}
	}

	/** Signals to UnitController that this panel was selected
	 * NOTE: need to set texture
	 * 
	 */
	public void signalSelected(){
		unitUpdater.setSelectedPanel(this);
	}


//	public void tileTextureChooser() {
//
////		if (blocked){
////			moveableTo = false;
////		}
////
////		if (selected || moveableTo) {
////			//currTex = skin.get("panelDown", Texture.class);
////			//setTexture(currTex); //<--------- this does not change to this on stage NEED TO use skins
////			this.setTexture(panelDown);
////			if (selected){
////				unitUpdater.setSelectedPanel(this);
////			}
//// 		}//change to another texture
////		else if (!selected || !moveableTo ) {
////			//currTex = oriTex;
////			//setTexture(currTex); //<--------- this does not change to this on stage NEED TO use skins
////			this.setTexture(panelUp);
//// 		}
////		else if (viewing){
////			this.setTexture(panelView);
////		}
//
//	}

	public void viewStateChange(){
		log("State changed to " + state.toString() + "\n Index: " + state.getIndex());
	}

	/** Sets up the Panel Tile textures which change based
	 * on the PanelState value. Uses Panel sheet to set up
	 *
	 */
	public void setTileTextures(){
		TextureRegion panelTiles = new TextureRegion(Assets.assetManager.get(Constants.PANELS_32X32, Texture.class));
		//cutting from the top left corner, going right, ending at bottom right, splitting into 32x32 regions
		// 128x128 sheet with 2 32x32 in rows[1,2] & 3 in rows[3]
		TextureRegion[][] panelTextures = panelTiles.split(32, 32);

		//add to textures array
		textures.add(panelTextures[0][1]); //grid texture
		textures.add(panelTextures[1][1]); //selected texture
		textures.add(panelTextures[1][0]); //ally texture
		textures.add(panelTextures[0][0]);//enemy texture
		textures.add(panelTextures[2][0]); //attack texture

		//create move animation
		moveAnim = new Animation(1.3f, new TextureRegion[]{panelTextures[2][1], panelTextures[2][2]});

		//set initial gridTexture
		gridTexture = textures.get(0);
	}

	/** Sets terrain: name & texture
	 *
	 * @param terrainName : name of terrain
	 */
	public void setTerrain(String terrainName, TiledMapTileLayer.Cell cell){
		//set texture & info about terrain
		terrainTex = cell.getTile().getTextureRegion();
		this.terrainName = terrainName;
		setTerrainType();//set type
	}

	//sets terrain type based on name or edge
	// used to get PanelNode type
	public void setTerrainType(){
		if (terrainName.equals("grass")) {
			terrainType = LAND;
		} else if (terrainName.equals("obstacles")) {
			terrainType = OBSTACLE;
		} else {
			terrainType = WATER;
		}
	}


	public int getTerrainType(){
		return terrainType;
	}

	public void setUnitUpdater(UnitController controller){
		this.unitUpdater = controller;
	}


	//sets panel state
	public void setPanelState(PanelState state){
		if (this.state != state) {
			log("New state = " + state.toString() + " INDEX = " + state.getIndex());
			this.state = state;
		}
	}

	public boolean isPanelOccupied(){
		Unit currUnit = UnitController.getInstance().getCurrUnit();
		if (currUnit==null)
			return false;
		else{
			int[] unitPos = currUnit.getPosData().positions.get(0);
			return unitPos[0] == gridPosX && unitPos[1] == gridPosY;
		}
	}


	protected void setInputListener(){
		//addListener(MapUtils.createPanelListener(this)); //method initiates listeners
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

				clickCount++;

				if (!isPanelOccupied()){
					setPanelState(PanelState.SELECTED);
					return false;
				}
				log("touchDown Panel, State: " + state.toString());

				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

				if (!isPanelOccupied() && state == PanelState.SELECTED) {

					if (state == PanelState.MOVEABLE) {
						signalSelected();
					}
					else{
						if (clickCount == 2){
							setPanelState(PanelState.NONE);
							clickCount = 0;
						}
					}
				}

				log("touchUP Panel, State: " + state.toString());
			}

		});
	}


	/**
	 * @return the matrixPosX
	 */
	public int getNodePosX() {
		return gridPosX;
	}

	/**
	 * @param matrixPosX the matrixPosX to set
	 */
	public void setNodePosX(int matrixPosX) {
		this.gridPosX = matrixPosX;
	}

	/**
	 * @return the matrixPosY
	 */
	public int getNodePosY() {
		return gridPosY;
	}

	/**
	 * @param matrixPosY the matrixPosY to set
	 */
	public void setNodePosY(int matrixPosY) {
		this.gridPosY = matrixPosY;
	}


	public Vector2 getLocation() {
		return location;
	}

	public float getCostFromStart() {
		return costFromStart;
	}

	public void setCostFromStart(float costFromStart) {
		this.costFromStart = costFromStart;
	}



	private void log(String message){
		Gdx.app.log("Panel LOG: ", message);
	}
 
}
