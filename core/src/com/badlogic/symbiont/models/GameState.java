package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.controllers.CollisionFilters;

public class GameState {

    public enum State {WAITING_TO_START, PLAYING, WON, LOST}

    public String backgroundPath;
    public float currentEnergy;
    public transient float totalEnergy;
    public List<PhysicsEntityModel> entities = new ArrayList<PhysicsEntityModel>();
    
    // References for collision handling (alien and other are also in entities)
    public transient PhysicsEntityModel alien;
    public transient boolean deflected;
    public transient boolean spedUp;

    /*
     * private/transient (don't get serialized)
     */
    private transient Texture backgroundTexture;
    public transient List<MistModel> mistModels = new ArrayList<MistModel>();
    public transient DeflectorEndpoint[] deflectorEndpoints = new DeflectorEndpoint[2];

    public transient State state = State.WAITING_TO_START;

    public void startIfWaiting() {
        if (SymbiontMain.gameState.state == GameState.State.WAITING_TO_START) {
            SymbiontMain.gameState.state = GameState.State.PLAYING;
        }
    }

    public void setDeflectorEndpoint(float x, float y, int pointer) {
        if (x < 0 || x > GameConstants.VIRTUAL_WIDTH || y < 0 || y > GameConstants.VIRTUAL_HEIGHT)
            return;
        for(MistModel mistModel : mistModels){
            if(mistModel.contains(x, y)){
                return;
            }
        }
        deflectorEndpoints[pointer].x = x;
        deflectorEndpoints[pointer].y = y;
        deflectorEndpoints[pointer].active = true;
    }

    public boolean deflector() {
        return deflectorEndpoints[0].active && deflectorEndpoints[1].active;
    }

    public GameState() {
        for (int i = 0; i < 2; i++) {
            deflectorEndpoints[i] = new DeflectorEndpoint();
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
        gameState.totalEnergy = gameState.currentEnergy;
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
        topWallBody.createFixture(topWallBox, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(-halfwidth, GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2));
        Body leftWallBody = world.createBody(leftWallDef);
        leftWallBody.setUserData(wallPhysicsEntityModel);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(halfwidth, GameConstants.VIRTUAL_HEIGHT / GameConstants.PIXELS_PER_METER / 2 - groundHeight);
        leftWallBody.createFixture(leftWallBox, 0f);

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
        rightWallBody.createFixture(rightWallBox, halfwidth);
    }

    public Texture getBackgroundTexture() {
        if (backgroundTexture != null) {
            return backgroundTexture;
        }
        backgroundTexture = Assets.loadTexture(backgroundPath);
        return backgroundTexture;
    }
}
