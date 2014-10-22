/* Creates the side panel buttons, timer, etc for gameplay
 *  
 * @author Allen
 */
package com.fs.game.unused_old_classes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.fs.game.utils.Constants;
import com.fs.game.utils.GameManager;

/** @deprecated merged with the HUDUtils class
 * 
 * @author Allen
 *
 */
public class SidePanel {
	
	String LOG = "LevelScreen log: ";
	
	protected Table t1; //table for 
	protected Table t2;
	
	protected Button b1;
	protected Button b2;
	protected ButtonStyle buttonStyle;
	protected Skin skin;
	
	InputListener buttonListener;
	Texture go;
	Texture stop;

	/** stores buttons within a table which indicate player turn
	 * 
	 */
	public SidePanel() {

		skin = GameManager.uiSkin;

//		go = TextureUtils.createPixmapCircle(64, Color.GREEN);
//		stop = TextureUtils.createPixmapCircle(64, Color.RED);
//		
		t1 = new Table();
		t2 = new Table();
		
 		createButtons(); //create the buttons
 		createTables(); //create the tables
	}

	
	/*-------Creates the buttons-----*/
	public void createButtons() {
		//go is when player goes
		
		//creating actors out of circles
		buttonStyle = new ButtonStyle();
		buttonStyle.up = skin.getDrawable("go-tex");
		buttonStyle.checked = skin.getDrawable("stop-tex");

		//create 1st player-go indication button
		b1 = new Button(buttonStyle);
		b1.setBounds(60, 240, 30, 30);
		
		//create 2nd player-go indication button
		b2 = new Button(buttonStyle);
		b2.setBounds(Constants.BT2_X, Constants.BT_Y, 30, 30);

	}
	
	/** create tables out of the buttons
	 * 
	 */
	public void createTables() {
		 
 		try {
			t1.setFillParent(false);
			t1.add(b1).width(30).height(30);
			t1.addActor(b1);
			
			t2.setFillParent(false);
			t2.add(b2).width(30).height(30);
			t2.addActor(b2);	
		}catch(NullPointerException exception) {
			exception = new NullPointerException("no buttons created");
		}
		
		
	}
	

	/** the go button which when activated returns true
	 * 
	 * @return
	 */
	public TextButton createGoButton(){
		TextButtonStyle style = new TextButtonStyle();
		style.up = skin.getDrawable("lets-go-tex");
		style.down = skin.getDrawable("lets-go-tex");
		style.font = skin.getFont("retro2");
		style.fontColor = Color.GREEN;
		
		TextButton goButton = new TextButton("GO",style);
 
		goButton.setBounds(100, 100, 32, 32);
 
		
		return goButton;
		
	}
	
	/**
	 * @return the t1
	 */
	public Table getT1() {
		return t1;
	}


	/**
	 * @param t1 the t1 to set
	 */
	public void setT1(Table t1) {
		this.t1 = t1;
	}


	/**
	 * @return the t2
	 */
	public Table getT2() {
		return t2;
	}


	/**
	 * @param t2 the t2 to set
	 */
	public void setT2(Table t2) {
		this.t2 = t2;
	}


	/**
	 * @return the b1
	 */
	public Button getB1() {
		return b1;
	}


	/**
	 * @param b1 the b1 to set
	 */
	public void setB1(Button b1) {
		this.b1 = b1;
	}


	/**
	 * @return the b2
	 */
	public Button getB2() {
		return b2;
	}


	/**
	 * @param b2 the b2 to set
	 */
	public void setB2(Button b2) {
		this.b2 = b2;
	}


	/**
	 * @return the buttonStyle
	 */
	public ButtonStyle getButtonStyle() {
		return buttonStyle;
	}


	/**
	 * @param buttonStyle the buttonStyle to set
	 */
	public void setButtonStyle(ButtonStyle buttonStyle) {
		this.buttonStyle = buttonStyle;
	}


	/**
	 * @return the skin
	 */
	public Skin getSkin() {
		return skin;
	}


	/**
	 * @param skin the skin to set
	 */
	public void setSkin(Skin skin) {
		this.skin = skin;
	}
	
}
