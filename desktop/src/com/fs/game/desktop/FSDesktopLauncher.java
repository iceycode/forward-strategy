package com.fs.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fs.game.main.MainGame;

public class FSDesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Forward Strategy";
		config.width = 800;
		config.height = 500;
		
		new LwjglApplication(new MainGame(), config);
	}
}
