package com.fs.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.fs.game.main.MainGame;

/** NOTES:
 *  - 12 x 12 grid each box of size 32 px
 *  - 384 pixels wide 
 *  - 384 pixels length
 *  
 *  Menu buttons sizes:
 *   200 pixels x 100 pixels
 *
 * @author Allen
 *
 */

public class MenuScreen implements Screen{
	
	final MainGame game; //game with the main Sprite
	
	final String LOG = "Main menu Log: ";
	
	OrthographicCamera camera;
	Stage stage;
	Skin skin;
	
	protected boolean clickedSpecies = false;
	protected boolean clickedMaps = false;
	protected boolean clickedSettings = false;
	
	public MenuScreen(final MainGame game) {
		// TODO Auto-generated constructor stub
		
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false,800,500);
		
		stage = new Stage();
		skin = new Skin();
		
		//store default Libgdx font
		BitmapFont bfont = new BitmapFont();
		bfont.scale(.23f);
		skin.add("default",bfont);
		
		//store the components of first menu
		skin.add("maps", new Texture(Gdx.files.internal("menu/mapsButton.png")));
		skin.add("settings", new Texture(Gdx.files.internal("menu/settingButton.png")));
		skin.add("species", new Texture(Gdx.files.internal("menu/speciesButton.png")));
		
		//info panel for units when choosing units
		skin.add("infoPanel", new Texture(Gdx.files.internal("infopanel/mainpanel.png")));
		
		createMenu();
	}
	
	public void createMenu() {
		TextButtonStyle mapsStyle = new TextButtonStyle();
		TextButtonStyle speciesStyle = new TextButtonStyle();
		TextButtonStyle settingStyle = new TextButtonStyle();
		
		/* TextButtonStyle for the maps menu button */
		mapsStyle.up = skin.newDrawable("maps");
		mapsStyle.down = skin.newDrawable("maps", Color.GRAY);
		mapsStyle.font = skin.getFont("default");
		
		/* TextButtonStyle for the species menu */
		speciesStyle.up = skin.newDrawable("species");
		speciesStyle.down = skin.newDrawable("species", Color.GRAY);
		speciesStyle.font = skin.getFont("default");
		
		/* TextButtonStyle for the settings menu button */
		settingStyle.up = skin.newDrawable("settings");
		settingStyle.down = skin.newDrawable("settings", Color.GRAY);
		settingStyle.font = skin.getFont("default");

		/*TextButtonStyle for the info panel (same as one in game play) */
		TextButtonStyle infoStyle = new TextButtonStyle();
		infoStyle.up = skin.newDrawable("infoPanel");
		infoStyle.font = skin.getFont("default");

		
		/* creates the TextButton actors */
		final TextButton maps = new TextButton("Choose a map", mapsStyle); //1st submenu
		maps.setPosition(150, 280); //set position of the map menu

		final TextButton species = new TextButton("Choose a species", speciesStyle); //1st submenu
		species.setPosition(480, 280); //set position of the map menu
		species.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
		 		game.setPlayer(1);
				return true;
		 	}
		 
		 	@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log(LOG, " chose  :" + event.getTarget().toString());
		 		game.setFaction(event.getTarget().toString());
		 		game.setScreen(new FactionScreen(game));
				hide();		 	
			}
		});
		
		final TextButton settings = new TextButton("Choose a faction", settingStyle); //1st submenu
		settings.setPosition(300, 90); //set position of the map menu
		
		final TextButton infoPan = new TextButton("", infoStyle);
		infoPan.setPosition(800/2 - 384/2, 0);
		infoPan.setVisible(false);
		
		stage.addActor(maps);
		stage.addActor(species);
		stage.addActor(settings);
		stage.addActor(infoPan);
	}
	
	
	@Override
	public void render(float delta) {
		
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
	    // blue and alpha component in the range [0,1]
	    // of the color to be used to clear the screen.	
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//*******THESE ARE NOT NEEDED WHEN USING A STAGE (has its own camera)
		//camera.translate(32, 32);
		//camera.update(); 
		//game.batch.setProjectionMatrix(camera.combined);
 
		stage.act(delta);
		stage.draw();
		//Table.drawDebug(stage); //removed in libgdx 1.4.1
		
		Gdx.input.setInputProcessor(stage);
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(game.getMainScreen());
		}
		
		if (Gdx.input.isKeyPressed(Keys.ENTER)) {
			//game.setScreen(new LevelScreen(game));
			hide();
		}
	
	}//render method
	

	@Override
	public void resize(int width, int height) {
	 
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
//		grid.dispose();
//		background.dispose();
//		unit.dispose();
//		//stage.dispose();
//		game.batch.dispose();
		//obstacle.dispose();
	}

}
