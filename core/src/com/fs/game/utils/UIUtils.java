/*************** InfoScreen.java************
 * Another screen that shows unit information and other game info
 * - has its own stage
 * - acts in conjunction with what is happening in LevelScreen
 * - level screen 
 */

package com.fs.game.utils;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.data.GameData;
import com.fs.game.stages.GameStage;


public class UIUtils {


    public static Table createUnitScrollTable(Label unitDetail, Label unitDamage){
        //individual ScrollPane for each Label sets widget to Table to display:
        //UnitInfo
        Table infoTable = new Table();
        infoTable.add(unitDetail).width(unitDetail.getWidth()).height(unitDetail.getHeight());

        //unit damageList
        Table damTable = new Table();
        damTable.add(unitDamage).width(unitDamage.getWidth()).height(unitDamage.getHeight());

        //the scrollpanes
        ScrollPane infoScroll = createInfoScroll(infoTable, Constants.INFO_X, Constants.INFO_Y, Constants.INFO_W, Constants.INFO_H);
        ScrollPane damageScroll = createInfoScroll(damTable, Constants.INFO_X + Constants.INFO_W, Constants.INFO_Y,
                                        Constants.INFO_W, Constants.INFO_H);

        //scrollTable is the Table which holds the ScrollPane objects
        Table scrollTable = createInfoTable(infoScroll, damageScroll);

        return scrollTable;
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
 		style.background = Assets.uiSkin.getDrawable("infoPane");
		style.vScroll = Assets.uiSkin.getDrawable("vert-scroll");
				
		ScrollPane unitScrollPane = new ScrollPane(scrollTable, style);
		unitScrollPane.setBounds(posX, posY, width, height);

		unitScrollPane.setFillParent(false);
		//true, false <--  (disables x-scroll, disables y-scrolling)
 		unitScrollPane.setScrollingDisabled(true, false);
		
		return unitScrollPane;
	}


 
	/** creates a timer as a Label
	 * 
	 * @return
	 */
	public static Label createTimer() {
		//create the timer label
		LabelStyle timerStyle = new LabelStyle();
		timerStyle.background = Assets.uiSkin.getDrawable("timer");
		timerStyle.font = Assets.uiSkin.getFont("font1");
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
		styleDetail.background = Assets.uiSkin.getDrawable("detail-popup");
		styleDetail.font = Assets.uiSkin.getFont("retro2");
		styleDetail.fontColor = Color.GREEN;
		styleDetail.font.scale(.01f);	//scale font a bit
		
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
		styleDamage.background = Assets.uiSkin.getDrawable("damage-popup");
 		styleDamage.font = Assets.uiSkin.getFont("retro2");
		styleDamage.fontColor = Color.RED;
//		styleDamage.font.scale(.01f);
		
		Label unitDamageList = new Label("Attack", styleDamage);
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
	public static TextButton createSideButton(String player, float posX, float posY) {
		//go is when player goes

		//creating actors out of circles
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = Assets.uiSkin.getDrawable("go-tex");
		buttonStyle.checked = Assets.uiSkin.getDrawable("stop-tex");
        buttonStyle.font = Assets.uiSkin.getFont("retro2");
        buttonStyle.fontColor = Color.DARK_GRAY;

		//create 1st player-go indication button
		TextButton button = new TextButton(player, buttonStyle);
		button.setBounds(posX, posY, Constants.SIDE_BUTTON_RADIUS, Constants.SIDE_BUTTON_RADIUS);

		return button;
	}

	/** the go button which when activated returns true
	 *
	 * @return
	 */
	public static TextButton createGoButton(final GameStage stageMap){
		TextButtonStyle style = new TextButtonStyle();
		style.up = Assets.uiSkin.getDrawable("lets-go-tex");
		style.down = Assets.uiSkin.getDrawable("lets-go-tex");
		style.font = Assets.uiSkin.getFont("retro2");
		style.fontColor = Color.GREEN;

		TextButton goButton = new TextButton("GO",style);

		goButton.setBounds(100, 100, 32, 32);

        //add a listener to the go button so player turn changes
        goButton.addListener(new ActorGestureListener(){
            @Override
            public void touchDown (InputEvent event, float x, float y, int pointer, int button) {
                GameData.playerTurn = false;  //player is manually finished turn (timer did not reset)
                GameUtils.StageUtils.clearBoard(stageMap);	//clears board of selected panels
            }
        });

		return goButton;

	}

    /** creates a pop-up window for unit info
     * - pops up
     *
     */
    public static Window popUpInfo(Table scrollTable, float oriX, float oriY, float width, float height) {
        //sets the window style
        WindowStyle winStyle = new WindowStyle();
        winStyle.titleFont = Assets.uiSkin.getFont("default-small");
        winStyle.titleFont.scale(.01f); //scale it down a bit
        winStyle.stageBackground = Assets.uiSkin.getDrawable("infoPane");
        //create the window
        Window win = new Window("Unit Information", winStyle);

        win.add(scrollTable).fill().expand();
        win.addActor(scrollTable);
        //+/- 64 accounts for timer width (64 pix)
        win.setBounds(oriX, oriY, width, height);
        win.setFillParent(false);

        return win;
    }


    /** creates a confirmation dialog (used in unitscreen to confirm chosing unit
     *
     * @param dialogType
     * @param label
     * @param styleText
     * @return
     */
    public static Dialog confirmDialog(String dialogType, String label, String styleText, Stage stage) {
        //create dialog box
        final Dialog dialogUnit = new Dialog("", Assets.uiSkin, dialogType) {
            @Override
            protected void result (Object chosen) {
                System.out.println("Chose unit");
            }
        };
        dialogUnit.text(label);

        dialogUnit.padTop(10).padBottom(25);
        dialogUnit.getContentTable().add(label).width(850).row();
        dialogUnit.getButtonTable().padTop(50);

        //adding yes/no buttons
        TextButton textBtn = new TextButton("Yes",Assets.uiSkin, styleText);
        dialogUnit.button(textBtn, true);
        textBtn = new TextButton("No", Assets.uiSkin, styleText);
        dialogUnit.button(textBtn, false);

        dialogUnit.key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);

        dialogUnit.layout();
        //dialogUnit.show(stage);

        return dialogUnit;

    }

