package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        PhysicsEntityModel a = (PhysicsEntityModel) fixtureA.getBody().getUserData();
        PhysicsEntityModel b = (PhysicsEntityModel) fixtureB.getBody().getUserData();
        if (a == null || b == null) {
            return;
        }
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
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
