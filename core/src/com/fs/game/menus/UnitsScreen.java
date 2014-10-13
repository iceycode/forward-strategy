/**
 * 
 */
package com.fs.game.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.fs.game.main.MainGame;
import com.fs.game.units.Unit;
import com.fs.game.units.UnitInfo;
import com.fs.game.utils.UnitUtils;

/** UnitScreen.java (implements Screen)
 * 
 * - creates a menu to choose Units 
 * - seperates units into small, medium, large
 * - shows unit info when clicked on
 * - 
 * 
 * @author Allen Jagoda
 *
 */
public class UnitsScreen implements Screen {
	
	final MainGame game; //game with the main Sprite
	final String LOG = "unit menu log: ";
	final String REMOVE_ASK = "Are you sure you want to remove this unit?";
	final String ADD_ASK = "Are you sure you want to add this unit?";
	
	String uniName = "Name : ";
	String describe = "Description : ";
	String size = "Size : "; 
	String range = "Range : ";
	String terrain = "Terrain : ";
	String special = "Special : ";
			
	OrthographicCamera camera;
	Stage stage;
	Skin skin; //skin that contains all elements besides units
	//skins below hold textures of small, med & large units
	Skin skinSmall;
	Skin skinLarge;
	Skin skinMed;
	
 	UnitInfo unitInfo;
	
	Array<Texture> unitTexArr;
	
	//stores all unit info
	Array<UnitInfo> unitInfoArr;
	
	Array<Unit> unitsAll;
	Array<Unit> smallUnits;
	Array<Unit> medUnits;
	Array<Unit> largeUnits;
	
	Array<Unit> unitsP1;
	Array<Unit> unitsP2;
	
	Array<Integer> unitForPlayer;
	Array<Array<Integer>> unitsByID;
	
	String faction; //the faction of units
	InputListener unitListener; //listener for units
	GestureListener unitGestureListener; 
	
	//dialog box components
	LabelStyle labelStyle; 		//label style for dialog boxes
	WindowStyle winStyle;  		// window style
	Window window; 				//a window which stores ui elements
	Window infoWindow ;
	TextButtonStyle textStyle;  //text style for text boxes
	Label label;  				//label for dialog boxes text
	TextButton textBtn; 		//button for dialog box
	
	ScrollPane pane;  //a scrollable window
	Tree tree; //stores UI elements as a list
	/* 1 : player 1 ; 2 : player 2 */
	int player = 0; //represents player turn
	boolean choseUnit = false; //when player officially adds unit
 	protected String unitDetail = ""; //text that will show on info panel when unit clicked
 	
	public UnitsScreen(final MainGame game, String faction, int player) {		
		this.game = game;
		this.faction = game.getFaction();
		
		//init stage, skin & UnitFactory classes
 		skin = new Skin(); //skin for panels, info panel
		skinLarge = new Skin(); //skin for large units
		skinMed = new Skin(); //skin contains medium units
		skinSmall = new Skin(); //skin contains small units
		
		stage = new Stage();
		
		//these arrays store units for each player
		unitsP1 = new Array<Unit>();
		unitsP2 = new Array<Unit>();
		
		unitsByID = new Array<Array<Integer>>(); //stores both players data
		unitForPlayer = new Array<Integer>();    //stores single players data
	
		//sets the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 500);
		camera.update();
		
		//get the textures to add to skin
		unitTexArr = new Array<Texture>();
		game.manager.getAll(Texture.class, unitTexArr); //get all Textures
		
		//gets all the unit textures based on faction choice
	//	unitInfoArr = UnitUtils.getArrayUnitInfo();
	//	unitsAll = uf.getArrayUnits();
	
		populateSkin();    //populate the main skin
//		createListeners(); //create the listeners
		getUnitsBySize();  //returns arrays for each unit
		
		createPanels(); //adds boxes that contain units to the screen
		
		createImageButtons(); //creates image Buttons containing actors/listeners
		
	
	}
	
	/*** populates the skin with panel textures**
	 * 
	 */
	public void populateSkin() {
		//the fonts
		BitmapFont bfont = new BitmapFont();
		bfont.scale(.23f);
		skin.add("default",bfont); //store default Libgdx font
		bfont.scale(.44f);
		skin.add("default-info", bfont);
		//the Textures
		skin.add("infoPanel", new Texture(Gdx.files.internal("menu/infoUnits.png")));
		skin.add("smallPanel", new Texture(Gdx.files.internal("menu/smallUnits.png")));
		skin.add("medPanel", new Texture(Gdx.files.internal("menu/medUnits.png")));
		skin.add("largePanel", new Texture(Gdx.files.internal("menu/largeUnits.png")));
		skin.add("backBtn", new Texture(Gdx.files.internal("menu/backButton.png")));
		skin.add("dialog1", new Texture(Gdx.files.internal("menu/dialogBox.png")));
		skin.add("confirm1", new Texture(Gdx.files.internal("menu/confirm1.png")));
		skin.add("counter", new Texture(Gdx.files.internal("menu/counterBox.png")));
		skin.add("checkS", new Texture(Gdx.files.internal("menu/checkedUniSmall.png")));
		skin.add("checkM", new Texture(Gdx.files.internal("menu/checkedUniMed.png")));
		skin.add("checkL", new Texture(Gdx.files.internal("menu/checkedUniLarge.png")));
		
	}
	
