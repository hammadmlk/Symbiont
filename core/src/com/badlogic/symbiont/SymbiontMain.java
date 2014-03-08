package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.symbiont.controllers.AlienContactListener;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.controllers.GameInputListener;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.views.GameView;

public class SymbiontMain extends ApplicationAdapter {
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 800;

    private Stage stage;
    private GameView gameView;

    public static GameState gameState;
    private String currentLevelFileName = "second";
    public static World world;

    public static final float PIXELS_PER_METER = 50;

    public static boolean EMIT_POINTS = false;

    public static Skin skin;

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
    }


    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        gameView.dispose();
        world.dispose();
        GameEngine.dispose();
        Assets.dispose();
    }

    @Override
    public void render() {
        // clear the window
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set up deflector
        Body deflectorBody = null;
        if (gameState.deflectorEndpoints[0].active && gameState.deflectorEndpoints[1].active &&
                Util.distance(
                        gameState.deflectorEndpoints[1].x,
                        gameState.deflectorEndpoints[1].y,
                        gameState.deflectorEndpoints[0].x,
                        gameState.deflectorEndpoints[0].y
                ) > 1) {
            deflectorBody = GameEngine.setUpDeflector();
        }

        // step physics engine
        GameEngine.step(gameState, world, 1/60f);

        stage.draw();

        if (deflectorBody != null) {
            GameEngine.tearDownDeflector(deflectorBody);
        }
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
