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
    public List<PhysicsEntityModel> entities = new ArrayList<PhysicsEntityModel>();

    /*
     * private/transient (don't get serialized)
     */
    private Texture backgroundTexture;
    public transient List<MistModel> mistModels = new ArrayList<MistModel>();
    public transient DeflectorEndpoint[] deflectorEndpoints = new DeflectorEndpoint[2];

    public transient State state = State.WAITING_TO_START;

    public void startIfWaiting() {
        if (SymbiontMain.gameState.state == GameState.State.WAITING_TO_START) {
            SymbiontMain.gameState.state = GameState.State.PLAYING;
        }
    }

    public void setDeflectorEndpoint(float x, float y, int pointer) {
        if (x < 0 || x > SymbiontMain.VIRTUAL_WIDTH || y < 0 || y > SymbiontMain.VIRTUAL_HEIGHT)
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
        float halfwidth = 50 / SymbiontMain.PIXELS_PER_METER;

        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(
                SymbiontMain.VIRTUAL_WIDTH / SymbiontMain.PIXELS_PER_METER / 2,
                -halfwidth
                )
            );

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);
        PhysicsEntityModel groundPhysicsEntityModel = new PhysicsEntityModel();
        groundPhysicsEntityModel.entityType = PhysicsEntityModel.Type.GROUND;
        groundBody.setUserData(groundPhysicsEntityModel);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(SymbiontMain.VIRTUAL_WIDTH / SymbiontMain.PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        // Create a fixture from our polygon shape and add it to our ground body
        Fixture groundFixture = groundBody.createFixture(groundBox, 0f);
        groundFixture.setFilterData(CollisionFilters.GROUND);

        BodyDef topWallDef = new BodyDef();
        topWallDef.position.set(new Vector2(
                    SymbiontMain.VIRTUAL_WIDTH / SymbiontMain.PIXELS_PER_METER / 2,
                    SymbiontMain.VIRTUAL_HEIGHT / SymbiontMain.PIXELS_PER_METER + halfwidth
                )
            );
        Body topWallBody = world.createBody(topWallDef);
        PhysicsEntityModel topPhysicsEntityModel = new PhysicsEntityModel();
        topPhysicsEntityModel.entityType = PhysicsEntityModel.Type.WALL;
        topWallBody.setUserData(topPhysicsEntityModel);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(SymbiontMain.VIRTUAL_WIDTH / SymbiontMain.PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        topWallBody.createFixture(topWallBox, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(-halfwidth, SymbiontMain.VIRTUAL_HEIGHT / SymbiontMain.PIXELS_PER_METER / 2));
        Body leftWallBody = world.createBody(leftWallDef);
        PhysicsEntityModel leftPhysicsEntityModel = new PhysicsEntityModel();
        leftPhysicsEntityModel.entityType = PhysicsEntityModel.Type.WALL;
        leftWallBody.setUserData(leftPhysicsEntityModel);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(halfwidth, SymbiontMain.VIRTUAL_HEIGHT / SymbiontMain.PIXELS_PER_METER / 2 + halfwidth);
        leftWallBody.createFixture(leftWallBox, 0f);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(
                    SymbiontMain.VIRTUAL_WIDTH / SymbiontMain.PIXELS_PER_METER + halfwidth,
                    SymbiontMain.VIRTUAL_HEIGHT / SymbiontMain.PIXELS_PER_METER / 2
                )
            );
        Body rightWallBody = world.createBody(rightWallDef);
        PhysicsEntityModel rightPhysicsEntityModel = new PhysicsEntityModel();
        rightPhysicsEntityModel.entityType = PhysicsEntityModel.Type.WALL;
        rightWallBody.setUserData(rightPhysicsEntityModel);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(halfwidth, SymbiontMain.VIRTUAL_HEIGHT / SymbiontMain.PIXELS_PER_METER / 2 + halfwidth);
        rightWallBody.createFixture(rightWallBox, halfwidth);
    }

    public Texture getBackgroundTexture() {
        if (backgroundTexture != null) {
            return backgroundTexture;
        }
        backgroundTexture = Assets.load(backgroundPath);
        return backgroundTexture;
    }
}