    /** creates a simple label using specified stype string
     *
     * @param labelText : the text that goes with this label
     * @param styleName : obatained from GameManager.uiSkin
     * @return
     */
    public static Label createLabel(String labelText, String styleName){
        Label label = new Label(labelText, Assets.uiSkin, styleName);
        label.setWrap(true); //need this for dialogs
        label.setFontScale(.9f);
        label.setAlignment(Align.center);
        label.setLayoutEnabled(true);

        return label;
    }

    /** creates a ScrollPane just for unit info
     *
     * @param label : either info or damage list
     * @return
     */
    public static ScrollPane createLabelScroll(Label label) {
        //the style for the ScrollPane
        ScrollPaneStyle style = new ScrollPaneStyle();
        style.background = Assets.uiSkin.getDrawable("infoPane");
        style.vScroll = Assets.uiSkin.getDrawable("vert-scroll");

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


    //TODO: implement this
    public static Dialog confirmStart(final Stage stage){
        Dialog confirm = new Dialog("confirmStart", Assets.uiSkin) {

            {
                text("Do you really want to start?"). button("No")
                .button("yes", "proceeding to game").addListener(new EventListener() {
                @Override
                public boolean handle(Event event) {

                    return false;
                }
                });

            }

            @Override
            protected void result(final Object object) {
                new Dialog("", Assets.uiSkin) {

                    {
                        text(object.toString());
                        button("OK");
                    }

                }.show(stage);

            }

        }.show(stage);

        return confirm;
    }

    public static Label scoreBoard(int score, float posX, float posY){
        Label scoreLabel = new Label("Score: \n" + Integer.toString(score), Assets.uiSkin, "scoreStyle");
        scoreLabel.setWidth(64);
        scoreLabel.setHeight(45);
        scoreLabel.setX(posX);
        scoreLabel.setY(posY);

        return scoreLabel;
    }

}
