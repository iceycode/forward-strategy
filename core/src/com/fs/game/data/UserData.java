package com.fs.game.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.fs.game.actors.Unit;

//TODO: put Unit Data in here

/** class for storing user data from json
 *
 * Created by Allen on 11/26/14.
 *
 */
public class UserData implements Json.Serializable{

    private int score;
    private int player;
    private String name; //player's name
    private float playerID; //player's unique identifier
    private String faction; // player's faction
    private Array<UnitData> unitList; //unit data list
    private boolean isPlayerTurn; //players turn if true
    private int updateState;
    private UnitData unitData; //for updating individual units

    public UserData(){

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

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean isTurn) {
        this.isPlayerTurn = isTurn;
    }

    public float getPlayerID() {
        return playerID;
    }

    public void setPlayerID(float playerID) {
        this.playerID = playerID;
    }

    public int getUpdateState() {
        return updateState;
    }

    public void setUpdateState(int updateState) {
        this.updateState = updateState;
    }

    public UnitData getUnitData() {
        return unitData;
    }

    public void setUnitData(UnitData unitData) {
        this.unitData = unitData;
    }

    @Override
    public void write(Json json) {
        json.setIgnoreUnknownFields(true);
        json.addClassTag("userData", UserData.class);

        json.writeValue("score", score);
        json.writeValue("player", player);
        json.writeValue("name", name);
        json.writeValue("unitList", unitList);
        json.writeValue("faction", faction);
        json.writeValue("isPlayerTurn", isPlayerTurn);
        json.writeValue("playerID", playerID);
        json.writeValue("updateState", updateState);
        json.writeValue("unitData", unitData);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.setIgnoreUnknownFields(true);
        json.addClassTag("userData", UserData.class);

        score = json.readValue("score", Integer.class, jsonData);
        player = json.readValue("player", Integer.class, jsonData);
        name = json.readValue("name", String.class, jsonData);
        unitList = json.readValue("unitList", Array.class, Unit.class, jsonData);
        faction = json.readValue("faction", String.class, jsonData);
        isPlayerTurn = json.readValue("isPlayerTurn", Boolean.class, jsonData);
        playerID = json.readValue("playerID", Integer.class, jsonData);
        updateState = json.readValue("updateState", Integer.class, jsonData);
        unitData = json.readValue("unitData", UnitData.class, jsonData);
    }
}
