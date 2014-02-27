package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    public static Map<String, Texture> textureDictionary;

    public static Texture backgroundTexture;

    static {
        Texture.setEnforcePotImages(false);

        FileHandle nongit = Gdx.files.internal("non-git");
        textureDictionary = new HashMap<String, Texture>();

        for (FileHandle file : nongit.list()) {
            String name = file.name();
            if (name.endsWith("png")) {
                textureDictionary.put(name.substring(0, name.lastIndexOf('.')), new Texture(file));
            }
        }

        backgroundTexture = textureDictionary.get("background");
    }

}
