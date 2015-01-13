package com.fs.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.fs.game.main.MainGame;

public class HtmlLauncher extends GwtApplication {
        //original dimensions in GwtApplicationConfiguration 480, 320
        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(800, 500);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new MainGame();
        }
}