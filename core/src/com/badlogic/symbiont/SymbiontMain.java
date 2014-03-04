package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

    public static GameState gameState;
    private String currentLevelFileName = "first";
    public static World world;

    public static final float PIXELS_PER_METER = 50;

    Skin skin;

    public static boolean debug = false;

    public static String toggleDebug() {
        if (debug) {
            debug = false;
            return "Enable Debug";
        } else {
            debug = true;
            return "Disable Debug";
        }
    }

    @Override
    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        gameView = new GameView();
        gameView.setBounds(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        GameInputListener gameInputListener = new GameInputListener();
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table().top().left();
		table.setFillParent(true);
		stage.addActor(table);

        toggleDebug();
		final TextButton debugToggleButton = new TextButton(toggleDebug(), skin);
        table.add(debugToggleButton);
        final TextButton loadGameButton = new TextButton("Load Game:", skin);
        table.add(loadGameButton);
        final TextField levelPath = new TextField(currentLevelFileName, skin);
        table.add(levelPath);
        levelPath.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                currentLevelFileName = textField.getText();
            }
        });

		debugToggleButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                debugToggleButton.setText(toggleDebug());
            }
        });

        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGame();
            }
        });

        loadGame();
    }

    private void loadGame() {
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new AlienContactListener());

        FileHandle gamestateFile = Gdx.files.internal("levels/" + currentLevelFileName + ".json");
        String rawGameStateJSON = gamestateFile.readString();
        gameState = GameState.fromJSON(rawGameStateJSON);
        gameState.addToWorld(world);

        setUpWalls();
    }

    private void setUpWalls() {
        float halfwidth = 50 / PIXELS_PER_METER;

        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER / 2, -halfwidth));

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(VIRTUAL_WIDTH / PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0f);

        BodyDef topWallDef = new BodyDef();
        topWallDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER / 2, VIRTUAL_HEIGHT / PIXELS_PER_METER + halfwidth));
        Body topWallBody = world.createBody(topWallDef);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(VIRTUAL_WIDTH / PIXELS_PER_METER / 2 + halfwidth, halfwidth);
        topWallBody.createFixture(topWallBox, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(-halfwidth, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2));
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(halfwidth, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2 + halfwidth);
        leftWallBody.createFixture(leftWallBox, 0f);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(VIRTUAL_WIDTH / PIXELS_PER_METER + halfwidth, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2));
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(halfwidth, VIRTUAL_HEIGHT / PIXELS_PER_METER / 2 + halfwidth);
        rightWallBody.createFixture(rightWallBox, halfwidth);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
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
        if (gameState.touches[0].touched && gameState.touches[1].touched &&
                new Vector2(
                        gameState.touches[1].x - gameState.touches[0].x,
                        gameState.touches[1].y - gameState.touches[0].y
                        ).len() > 1) {
            trampolineBody = setUpTrampoline();
        }

        // step physics engine
        if (gameState.started) {
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
        }

        stage.draw();

        if (trampolineBody != null) {
            tearDownTrampoline(trampolineBody);
        }
    }

    private boolean mistDetection(float x, float y){
    	if(gameState.touches[0].touched && gameState.touches[1].touched){
    		for(Mist mist: gameState.mists){
    			if(mist.contains(x, y)){
    				return true;
    			}	
    		}
    	}
    	return false;
    }
    
    
    private Body setUpTrampoline() {
        boolean flag1= mistDetection(gameState.touches[0].x,gameState.touches[0].y);
        boolean flag2= mistDetection(gameState.touches[1].x,gameState.touches[1].y);

        if (flag1 || flag2) {
            return null;
        }

        BodyDef trampolineDef = new BodyDef();
        trampolineDef.type = BodyDef.BodyType.StaticBody;
        trampolineDef.position.set(new Vector2(
                gameState.touches[0].x,
                gameState.touches[0].y
            ).scl(1 / PIXELS_PER_METER)
        );
        Vector2[] points = new Vector2[4];
        float trampoline_width = 10 / PIXELS_PER_METER;
        points[0] = new Vector2(0,0);
        points[1] = new Vector2(
                gameState.touches[1].x - gameState.touches[0].x,
                gameState.touches[1].y - gameState.touches[0].y
        ).scl(1 / PIXELS_PER_METER);
        Vector2 normal = new Vector2(-points[1].y, points[1].x);
        normal.nor();
        normal.scl(trampoline_width);
        points[2] = new Vector2(
                points[1].x + normal.x,
                points[1].y + normal.y
        );
        points[3] = normal;

        for (Vector2 point : points) {
            point.sub(new Vector2(normal.x, normal.y).scl(.5f));
        }
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
        stage.setViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
