package com.fs.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.fs.game.assets.Assets;
import com.fs.game.assets.Constants;
import com.fs.game.enums.GameState;
import com.fs.game.main.MainGame;

/** Screen which shows the rules of the game
 *
 * Created by Allen on 1/2/15.
 */
public class InfoScreen implements Screen{

    MainGame game;

    OrthographicCamera camera;
    float SCREEN_WIDTH = Constants.SCREENWIDTH;
    float SCREEN_HEIGHT = Constants.SCREENHEIGHT;

    BitmapFont font;
    Texture backTex;
    Texture rulesBackground;
    Rectangle backBounds;
    Vector3 touchPoint;

    Stage stage; //stage for scrollabe rules

    //TODO: these will eventually become part of full-fledged tutorial
    Window tutorial1;
    Window tutorial2;
    Window tutorial3;
    Window tutorial4;
    Window tutorial5;

    private final String RULES = Constants.RULES;
    private final float[] RULES_POS = Constants.RULES_POS;
    private final float[] BACK_POS = Constants.BACK_TEX_POS;

    public InfoScreen(MainGame game){
        this.game = game;
        this.font = Assets.uiSkin.getFont("default-small");
        this.font.scale(.1f);

        //load the textures
        this.rulesBackground = Assets.uiSkin.get("rulesBackground", Texture.class);
        this.backTex = Assets.uiSkin.get("backTex", Texture.class);

        //load the background
        this.backBounds = new Rectangle(backTex.getWidth(), backTex.getHeight(), BACK_POS[0], BACK_POS[1]);
        this.touchPoint = new Vector3();

        setupCamera();
    }

    public void setupCamera(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void draw(){
        game.batch.begin();
        game.batch.draw(backTex, BACK_POS[0], BACK_POS[1]);
        game.batch.draw(rulesBackground, RULES_POS[0], RULES_POS[1]);

        drawRules();

        game.batch.end();
    }

    public void drawRules(){
        float width = rulesBackground.getWidth();
        float height = rulesBackground.getHeight();
        BitmapFont.TextBounds bounds = font.getMultiLineBounds(RULES);
        float x = RULES_POS[0] + width/2 - bounds.width/2;
        float y = RULES_POS[1] + height/2 + bounds.height/2;

        font.drawMultiLine(game.batch, RULES, x, y, width, BitmapFont.HAlignment.LEFT);
    }

    public void update(){
        if (Gdx.input.isTouched()){
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (backBounds.contains(touchPoint.x, touchPoint.y)){
                game.getMainScreen().gameState = GameState.START_SCREEN;
                game.setScreen(game.getMainScreen());
            }
        }

    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }
}