	/** creates arrays based on unit size
	 * 
	 */
	public void getUnitsBySize() {
		
		//initializes arrays of unit sizes
		smallUnits = new Array<Unit>(5);
		medUnits = new Array<Unit>(3);
		largeUnits = new Array<Unit>(2);
		
		for (Unit u : unitsAll) {
			if (u.getUnitInfo().getFaction().equals(faction)) {
				if (u.getTexture().getWidth() == 32 && u.getTexture().getHeight() == 32) {
					smallUnits.add(u);
					skinSmall.add("u"+u.unitInfo.getId(), u.getTexture());
				}//gets small Unit array
				else if (u.getTexture().getWidth() == 64 && u.getTexture().getHeight() == 32) {
					medUnits.add(u);
					skinMed.add("u"+u.unitInfo.getId(), u.getTexture());
				}//gets the large units
				else {
					largeUnits.add(u);
					skinLarge.add("u"+u.unitInfo.getId(), u.getTexture());
				}//adds the medium units to array
			}
		}
	}
	
	/** creates the listeners for units, panel & infopanel
	 * 
	 */
	public ChangeListener createChangeListener() {
		
		ChangeListener unitChangeListener = new ChangeListener() {
			private int clickCount = 0;
 
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int id = Integer.parseInt(event.getTarget().toString());
				
				clickCount++;
  
				Gdx.app.log(LOG, " clicked unit " + "unit & button id: " + id +
						", clickcount : " + clickCount);
				
				//show dialogs based on click count
				if (clickCount == 1) {
					Dialog dia = createDialogBox(1);
					dia.show(stage);
				}
				
				
				if (clickCount == 2 && choseUnit == true) {
					Dialog dia = createDialogBox(2);
					dia.show(stage);
 				}
				
				//gets the unit id from image buttons name
				if (choseUnit == true) {
					if (!unitForPlayer.contains(id, true)) {
						unitForPlayer.add(id);
						this.clickCount = 0;
					}//only adds if not already there
				}
				
				if (choseUnit == false) {
					if (unitForPlayer.contains(id, true)) {
						unitForPlayer.removeValue(id, true);
						clickCount = 0;
					}//only adds if not already there
				}
				
				Gdx.app.log(LOG, "Current size of array: " + unitForPlayer.size);
			}
		};
		
