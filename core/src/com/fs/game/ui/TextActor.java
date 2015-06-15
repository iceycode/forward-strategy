package com.fs.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    boolean showMsg = false;

    float currTime = 0;
    float msgTime = 0; //message time


    public TextActor(BitmapFont font, float[] coords){
        this.font = font;
        this.background = Assets.uiSkin.get("textBackTex", Texture.class);

        this.fadeAction = new AlphaAction();

        setPosition(coords[0], coords[1]);
    }


    /** Shows a message for a specified time
     *
     * @param message : message to show
     * @param time : time to show it for
     */
    public void showTimedMessage(String message, float time){
        this.msg = message;
        showMsg = true;
        msgTime = time + .5f; //extra .5 for rounding up

        log("show TextActor message");
        setFadeAction(time); //setup & add the fadeAction
        toFront(); //sends to front
    }


    public void setFadeAction(float fadeTime){

        fadeAction.setDuration(fadeTime);
        addAction(fadeAction); //fades textactor
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (showMsg) {
            if (currTime < msgTime){

                batch.draw(background, getX(), getY());

//                BitmapFont.TextBounds bounds = font.getBounds(msg);
                GlyphLayout layout = new GlyphLayout();
                layout.setText(font, msg);
                float x = getX() + layout.width/4 - layout.width/2;
                float y = getY() + layout.height/4 + layout.height/2;

                font.draw(batch, layout, x, y);
            }
            else{
                currTime = 0;
                showMsg = false;
                toBack(); //sends to back
                fadeAction.reset();
            }
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (showMsg) {
            currTime += delta;
        }
    }


    private void log(String message){
        Gdx.app.log(LOG, message);
    }
}
