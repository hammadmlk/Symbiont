package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;
import com.badlogic.symbiont.models.PhysicsEntityModel;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    public static void step(GameState gameState, World world, float delta) {
        if (gameState.state == GameState.State.PLAYING) {
            world.step(1/60f, 6, 2);

            // update physics entities
            Array<Body> bodies = new Array<Body>();
            world.getBodies(bodies);
            for (Body b : bodies) {
                if (b.getUserData() instanceof PhysicsEntityModel) {
                    PhysicsEntityModel o = (PhysicsEntityModel) b.getUserData();
                    if (o.toBeDestroyed) {
                        world.destroyBody(b);
                    } else {
                        o.update(b);
                    }
                }
            }

            // clean up physics entities TODO this might make the gc sad
            List<PhysicsEntityModel> stillAlive = new ArrayList<PhysicsEntityModel>();
            for (PhysicsEntityModel e : gameState.entities) {
                if (!e.toBeDestroyed) {
                    stillAlive.add(e);
                } else {
                    e.cleanUP();
                }
            }
            gameState.entities = stillAlive;

            // update mist, and clean up mist
            List<MistModel> stillMisty = new ArrayList<MistModel>();
            for (MistModel mistModel : gameState.mistModels) {
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
            gameState.mistModels = stillMisty;

            // Check win conditions
            if (gameState.mistModels.size() == 0) {
                gameState.state = GameState.State.WON;
            }
        }
    }
}
