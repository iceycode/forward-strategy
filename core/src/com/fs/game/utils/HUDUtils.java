/*************** InfoScreen.java************
 * Another screen that shows unit information and other game info
 * - has its own stage
 * - acts in conjunction with what is happening in LevelScreen
 * - level screen 
 */

package com.fs.game.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.fs.game.maps.Panel;
import com.fs.game.units.Unit;
import com.fs.game.unused_old_classes.TextureUtils;


public class HUDUtils {
	
	final String LOG = "InfoPanel log : ";
	
	public static Skin skin = GameManager.infoPanelSkin(); //get skin from GameManager
	
	Texture infoLayer;
	Texture infoBackPan;
	TextButton exitButton;
	TextButton panel;	
	
    ShapeRenderer shapeRenderer;
    
	Vector2 vec1 = new Vector2(10,0);
	Vector2 vec2 = new Vector2(400, 100);
	
	static int rectHeight = 100;
	static int rectWidth = (int) Constants.GRID_WIDTH_B;
 
    
	//the various widgets, actors, etc used for UI
	Table infoTable; //used to create table 
    Window infoWin; 
	Button infoButton; //button object for Table
	Image unitPic;
	Dialog unitDetails;
	Dialog unitMoves;
	ScrollPane enemyUnitsPane;
			 
	//TextureRegion upRegion = ...
	//TextureRegion downRegion = ...
	BitmapFont buttonFont;
  
 	Panel panelActor;
	//Unit unitActor;
	InputProcessor inputProcessor;
	Array<Panel> gridActors;

	protected InputListener unitListener;
	protected InputListener turnListener;
 

	/** gets the unit selected on the stage
	 * 
	 * @param stageActors
	 * @return
	 */
	public static Unit findUnit(Array<Actor> stageActors) {
		Unit unit = new Unit();
		for (Actor a : stageActors) {
			if (a instanceof Unit) {
				Unit u = (Unit)a;
				if (u.chosen) {
					 unit = u;
				}
			}
		}
		return unit;
	}
	
	/** creates a pop-up window for unit info
	 * - pops up 
	 * 
	 */
	public static Window popUpInfo(ScrollPane pane, float oriX, float oriY, float width, float height) {
		//sets the window style
		WindowStyle winStyle = new WindowStyle();
		winStyle.titleFont = skin.getFont("default-small");
		winStyle.titleFont.scale(.01f); //scale it down a bit
		winStyle.stageBackground = skin.getDrawable("infoPane");
		//create the window
		Window win = new Window("Details", winStyle);
		
		win.add(pane).fill().expand();
		win.addActor(pane);
 		//+/- 64 accounts for timer width (64 pix)
		win.setBounds(Constants.GAMEBOARD_X, 0, width-64, height);
		win.setFillParent(false);
		
		return win;
	}
 
 
	/** creates a scroll pane for insertion into the main info panel table
	 * 
	 * @param scrollTable
	 * @param posX
	 * @param posY
	 * @param width
	 * @param height
	 * @return ScrollPane
	 */
	public static ScrollPane createInfoScroll(Table scrollTable, float posX, float posY, float width, float height) {
		//the style for the ScrollPane
		ScrollPaneStyle style = new ScrollPaneStyle();
 		style.background = skin.getDrawable("infoPane");
		style.vScroll = skin.getDrawable("vert-scroll");
				
		ScrollPane unitScrollPane = new ScrollPane(scrollTable, style);
		unitScrollPane.setBounds(posX, posY, width, height);

		unitScrollPane.setFillParent(false);
		//true, false <--  (disables x-scroll, disables y-scrolling)
 		unitScrollPane.setScrollingDisabled(true, false);
		
		return unitScrollPane;
	}
	
	/** creates a ScrollPane just for unit info
	 * 
	 * @param label : either info or damage list
	 * @return
	 */
	public static ScrollPane createLabelScroll(Label label) {
		//the style for the ScrollPane
		ScrollPaneStyle style = new ScrollPaneStyle();
 		style.background = skin.getDrawable("infoPane");
		style.vScroll = skin.getDrawable("vert-scroll");
				
		//the scroll pane for this label
		ScrollPane scrollPane = new ScrollPane(label, style);
		scrollPane.setWidth(Constants.INFO_W);
		scrollPane.setHeight(Constants.INFO_H); 
		scrollPane.setWidget(label);

		scrollPane.setFillParent(false); //does not fill up parent
		//(true, fale) <-- (disables x-scroll, does not disable y-scrolling)
		scrollPane.setScrollingDisabled(true, false); 
  		
		return scrollPane;
	}
	
 
	/** creates a timer as a Label
	 * 
	 * @return
	 */
	public static Label createTimer() {
		//create the timer label
		LabelStyle timerStyle = new LabelStyle();
		timerStyle.background = skin.getDrawable("timer");
		timerStyle.font = skin.getFont("font1");
		timerStyle.fontColor = Color.MAGENTA;
				
		//initialize the timer to 0
		Label timer = new Label(Float.toString(0), timerStyle); 
		timer.setBounds(Constants.GAMEBOARD_X, 0, Constants.TIMER_WIDTH, Constants.TIMER_HEIGHT);
		timer.setAlignment(Align.center);
 		timer.setWrap(true);
		
		return timer;
	}


