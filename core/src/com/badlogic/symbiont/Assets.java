package com.badlogic.symbiont;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.symbiont.models.AnimationModel;
import com.badlogic.symbiont.models.ConstantsConfigLoader;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.physicsEditorLoader.Loader;

public class Assets {

    private static Map<String, Texture> textureDictionary = new HashMap<String, Texture>();

    private static Map<String, TextureAtlas.AtlasRegion> atlasRegionMap = new HashMap<String, TextureAtlas.AtlasRegion>();

    private static Map<String, TextureAtlas.AtlasRegion[]> atlasAnimationMap =
            new HashMap<String, TextureAtlas.AtlasRegion[]>();

    private static TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("non-git/textures-packed/pack.atlas"));

    public static final Loader physicsLoader = new Loader("physics.json");

    public static final ConstantsConfigLoader constantsConfigLoader = ConstantsConfigLoader.fromFileFactory("constants.json");
    
    private static Music music;
    
    private static Map<String, Sound> soundBank = new HashMap<String, Sound>();

    public static ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/" + name + ".p"), textureAtlas);
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

    public static TextureAtlas.AtlasRegion[] loadAnimationByName(String name) {
        if (atlasAnimationMap.containsKey(name)) {
            return atlasAnimationMap.get(name);
        }

        AnimationModel animationMode = constantsConfigLoader.namesToAnimations.get(name);

        TextureAtlas.AtlasRegion[] atlasRegions = new TextureAtlas.AtlasRegion[animationMode.numFrames];

        for (int i = 0; i < animationMode.numFrames; i++) {
            atlasRegions[i] = textureAtlas.findRegion(name, i);
        }

        atlasAnimationMap.put(name, atlasRegions);

        return atlasRegions;
    }

    public static void dispose() {
        for (Texture texture : textureDictionary.values()) {
            texture.dispose();
        }
        textureDictionary = new HashMap<String, Texture>();

        if (music != null) {
            music.dispose();
        }
        
        for (Sound sound : soundBank.values()) {
            sound.dispose();
        }
    }
    
    /**
     * Plays a song, specified by it's filename. Stops any currently playing song.
     * @param filename the name of the file (with file extension)
     */
    public static void playSong(String filename) {
        if (music != null) {
            music.stop();
            music.dispose();
        }
        music = Gdx.audio.newMusic(Gdx.files.internal("non-git/audio/"+filename));
        music.setVolume(GameConstants.DEFAULT_MUSIC_VOLUME);
        music.setLooping(true);
        music.play();
    }
    
    /**
     * Preload and cache sound effects to prevent lag.
     */
    public static void loadSoundEffects() {
        // TODO make this use JSON
        Sound effect1 = Gdx.audio.newSound(Gdx.files.internal("non-git/audio/bounce1.ogg"));
        soundBank.put("bounce1.ogg", effect1);
        Sound effect2 = Gdx.audio.newSound(Gdx.files.internal("non-git/audio/bounce2.ogg"));
        soundBank.put("bounce2.ogg", effect2);   
    }
    
    /**
     * Plays a sound effect, specified by it's filename.
     * @param filename the name of the file (with file extension)
     */
    public static void playEffect(String filename) {
        soundBank.get(filename).play();
    }

    private static final Random rand = new Random();

    /**
     * Helper function to play different sounds, quasi-randomly.
     * Maybe should be refactored elsewhere.
     */
    public static void playBeepEffect() {
        if (rand.nextBoolean()) {
            playEffect("bounce1.ogg");
        } else {
            playEffect("bounce2.ogg");
        }
    }

}
