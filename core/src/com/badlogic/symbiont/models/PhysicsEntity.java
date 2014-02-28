package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;

public class PhysicsEntity {
    /**
     * All physics entities must have textures
     */
    public transient Texture texture;
    public String name;
    public float scale = 1;
    public float breakingPoint = -1;
    public enum Type {ALIEN, WALL, PLANT};
    public Type entityType = Type.WALL;
    public boolean toBeDestroyed;

    private Vector2 origin;

    /*
     * BodyDef fields. Can't use a BodyDef because fields are marked final
     */

    /** The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the mass is set to one. **/
    public BodyDef.BodyType type = BodyDef.BodyType.StaticBody;

    /** The world position of the body. Avoid creating bodies at the origin since this can lead to many overlapping shapes. **/
    public Vector2 position = new Vector2();

    /** The world angle of the body in radians. **/
    public float angle = 0;

    /** The linear velocity of the body's origin in world co-ordinates. **/
    public Vector2 linearVelocity = new Vector2();

    /** The angular velocity of the body. **/
    public float angularVelocity = 0;

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

    /** Does this body start out active? **/
    public boolean active = true;

    /** Scale the gravity applied to this body. **/
    public float gravityScale = 1;

    /**
     * Don't want to serialize this cyclic reference, so mark it transient
     */
    public transient Body body;

    /*
     * Here's all the fixtureDef stuff, we'll apply it to every fixture
     * in this physics entity
     */

    /** The friction coefficient, usually in the range [0,1]. **/
    public float friction = 0.2f;

    /** The restitution (elasticity) usually in the range [0,1]. **/
    public float restitution = 0;

    /** The density, usually in kg/m^2. **/
    public float density = 0;

    /** A sensor shape collects contact information but never generates a collision response. */
    public boolean isSensor = false;

    /**
     * use this in the game loop to keep position, linearVelocity, angle, angularVelocity up to date
     * @param body
     */
    public void update(Body body) {
        position = body.getPosition();
        linearVelocity = body.getLinearVelocity();
        angle = body.getAngle();
        angularVelocity = body.getAngularVelocity();
    }

    public void addToWorld(World world) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        bodyDef.position.set(position.x, position.y);
        bodyDef.type = type;
        bodyDef.linearVelocity.set(linearVelocity.x, linearVelocity.y);
        bodyDef.linearDamping = linearDamping;
        bodyDef.active = active;
        bodyDef.allowSleep = allowSleep;
        bodyDef.angle = angle;
        bodyDef.angularDamping = angularDamping;
        bodyDef.angularVelocity = angularVelocity;
        bodyDef.awake = awake;
        bodyDef.bullet = bullet;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.gravityScale = gravityScale;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        this.body = body;

        Assets.physicsLoader.attach(body, name, fixtureDef, scale, getImg().getWidth());
    }

    public Texture getImg() {
        if (texture != null) {
            return texture;
        }
        String imgPath = Assets.physicsLoader.getRigidBody(name).imagePath;
        texture = Assets.load(imgPath);
        assert texture != null;
        return texture;
    }

    public Vector2 getOrigin() {
        if (origin != null)
            return origin;
        float combinedScale = scale * getImg().getWidth() / SymbiontMain.PIXELS_PER_METER;
        Vector2 unscaledOrigin = Assets.physicsLoader.getRigidBody(name).origin;
        origin = new Vector2(unscaledOrigin.x, unscaledOrigin.y).scl(combinedScale);
        return origin;
    }
}
