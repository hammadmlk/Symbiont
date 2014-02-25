package com.badlogic.symbiont.models;

import com.badlogic.gdx.utils.Json;

public class GameState {

    public PhysicsEntity alien;

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        return json.fromJson(GameState.class, serialized);
    }

    public String toJSON() {
        Json json = new Json();
        return json.prettyPrint(this);
    }

}
