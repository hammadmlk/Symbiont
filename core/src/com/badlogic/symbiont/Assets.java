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

    protected final Map<String, Texture> textureDictionary = new HashMap<String, Texture>();

    protected final Map<String, TextureAtlas.AtlasRegion> atlasRegionMap = new HashMap<String, TextureAtlas.AtlasRegion>();

    protected final Map<String, AnimationModel> atlasAnimationMap =
            new HashMap<String, AnimationModel>();

    protected final TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("non-git/textures-packed/pack.atlas"));

    public final Loader physicsLoader = new Loader("physics.json");

    public final ConstantsConfigLoader constantsConfigLoader = ConstantsConfigLoader.fromFileFactory("constants.json");
    
    protected Music music;

    protected String currentSong;
    
    protected Map<String, Sound> soundBank = new HashMap<String, Sound>();

    private static Assets instance;

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public static ParticleEffect getParticleEffect(String name) {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/" + name + ".p"), getInstance().textureAtlas);
        return particleEffect;
    }

    /**
     * loads an AtlasRegion. This lets the spriteBatch
     * batch better, and is preferred
     * @param path
     * @return
     */
    public static TextureAtlas.AtlasRegion loadAtlas(String path) {
        String name = Gdx.files.internal(path).nameWithoutExtension();
        Assets assets = getInstance();
        if (assets.atlasRegionMap.containsKey(name)) {
            return assets.atlasRegionMap.get(name);
        }
        TextureAtlas.AtlasRegion atlasRegion = assets.textureAtlas.findRegion(name);
        assets.atlasRegionMap.put(name, atlasRegion);
        return atlasRegion;
    }

    public static AnimationModel loadAnimation(String name) {
        Assets assets = getInstance();

        if (assets.atlasAnimationMap.containsKey(name)) {
            return assets.atlasAnimationMap.get(name);
        }

        AnimationModel animationModel = assets.constantsConfigLoader.animations.get(name);

        animationModel.frames = new TextureAtlas.AtlasRegion[animationModel.numFrames];

        for (int i = 0; i < animationModel.numFrames; i++) {
            TextureAtlas.AtlasRegion frame = assets.textureAtlas.findRegion(name, i);
            // hack so that we don't have to add _0 to the file names
            // for our singleton animations
            if (frame == null && i == 0) {
                frame = assets.textureAtlas.findRegion(name);
            }
            animationModel.frames[i] = frame;
        }

        assets.atlasAnimationMap.put(name, animationModel);

        return animationModel;
    }

    public static void dispose() {
        if (instance != null) {
            instance._dispose();
        }
        instance = null;
    }

    protected void _dispose() {
        for (Texture texture : textureDictionary.values()) {
            texture.dispose();
        }

        if (textureAtlas != null) {
            textureAtlas.dispose();
        }

        if (music != null) {
            music.dispose();
        }
        
        for (Sound sound : soundBank.values()) {
            sound.dispose();
        }
    }
    
    /**
     * Plays a song, specified by it's filename. If a different song is
     * currently playing, it stops that one first. If the same song is already
     * playing, it does nothing.
     * 
     * @param filename
     *            the name of the file (with file extension)
     */
    public static void playSong(String filename) {
        getInstance()._playSong(filename);
    }

    protected void _playSong(String filename) {
        if (music != null && currentSong != null) {
            if (currentSong.equals(filename)) {
                if (!music.isPlaying()) {
                    music.play();
                }
                return;
            } else {
                music.stop();
                music.dispose();
            }
        }
        currentSong = filename;
        music = Gdx.audio.newMusic(Gdx.files.internal("non-git/audio/"+filename));
        music.setVolume(GameConstants.DEFAULT_MUSIC_VOLUME);
        music.setLooping(true);
        music.play();
    }

    public static void pauseSong() {
        Assets assets = getInstance();
        if (assets.music != null && assets.music.isPlaying()) {
            assets.music.pause();
        }
    }
    
    /**
     * Preload and cache sound effects to prevent lag.
     */
    public static void loadSoundEffects() {
        // TODO make this use JSON
        Assets assets = getInstance();
        Sound effect1 = Gdx.audio.newSound(Gdx.files.internal("non-git/audio/bounce1.ogg"));
        assets.soundBank.put("bounce1.ogg", effect1);
        Sound effect2 = Gdx.audio.newSound(Gdx.files.internal("non-git/audio/bounce2.ogg"));
        assets.soundBank.put("bounce2.ogg", effect2);
        Sound effect3 = Gdx.audio.newSound(Gdx.files.internal("non-git/audio/buzz.ogg"));
        assets.soundBank.put("buzz.ogg", effect3);
    }
    
    /**
     * Plays a sound effect, specified by it's filename.
     * @param filename the name of the file (with file extension)
     */
    public static void playEffect(String filename) {
        getInstance().soundBank.get(filename).play();
    }

    private static final Random rand = new Random();

    /**
     * Helper function to play different sounds, quasi-randomly.
     * Maybe should be refactored elsewhere.
     */
    public static void playBounceEffect() {
        if (rand.nextBoolean()) {
            playEffect("bounce1.ogg");
        } else {
            playEffect("bounce2.ogg");
        }
    }
    
    private static long buzzId = -1;
    
    public static void playBuzzEffect() {
        if (buzzId == -1) {
            buzzId = getInstance().soundBank.get("buzz.ogg").loop();
        }
    }
    
    public static void stopBuzzEffect() {
        if (buzzId >= 0) {
            getInstance().soundBank.get("buzz.ogg").stop(buzzId);
            buzzId = -1;
        }
    }

}
