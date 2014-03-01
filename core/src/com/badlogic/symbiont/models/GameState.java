package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.symbiont.Assets;

public class GameState {

    public String backgroundPath;

    private transient Texture backgroundTexture;

    public List<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();

    public List<Mist> mists = new ArrayList<Mist>();

    public class TouchInfo {
        public float x;
        public float y;
        public boolean touched = false;

        private ParticleEffect particleEffect;

        public ParticleEffect getParticleEffect() {
            if (particleEffect != null) {
                return particleEffect;
            }
            particleEffect = Assets.getParticleEffect("deflector");
            return particleEffect;
        }

        public void resetParticleEffect() {
            getParticleEffect().reset();
        }
    }

    public transient TouchInfo[] touches = new TouchInfo[2];

    public GameState() {
        for (int i = 0; i < 2; i++) {
            touches[i] = new TouchInfo();
        }
    }

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        GameState gameState = json.fromJson(GameState.class, serialized);
        for (PhysicsEntity o : gameState.entities) {
            if (o.entityType == PhysicsEntity.Type.PLANT) {
                Plant plant = (Plant) o;
                plant.makeMistReferences(gameState.mists);
            }
        }
        return gameState;
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
