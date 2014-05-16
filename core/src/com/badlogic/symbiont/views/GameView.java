package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class GameView extends Actor {
    
    private final GameEngine gameEngine;

    private MistView mistView;
    private DeflectorView deflectorView;
    private EnergyBarView energyBarView;

    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    
    public GameView(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        mistView = new MistView();
        deflectorView = new DeflectorView();
        energyBarView = new EnergyBarView();
    }
    
    /**
     * render the game. calls the other views
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        GameState gameState = gameEngine.edit ? gameEngine.levelEditor.editorGameState : gameEngine.gameState;
        World world = gameEngine.edit ? gameEngine.levelEditor.editorWorld : gameEngine.world;

        // render background
        // disable blending optimization as recommended here
        // https://github.com/libgdx/libgdx/wiki/Spritebatch%2C-textureregions%2C-and-sprite#wiki-blending
        batch.disableBlending();
        batch.draw(gameState.getBackgroundTexture(), 0, 0, getWidth(), getHeight());
        batch.enableBlending();

        // render game state
        for (PhysicsEntityModel entity : gameState.entities) {
            PhysicsEntityView.render(batch, entity);
        }

        mistView.render(batch, gameState);

        if (!gameEngine.edit) {
            deflectorView.render(batch, gameState, gameEngine.elasticDeflector);
        }

        energyBarView.render(batch, gameState);

        // debug render
        if (gameEngine.debug) {
            batch.end();
            debugRenderer.render(world, batch.getProjectionMatrix().cpy().scale(
                    GameConstants.PIXELS_PER_METER,
                    GameConstants.PIXELS_PER_METER,
                    GameConstants.PIXELS_PER_METER
                )
            );

            batch.begin();

            drawTextCenteredBottom(batch, String.format("fps: %d", Gdx.graphics.getFramesPerSecond()));
        }

        if (gameState.state == GameState.State.WAITING_TO_START && gameState.tutorialModel != null) {
            batch.draw(gameState.tutorialModel.getFrame(), 0, 0, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);
        }
        
        //level transition animation
        if (gameState.state == GameState.State.WON) {
            float y = (float) (GameConstants.VIRTUAL_HEIGHT -
                    1.1*(gameState.timeElapsedSinceWon /
                    Assets.getInstance().constantsConfigLoader.winAnimationDuration)
                            * GameConstants.VIRTUAL_HEIGHT);
            
            //ratio of transition asset image
            float ratio = (float)Assets.loadAtlas("leavesImg").originalHeight/(float)Assets.loadAtlas("leavesImg").originalWidth;
            
            //black background
            batch.draw(Assets.loadAtlas("black"), 0, y+GameConstants.VIRTUAL_WIDTH*ratio, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);
            
            batch.draw(Assets.loadAtlas("leavesImg"), 0, y, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_WIDTH*ratio);
            
            
            for (int i = 0; i < 5; i++) {
            	gameState.levelTransitionParticleEffect[i].setPosition((GameConstants.VIRTUAL_WIDTH/5)*i, (y+(GameConstants.VIRTUAL_WIDTH*ratio)/2));
            	gameState.levelTransitionParticleEffect[i].draw(batch);
            }
            
            
        }

        if (gameState.state == GameState.State.WON) {
            drawImageCentered(batch, "youwin");
        } else if (gameState.state == GameState.State.LOST) {
            drawImageCentered(batch, "youlose");
        }
    }

    private void drawTextCenteredBottom(SpriteBatch batch, String text) {
        BitmapFont bitmapFont = gameEngine.skin.getFont("default-font");
        float fontX = GameConstants.VIRTUAL_WIDTH / 2 - bitmapFont.getBounds(text).width/2;
        float fontY = 50;
        bitmapFont.draw(batch, text, fontX, fontY);
    }
    
    /**
     * Draws an image on screen center. Image width is (1/1.5) times screen width. Aspect ratio of
     * image is maintained
     * 
     * @param batch
     * @param imageName
     */
    private void drawImageCentered(SpriteBatch batch, String imageName) {
        TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas(imageName);
        float width = GameConstants.VIRTUAL_WIDTH/1.5f;
        float height = width * atlasRegion.packedHeight/atlasRegion.packedWidth;
        float x = GameConstants.VIRTUAL_WIDTH / 2 - width/2;
        float y = GameConstants.VIRTUAL_HEIGHT / 2 - height/2;
        batch.draw(atlasRegion, x, y, width, height);
    }

    /**
     * This allows us to receive touch events from outside this actor
     */
    @Override
    public Actor hit (float x, float y, boolean touchable) {
        return this;
    }

    public void dispose() {
        mistView.dispose();
        debugRenderer.dispose();
    }
}
