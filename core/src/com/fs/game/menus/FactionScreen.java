/**
 * 
 */
package com.fs.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fs.game.main.MainGame;

/** FactionScreen.java
 * implements Screen from Libgdx
 * - creates the screen for choosing species
 * 
 * @author Allen Jagoda
 *
 */
public class FactionScreen implements Screen{
	
	final MainGame game;
	Stage stage;
	OrthographicCamera camera;
	String LOG = "Faction Select Log: ";
	
	Table tableHum; //table for human elements
	Table tableRep;
	Table tableArt;
	
	//skin stores textures relevant to buttons (
	Skin skin;
	
	String faction;
	
	/** Player turns
	 * 0 : player 1
	 * 1: player 2
	 */
	int player = 0; //represents player turn
	
	/**
	 * 
	 */
	public FactionScreen(final MainGame game) {
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false,800,500);
		
		stage = new Stage();
		skin = new Skin();
		
		BitmapFont bfont = new BitmapFont();
		bfont.scale(.23f);
		skin.add("default", bfont); //add font to skin
		
		//add menu Texture images
		skin.add("humButton", new Texture(Gdx.files.internal("menu/humansButton.png")));
		skin.add("artButton", new Texture(Gdx.files.internal("menu/artButton.png")));
		skin.add("repButton", new Texture(Gdx.files.internal("menu/repButton.png")));
		
		createMenu();
		
	}
 
	
	public void createMenu() {
		
		ButtonStyle humStyle= new ButtonStyle();
		humStyle.up = skin.getDrawable("humButton");
		
		ButtonStyle repStyle = new ButtonStyle();
		repStyle.up = skin.getDrawable("repButton");
		
		ButtonStyle artStyle= new ButtonStyle();
		artStyle.up = skin.getDrawable("artButton");
		
		//create the textbuttons
		final Button humButton = new Button(humStyle);
		humButton.setPosition(50, 40);
		humButton.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(LOG, "selected humans ");
				game.setFaction("Human");
				game.setScreen(new UnitsScreen(game, faction, player));
				hide();
			}
			
		});
		
		Button repButton = new Button(repStyle);
		repButton.setPosition(300, 40);
		repButton.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(LOG, "selected retoids ");
				game.setFaction("Reptoid");
				game.setScreen(new UnitsScreen(game, faction, player));
				hide();
			}
 		});
		
		final Button artButton = new Button(artStyle);
		artButton.setPosition(550, 40);
		artButton.addCaptureListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log(LOG, "selected arts ");
				game.setFaction("Arthropodan");
				game.setScreen(new UnitsScreen(game, faction, player));
				hide();
			}
 		});
		
		stage.addActor(humButton);
		stage.addActor(artButton);
		stage.addActor(repButton);
		
		//put them in individual tables
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//camera.translate(32, 32);
		//camera.update();
		//game.batch.setProjectionMatrix(camera.combined);
 
		stage.act(delta);
		stage.draw();
		//Table.drawDebug(stage); <-----this no longer exists in libgdx 1.4.1
		
		Gdx.input.setInputProcessor(stage);
			
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
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
	 * @return the faction
	 */
	public String getFaction() {
		return faction;
	}

	/**
	 * @param string the faction to set
	 */
	public void setFaction(String string) {
		this.faction = string;
	}
	
}
