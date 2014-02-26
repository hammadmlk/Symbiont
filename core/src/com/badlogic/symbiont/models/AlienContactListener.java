package com.badlogic.symbiont.models;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class AlienContactListener implements ContactListener
{
    PhysicsEntity alien;
    PhysicsEntity other;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        PhysicsEntity a = (PhysicsEntity) fixtureA.getBody().getUserData();
        PhysicsEntity b = (PhysicsEntity) fixtureB.getBody().getUserData();
        if (a == null || b == null) {
            return;
        }
        if (a.entityType == PhysicsEntity.Type.ALIEN) {
            alien = a; other = b;
        } else {
            alien = b; other = a;
        }

        if (other.entityType == PhysicsEntity.Type.WALL && other.breakingPoint != -1 &&
                alien.linearVelocity.len() > other.breakingPoint) {
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