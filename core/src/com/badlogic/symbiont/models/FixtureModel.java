package com.badlogic.symbiont.models;

import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Can't serialize box2d shapes due to C++ bindings,
 * instead we just make our own shapes/fixtures
 */
public class FixtureModel {
    public ShapeModel shape;

    /** The friction coefficient, usually in the range [0,1]. **/
    public float friction = 0.2f;

    /** The restitution (elasticity) usually in the range [0,1]. **/
    public float restitution = 0;

    /** The density, usually in kg/m^2. **/
    public float density = 0;

    /** A sensor shape collects contact information but never generates a collision response. */
    public boolean isSensor = false;

    /**
     * don't forget to dispose of the shape!
     */
    public FixtureDef getFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape.makeShape();
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;
        return fixtureDef;
    }
}
