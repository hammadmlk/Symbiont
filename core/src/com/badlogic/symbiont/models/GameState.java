package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

public class GameState {

    public List<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        return json.fromJson(GameState.class, serialized);
    }

    public String toJSON() {
        Json json = new Json();
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

}
