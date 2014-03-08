package com.badlogic.symbiont.controllers;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class GameContactListener implements ContactListener {
    PhysicsEntityModel alien;
    PhysicsEntityModel other;
    boolean deflected;

    @Override
    public void beginContact(Contact contact) {
    	deflected = false;
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        PhysicsEntityModel a = (PhysicsEntityModel) fixtureA.getBody().getUserData();
        PhysicsEntityModel b = (PhysicsEntityModel) fixtureB.getBody().getUserData();

        assert (a != null && b != null);

        if (a.entityType == PhysicsEntityModel.Type.ALIEN) {
            handleAlienContact(a, b);
        } else if (b.entityType == PhysicsEntityModel.Type.ALIEN) {
            handleAlienContact(b, a);
        } else if (a.entityType == PhysicsEntityModel.Type.BROKEN) {
            a.toBeDestroyed = true;
        } else if (b.entityType == PhysicsEntityModel.Type.BROKEN) {
            b.toBeDestroyed = true;
        }

    }

    private void handleAlienContact(PhysicsEntityModel alien, PhysicsEntityModel other) {
        if (other.breakable) {
            other.entityType = PhysicsEntityModel.Type.BROKEN;
        }
        if (other.entityType == PhysicsEntityModel.Type.PLANT) {
            other.toBeDestroyed = true;
        }
        if (other.entityType == PhysicsEntityModel.Type.GROUND) {
            SymbiontMain.gameState.state = GameState.State.LOST;
        }
        if (other.entityType == PhysicsEntityModel.Type.DEFLECTOR) {
        	deflected = true;
        }
        
    }

    @Override
    public void endContact(Contact contact) {
    	if (deflected) {
    		deflected = false;
    		
    		// TODO this is a constant and should be stored somewhere else
    		float desiredVel = 15;
    		Vector2 vel = alien.body.getLinearVelocity();
    		
    		// Only add impulse if the alien isn't moving fast enough
    		if (vel.len() > desiredVel) {
    			return;
    		}
    		float velChange = desiredVel - vel.len();
    		float impulse = alien.body.getMass() * velChange;
    		
    		float xdiff = SymbiontMain.gameState.deflectorEndpoints[1].x
					- SymbiontMain.gameState.deflectorEndpoints[0].x;
			float ydiff = SymbiontMain.gameState.deflectorEndpoints[1].y
					- SymbiontMain.gameState.deflectorEndpoints[0].y;
			
			Vector2 impulseDir = new Vector2(ydiff, -xdiff);
			impulseDir.nor();
    		impulseDir.scl(impulse);
    		if (impulseDir.y < 0) {
    			impulseDir.scl(-1);
    		}
    		
    		alien.body.applyLinearImpulse(impulseDir, alien.body.getWorldCenter(), true);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
