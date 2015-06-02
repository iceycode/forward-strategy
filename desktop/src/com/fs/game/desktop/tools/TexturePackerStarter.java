package com.fs.game.desktop.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/** Starts TexturePacker for packing textures
 * - packs mainly for menu UI (buttons, fonts, dialog, window, slider, etc)
 *
 * Created by Allen on 5/5/15.
 */
public class TexturePackerStarter {

    //Commented out but left ...just in case multiple skins need to be packed in future
//    private static String choiceMessage = "  ";
//    private static String errorMsg = "Wrong value entered, try again";

    //input & output directories and packfile name
    private static String inputDir_Menu = "menu/textures";
    private static String outputDir = "skins/";
    private static String packFileName_Menu = "skins/menuSkin";


    //texture packer
    private static TexturePacker.Settings settings;


    public static void main(String[] args) throws Exception{
        settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.Linear;

        System.out.println("Packing menu textures...");
        TexturePacker.process(settings, inputDir_Menu, outputDir, packFileName_Menu);

//        getUserInput(choiceMessage);
    }

//    //get user input
//    protected static void getUserInput(String message){
//        System.out.print(message);
//        Scanner reader = new Scanner(System.in);
//        int num = reader.nextInt();
//
//        TexturePacker.process(settings, inputDir, outputDir, packFileName_Menu);
//    }
}
