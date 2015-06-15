package com.fs.game.desktop.tests;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.fs.game.MainGame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Tests for saving and retrieving data, both locally and online.
 *  Local : JSON format documents
 *  Online : AppWarp data storage
 *
 * @author Allen
 *         Created on 6/12/15.
 */
@RunWith(GdxTestRunner.class)
public class DataTests {

    @Mock
    MainGame gameListener;

    HeadlessApplication game;


    //setup the test constants beforehand
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        game = new HeadlessApplication(gameListener);

        try{
            Thread.sleep(100);
        }
        catch(InterruptedException e ){
            e.printStackTrace();
        }
    }


    @Test
    public void pathfindTest1(){

    }


}
