package com.fs.game.maps;
/** Panel class
 * a gameboard panel component that lies under the tiled map 
 * guides the Unit's movements & lights up path it can take
 * 
 * @author Allen
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.fs.game.units.Unit;
import com.fs.game.utils.GameManager;
import com.fs.game.utils.MapUtils;

public class Panel extends Actor{

	final String LOG = "panel actor log : ";
	ActorGestureListener unitListener;
	EventListener eventListener;
	
	//A* scores for pathfinding algorithm
	public float totalCost; //cost from start + heuristic
	public float costFromStart;//distance from start panel to current panel
	public float heuristic;//estimated distance from current panel to goal
	
	public Panel neighbor;
	public Panel panelAbove;
	public Panel panelBelow;
	public Panel panelRight;
	public Panel panelLeft;
	public float panelCost = 32f;
	
	Texture panelUp; //regular panel texture
	Texture panelDown ; //selected/viewing/moveableTo
	Texture panelView; //when map info being viewed
	Texture currentTex; //current texture

	public Vector2 location; //the location on the grid from (0,0) to (11,11)
	
 	public String terrainType;
	Skin skin;
	

	public Rectangle panelBox;
	
	public int gridPosX = 0; 
	public int gridPosY = 0;

	public boolean selected = false;
	public boolean moveableTo = false; 
	public boolean blocked = false; //either an obstacle or unit on it
	public boolean viewing = false;//if player wants to see info about map tile
	public int clickCount = 0;
 
	public Panel() { }//default empty constructor 

	
	public Panel(float actorX, float actorY) {
		skin = GameManager.gameSkin; //sets the skin for panels
  	 
		panelUp = skin.get("panelUp", Texture.class); //the original texture
		panelDown = skin.get("panelDown", Texture.class);
		panelView = skin.get("panelView", Texture.class);
		currentTex = panelUp;
		location = new Vector2(actorX, actorY);
		
		setPosition(actorX, actorY);
		setBounds(actorX, actorY, panelUp.getWidth(), panelUp.getHeight());
 
		 		
		//this is for collision detection
		panelBox = new Rectangle(actorX, actorY, this.getWidth(), this.getHeight());
		
		//addListener(MapUtils.createPanelListener(this)); //method initiates listeners
		addListener(new InputListener(){
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				clickCount++;

				if (clickCount == 1) {
					Gdx.app.log(LOG, "panel " +	getName() + " is now being viewed");
 
					selected = true;
 				}
				
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (clickCount == 2) {
					selected = false;
 					clickCount = 0;
				}

			}
			  
 		});
   	}

	@Override
	public void draw(Batch batch, float alpha){

		super.draw(batch, alpha); 

        //draws a certain way, tinting color to change

 		batch.draw(this.getTexture(), getX(), getY());

 	}

	@Override
	public void act(float delta){

		super.act(delta);
		tileTextureChooser();

	}

	/** tileTextureChooser
	 * 
	 * the method which selects or deselects panels
	 * 
	 * NOTE: need to set texture
	 * 
	 */
	public void tileTextureChooser() {
		
		if (blocked){
			selected = false;
			moveableTo = false;
		}
		
		if (selected || moveableTo) {
			// TODO: look more into this problem
			//currTex = skin.get("panelDown", Texture.class);
			//setTexture(currTex); //<--------- this does not change to this on stage NEED TO use skins
			this.setTexture(panelDown);
 		}//change to another texture
		else if (!selected || !moveableTo ) {
			//currTex = oriTex;
			//setTexture(currTex); //<--------- this does not change to this on stage NEED TO use skins
			this.setTexture(panelUp);
 		}
		else if (viewing){
			this.setTexture(panelView);
		}
		
	}
 
	
	public Texture getTexture() {
		return currentTex;
	}
 
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setTexture(Texture texture) {
		this.currentTex = texture;
	}

	/**
	 * @return the moveableTo
	 */
	public boolean isMoveableTo() {
		return moveableTo;
	}

	/**
	 * @param moveableTo the moveableTo to set
	 */
	public void setMoveableTo(boolean moveableTo) {
		this.moveableTo = moveableTo;
	}
  
	 
	/**
	 * @return the matrixPosX
	 */
	public int getMatrixPosX() {
		return gridPosX;
	}

	/**
	 * @param matrixPosX the matrixPosX to set
	 */
	public void setMatrixPosX(int matrixPosX) {
		this.gridPosX = matrixPosX;
	}

	/**
	 * @return the matrixPosY
	 */
	public int getMatrixPosY() {
		return gridPosY;
	}

	/**
	 * @param matrixPosY the matrixPosY to set
	 */
	public void setMatrixPosY(int matrixPosY) {
		this.gridPosY = matrixPosY;
	}

	/**
	 * @return the terrainType
	 */
	public String getTerrainType() {
		return terrainType;
	}

	/**
	 * @param terrainType the terrainType to set
	 */
	public void setTerrainType(String terrainType) {
		this.terrainType = terrainType;
	}

	public Vector2 getLocation() {
		return location;
	}

	public float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}

	public float getCostFromStart() {
		return costFromStart;
	}

	public void setCostFromStart(float costFromStart) {
		this.costFromStart = costFromStart;
	}

	public float getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(float heuristic) {
		this.heuristic = heuristic;
	}
 
}
