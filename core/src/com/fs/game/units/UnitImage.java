package com.fs.game.units;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.fs.game.assets.GameManager;
import com.fs.game.utils.UnitUtils;

/** Extension of Image widget to show a unit still animation for selection in
 *  UnitsScreen
 *
 * Created by Allen on 11/16/14.
 */
public class UnitImage extends Image {

    final Skin skin = GameManager.uiSkin;

    public UnitInfo unitInfo;
    public String size;
    public String drawableName;

    protected Animation stillAnim; //animation
    protected Texture frameSheet; //framesheet for image
    protected float timeInterval;//animation timer

    public boolean selected; //whether this unit was selected
    public int tapCount = 0; //counter for number of times unit tapped/clicked



    //constructor for animated unit image widget that will be added to a table
    public UnitImage(UnitInfo unitInfo) {
        this.unitInfo = unitInfo;
        this.size = unitInfo.getSize();

        setupAnimation();
        setupDrawables();

        this.addListener(new ActorGestureListener() {
            @Override
            public boolean handle(Event event) {
                return true;
            }

            @Override
            public void touchDown (InputEvent event, float x, float y, int pointer, int button) {
                tapCount++;
                if (tapCount < 2)
                    selected = true;
                else
                    selected = false;
            }
        });
    }


    public void setupAnimation(){
        this.frameSheet = UnitUtils.getUnitStill(unitInfo, false); //returns stillLeft framesheet
        setWidth(unitInfo.getWidth());
        setHeight(unitInfo.getHeight());
        this.stillAnim = UnitUtils.createAnimation(.1f, frameSheet, getWidth(), getHeight());

        this.needsLayout();
    }


    //sets up panels which show behind unit image it is clicked on
    public void setupDrawables(){

        if (size.equals("32x32")) {
            drawableName = "checkS";
        }
        else if (size.equals("64x32")) {
            this.drawableName = "checkM";
        }
        else {
            this.drawableName = "checkL";
        }

        this.setDrawable(skin, drawableName); //for check down

    }


    public void imageActs(){

    }


    @Override
    public void draw(Batch batch, float parentAlpha){
        batch.draw(stillAnim.getKeyFrame(timeInterval), getX(), getY(), getWidth(), getHeight());

        if (selected){
            this.getDrawable().draw(batch, getX(), getY(), getWidth(), getHeight());
        }

        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta){
        timeInterval = delta;

        imageActs();
        super.act(delta);

    }


}
