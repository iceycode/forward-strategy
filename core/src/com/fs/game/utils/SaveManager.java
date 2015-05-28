package com.fs.game.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.fs.game.constants.Constants;

/** Manager for game saves & user preferences
 * - stores game saveData as JSON
 *
 * TODO: finish save manager
 * Created by Allen on 5/10/15.
 */
public class SaveManager {

    private static SaveManager instance;

    private String saveName; //where Save JSON is located
    private String prefName;
    private Save save; //save object which stores save info
    private FileHandle saveFile;

    private boolean encoded;

    Preferences prefs; //preferences for game

    public SaveManager(){
        this.saveFile = Gdx.files.local(saveName);
        this.save = getSave();

        prefs = Gdx.app.getPreferences(prefName);
    }

    public Save getSave(){
        Save save = new Save();

        if (saveFile.exists()) {
            Json json = new Json();

            if (encoded)
                save = json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString()));
            else
                save = json.fromJson(Save.class, saveFile.readString());
        }

        if (save == null)
            return new Save();

        return save;
    }


    /** sets the names of where preferences are stored based on app type
     *  also sets save file based on whether encoding is enabled or not
     */
    protected void getSaveSettings(){
        Application.ApplicationType appType = Gdx.app.getType();//get application type

        //sets the preferences name based on ApplicationType enum
        if (appType == Application.ApplicationType.Android)
            prefName = Constants.SETTINGS_Android;
        else if (appType == Application.ApplicationType.WebGL)
            prefName = Constants.SETTINGS_Web;
        else if (appType == Application.ApplicationType.iOS)
            prefName = Constants.SETTINGS_iOS;
        else
            prefName = Constants.SETTINGS_Desktop;


        if (encoded)
            saveName = Constants.SAVE_ENCODED;
        else
            saveName = Constants.SAVE;
    }


    //saves locally to a JSON file, either encoded or not
    public void saveToJson(){
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        String jsonString; //String that will go into JSON doc

        if (encoded)
            jsonString = Base64Coder.encodeString(json.prettyPrint(save));
        else
            jsonString = json.prettyPrint(save);

        //saves to local JSON file
        saveFile.writeString(jsonString, false);

        log(json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString())).toString());
    }


    //---Save data----
    public static class Save{
        ObjectMap<String, Object> data = new ObjectMap<String, Object>();
    }



    private void log(String message){
        Gdx.app.log("SaveManager LOG: ", message);
    }
}
