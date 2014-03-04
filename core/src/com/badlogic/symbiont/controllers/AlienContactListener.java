package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class AlienContactListener implements ContactListener
{
    PhysicsEntityModel alien;
    PhysicsEntityModel other;

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
            alien = a; other = b;
        } else if (b.entityType == PhysicsEntityModel.Type.ALIEN) {
            alien = b; other = a;
        } else {
            // only handle contact with alien
            return;
        }

        if (other.entityType == PhysicsEntityModel.Type.WALL && other.breakingPoint != -1 &&
                alien.linearVelocity.len() > other.breakingPoint) {
            other.toBeDestroyed = true;
        }
        if (other.entityType == PhysicsEntityModel.Type.PLANT) {
            other.toBeDestroyed = true;
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