package com.badlogic.symbiont.controllers;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.InGameMenu;
import com.badlogic.symbiont.controllers.levelEditor.LevelEditor;
import com.badlogic.symbiont.models.DeflectorEndpoint;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;
import com.badlogic.symbiont.models.PhysicsEntityModel;
import com.badlogic.symbiont.views.GameView;

public class GameEngine implements Screen {

    private int currentLevelNum = 0;

    public GameState gameState;
    public World world;
    public Skin skin;
    public GameView gameView;
    private Stage stage;
    public GameInputListener gameInputListener;

    /**
     * when true we the deflector is elastic
     */
    public boolean elasticDeflector = false;

    /**
     * when true we draw debug polygons and fps
     */
    public boolean debug = false;

    /**
     * when true the level editor is our main
     */
    public boolean edit = false;

    public LevelEditor levelEditor;

    public GameEngine() {
        initialize();
        loadLevel(0);
    }

    private void initialize() {
        gameInputListener = new GameInputListener(this);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        gameView = new GameView(this);
        gameView.setBounds(0, 0, GameConstants.VIRTUAL_WIDTH,
                GameConstants.VIRTUAL_HEIGHT);
        gameView.addListener(gameInputListener);
        stage.addActor(gameView);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        stage.addActor(new InGameMenu(skin, this));

        // Play background music
        // TODO this needs to change depending on the level or world
        Assets.loadSoundEffects();
        Assets.playSong("song1.wav");
    }
    
    @Override
    public void render(float delta) {
        // clear the window
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // set up deflector
        Body deflectorBody = null;
        if (gameState.deflector() && gameState.getDeflectorLength() > 1) {
            deflectorBody = setUpDeflector();
        }

        // step main game loop
        step(delta);

        stage.draw();

        if (deflectorBody != null) {
            tearDownDeflector(deflectorBody);
        }
    }
    
