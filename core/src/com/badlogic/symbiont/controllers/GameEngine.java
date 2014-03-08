package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;
import com.badlogic.symbiont.models.PhysicsEntityModel;

import java.util.Iterator;

public class GameEngine {
    public static void step(GameState gameState, World world, float delta) {
        if (gameState.state == GameState.State.PLAYING) {
            world.step(1/60f, 6, 2);

            // update physics entities
            Iterator<PhysicsEntityModel> physicsEntityModelIterator = gameState.entities.iterator();
            while (physicsEntityModelIterator.hasNext()) {
                PhysicsEntityModel physicsEntityModel = physicsEntityModelIterator.next();
                if (physicsEntityModel.toBeDestroyed) {
                    physicsEntityModel.cleanUP();
                    physicsEntityModelIterator.remove();
                    world.destroyBody(physicsEntityModel.body);
                } else {
                    physicsEntityModel.update();
                }
            }

            // update mist, and clean up mist
            Iterator<MistModel> mistModelIterator = gameState.mistModels.iterator();
            while (mistModelIterator.hasNext()) {
                MistModel mistModel = mistModelIterator.next();
                if (mistModel.fading) {
                    mistModel.secondsLeft -= delta;
                    if (mistModel.secondsLeft <= 0) {
                        mistModel.getMistEffect().dispose();
                        mistModelIterator.remove();
                    }
                }
            }

            // Check win conditions
            if (gameState.mistModels.size() == 0) {
                gameState.state = GameState.State.WON;
            }
        }
    }
}
