package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.symbiont.controllers.AlienContactListener;
import com.badlogic.symbiont.controllers.GameInputListener;
import com.badlogic.symbiont.models.*;
import com.badlogic.symbiont.views.GameView;

public class SymbiontMain extends ApplicationAdapter {
    private static final int VIRTUAL_WIDTH = 480;
    private static final int VIRTUAL_HEIGHT = 800;

    private Stage stage;
    private GameView gameView;
    private GameInputListener gameInputListener;

    public static GameState gameState;
    public static World world;

    public static final float PIXELS_PER_METER = 50f;

    @Override
    public void create() {

        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new AlienContactListener());

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        gameView = new GameView();
        gameView.setBounds(0, 0, VIRTUAL_WIDTH / PIXELS_PER_METER, VIRTUAL_HEIGHT / PIXELS_PER_METER);
        gameInputListener = new GameInputListener();
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

        FileHandle gamestateFile = Gdx.files.internal("levels/gamestate.json");
        String rawGameStateJSON = gamestateFile.readString();
        gameState = GameState.fromJSON(rawGameStateJSON);
        gameState.addToWorld(world);
        
        setUpWalls();
    }

    private void setUpWalls() {
        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER / 2, -1));

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(VIRTUAL_WIDTH / PIXELS_PER_METER / 2 + 1, 1);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0f);

        BodyDef topWallDef = new BodyDef();
        topWallDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER / 2, VIRTUAL_HEIGHT / PIXELS_PER_METER + 1));
        Body topWallBody = world.createBody(topWallDef);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(VIRTUAL_WIDTH / PIXELS_PER_METER / 2 + 1, 1f);
        topWallBody.createFixture(topWallBox, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(-1, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2));
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(1f, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2 + 1);
        leftWallBody.createFixture(leftWallBox, 0f);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER + 1, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2));
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(1f, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2 + 1);
        rightWallBody.createFixture(rightWallBox, 1f);
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameView.dispose();
        world.dispose();
        Assets.dispose();
    }

    @Override
    public void render() {
        // clear the window
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set up trampoline
        Body trampolineBody = null;
        if (gameInputListener.touches[0].touched && gameInputListener.touches[1].touched &&
                new Vector2(
                        gameInputListener.touches[1].x - gameInputListener.touches[0].x,
                        gameInputListener.touches[1].y - gameInputListener.touches[0].y
                        ).len() > 1 / PIXELS_PER_METER) {
            trampolineBody = setUpTrampoline();
        }

        // step physics engine
        world.step(1/60f, 6, 2);

        // update game state
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body b : bodies) {
            if (b.getUserData() instanceof PhysicsEntity) {
                PhysicsEntity o = (PhysicsEntity) b.getUserData();
                if (o.toBeDestroyed) {
                    world.destroyBody(b);
                } else {
                    o.update(b);
                }
            }
        }
        gameState.cleanDeadEntities(1 / 60f);

        stage.draw();

        if (trampolineBody != null) {
            tearDownTrampoline(trampolineBody);
        }
    }

    private Body setUpTrampoline() {
        BodyDef trampolineDef = new BodyDef();
        trampolineDef.type = BodyDef.BodyType.StaticBody;
        trampolineDef.position.set(new Vector2(gameInputListener.touches[0].x, gameInputListener.touches[0].y));
        Vector2[] points = new Vector2[4];
        float trampoline_width = 10 / SymbiontMain.PIXELS_PER_METER;
        points[0] = new Vector2(0,0);
        points[1] = new Vector2(gameInputListener.touches[1].x - gameInputListener.touches[0].x, gameInputListener.touches[1].y - gameInputListener.touches[0].y);
        Vector2 normal = new Vector2(-points[1].y, points[1].x);
        normal.nor();
        normal.x *= trampoline_width;
        normal.y *= trampoline_width;
        points[2] = new Vector2(points[1].x + normal.x, points[1].y + normal.y);
        points[3] = normal;
        Body trampolineBody = SymbiontMain.world.createBody(trampolineDef);
        PolygonShape trampolineBox = new PolygonShape();
        trampolineBox.set(points);
        trampolineBody.createFixture(trampolineBox, 0f);
        trampolineBox.dispose();
        return trampolineBody;
    }

    private void tearDownTrampoline(Body trampolineBody) {
        SymbiontMain.world.destroyBody(trampolineBody);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(VIRTUAL_WIDTH / PIXELS_PER_METER, VIRTUAL_HEIGHT / PIXELS_PER_METER, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
