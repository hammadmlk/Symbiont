package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.symbiont.models.PhysicsConfigLoader;
import com.badlogic.symbiont.models.physicsEditorLoader.Loader;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, Texture> textureDictionary = new HashMap<String, Texture>();

    public static final Loader physicsLoader = new Loader("physics.json");

    public static final PhysicsConfigLoader physicsConfigLoader = PhysicsConfigLoader.fromFileFactory("physics_constants.json");

    public static ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = new ParticleEffect();
        FileHandle particleDir = Gdx.files.internal("non-git");
        particleEffect.load(Gdx.files.internal("particles/" + name + ".p"), particleDir);
        return particleEffect;
    }

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

    public static void dispose() {
        for (Texture texture : textureDictionary.values()) {
            texture.dispose();
        }
        textureDictionary = new HashMap<String, Texture>();
    }

}
