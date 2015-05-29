package com.fs.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.fs.game.assets.Assets;

/** simple text actor that works in conjunction with stages
 *
 * Created by Allen on 1/6/15.
 */
public class TextActor extends Actor{

    final String LOG = "TextActor LOG: ";

    BitmapFont font; //font
    String msg; //message for player
    Texture background; //background texture

    AlphaAction fadeAction;
    public boolean showTurnMsg = false;


    public TextActor(BitmapFont font, float[] coords){
        this.font = font;
        this.background = Assets.uiSkin.get("textBackTex", Texture.class);
        this.fadeAction = new AlphaAction();
        fadeAction.setDuration(3f);

        setX(coords[0]);
        setY(coords[1]);
    }
    public void setText(String message){
        this.msg = message;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);

        if (showTurnMsg && msg != null) {
            Gdx.app.log(LOG, "drawing textactor");
            batch.draw(background, getX(), getY());

            BitmapFont.TextBounds bounds = font.getBounds(msg);
            float x = getX() + bounds.width/4 - bounds.width/2;
            float y = getY() + bounds.height/4 + bounds.height/2;

            font.draw(batch, msg, x, y);
        }
    }

    @Override
    public void act(float delta) {

        if (showTurnMsg) {
            addAction(fadeAction);
        }
        else{
            fadeAction.reset();
        }


    }
}
