package com.fs.game.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fs.game.constants.Constants;
import com.fs.game.utils.UIUtils;

/** Creates a popup dialog that fades or not
 * Created by Allen on 6/4/15.
 */
public class PopupDialog extends Actor{

    final float[] FD_COORDS  = Constants.TURN_MSG_COORD;
    final float[] FD_SIZE = Constants.POPUP_SIZE;

    Stage stage;

    private Skin skin;

    float msgTime = 0;
    boolean showMessage = false;

    public PopupDialog(Skin skin, Viewport viewport) {
        this.skin = skin;
        this.stage = new Stage(viewport); //set a new stage just for dialog

        setBounds(FD_COORDS[0], FD_COORDS[1], FD_SIZE[0], FD_SIZE[1]);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

     }

    public void showMessage(String message, float time){
        this.msgTime = time;
        UIUtils.fadingDialog(message, time, skin, FD_COORDS, FD_SIZE, stage);

    }
}
