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

    @Override
    public void beginContact(Contact contact) {
    	SymbiontMain.gameState.deflected = false; // for sanity
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
        	SymbiontMain.gameState.alien = alien;
        	SymbiontMain.gameState.deflected = true;
        }
        
    }

    @Override
    public void endContact(Contact contact) {
    	if (SymbiontMain.gameState.deflected) {
    		SymbiontMain.gameState.deflected = false;
    		PhysicsEntityModel alien = SymbiontMain.gameState.alien;
    		
    		float xdiff = SymbiontMain.gameState.deflectorEndpoints[1].x
					- SymbiontMain.gameState.deflectorEndpoints[0].x;
			float ydiff = SymbiontMain.gameState.deflectorEndpoints[1].y
					- SymbiontMain.gameState.deflectorEndpoints[0].y;
			
			Vector2 impulseDir = new Vector2(ydiff, -xdiff);
    		
    		// TODO this is a constant and should be stored somewhere else
    		float desiredVel = impulseDir.len()/20;
    		Vector2 vel = alien.body.getLinearVelocity();
    		

    		float velChange = desiredVel - vel.len();
    		float impulse = alien.body.getMass() * velChange;
    		System.out.println("421IMPULSE: "+impulse);
    		if (impulse > 15) {
    			impulse = 15;
    		} else if (impulse < -15) {
    			impulse = -15;
    		}

    		System.out.println("241IMPULSE: "+impulse);
    		
			impulseDir.nor();
    		impulseDir.scl(impulse);
    		
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
