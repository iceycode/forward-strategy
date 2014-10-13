package com.fs.game.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

/** a class containing methods for creating menu components
 * 
 * @author Allen
 *
 */

public class MenuUtils {

	/** creates a pause window
	 * 
	 * @return
	 */
	public static Window pauseWindow(){
		//sets the window style
		WindowStyle winStyle = new WindowStyle();
		winStyle.titleFont = GameManager.uiSkin.getFont("default-small");
		winStyle.titleFont.scale(.01f); //scale it down a bit
		winStyle.stageBackground = GameManager.uiSkin.getDrawable("infoPane");
 		//create the window
		Window win = new Window("GAME PAUSED", winStyle);
		// "Ooze (Blue Lunar Monkey Remix)" by Manmademan
		 
		//+/- 64 accounts for timer width (64 pix)
		win.setBounds(Constants.SCREENWIDTH/4, Constants.SCREENHEIGHT/4, 100, 200);
		win.setFillParent(false);
		
		return win;
		
 	}

}
