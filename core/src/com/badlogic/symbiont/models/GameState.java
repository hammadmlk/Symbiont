package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.Util;
import com.badlogic.symbiont.controllers.CollisionFilters;

public class GameState {

    public enum State {WAITING_TO_START, PLAYING, WON, LOST}

    public String backgroundPath;
    public float currentEnergy;
    public float totalEnergy;
    public List<PhysicsEntityModel> entities = new ArrayList<PhysicsEntityModel>();
    
    //deflector beam
    public float deflectorTime = 0;
    
    // References for collision handling (alien and other are also in entities)
    public transient PhysicsEntityModel alien;
    public transient boolean deflected;
    public transient boolean spedUp;

    /*
     * private/transient (don't get serialized)
     */
    private transient TextureAtlas.AtlasRegion backgroundTexture;
    public transient List<MistModel> mistModels = new ArrayList<MistModel>();
    public transient DeflectorEndpoint[] deflectorEndpoints = new DeflectorEndpoint[2];
    public transient ParticleEffect energyBarParticleEffect = Assets.getParticleEffect("energybar");
    public transient ParticleEffect[] levelTransitionParticleEffect = new ParticleEffect[5];
    
    
    public State state = State.WAITING_TO_START;

    public TutorialModel tutorialModel;

    public void setDeflectorEndpoint(float x, float y, int pointer) {
        if (currentEnergy <= 0
                || x < 0 || x > GameConstants.VIRTUAL_WIDTH
                || y < 0 || y > GameConstants.VIRTUAL_HEIGHT) {
            return;
        }
        for (MistModel mistModel : mistModels) {
            if (mistModel.contains(x, y) && !mistModel.fading) {
                return;
            }
        }
        deflectorEndpoints[pointer].x = x;
        deflectorEndpoints[pointer].y = y;
        deflectorEndpoints[pointer].active = true;
    }

    /**
     * @return Whether the deflector is active or not
     */
    public boolean deflector() {
        return deflectorEndpoints[0].active && deflectorEndpoints[1].active;
    }
    
    /**
     * @return The length of the deflector
     */
    public float getDeflectorLength() {
        return Util.distance(deflectorEndpoints[1].x, deflectorEndpoints[1].y,
                deflectorEndpoints[0].x, deflectorEndpoints[0].y);
    }

    public GameState() {
        for (int i = 0; i < 2; i++) {
            deflectorEndpoints[i] = new DeflectorEndpoint();
        }
        for (int i = 0; i < 5; i++) {
        	levelTransitionParticleEffect[i] = Assets.getParticleEffect("leaves2");  
        } 
    }

    public static GameState fromJSON(String serialized) {
        Json json = new Json();
        GameState gameState = json.fromJson(GameState.class, serialized);
        for (PhysicsEntityModel o : gameState.entities) {
            if (o.entityType == PhysicsEntityModel.Type.PLANT) {
                PlantModel plantModel = (PlantModel) o;
                gameState.mistModels.addAll(plantModel.mistModels);
            }
        }
        return gameState;
    }

    public String toJSON() {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        return json.prettyPrint(this);
    }

    public void addToWorld(World world) {
        for (PhysicsEntityModel o : entities) {
            o.addToWorld(world);
        }
        setUpWalls(world);
    }

    private void setUpWalls(World world) {
        float halfwidth = 50 / GameConstants.PIXELS_PER_METER;

        float groundHeight = - halfwidth - GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2;

        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(
                GameConstants.VIRTUAL_WIDTH / GameConstants.PIXELS_PER_METER / 2,
                groundHeight
                )
            );

        PhysicsEntityModel wallPhysicsEntityModel = new PhysicsEntityModel();
        wallPhysicsEntityModel.breakable = false;
        wallPhysicsEntityModel.entityType = PhysicsEntityModel.Type.WALL;

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);
        PhysicsEntityModel groundPhysicsEntityModel = new PhysicsEntityModel();
        groundPhysicsEntityModel.entityType = PhysicsEntityModel.Type.GROUND;
        groundPhysicsEntityModel.breakable = false;
        groundBody.setUserData(groundPhysicsEntityModel);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(GameConstants.VIRTUAL_WIDTH / GameConstants.PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        // Create a fixture from our polygon shape and add it to our ground body
        Fixture groundFixture = groundBody.createFixture(groundBox, 0f);
        groundFixture.setFilterData(CollisionFilters.GROUND);

        BodyDef topWallDef = new BodyDef();
        topWallDef.position.set(new Vector2(
                    GameConstants.VIRTUAL_WIDTH / GameConstants.PIXELS_PER_METER / 2,
                    GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER + halfwidth
                )
            );
        Body topWallBody = world.createBody(topWallDef);
        topWallBody.setUserData(wallPhysicsEntityModel);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(GameConstants.VIRTUAL_WIDTH / GameConstants.PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        Fixture topFixture = topWallBody.createFixture(topWallBox, 0f);
        topFixture.setRestitution(1);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(-halfwidth, GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2));
        Body leftWallBody = world.createBody(leftWallDef);
        leftWallBody.setUserData(wallPhysicsEntityModel);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(halfwidth, GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2 - groundHeight);
        Fixture leftFixture = leftWallBody.createFixture(leftWallBox, 0f);
        leftFixture.setRestitution(1);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(
                    GameConstants.VIRTUAL_WIDTH / GameConstants.PIXELS_PER_METER + halfwidth,
                    GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2
                )
            );
        Body rightWallBody = world.createBody(rightWallDef);
        rightWallBody.setUserData(wallPhysicsEntityModel);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(halfwidth, GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2 - groundHeight);
        Fixture rightFixture = rightWallBody.createFixture(rightWallBox, halfwidth);
        rightFixture.setRestitution(1);
    }

    public TextureAtlas.AtlasRegion getBackgroundTexture() {
        if (backgroundTexture != null) {
            return backgroundTexture;
        }
        backgroundTexture = Assets.loadAtlas(backgroundPath);
        return backgroundTexture;
    }
}
