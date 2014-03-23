package com.badlogic.symbiont.models;

import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * This model holds all the physics constants for each type of physics entity
 */
public class ConstantsConfig {

    public float scale = 1;

    public PhysicsEntityModel.Type entityType;

    public boolean breakable = false;

    /** The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the mass is set to one. **/
    public BodyDef.BodyType type = BodyDef.BodyType.StaticBody;

    /** Linear damping is use to reduce the linear velocity. The damping parameter can be larger than 1.0f but the damping effect
     * becomes sensitive to the time step when the damping parameter is large. **/
    public float linearDamping = 0;

    /** Angular damping is use to reduce the angular velocity. The damping parameter can be larger than 1.0f but the damping effect
     * becomes sensitive to the time step when the damping parameter is large. **/
    public float angularDamping = 0;

    /** Set this flag to false if this body should never fall asleep. Note that this increases CPU usage. **/
    public boolean allowSleep = true;
    /** Is this body initially awake or sleeping? **/
    public boolean awake = true;

    /** Should this body be prevented from rotating? Useful for characters. **/
    public boolean fixedRotation = false;

    /** Is this a fast moving body that should be prevented from tunneling through other moving bodies? Note that all bodies are
     * prevented from tunneling through kinematic and static bodies. This setting is only considered on dynamic bodies.
     * @warning You should use this flag sparingly since it increases processing time. **/
    public boolean bullet = false;

    /** Scale the gravity applied to this body. **/
    public float gravityScale = 1;

    /** Does this body start out active? **/
    public boolean active = true;

    /** The friction coefficient, usually in the range [0,1]. **/
    public float friction = 0.2f;

    /** The restitution (elasticity) usually in the range [0,1]. **/
    public float restitution = 0;

    /** The density, usually in kg/m^2. **/
    public float density = 0;

    /** A sensor shape collects contact information but never generates a collision response. */
    public boolean isSensor = false;
    
    /** The desired velocity of the alien upon hitting an object to reduce loss of momentum */
    public static float impulseVelocity = 15;
    
    /** The desired velocity of the alien upon consuming a speed powerup */
    public static float powerupSpeed = 25;

}
