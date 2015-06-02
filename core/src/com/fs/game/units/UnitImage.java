package com.fs.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fs.game.assets.Assets;
import com.fs.game.data.GameData;
import com.fs.game.utils.UnitUtils;

/** Extension of Image widget to show a unit still animation for selection in
 *  UnitsScreen
 *
 * Created by Allen on 11/16/14.
 */
public class UnitImage extends Image {
    final String LOG = "UNITIMAGE LOG: ";

    public UnitInfo unitInfo;
    public String key; //for key in hashmap (combination of currUnit & player number*100)
    public String size;
    public String drawableName;

    Animation stillAnim; //animation
    Texture frameSheet; //framesheet for image
    TextureRegion checked;

    float timeInterval;//animation timer

    public boolean selected; //whether this unit was selected (clicked on)
    public boolean viewing; //being viewed
    public boolean chosen; //added to list
    public int tapCount = 0; //counter for number of times unit tapped/clicked
    public boolean copy; //whether this unit image has been copied to roster or not



    //constructor for animated unit image widget that will be added to a table
    public UnitImage(UnitInfo unitInfo) {
        this.unitInfo = unitInfo;
        this.size = unitInfo.getSize();
        this.key = GameData.getInstance().playerName;
        this.setName(unitInfo.getUnit());
        this.copy = false; //initail animState is false

        this.setWidth(unitInfo.getWidth());
        this.setHeight(unitInfo.getHeight());

        setupAnimation();
        setupCheckTexture();

        this.addListener(UnitUtils.Listeners.unitImageActorListener);
//        this.addListener(UnitUtils.Listeners.unitImageChangeListener);
    }


    public void setupAnimation(){
        this.frameSheet = UnitUtils.Setup.getUnitStill(unitInfo, false); //returns stillLeft framesheet
        setWidth(unitInfo.getWidth());
        setHeight(unitInfo.getHeight());
        this.stillAnim = UnitUtils.Setup.createAnimation(.1f, frameSheet, getWidth(), getHeight());

    }


    //sets up panels which show behind unit image it is clicked on
    public void setupCheckTexture(){

        if (size.equals("32x32")) {
            drawableName = "checkS";
        }
        else if (size.equals("64x32")) {
            this.drawableName = "checkM";
        }
        else {
            this.drawableName = "checkL";
        }

        checked = new TextureRegion(Assets.uiSkin.get(drawableName, Texture.class)); //for check down

    }


    //whether or not to add this unit to hashmap
    public void imageActs(){
        if (chosen){
            GameData.playerUnitChoices.add(unitInfo);
            Gdx.app.log(LOG, "Now selected unit with key = " + key);
        }
        else if (GameData.playerUnitChoices.contains(unitInfo, true) && !chosen){
            GameData.playerUnitChoices.removeValue(unitInfo, true);
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);

        batch.draw(stillAnim.getKeyFrame(timeInterval), getX(), getY());

        if (selected){
            batch.draw(checked, getX(), getY(), getWidth(), getHeight());
        }


    }

    @Override
    public void act(float delta){
        timeInterval += delta;

        super.act(delta);
        imageActs();
    }


}
