package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.symbiont.controllers.GameContactListener;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.controllers.GameInputListener;
import com.badlogic.symbiont.controllers.levelEditor.LevelEditor;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.views.GameView;

public class SymbiontMain extends ApplicationAdapter {

    private Stage stage;
    public static GameView gameView;

    public static GameState gameState;
    public static String currentLevelFileName = "first";
    public static World world;

    public static Skin skin;

    public static boolean debug = false;

    public static boolean edit = false;

    public static LevelEditor levelEditor;

    public static GameInputListener gameInputListener = new GameInputListener();

    @Override
    public void create() {

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        
        gameView = new GameView();
        gameView.setBounds(0, 0, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage.addActor(Menu.createMenu(skin, stage.getGutterHeight()));

        loadFile();
    }

    public static void loadFile() {
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

        float delta = Gdx.graphics.getDeltaTime();

        // step physics engine
        GameEngine.step(gameState, world, delta);

        stage.draw();

        if (deflectorBody != null) {
            GameEngine.tearDownDeflector(deflectorBody);
        }
    }


    @Override
    public void resize(int width, int height) {
        stage.setViewport(GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
