package com.fs.game.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.fs.game.units.Unit;

//TODO: put Unit Data in here

/** class for storing user data from json
 *
 * Created by Allen on 11/26/14.
 *
 */
public class UserData implements Json.Serializable{

    private int score;
    private int player;
    private String name;
    private Array<UnitData> unitList;

    public UserData(int player, int score, String name){
        this.setPlayer(player);
        this.setScore(score);
        this.setName(name);

    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitList(Array<UnitData> unitList) {
        this.unitList = unitList;
    }


    public int getScore() {
        return score;
    }

    public int getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public Array<UnitData> getUnitList(){
        return unitList;
    }

    @Override
    public void write(Json json) {
        json.setIgnoreUnknownFields(true);

        json.writeValue("score", score);
        json.writeValue("player", player);
        json.writeValue("name", name);
        json.writeValue("unitList", unitList);
//        json.writeValue("stage", stage);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.setIgnoreUnknownFields(true);

        score = json.readValue("score", Integer.class, jsonData);
        player = json.readValue("player", Integer.class, jsonData);
        name = json.readValue("name", String.class, jsonData);
        unitList = json.readValue("unitList", Array.class, Unit.class, jsonData);
//        stage = json.readValue("stage", GameStage.class, jsonData);
    }
}
