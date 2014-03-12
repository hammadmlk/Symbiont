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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.symbiont.controllers.GameContactListener;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.controllers.GameInputListener;
import com.badlogic.symbiont.controllers.levelEditor.LevelEditor;
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

    public static Skin skin;

    public static boolean debug = false;

    public static boolean edit = false;

    public static LevelEditor levelEditor;

    private GameInputListener gameInputListener = new GameInputListener();

    @Override
    public void create() {

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        gameView = new GameView();
        gameView.setBounds(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table().top().left().padTop(20);
		table.setFillParent(true);
		stage.addActor(table);

		final CheckBox debugCheckBox = new CheckBox("Debug", skin);
        table.add(debugCheckBox);
        final CheckBox editCheckBox = new CheckBox("Edit", skin);
        table.add(editCheckBox);
        final TextButton saveFileButton = new TextButton("Save", skin);
        table.add(saveFileButton);
        final TextButton loadFileButton = new TextButton("Load File:", skin);
        table.add(loadFileButton);
        final TextField levelPath = new TextField(currentLevelFileName, skin);
        table.add(levelPath);
        levelPath.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                currentLevelFileName = textField.getText();
            }
        });

		debugCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                debug = !debug;
            }
        });

        editCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                edit = !edit;
                if (!edit) {
                    if (world != null) {
                        world.dispose();
                    }
                    world = new World(new Vector2(0, -10), true);
                    world.setContactListener(new GameContactListener());
                    gameState = GameState.fromJSON(levelEditor.editorGameState.toJSON());
                    gameState.addToWorld(world);
                    gameView.clearListeners();
                    gameView.addListener(gameInputListener);
                } else {
                    gameView.clearListeners();
                    gameView.addListener(levelEditor);
                }
            }
        });

        loadFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadFile();
            }
        });

        saveFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                FileHandle fileHandle = Gdx.files.local("levels/" + currentLevelFileName + ".json");
                fileHandle.writeString(levelEditor.editorGameState.toJSON(), false);
            }
        });

        loadFile();
    }

    private void loadFile() {
        if (world != null) {
            world.dispose();
        }
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener());

        FileHandle gamestateFile = Gdx.files.internal("levels/" + currentLevelFileName + ".json");
        String rawGameStateJSON = gamestateFile.readString();
        gameState = GameState.fromJSON(rawGameStateJSON);
        gameState.addToWorld(world);

        if (levelEditor != null) {
            levelEditor.dispose();
        }
        levelEditor = new LevelEditor(GameState.fromJSON(gameState.toJSON()));
        if (edit) {
            gameView.clearListeners();
            gameView.addListener(levelEditor);
        } else {
            gameView.clearListeners();
            gameView.addListener(gameInputListener);
        }
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

        float delta = 1/60f;

        // step physics engine
        GameEngine.step(gameState, world, delta);

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
