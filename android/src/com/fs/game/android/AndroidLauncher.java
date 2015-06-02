package com.fs.game.android;

/*
 * 
 */

//NOTE: as of gdx version 1.4.1, added archive android-4.4.jar as referenced by gdx-backend-android 
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fs.game.MainGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		/* Android configuration goes here
		 * 
		 */
 
		
		initialize(new MainGame(), config);
	}
}
