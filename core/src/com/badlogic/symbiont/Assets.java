package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, Texture> textureDictionary = new HashMap<String, Texture>();

    static {
        Texture.setEnforcePotImages(false);
    }

    public static Texture load(String path) {
        if (textureDictionary.containsKey(path)) {
            return textureDictionary.get(path);
        }
        Texture texture = new Texture(Gdx.files.internal(path));
        textureDictionary.put(path, texture);
        return texture;
    }

}
