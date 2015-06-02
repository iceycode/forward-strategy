package com.fs.game.tests;

import com.badlogic.gdx.Gdx;

/** Game test class
 *
 * Created by Allen on 5/7/15.
 */
public class GameTest extends GdxTest{

    float time = 0;
     

    @Override
    public void create(){

    }


    @Override
    public void render(){
        time = Gdx.graphics.getRawDeltaTime();

    }
}
