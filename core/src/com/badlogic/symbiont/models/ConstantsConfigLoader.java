package com.badlogic.symbiont.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class ConstantsConfigLoader {

    HashMap<String, ConstantsConfig> namesToConfigs;

    public String[] listOfLevels;

    public static ConstantsConfigLoader fromFileFactory(String file) {
        FileHandle fileHandle = Gdx.files.internal(file);
        Json json = new Json();
        return json.fromJson(ConstantsConfigLoader.class, fileHandle.readString());
    }

    public ConstantsConfig getConfig(String name) {
        return namesToConfigs.get(name);
    }

}
