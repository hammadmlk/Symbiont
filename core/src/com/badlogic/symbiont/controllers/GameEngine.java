package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.DeflectorEndpoint;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;
import com.badlogic.symbiont.models.PhysicsEntityModel;

import java.util.Iterator;

public class GameEngine {
    public static void step(GameState gameState, World world, float delta) {
        if (gameState.state == GameState.State.PLAYING) {
            world.step(delta, GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS);

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
                mistModel.update(delta);
                if (mistModel.fading) {
                    mistModel.secondsLeft -= delta;
                    if (mistModel.secondsLeft <= 0) {
                        mistModel.getMistEffect().dispose();
                        mistModelIterator.remove();
                    }
                }
            }

            for (DeflectorEndpoint deflectorEndpoint : gameState.deflectorEndpoints) {
                deflectorEndpoint.update(delta);
            }
            
            // Get deflector width and update energy meter
            if (gameState.deflectorEndpoints[0].active && gameState.deflectorEndpoints[1].active) {
				float xdiff = gameState.deflectorEndpoints[1].x
						- gameState.deflectorEndpoints[0].x;
				float ydiff = gameState.deflectorEndpoints[1].y
						- gameState.deflectorEndpoints[0].y;
				float length = (float) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
				gameState.currentEnergy -= length*GameConstants.DEFLECTOR_ENERGY;
            }

            // Check win conditions
            if (gameState.mistModels.size() == 0) {
                gameState.state = GameState.State.WON;
            } else if (gameState.currentEnergy <= 0) {
            	gameState.state = GameState.State.LOST;
            }
        }
    }

    private static BodyDef deflectorDef = new BodyDef();
    private static Vector2[] deflectorPoints = new Vector2[4];
    private static Vector2 deflectorNormal = new Vector2();
    private static PolygonShape deflectorBox = new PolygonShape();
    static {
        for (int i = 0; i < 4; i++) {
            deflectorPoints[i] = new Vector2();
        }
    }

    public static Body setUpDeflector() {
        if (!SymbiontMain.gameState.deflector()) {
            return null;
        }
        deflectorDef.type = BodyDef.BodyType.StaticBody;
        deflectorDef.position.set(
                SymbiontMain.gameState.deflectorEndpoints[0].x / GameConstants.PIXELS_PER_METER,
                SymbiontMain.gameState.deflectorEndpoints[0].y / GameConstants.PIXELS_PER_METER
        );

        deflectorPoints[0].set(0,0);
        deflectorPoints[1].set(
                (SymbiontMain.gameState.deflectorEndpoints[1].x - SymbiontMain.gameState.deflectorEndpoints[0].x) / GameConstants.PIXELS_PER_METER,
                (SymbiontMain.gameState.deflectorEndpoints[1].y - SymbiontMain.gameState.deflectorEndpoints[0].y) / GameConstants.PIXELS_PER_METER
        );
        deflectorNormal.set(-deflectorPoints[1].y, deflectorPoints[1].x);
        deflectorNormal.nor();
        deflectorNormal.scl(GameConstants.DEFLECTOR_THICKNESS);
        deflectorPoints[2].set(
                deflectorPoints[1].x + deflectorNormal.x,
                deflectorPoints[1].y + deflectorNormal.y
        );
        deflectorPoints[3] = deflectorNormal;

        for (Vector2 point : deflectorPoints) {
            point.set(point.x - deflectorNormal.x / 2, point.y - deflectorNormal.y / 2);
        }
        Body deflectorBody = SymbiontMain.world.createBody(deflectorDef);
        deflectorBody.setUserData(PhysicsEntityModel.getDeflectorInstance());
        deflectorBox.set(deflectorPoints);
        deflectorBody.createFixture(deflectorBox, 0f);
        return deflectorBody;
    }

    public static void dispose() {
        deflectorBox.dispose();
    }

    public static void tearDownDeflector(Body deflectorBody) {
        SymbiontMain.world.destroyBody(deflectorBody);
    }
}
