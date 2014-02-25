package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.symbiont.Assets;

public class TextureModel implements Json.Serializable {

    public String textureKey;
    public Texture img;

    private String jsonKey = "textureKey";

    @Override
    public void write(Json json) {
        json.writeValue(jsonKey, textureKey);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        assert jsonData.child().name().equals(jsonKey);
        textureKey = jsonData.child().asString();
        assert Assets.textureDictionary.containsKey(textureKey);
        img = Assets.textureDictionary.get(textureKey);
    }
}
