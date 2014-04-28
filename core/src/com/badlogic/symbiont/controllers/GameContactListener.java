package com.badlogic.symbiont.controllers;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;
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

    /**
     * We only handle collisions between the alien and other entities at the moment
     * @param alien
     * @param other
     */
    private void handleAlienContact(PhysicsEntityModel alien, PhysicsEntityModel other) {
        if (other.breakable && (alien.linearVelocity.len() / GameConstants.PIXELS_PER_METER) >= 
                (GameConstants.powerupSpeed * 0.8)) {
            other.entityType = PhysicsEntityModel.Type.BROKEN;
        }
        if (other.entityType == PhysicsEntityModel.Type.PLANT) {
            other.toBeDestroyed = true;
            alien.getAnimator().overrideAnimation(Assets.loadAnimation("eating"));
        }
        if (other.entityType == PhysicsEntityModel.Type.GROUND) {
            SymbiontMain.gameState.state = GameState.State.LOST;
        }
        if (other.entityType == PhysicsEntityModel.Type.DEFLECTOR) {
        	//TODO: Do same when deflected by elastic deflector??
        	//deflected = true is used in endcontact function below
            Assets.playBeepEffect();
        	SymbiontMain.gameState.alien = alien;
        	SymbiontMain.gameState.deflected = true;
        }
        if (other.entityType == PhysicsEntityModel.Type.POWERUP_SPEED) {
            other.toBeDestroyed = true;
            SymbiontMain.gameState.alien = alien;
            SymbiontMain.gameState.spedUp = true;
        }
        if (other.entityType == PhysicsEntityModel.Type.POWERUP_SHRINK) {
            other.toBeDestroyed = true;
            alien.toBeShrunk = true;
            SymbiontMain.gameState.alien = alien;
        }
    }

    @Override
    public void endContact(Contact contact) {
    	if (SymbiontMain.gameState.deflected) {
    		//TODO: this block can be removed when elastic deflector is used
    		// If we keep it, the rope length should affect the 
    		// power (in deflectorView), no need to do it here
    		SymbiontMain.gameState.deflected = false;
    		PhysicsEntityModel alien = SymbiontMain.gameState.alien;
    		
    		float xdiff = SymbiontMain.gameState.deflectorEndpoints[1].x
					- SymbiontMain.gameState.deflectorEndpoints[0].x;
			float ydiff = SymbiontMain.gameState.deflectorEndpoints[1].y
					- SymbiontMain.gameState.deflectorEndpoints[0].y;
			
			// Always assume we're trying to bounce it upwards
			// Weird corner case is if alien hits deflector from underneath,
			// it will still bounce upwards
			Vector2 impulseDir = new Vector2(ydiff, -xdiff);
			if (impulseDir.y < 0) {
				impulseDir.scl(-1);
			}
    		
    		float desiredVel = impulseDir.len()*GameConstants.DEFLECTOR_IMPULSE;
    		Vector2 vel = alien.body.getLinearVelocity();

    		float velChange = desiredVel - vel.len();
    		float imp = alien.body.getMass() * velChange;

			impulseDir.nor().scl(imp);
    		alien.body.applyLinearImpulse(impulseDir, alien.body.getWorldCenter(), true);
        }
    	if (SymbiontMain.gameState.spedUp) {
    	    SymbiontMain.gameState.spedUp = false;
    	    PhysicsEntityModel alien = SymbiontMain.gameState.alien;
    	    
            float desiredVel = GameConstants.powerupSpeed;
            Vector2 vel = alien.body.getLinearVelocity();
            
            float velChange = desiredVel - vel.len();
            float impulse = alien.body.getMass() * velChange;
            
            vel.nor();
            vel.scl(impulse);
            
            alien.body.applyLinearImpulse(vel, alien.body.getWorldCenter(), true);	    
    	}
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
