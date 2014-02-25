package com.badlogic.symbiont.models;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;

public class GameState {

    public PhysicsEntity alien;

    public PhysicsEntity leftWall;
    public PhysicsEntity rightWall;
    public PhysicsEntity bottomWall;
    public PhysicsEntity topWall;

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        return json.fromJson(GameState.class, serialized);
    }

    public String toJSON() {
        Json json = new Json();
        return json.prettyPrint(this);
    }

    public void addToWorld(World world) {
        alien.addToWorld(world);
        leftWall.addToWorld(world);
        rightWall.addToWorld(world);
        bottomWall.addToWorld(world);
        topWall.addToWorld(world);
    }

}
