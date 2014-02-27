package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.symbiont.Assets;

public class GameState {

    public String backgroundPath;

    private transient Texture backgroundTexture;

    public List<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        return json.fromJson(GameState.class, serialized);
    }

    public String toJSON() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        return json.prettyPrint(this);
    }

    public void cleanDeadEntities() {
        // TODO this might make the gc sad
        List<PhysicsEntity> stillAlive = new ArrayList<PhysicsEntity>();
        for (PhysicsEntity e : entities) {
            if (!e.toBeDestroyed) {
                stillAlive.add(e);
            }
        }
        entities = stillAlive;
    }

    public void addToWorld(World world) {
        for (PhysicsEntity o : entities) {
            o.addToWorld(world);
        }
    }

    public Texture getBackgroundTexture() {
        if (backgroundTexture != null) {
            return backgroundTexture;
        }
        backgroundTexture = Assets.load(backgroundPath);
        return backgroundTexture;
    }
}
