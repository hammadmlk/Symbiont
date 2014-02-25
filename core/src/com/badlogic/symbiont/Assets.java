package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    static {
        Texture.setEnforcePotImages(false);
    }

    public static Texture ballTexture = new Texture(Gdx.files.internal("ball.png"));
    public static Texture backgroundTexture = new Texture(Gdx.files.internal("background.png"));

}
