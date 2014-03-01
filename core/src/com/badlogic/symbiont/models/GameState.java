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

    public List<Mist> mists = new ArrayList<Mist>();

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        return json.fromJson(GameState.class, serialized);
    }

    public String toJSON() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        return json.prettyPrint(this);
    }

    public void cleanDeadEntities(float delta) {
        // TODO this might make the gc sad
        List<PhysicsEntity> stillAlive = new ArrayList<PhysicsEntity>();
        for (PhysicsEntity e : entities) {
            if (!e.toBeDestroyed) {
                stillAlive.add(e);
            } else {
                e.cleanUP();
            }
        }
        entities = stillAlive;
        // Also update mist I guess TODO refactor into controller
        List<Mist> stillMisty = new ArrayList<Mist>();
        for (Mist mist : mists) {
            if (mist.fading) {
                mist.secondsLeft -= delta;
                if (mist.secondsLeft > 0) {
                    stillMisty.add(mist);
                } else {
                    mist.getMistEffect().dispose();
                }
            } else {
                stillMisty.add(mist);
            }
        }
        mists = stillMisty;
    }

    /**
     * perform all initialization logic. Also
     * gives plants their mist references
     * @param world
     */
    public void addToWorld(World world) {
        for (PhysicsEntity o : entities) {
            o.addToWorld(world);
            if (o.entityType == PhysicsEntity.Type.PLANT) {
                Plant plant = (Plant) o;
                plant.makeMistReferences(mists);
            }
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
