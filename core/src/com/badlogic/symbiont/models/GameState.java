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

    public boolean deflector(){
    	if(!touches[0].touched || !touches[1].touched){
            return false;
        }
        for(MistModel mistModel : mistModels){
            if(mistModel.contains(touches[0].x, touches[0].y)){
                return false;
            }
            if(mistModel.contains(touches[1].x, touches[1].y)){
                return false;
            }
        }
    	return true;
    }

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

    public boolean started = false;

    public GameState() {
        for (int i = 0; i < 2; i++) {
            touches[i] = new TouchInfo();
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