	/** the table which sets Label layout of ScrollPane
	 * 
	 * @param unitDetail
	 * @param unitDamageList
	 * @return
	 */
	public static Table createInfoTable(ScrollPane unitDetail, ScrollPane unitDamageList) {
		Table scrollTable = new Table();
		//scrollTable.setFillParent(true);
		
		//add the labels to the table   width(unitDetail.getWidth()).height(unitDetail.getWidth()).align(Align.left);
		scrollTable.add(unitDetail).width(unitDetail.getWidth()).height(unitDetail.getHeight()) ;
		scrollTable.add(unitDamageList).width(unitDamageList.getWidth()).height(unitDamageList.getHeight());
		scrollTable.setBounds(Constants.INFO_X, Constants.INFO_Y, Constants.INFO_W*2, Constants.INFO_H);
		
		//scrollTable.addActor(unitDetail);
 		
		//scrollTable.addActor(unitDamageList);
  		
		return scrollTable;
	}
 
 
	/** for the labels which are added to ScrollTable
	 * 
	 * @return the unit details
	 */
	public static Label createLabelInfo() {

		float width = Constants.INFO_W ;
		float height = Constants.INFO_H + 100; //extra 100 for scrolling
		
		//the label style
		LabelStyle styleDetail = new LabelStyle();
		styleDetail.background = skin.getDrawable("detail-popup");
		styleDetail.font = skin.getFont("retro2");
		styleDetail.fontColor = Color.GREEN;
		styleDetail.font.scale(.01f);	//scale font a bit (TODO: get new font)
		
		Label unitDetails = new Label("Details (click on unit)",styleDetail);
		unitDetails.setAlignment(Align.top, Align.left); //sets text alignment
		unitDetails.setWidth(width);	//sets width
		unitDetails.setHeight(height);	//sets height
		
 		//unitDetails.setBounds(posX, posY, width, height); <---this can be set by a parent widget
		unitDetails.setWrap(true);
  		unitDetails.setFillParent(false);
		
		return unitDetails;
	}
	
	/** a label showing damage against units on board
	 * 
	 * is to the right of unit details (last info panel widget)
	 * 
	 * 
	 * @return the damage list
	 */
	public static Label createLabelDamage() {

		float width = Constants.INFO_W;
		float height = Constants.INFO_H + 100; //100 extra pixels for scrolling
		
		LabelStyle styleDamage = new LabelStyle();
		styleDamage.background = skin.getDrawable("damage-popup");
 		styleDamage.font = skin.getFont("retro2");
		styleDamage.fontColor = Color.RED;
//		styleDamage.font.scale(.01f);
		
		Label unitDamageList = new Label("Damage", styleDamage);
	//	unitDamageList.setTouchable(Touchable.disabled);
		unitDamageList.setAlignment(Align.top, Align.left);
		unitDamageList.setWidth(width);
		unitDamageList.setHeight(height);
		
		//unitDamageList.setBounds(posX, posY, width, height);
		unitDamageList.setWrap(true);
		unitDamageList.setFillParent(false);
 		
		return unitDamageList;
	}//Radio Schizoid - Chillout - "R Who" by Cydelix

	/** creates the side buttons which indicate player turn
	 * 
	 * @param posX
	 * @param posY
	 * @return
	 */
	public static Button createSideButton(float posX, float posY) {
		//go is when player goes
		
		//creating actors out of circles
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = skin.getDrawable("go-tex");
		buttonStyle.checked = skin.getDrawable("stop-tex");

		//create 1st player-go indication button
		Button button = new Button(buttonStyle);
		button.setBounds(posX, posY, Constants.SIDE_BUTTON_RADIUS, Constants.SIDE_BUTTON_RADIUS);
		
		return button;
	}
	
	/** the go button which when activated returns true
	 * 
	 * @return
	 */
	public static TextButton createGoButton(){
		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("lets-go-tex");
		style.down = skin.getDrawable("lets-go-tex");
		style.font = skin.getFont("retro2");
		style.fontColor = Color.GREEN;
		
		TextButton goButton = new TextButton("GO",style);
 
		goButton.setBounds(100, 100, 32, 32);
 
		
		return goButton;
		
	}

	
}
