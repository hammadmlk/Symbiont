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

    public HashMap<String, AnimationModel> namesToAnimations;

    /**
     * private to force use of factory
     */
    private ConstantsConfigLoader() {}

    /**
     * The only way instances of ConstantsConfigLoader can be
     * instantiated
     * @param file
     * @return
     */
    public static ConstantsConfigLoader fromFileFactory(String file) {
        FileHandle fileHandle = Gdx.files.internal(file);
        Json json = new Json();
        return json.fromJson(ConstantsConfigLoader.class, fileHandle.readString());
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
