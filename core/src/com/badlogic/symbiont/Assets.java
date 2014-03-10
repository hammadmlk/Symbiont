package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.symbiont.models.ConstantsConfigLoader;
import com.badlogic.symbiont.models.physicsEditorLoader.Loader;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static Map<String, Texture> textureDictionary = new HashMap<String, Texture>();

    private static Map<String, TextureAtlas.AtlasRegion> atlasRegionMap = new HashMap<String, TextureAtlas.AtlasRegion>();

    private static TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("non-git/textures-packed/pack.atlas"));

    public static final Loader physicsLoader = new Loader("physics.json");

    public static final ConstantsConfigLoader constantsConfigLoader = ConstantsConfigLoader.fromFileFactory("constants.json");

    public static ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = new ParticleEffect();
        FileHandle particleDir = Gdx.files.internal("non-git");
        particleEffect.load(Gdx.files.internal("particles/" + name + ".p"), particleDir);
        return particleEffect;
    }

    static {
        Texture.setEnforcePotImages(false);
    }

    /**
     * loads textures. Should only be used for backgrounds,
     * as they are too big to be texture packed
     * @param path
     * @return
     */
    public static Texture loadTexture(String path) {
        if (textureDictionary.containsKey(path)) {
            return textureDictionary.get(path);
        }
        Texture texture = new Texture(Gdx.files.internal(path));
        textureDictionary.put(path, texture);
        return texture;
    }

    /**
     * loads an AtlasRegion. This lets the spriteBatch
     * batch better, and is preferred
     * @param path
     * @return
     */
    public static TextureAtlas.AtlasRegion loadAtlas(String path) {
        String name = Gdx.files.internal(path).nameWithoutExtension();
        if (atlasRegionMap.containsKey(name)) {
            return atlasRegionMap.get(name);
        }
        TextureAtlas.AtlasRegion atlasRegion = textureAtlas.findRegion(name);
        atlasRegionMap.put(name, atlasRegion);
        return atlasRegion;
    }

    public static void dispose() {
        for (Texture texture : textureDictionary.values()) {
            texture.dispose();
        }
        textureDictionary = new HashMap<String, Texture>();
    }

}
