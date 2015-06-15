package com.fs.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.io.File;

/**
 * Created by Allen on 5/10/15.
 */
public class AssetHelper {
    //------GameManager helper methods----------
    //NOTE: FreeType will not work in HTML5
    public static BitmapFont fontFNTGenerator(String fontPath, int size, Color color) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;

        //1st retro font
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        font.setColor(color);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        return font;
    }

    public static Pixmap createPixmap(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // pixmap.drawRectangle(200, 200, width, height);
        pixmap.setColor(color);
        pixmap.fill(); // fill with the color

        return pixmap;
    }



    public static Texture createMMTexture(int width, int height, Color color){
        Pixmap pixmap = createPixmap(width, height, color);

        return new Texture(pixmap);
    }

//    /** Only TEMPORARY setup for Tests
//     *
//     * @return : an Array of MiniMap textures
//     */
//    public static Array<Texture> getMMAssets(){
//        Array<Texture> textures = new Array<Texture>();
//
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.LIGHT_GRAY)); //just regular panel
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.YELLOW)); //a selected Unit
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.GREEN)); //ALLY
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.RED)); //ENEMY
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.OLIVE)); //a land obstacle
//        textures.add(AssetHelper.createMMTexture(3, 3, Color.BLUE)); //a water obstacle
//
//
//        return textures;
//    }

    /** Creates a PNG file using Pixmap
     *
     * @param pixmap : pixmap that will be turned into a png
     * @param dir: directory of new file - needs to be ABSOLUTE
     * @param fileName : name of the new file
     */
    public static void pixmapToFile(Pixmap pixmap, String dir, String fileName){

        //first check if file exists
        try{
            FileHandle fhCheck = Gdx.files.absolute(dir + fileName);
            if (!fhCheck.exists()){
                File file = new File(dir, fileName);
                FileHandle fileHandle = new FileHandle(file);

                PixmapIO.writePNG(fileHandle, pixmap);
            }
        }
        catch(Exception e){
            System.out.println("File already exists!");
            e.printStackTrace();
        }
    }

}
