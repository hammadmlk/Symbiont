package com.badlogic.symbiont.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class ConstantsConfigLoader {

    /**
     * edit in json
     */
    HashMap<String, ConstantsConfig> namesToConfigs;

    /**
     * The list of levels in order, as presented in the menu
     * edit in json
     */
    public String[] listOfLevels;

    /**
     * never access directly! use Assets.loadAnimation
     */
    public HashMap<String, AnimationModel> animations;

    /**
     * private to force use of factory
     */
    private ConstantsConfigLoader() {}

    /**
     * duration of WIN leaf animation
     */
    public float winAnimationDuration;

    /**
     * The only way instances of ConstantsConfigLoader can be
     * instantiated
     * @param file
     * @return
     */
    public static ConstantsConfigLoader fromFileFactory(String file) {
        FileHandle fileHandle = Gdx.files.internal(file);
        Json json = new Json();
        ConstantsConfigLoader constantsConfigLoader = json.fromJson(ConstantsConfigLoader.class, fileHandle.readString());
        return constantsConfigLoader;
    }

    /**
     * get the config associated with name
     * @param name
     * @return
     */
    public ConstantsConfig getConfig(String name) {
        return namesToConfigs.get(name);
    }

}
