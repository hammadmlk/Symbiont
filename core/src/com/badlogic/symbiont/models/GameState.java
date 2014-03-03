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

    public List<PhysicsEntityModel> entities = new ArrayList<PhysicsEntityModel>();

    public transient List<MistModel> mistModels = new ArrayList<MistModel>();

    public void setDeflectorEndpoint(float x, float y, int pointer) {
        for(MistModel mistModel : mistModels){
            if(mistModel.contains(x, y)){
                return;
            }
        }
        deflectorEndpoints[pointer].x = x;
        deflectorEndpoints[pointer].y = y;
        deflectorEndpoints[pointer].active = true;
    }

    public boolean deflector() {
        return deflectorEndpoints[0].active && deflectorEndpoints[1].active;
    }

    public class deflectorEndpointInfo {
        public float x;
        public float y;
        public boolean active = false;

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

    public transient deflectorEndpointInfo[] deflectorEndpoints = new deflectorEndpointInfo[2];

    public transient boolean started = false;

    public GameState() {
        for (int i = 0; i < 2; i++) {
            deflectorEndpoints[i] = new deflectorEndpointInfo();
        }
    }

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        GameState gameState = json.fromJson(GameState.class, serialized);
        for (PhysicsEntityModel o : gameState.entities) {
            if (o.entityType == PhysicsEntityModel.Type.PLANT) {
                PlantModel plantModel = (PlantModel) o;
                gameState.mistModels.addAll(plantModel.mistModels);
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
        List<PhysicsEntityModel> stillAlive = new ArrayList<PhysicsEntityModel>();
        for (PhysicsEntityModel e : entities) {
            if (!e.toBeDestroyed) {
                stillAlive.add(e);
            } else {
                e.cleanUP();
            }
        }
        entities = stillAlive;
        // Also update mist I guess TODO refactor into controller
        List<MistModel> stillMisty = new ArrayList<MistModel>();
        for (MistModel mistModel : mistModels) {
            if (mistModel.fading) {
                mistModel.secondsLeft -= delta;
                if (mistModel.secondsLeft > 0) {
                    stillMisty.add(mistModel);
                } else {
                    mistModel.getMistEffect().dispose();
                }
            } else {
                stillMisty.add(mistModel);
            }
        }
        mistModels = stillMisty;
    }

    public void addToWorld(World world) {
        for (PhysicsEntityModel o : entities) {
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
