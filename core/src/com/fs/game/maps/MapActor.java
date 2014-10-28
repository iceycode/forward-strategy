/**
 * 
 */
package com.fs.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fs.game.utils.GameManager;

/** MapActor extends Actor
 * the main actor that gets created on new stage
 * via the MapsFactory
 * 
 * @author Allen
 *
 */
public class MapActor extends Actor {

	final String LOG = "MapActor log : ";
	
	TiledMap tiledMap; //the tiled map
	TiledMapTileLayer tiledLayer; //the tile layer
	TiledMapTileLayer.Cell cell; //will become an actor
	
 	public MapProperties property; 
 	
 	//for unit movement selection
 	Texture panelUp;
 	Texture panelDown;
 	Texture panelView;
 	
 	Texture currentTexture; //current tile state
 	Skin skin;
 	
 	public boolean selected = false;
	public boolean moveableTo = false;
	public boolean view = false;
 		
	public String terrainType; 
	Texture texture; //this map tiles textures
 
    //TiledMap actor listeners
    protected EventListener eventListener;
    protected InputListener inputListener;
    protected ClickListener clickListener;
    
    //A* scores for pathfinding algorithm
  	public float totalCost; //cost from start + heuristic
  	public float costFromStart;//distance from start panel to current panel
  	public float heuristic;//estimated distance from current panel to goal
    
    public MapActor(TiledMapTile tile, float actorX, float actorY) {
    	
     	this.setX(actorX);
    	this.setY(actorY);
     }
    
    /** creates a clickable actor from the TiledMap cells
     * 
     * @param tiledMap
     * @param tiledLayer
     * @param cell
     */
    public MapActor(TiledMap tiledMap, TiledMapTileLayer tiledLayer, TiledMapTileLayer.Cell cell) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
        this.cell = cell;
        this.property = tiledLayer.getProperties();
        this.terrainType = tiledLayer.getName();

        
         //adds a clicklistener
        addClickListener();
    }
    
 
  
    @Override
	public void draw(Batch batch, float alpha){
		//render();		

    	super.draw(batch, alpha);
 		//batch.draw(currentTexture, getX(), getY());

//
//    	if (moveableTo){
//    		batch.draw(panelDown, getX(), getY());
//    	}
//    	else if (view){
//    		batch.draw(panelView, getX(), getY());
//     	}
     }

	@Override
	public void act(float delta){
		super.act(delta);
		
		//tileTextureChooser();
	}
	
	 

    public void addClickListener() {
    	clickListener = new ClickListener() {
    		@Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log(LOG, ((MapActor)event.getTarget()).cell + " has been clicked.");
            }
    		
    		@Override
    		public boolean isOver(Actor actor, float x, float y){
    			
    			
    			
    			return true;
    		}
    		
    	};
    	
        addListener(clickListener);

        /* the input listener for this tile
         * - used to return info related to map property
         * 	- ie, if tile is water, obstacle or ground
         */
        InputListener inputListener = new InputListener() {
        	int clickCount = 0;
        	@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        		MapActor ma = ((MapActor)event.getTarget());
        		clickCount++;
        		if (clickCount < 2){
         			view = true;
         			selected = true;
        		}
        		
         		Gdx.app.log(LOG, "this actors property name :  " + ma.terrainType);
         		
         		return true;
         	}//touch down
        	
         	@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
         		Gdx.app.log(LOG, "touch done at (" + x + ", " + y + ")");
          		if (clickCount > 2){
         			view = false;
         			selected = false;
          		}
         	}//touch up
        };
        
        this.addListener(inputListener);
    }
	
	/**
	 * @return the tiledMap
	 */
	public TiledMap getTiledMap() {
		return tiledMap;
	}

	/**
	 * @param tiledMap the tiledMap to set
	 */
	public void setTiledMap(TiledMap tiledMap) {
		this.tiledMap = tiledMap;
	}

	/**
	 * @return the tiledLayer
	 */
	public TiledMapTileLayer getTiledLayer() {
		return tiledLayer;
	}

	/**
	 * @param tiledLayer the tiledLayer to set
	 */
	public void setTiledLayer(TiledMapTileLayer tiledLayer) {
		this.tiledLayer = tiledLayer;
	}

	/**
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @param texture the texture to set
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	/**
	 * @return the cell
	 */
	public TiledMapTileLayer.Cell getCell() {
		return cell;
	}

	/**
	 * @param cell the cell to set
	 */
	public void setCell(TiledMapTileLayer.Cell cell) {
		this.cell = cell;
	}
}
