package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    public static final Texture ballTexture;
    public static final Texture backgroundTexture;

    public static Map<String, Texture> textureDictionary;

    static {
        Texture.setEnforcePotImages(false);
        ballTexture = new Texture(Gdx.files.internal("non-git/ball.png"));
        backgroundTexture = new Texture(Gdx.files.internal("non-git/background.png"));

        textureDictionary = new HashMap<String, Texture>();

        textureDictionary.put("ball", ballTexture);
        textureDictionary.put("background", backgroundTexture);
    }

}
