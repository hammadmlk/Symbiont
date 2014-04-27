package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

    public static int currentLevelNum = 0;
    private Stage stage;
    public static GameView gameView;

    public static GameState gameState;
    public static String currentLevelFileName;
    public static World world;

    public static Skin skin;

    /**
     * when true we draw debug polygons and fps
     */
    public static boolean debug = false;

    /**
     * when true the level editor is our main
     */
    public static boolean edit = false;

    public static LevelEditor levelEditor;

    public static GameInputListener gameInputListener;

    @Override
    public void create() {
        initialize();

        currentLevelNum = 0;
        currentLevelFileName = Assets.constantsConfigLoader.listOfLevels[0];
        
        loadFile();
    }

    private void initialize() {
        gameInputListener = new GameInputListener();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        gameView = new GameView();
        gameView.setBounds(0, 0, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage.addActor(Menu.createMenu(skin));
    }

    private static void loadGameState(String gs) {
        if (world != null) {
            world.dispose();
        }
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener());
        gameState = GameState.fromJSON(gs);
        gameState.addToWorld(world);

        levelEditor = new LevelEditor(GameState.fromJSON(gameState.toJSON()));
    }

    /**
     * loads currentLevelFileName
     */
    public static void loadFile() {
        FileHandle gameStateFile = Gdx.files.internal("levels/" + currentLevelFileName + ".json");
        String rawGameStateJSON = gameStateFile.readString();
        loadGameState(rawGameStateJSON);

        if (levelEditor != null) {
            levelEditor.dispose();
        }

        if (edit) {
            gameView.clearListeners();
            gameView.addListener(levelEditor);
        } else {
            gameView.clearListeners();
            gameView.addListener(gameInputListener);
        }
    }

    /**
     * loads currentLevelFileName without clearing listeners. If we called the regular loadFile
     * from where we call this it would crash the game. Not sure if there are any side effects
     * of doing it this way, but it appears to work.
     */
    public static void loadFileKeepListeners() {
        FileHandle gameStateFile = Gdx.files.internal("levels/" + currentLevelFileName + ".json");
        loadGameState(gameStateFile.readString());
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

    /**
     * main game loop. called once per animation frame
     */
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

        // step main game loop
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
        if (gameState.state == GameState.State.PLAYING) {
            gameState.state = GameState.State.WAITING_TO_START;
        }

        Preferences prefs = Gdx.app.getPreferences("state");

        prefs.putInteger("levelNum", currentLevelNum);
        prefs.putString("levelFileName", currentLevelFileName);
        prefs.putString("gameState", gameState.toJSON());
        prefs.putBoolean("debug", debug);
        prefs.putBoolean("edit", edit);

        prefs.flush();
    }

    @Override
    public void resume() {
        Preferences prefs = Gdx.app.getPreferences("state");

        initialize();

        currentLevelFileName = prefs.getString("levelFileName");
        currentLevelNum = prefs.getInteger("levelNum");
        debug = prefs.getBoolean("debug");
        edit = prefs.getBoolean("edit");

        loadGameState(prefs.getString("gameState"));
    }

}