		return unitChangeListener;
	}
	
	
	/** creates the image buttons which go on stage
	 * 
	 */
	public void createImageButtons() {
		int posX = 55;
		int posY = 400 - 64;
		int i = 0;

		//create for small units
		for (final Unit u : smallUnits) {
			int unitID = u.unitInfo.getId();
			
			final ImageButtonStyle style = new ImageButtonStyle();
			style.imageUp = skinSmall.getDrawable("u"+ unitID);
			style.imageDown = skin.getDrawable("checkS");
			style.imageChecked = skinSmall.newDrawable("u"+ unitID, Color.LIGHT_GRAY);
			
			ImageButton smallBtn = new ImageButton(style);
			smallBtn.addListener(createChangeListener());
			smallBtn.setBounds(posX, posY, 32, 32);
			smallBtn.setName(""+unitID);

			posX+=50; //add position plus some padding
			i++; //add to counter
			if (i == 3) {
				posY+=-34;
				posX = 55;
			}
			stage.addActor(smallBtn);
		}
		
		posX = 305; //set to next
		posY = 400 - 64; //reset posY
		i = 0;
		
		//create for medium units
		for (final Unit u : medUnits) {
			int unitID = u.unitInfo.getId();//unit id
			
			ImageButtonStyle style = new ImageButtonStyle();
			style.up = skinMed.getDrawable("u"+ unitID);
			style.imageChecked = skinMed.newDrawable("u"+ unitID, Color.BLACK);
			style.imageDown = skinMed.newDrawable("u"+ unitID, Color.BLACK);

			ImageButton medBtn = new ImageButton(style);
			medBtn.addListener(createChangeListener());
			medBtn.setBounds(posX, posY, 64, 32);
 			medBtn.setName(""+unitID);
 			if (medBtn.isOver()) {
 				
 			}
 
			posX+=66;
			i++;
			if (i == 2) {
				posY += -34;
				posX = 305;
			}
			stage.addActor(medBtn);
		}
		
		posX = 555;
		posY = 400-66;
		//create for large units
		for (final Unit u : largeUnits) {
			int unitID = u.unitInfo.getId();//unit id

			ImageButtonStyle style = new ImageButtonStyle();
			style.imageUp = skinLarge.getDrawable("u"+ unitID);
			style.imageChecked = skinLarge.newDrawable("u"+ unitID, Color.LIGHT_GRAY);
			style.imageDown = skinLarge.newDrawable("u"+ unitID, Color.LIGHT_GRAY);
			
			ImageButton largeBtn = new ImageButton(style);
			largeBtn.addListener(createChangeListener());
			largeBtn.setBounds(posX, posY, 64, 64);
			largeBtn.setName(""+unitID);
			
			stage.addActor(largeBtn);
			
			posY+=-66;
		}
	}
	
	/** panels created that serve as background for units
	 * - small units :: medium units :: large units
	 * 
	 */
	public void createPanels() {
		
		Image backSmall = new Image(skin.getDrawable("smallPanel"));
		backSmall.setBounds(50, 200, 200, 200);
		stage.addActor(backSmall);
		
		Image backMed = new Image(skin.getDrawable("medPanel"));
		backMed.setBounds(300, 200, 200, 200);
		stage.addActor(backMed);
		
		Image backLarge = new Image(skin.getDrawable("largePanel"));
		backLarge.setBounds(550, 200, 200, 200);
		stage.addActor(backLarge);
		
		ImageButtonStyle styleBack = new ImageButtonStyle();
		styleBack.up = skin.getDrawable("backBtn");
		styleBack.imageDown = skin.newDrawable("backBtn", Color.LIGHT_GRAY);
		ImageButton backBtn = new ImageButton(styleBack);
		backBtn.setBounds(90, 500-100, 64, 64);
		
		Image infoPan = new Image(skin.getDrawable("infoPanel"));
		infoPan.setBounds(100, 20, 600, 100);
		stage.addActor(infoPan);
		
		
		WindowStyle winInfoStyle = new WindowStyle();
		winInfoStyle.background = skin.getDrawable("infoPanel");
		infoWindow = new Window("", winInfoStyle);
		
	}
	
	/** creates dialogs for confirming units chosen
	 * 
	 */
	public Dialog createDialogBox(int dialogNum) {		
 
		if (dialogNum == 1) {
			//String dialog text main, dialog font, dialog skin name
			dialogStyle(ADD_ASK, "default",  "confirm1");
			
		}//first pop-up showing info about unit
		else if (dialogNum == 2) {
			dialogStyle(REMOVE_ASK, "default", "confirm2");
		}
		
		//create dialog box
		Dialog dialogUnit = new Dialog("", skin, "dialog") {
 			@Override
			protected void result (Object chosen) {
		    	choseUnit = (Boolean) chosen;
		        System.out.println("choseUnit : " + chosen);
		    }
		};
		dialogUnit.text(label);
		
		dialogUnit.padTop(10).padBottom(25);
		dialogUnit.getContentTable().add(label).width(850).row();
		dialogUnit.getButtonTable().padTop(50);
		
		//adding yes/no buttons
		TextButton textBtn = new TextButton("Yes",textStyle);
		dialogUnit.button(textBtn, true);
		textBtn = new TextButton("No", textStyle);
		dialogUnit.button(textBtn, false);

		dialogUnit.key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);
 
		dialogUnit.layout();
 		//dialogUnit.show(stage);
		
		return dialogUnit;
  	}
	
	public void dialogStyle(String labelText, String font, String winBckgrnd) {
		
		/*
		 * label to be used instead of convenience method text creation
		 * - label style & text style use same font
		 */
		labelStyle = new LabelStyle();
		labelStyle.font = skin.getFont(font);
		skin.add("labelStyle", labelStyle);
		
		textStyle = new TextButtonStyle();
		textStyle.font = skin.getFont(font);
		
		label = new Label(labelText, labelStyle);
		label.setWrap(true); //need this for dialogs
		label.setFontScale(.9f);
		label.setAlignment(Align.center);
		label.setLayoutEnabled(true);
		skin.add("label", label);
		
		winStyle = new WindowStyle(); 
		winStyle.background = skin.getDrawable(winBckgrnd);
		winStyle.titleFont = skin.getFont(font);
		winStyle.titleFontColor = Color.BLACK;
		skin.add("dialog", winStyle);		//added to skin

	}
	
	/** determines whether the player has finished selecting units
	 * 
	 */
	public void showPlayerChoices() {
		if(unitsP1.size > 0 || unitsP2.size >0) {
			
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//draws the stage
		stage.act(delta);
		stage.draw();
		
		Gdx.input.setInputProcessor(stage);
		
		if (Gdx.input.isKeyPressed(Keys.ENTER) && unitsP1.size == 7){
			game.setScreen(game.getFactionScreen());
			hide();
		}//if player 1 has filled units
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(game.getFactionScreen());
			hide();
		}//if escape, goes back to faction screen
		
		
	}

	
	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
 
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
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	 
	/* unused snippets of code
	 *  - may use them in the future
	 *  - see usefulsnippets package for more snippets
	 * 
	 * 
	 */
	
//	smallBtn.addListener(new InputListener() {
//	int count = 0; //counter to see if units actually chosen
//	
//	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//		count++;
//		event.getTarget().toBack();
//
//		if (count == 1) {
//	 		Gdx.app.log(LOG, "added to p1 array");
//				unitsP1.add(u);
//			}
//		else {
//			unitsP1.removeValue(u, false);
//			count--;
//		}
// 		return true;
// 	}
//});
}
