package com.badlogic.symbiont.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;

public class PhysicsConfigLoader {

    HashMap<String, PhysicsConfig> namesToConfigs;

    public static PhysicsConfigLoader fromFileFactory(String file) {
        FileHandle fileHandle = Gdx.files.internal(file);
        Json json = new Json();
        PhysicsConfigLoader physicsConfigLoader = json.fromJson(PhysicsConfigLoader.class, fileHandle.readString());
        return physicsConfigLoader;
    }

    public PhysicsConfig getConfig(String name) {
        return namesToConfigs.get(name);
    }

}
