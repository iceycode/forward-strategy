package com.fs.game.desktop.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fs.game.constants.Constants;
import com.fs.game.tests.GdxTest;

/**
 * Created by Allen on 5/9/15.
 */
public class GdxTestLauncher extends GdxTest implements ApplicationListener{

    /** Launches a GdxTest class
     *
     * @param testApp : testApp being run
     */
    public static void launchTestApp(ApplicationListener testApp){
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = (int)Constants.SCREENWIDTH;
        config.height = (int)Constants.SCREENHEIGHT;

        new LwjglApplication(testApp, config);
    }


}
