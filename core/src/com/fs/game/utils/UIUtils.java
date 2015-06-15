/*************** InfoScreen.java************
 * Another screen that shows unit information and other game info
 * - has its own stage
 * - acts in conjunction with what is happening in LevelScreen
 * - level screen 
 */

package com.fs.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fs.game.MainGame;
import com.fs.game.assets.Assets;
import com.fs.game.constants.Constants;
import com.fs.game.screens.GameState;
import com.fs.game.screens.MainScreen;
import com.fs.game.stages.InfoStage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class UIUtils {

    /** sets up the screen stage widgets & which player goes first
     *
     * @param buttons : buttons on screen
     * @param labels : labels on screen
     * @param stage : stage on screen
     */
    public static void setupUI(TextButton[] buttons, Label[] labels, InfoStage stage) {

        //The side panel buttons indicating whose turn it is
        buttons[1] = createSideButton("P1", Constants.BT1_X, Constants.BT_Y);
        buttons[2] = createSideButton("P2", Constants.BT2_X, Constants.BT_Y);

        //for test purposes
        buttons[0] = createGoButton(stage);

        //add the actors
        for (TextButton tb : buttons){
            stage.addActor(tb);
        }

        labels[0] = createTimer();
        //----setup for ScrollPane panels as individual units within table----
        //the main pop-up window & widgets
        labels[1] = createLabelInfo();
        labels[2] = createLabelDamage();
        //score labels
        labels[3] = scoreBoard(0, 8f, Constants.SCREENHEIGHT - 40);
        labels[4] = scoreBoard(0, Constants.SCREENWIDTH - 72, Constants.SCREENHEIGHT - 40);

        for (Label label : labels){
            stage.addActor(label);
        }

        //adding labels within ScrollPane within Table to stage
        //scrollTable is the Table which holds the ScrollPane objects
        Table scrollTable = createUnitScrollTable(labels[1], labels[2]);
        stage.addActor(scrollTable);

    }


    /** Creates Table for ScrollPane with UnitInfo
     *
     * @param unitDetail : unit's details
     * @param unitDamage : unit's damage list
     * @return : a table
     */
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


    /** the table which sets Label layout of ScrollPane
     * TODO: setup damagelist so that clicking on a label will highlight where enemy is for player
     *
     * @param unitDetail : unit details
     * @param unitDamageList : unit damages to each other unit
     * @return
     */
    public static Table createInfoTable(ScrollPane unitDetail, ScrollPane unitDamageList) {
        Table scrollTable = new Table();
        //scrollTable.setFillParent(true);

        //add the labels to the table   width(unitDetail.getWidth()).height(unitDetail.getWidth()).align(Align.left);
        scrollTable.add(unitDetail).width(unitDetail.getWidth()).height(unitDetail.getHeight()) ;
        scrollTable.add(unitDamageList).width(unitDamageList.getWidth()).height(unitDamageList.getHeight());
        scrollTable.setBounds(Constants.INFO_X, Constants.INFO_Y, Constants.INFO_W * 2, Constants.INFO_H);

        return scrollTable;
    }



    /** creates a scroll pane for insertion into the main info panel table
	 * 
	 * @param scrollTable : the Table which goes into this ScrollPane
	 * @param posX : X position
	 * @param posY : y position
	 * @param width : width of scrollpane
	 * @param height : height of scrollpane
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


    /** Creates rule scrollpane for mainscreen
     *  Table <---ScrollPane[Table..] <---Table[Label[rules]] <---Label[rules]
     *
     * @param mainScreen : mainscreen
     * @param rules : rules as String
     * @return : a Table containing scrollpane with rules in table
     */
    public static Table rulesScrollPane(final MainScreen mainScreen, String rules){
        //Window window = new Window("GAME RULES", Assets.uiSkin.get("ruleWinStyle", WindowStyle.class));
        Table scrollTable = new Table();
        scrollTable.setFillParent(false);

        ScrollPaneStyle scrollStyle = Assets.uiSkin.get("ruleScrollStyle", ScrollPaneStyle.class);
        Table table = new Table();

        //create rules as a label
        Label rulesLabel = new Label(rules, Assets.uiSkin.get("ruleLabelStyle", LabelStyle.class));
        rulesLabel.setWrap(true);
        rulesLabel.setWidth(rulesLabel.getGlyphLayout().width);
        rulesLabel.setHeight(rulesLabel.getGlyphLayout().height);
        rulesLabel.setAlignment(Align.top, Align.left);
        rulesLabel.setLayoutEnabled(true);
        rulesLabel.setFillParent(false);

        table.add(rulesLabel).width(rulesLabel.getWidth()).height(rulesLabel.getHeight());

        ScrollPane ruleScrollPane = new ScrollPane(table, scrollStyle);
        ruleScrollPane.setWidth(rulesLabel.getGlyphLayout().width);
        ruleScrollPane.setHeight(Constants.RULES_SCROLL_SIZE[1]);
        ruleScrollPane.setFillParent(false);
        ruleScrollPane.setScrollingDisabled(true, false); //enables both vertical & horiz scrolling

        //add button to table containing scrollpane
        TextButton backBtn = new TextButton("BACK", Assets.uiSkin, "backBtnStyle");
        backBtn.addListener(new InputListener() {

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try{
                    mainScreen.scrollTable.remove();
                    mainScreen.gameState = GameState.START_SCREEN;
                }
                catch(NullPointerException e){
                    e.printStackTrace();
                }

                return true;
            }
        });

        scrollTable.add(ruleScrollPane).width(ruleScrollPane.getWidth()).height(ruleScrollPane.getHeight());
        scrollTable.row();

        scrollTable.add(backBtn).bottom().align(Align.left).width(Constants.BACK_SIZE[0]).height(Constants.BACK_SIZE[1]);
        scrollTable.setBounds(40f, 40f, rulesLabel.getGlyphLayout().width, Constants.RULES_SCROLL_SIZE[1]);

        return scrollTable;
    }


    /** Shows a dialog on stage that displays any message and in any position/size and
     *  fads over a designated time. Uses include indicating whose turn it is.
     *
     * @param message : message itself
     * @param time : time to display message
     * @param skin : skin used
     * @param coords : coordinates of dialog
     * @param size : size of dialog
     * @oaran stage : stage to show dialog on
     * @return : a dialog containing popup message
     */
    public static void fadingDialog(String message, float time, Skin skin, float[] coords, float[] size, Stage stage){
        Dialog fadingDialog = new Dialog("", skin).text(message);
        fadingDialog.setBounds(coords[0], coords[1], size[0], size[1]);

        fadingDialog.show(stage);
        fadingDialog.addAction(sequence(alpha(1), fadeOut(time, Interpolation.fade), removeActor(fadingDialog)));
    }
 
	/** Creates a timer as a Label
	 * 
	 * @return : a label showing the time
	 */
	public static Label createTimer() {
		//create the timer label
		LabelStyle timerStyle = new LabelStyle();
		timerStyle.background = Assets.uiSkin.getDrawable("timer");
		timerStyle.font = Assets.uiSkin.getFont("font1");
		timerStyle.fontColor = Color.MAGENTA;
				
		//initialize the timer to 0
		Label timer = new Label(Float.toString(0), timerStyle); 
		timer.setBounds(Constants.MAP_X, 0, Constants.TIMER_WIDTH, Constants.TIMER_HEIGHT);
		timer.setAlignment(Align.center);
 		timer.setWrap(true);

		
		return timer;
	}



 
	/** For the labels which are added to ScrollTable
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
//		styleDetail.font.scale(.01f);	//scale font a bit
		
		Label unitDetails = new Label("Details (click on unit)",styleDetail);
		unitDetails.setAlignment(Align.top, Align.left); //sets text alignment
		unitDetails.setWidth(width);	//sets width
		unitDetails.setHeight(height);	//sets height
		
 		//unitDetails.setBounds(posX, posY, width, height); <---this can be set by a parent widget
		unitDetails.setWrap(true);
  		unitDetails.setFillParent(false);
		
		return unitDetails;
	}
	
	/** A label showing damage against units on board
	 *  is to the right of unit details (last info panel widget)
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
	 * @param posX : x position of side button
	 * @param posY : y position of side button
	 * @return : textbutton
	 */
	public static TextButton createSideButton(String player, float posX, float posY) {
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

	/** the go button which when activated returns trueDark Summer - Exit Forever (Faxi Nadu Versio
	 *
	 * @return : button that when clicked, lets other player go
	 */
	public static TextButton createGoButton(final InfoStage stage){
        TextButtonStyle style = new TextButtonStyle();
		style.up = Assets.uiSkin.getDrawable("lets-go-tex");
		style.down = Assets.uiSkin.getDrawable("lets-go-tex");
		style.font = Assets.uiSkin.getFont("retro2");
		style.fontColor = Color.GREEN;

		final TextButton goButton = new TextButton("GO",style);

		goButton.setBounds(100, 100, 32, 32);
        goButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (goButton.isPressed()){
                    //if a multiplayer game, need to signal to other
                    // player that InfoStage needs to reset the turn
                    if (MainGame.isMultiGame()){
                        stage.multiTurnEnd( ); //signal manual turn change for other player
                    }
                    stage.resetTurn(); //reset turn
                }
            }
        });

        //TODO: add implementation in INfoStage



		return goButton;

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


    /** Creates a label for scores
     *
     * @param score : score
     * @param posX : x position of label
     * @param posY : y position of label
     * @return : label
     */
    public static Label scoreBoard(int score, float posX, float posY){
        Label scoreLabel = new Label("Score: \n" + Integer.toString(score), Assets.uiSkin, "scoreStyle");
        scoreLabel.setWidth(64);
        scoreLabel.setHeight(45);
        scoreLabel.setX(posX);
        scoreLabel.setY(posY);

        return scoreLabel;
    }





//    //TODO: implement this
//    public static Dialog confirmStart(final Stage stage){
//        Dialog confirm = new Dialog("confirmStart", Assets.uiSkin) {
//
//            {
//                text("Do you really want to start?"). button("No")
//                .button("yes", "proceeding to game").addListener(new EventListener() {
//                @Override
//                public boolean handle(Event event) {
//
//                    return false;
//                }
//                });
//
//            }
//
//            @Override
//            protected void result(final Object object) {
//                new Dialog("", Assets.uiSkin) {
//
//                    {
//                        text(object.toString());
//                        button("OK");
//                    }
//
//                }.show(stage);
//
//            }
//
//        }.show(stage);
//
//        return confirm;
//    }


}
