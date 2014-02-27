package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.symbiont.Assets;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEntity {
    public transient Texture texture;
    public String textureName;
    public float scale = 1;
    public float breakingPoint = -1;
    public enum Type {ALIEN, WALL, PLANT};
    public Type entityType;
    public boolean toBeDestroyed;
    public List<FixtureModel> fixtureModels = new ArrayList<FixtureModel>();

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

    /**
     * use this in the game loop to keep position, linearVelocity, angle, angularVelocity up to date
     * @param body
     */
    public void update(Body body) {
        this.body = body;
        position = body.getPosition();
        linearVelocity = body.getLinearVelocity();
        angle = body.getAngle();
        angularVelocity = body.getAngularVelocity();
    }

    public void addToWorld(World world) {
        BodyDef bodyDef = new BodyDef();
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

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        for (FixtureModel fixtureModel : fixtureModels) {
            FixtureDef fixtureDef = fixtureModel.getFixtureDef();
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }
    }

    public Texture getImg() {
        if (textureName == null)
            return null;
        if (texture == null) {
            texture = Assets.textureDictionary.get(textureName);
        }
        return texture;
    }
}