    /**
     * This is where our main game loop logic is implemented.
     * @param gameState The gameState to update
     * @param world The physics world associated with gameState
     * @param delta Milliseconds passed since last animation frame
     */
    private void step(float delta) {
        if (gameState.state == GameState.State.PLAYING) {
            world.step(delta, GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS);

            // update physics entities
            Iterator<PhysicsEntityModel> physicsEntityModelIterator = gameState.entities.iterator();
            while (physicsEntityModelIterator.hasNext()) {
                PhysicsEntityModel physicsEntityModel = physicsEntityModelIterator.next();
                if (physicsEntityModel.toBeDestroyed) {
                    physicsEntityModel.cleanUP();
                    physicsEntityModelIterator.remove();
                    world.destroyBody(physicsEntityModel.body);
                } else if (physicsEntityModel.toBeShrunk) {
                    physicsEntityModel.toBeShrunk = false;
                    world.destroyBody(physicsEntityModel.body);
                    physicsEntityModel.scale = physicsEntityModel.scale * GameConstants.powerupScale;
                    physicsEntityModel.addToWorld(world);
                } else {
                    physicsEntityModel.update(delta);
                }
            }

            // update mist, and clean up mist. Also checks for win condition
            boolean allFading = true;
            Iterator<MistModel> mistModelIterator = gameState.mistModels.iterator();
            while (mistModelIterator.hasNext()) {
                MistModel mistModel = mistModelIterator.next();
                mistModel.update(delta);
                if (mistModel.fading) {
                    mistModel.secondsLeft -= delta;
                    if (mistModel.secondsLeft <= 0) {
                        mistModel.getMistEffect().dispose();
                        mistModelIterator.remove();
                    }
                } else {
                    allFading = false;
                }
            }

            // update deflector endpoints
            for (DeflectorEndpoint deflectorEndpoint : gameState.deflectorEndpoints) {
                deflectorEndpoint.update(delta);
            }
            
            // Get deflector width and update energy meter
            if (gameState.deflector()) {
                gameState.currentEnergy -= gameState.getDeflectorLength()
                        * GameConstants.DEFLECTOR_ENERGY;
            }
            if (gameState.currentEnergy < 0) {
                gameState.energyBarParticleEffect.allowCompletion();
            }
            gameState.energyBarParticleEffect.update(delta);

            // check if energy depleted
            if (gameState.currentEnergy <= 0) {
                gameState.deflectorEndpoints[0].active = false;
                gameState.deflectorEndpoints[1].active = false;
            }

            // Check win conditions
            if (allFading) {
                gameState.state = GameState.State.WON;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(GameConstants.VIRTUAL_WIDTH,
                GameConstants.VIRTUAL_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(),
                -stage.getGutterHeight(), 0);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        if (gameState.state == GameState.State.PLAYING) {
            gameState.state = GameState.State.WAITING_TO_START;
        }

        Preferences prefs = Gdx.app.getPreferences(GameConstants.PREFERENCES);

        prefs.putInteger("levelNum", currentLevelNum);
        prefs.putString("gameState", gameState.toJSON());
        prefs.putBoolean("debug", debug);
        prefs.putBoolean("edit", edit);

        prefs.flush();
    }

    @Override
    public void resume() {
        Preferences prefs = Gdx.app.getPreferences(GameConstants.PREFERENCES);

        initialize();

        currentLevelNum = prefs.getInteger("levelNum");
        debug = prefs.getBoolean("debug");
        edit = prefs.getBoolean("edit");

        loadGameState(prefs.getString("gameState"));
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        gameView.dispose();
        world.dispose();
        deflectorBox.dispose();
        Assets.dispose();
    }

    private void loadGameState(String gs) {
        if (world != null) {
            world.dispose();
        }
        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener(this));
        gameState = GameState.fromJSON(gs);
        gameState.addToWorld(world);

        if (levelEditor != null) {
            levelEditor.dispose();
        }

        levelEditor = new LevelEditor(GameState.fromJSON(gameState.toJSON()));
    }

    /**
     * Loads a particular level, specified by its number.
     * 
     * @param levelNum
     */
    public void loadLevel(int levelNum) {
        currentLevelNum = levelNum;
        FileHandle gameStateFile = Gdx.files.internal("levels/"
                + getCurrentLevelName() + ".json");
        String rawGameStateJSON = gameStateFile.readString();
        loadGameState(rawGameStateJSON);
    }

    /**
     * Reloads the current level
     */
    public void reloadLevel() {
        loadLevel(currentLevelNum);
    }

    /**
     * @return The number of the current level (begins at 0)
     */
    public int getCurrentLevelNum() {
        return currentLevelNum;
    }

    /**
     * @return The filename of the current level
     */
    public String getCurrentLevelName() {
        return Assets.constantsConfigLoader.listOfLevels[currentLevelNum];
    }

    /**
     * @return The number of total levels in the game
     */
    public int getNumberOfLevels() {
        return Assets.constantsConfigLoader.listOfLevels.length;
    }
    

    private static BodyDef deflectorDef = new BodyDef();
    private static Vector2[] deflectorPoints = new Vector2[4];
    private static Vector2 deflectorNormal = new Vector2();
    private static PolygonShape deflectorBox = new PolygonShape();
    static {
        for (int i = 0; i < 4; i++) {
            deflectorPoints[i] = new Vector2();
        }
    }

    /**
     * set up the deflector in the physics engine. Happens every animation frame
     * @return
     */
    public Body setUpDeflector() {
        if (!gameState.deflector()) {
            return null;
        }
        deflectorDef.type = BodyDef.BodyType.StaticBody;
        
        DeflectorEndpoint defPoint0 = gameState.deflectorEndpoints[0];
        DeflectorEndpoint defPoint1 = gameState.deflectorEndpoints[1];

        if (elasticDeflector) {
            // Move non-elastic deflector physics engine body to an off screen
            // location
            defPoint0 = new DeflectorEndpoint();
            defPoint1 = new DeflectorEndpoint();
            defPoint0.x = 0;
            defPoint0.y = 2200;
            defPoint1.x = GameConstants.VIRTUAL_WIDTH;
            defPoint1.y = 2200;
        }
        
        deflectorDef.position.set (
                defPoint0.x / GameConstants.PIXELS_PER_METER,
                defPoint0.y / GameConstants.PIXELS_PER_METER
        );
        deflectorPoints[0].set(0, 0);
        deflectorPoints[1].set(
        		(defPoint1.x - defPoint0.x) / GameConstants.PIXELS_PER_METER,
        		(defPoint1.y - defPoint0.y) / GameConstants.PIXELS_PER_METER
        );
        deflectorNormal.set(-deflectorPoints[1].y, deflectorPoints[1].x);
        deflectorNormal.nor();
        deflectorNormal.scl(GameConstants.DEFLECTOR_THICKNESS);
        deflectorPoints[2].set(
                deflectorPoints[1].x + deflectorNormal.x,
                deflectorPoints[1].y + deflectorNormal.y
        );
        deflectorPoints[3] = deflectorNormal;

        for (Vector2 point : deflectorPoints) {
            point.set(point.x - deflectorNormal.x / 2, point.y - deflectorNormal.y / 2);
        }
        Body deflectorBody = world.createBody(deflectorDef);
        deflectorBody.setUserData(PhysicsEntityModel.getDeflectorInstance());
        deflectorBox.set(deflectorPoints);
        deflectorBody.createFixture(deflectorBox, 0f);
        return deflectorBody;
    }
    
    /**
     * tear down deflectorBody. Happens every animation frame
     * @param deflectorBody
     */
    public void tearDownDeflector(Body deflectorBody) {
        world.destroyBody(deflectorBody);
    }

}
