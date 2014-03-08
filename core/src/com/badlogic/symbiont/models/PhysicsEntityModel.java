package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.controllers.CollisionFilters;
import sun.font.PhysicalFont;

public class PhysicsEntityModel {

    /**
     * used to determine what happens in collisions
     */
    public enum Type {ALIEN, WALL, GROUND, PLANT, BRANCH, BROKEN, DEFLECTOR}

    /**
     * All physics entities must have textures
     * unless they're walls or grounds or the deflector, which aren't in
     * the gameState's list of physics entities, so won't get drawn or loaded from JSON
     */
    public transient Texture texture;
    public String name;
    public float scale = 1;
    public boolean breakable = false;
    public Type entityType;
    public boolean flipHorizontal = false;
    public boolean flipVertical = false;

    public transient boolean toBeDestroyed;
    private transient Vector2 origin;

    /*
     * BodyDef fields. Can't use a BodyDef because fields are marked final
     */

    /** The world position of the body. Avoid creating bodies at the origin since this can lead to many overlapping shapes. **/
    public Vector2 position = new Vector2();

    /** The world angle of the body in radians. **/
    public float angle = 0;

    /** The linear velocity of the body's origin in world co-ordinates. **/
    public Vector2 linearVelocity = new Vector2();

    /** The angular velocity of the body. **/
    public float angularVelocity = 0;

    /**
     * Don't want to serialize this cyclic reference, so mark it transient
     */
    public transient Body body;

    /**
     * use this in the game loop to keep position, linearVelocity, angle, angularVelocity up to date
     */
    public void update() {
        if (entityType == Type.BROKEN) {
            body.setType(BodyDef.BodyType.DynamicBody);
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setFilterData(CollisionFilters.BROKEN);
            }

            body.setFixedRotation(false);
        }
        position.set(
                body.getPosition().x * SymbiontMain.PIXELS_PER_METER,
                body.getPosition().y * SymbiontMain.PIXELS_PER_METER
            );
        linearVelocity.set(
                body.getLinearVelocity().x * SymbiontMain.PIXELS_PER_METER,
                body.getLinearVelocity().y * SymbiontMain.PIXELS_PER_METER
        );
        angle = body.getAngle();
        angularVelocity = body.getAngularVelocity();
    }

    public void addToWorld(World world) {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PhysicsConfig physicsConfig = Assets.physicsConfigLoader.getConfig(name);
        bodyDef.position.set(position.x / SymbiontMain.PIXELS_PER_METER, position.y / SymbiontMain.PIXELS_PER_METER);
        bodyDef.type = physicsConfig.type;
        bodyDef.linearVelocity.set(linearVelocity.x / SymbiontMain.PIXELS_PER_METER, linearVelocity.y / SymbiontMain.PIXELS_PER_METER);
        bodyDef.linearDamping = physicsConfig.linearDamping;
        bodyDef.active = physicsConfig.active;
        bodyDef.allowSleep = physicsConfig.allowSleep;
        bodyDef.angle = angle;
        bodyDef.angularDamping = physicsConfig.angularDamping;
        bodyDef.angularVelocity = angularVelocity;
        bodyDef.awake = physicsConfig.awake;
        bodyDef.bullet = physicsConfig.bullet;
        bodyDef.fixedRotation = physicsConfig.fixedRotation;
        bodyDef.gravityScale = physicsConfig.gravityScale;
        fixtureDef.friction = physicsConfig.friction;
        fixtureDef.restitution = physicsConfig.restitution;
        fixtureDef.density = physicsConfig.density;
        fixtureDef.isSensor = physicsConfig.isSensor;

        Body body = world.createBody(bodyDef);
        body.setUserData(this);

        this.body = body;

        Assets.physicsLoader.attach(
                body,
                name,
                fixtureDef,
                scale,
                getImg().getWidth(),
                getImg().getHeight(),
                flipHorizontal,
                flipVertical
        );
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
        float combinedScale = getImg().getWidth();
        Vector2 unscaledOrigin = Assets.physicsLoader.getRigidBody(name).origin;
        origin = new Vector2(unscaledOrigin.x, unscaledOrigin.y).scl(combinedScale);
        return origin;
    }

    /**
     * use this to clean things up before dying
     */
    public void cleanUP() {

    }

    /**
     * cache an instance with entityType = DEFLECTOR. We'll only ever need one
     */
    private static PhysicsEntityModel DEFLECTOR_INSTANCE;

    static {
        DEFLECTOR_INSTANCE = new PhysicsEntityModel();
        DEFLECTOR_INSTANCE.entityType = Type.DEFLECTOR;
    }

    public static PhysicsEntityModel getDeflectorInstance() {
        return DEFLECTOR_INSTANCE;
    }
}
