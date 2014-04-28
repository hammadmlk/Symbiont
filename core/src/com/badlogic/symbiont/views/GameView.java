package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class GameView extends Actor {

    private MistView mistView = new MistView();
    private DeflectorView deflectorView = new DeflectorView();
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private EnergyBarView energyBarView = new EnergyBarView();

    /**
     * render the game. calls the other views
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        GameState gameState = SymbiontMain.edit ? SymbiontMain.levelEditor.editorGameState : SymbiontMain.gameState;
        World world = SymbiontMain.edit ? SymbiontMain.levelEditor.editorWorld : SymbiontMain.world;


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

        deflectorView.render(batch);

        if (gameState.state == GameState.State.WON) {
            drawImageCentered(batch, "youwin");
        } else if (gameState.state == GameState.State.LOST) {
            drawImageCentered(batch, "youlose");
        }

        energyBarView.draw(batch, gameState.currentEnergy / gameState.totalEnergy);

        // debug render
        if (SymbiontMain.debug) {
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
    }

    private void drawTextCenteredBottom(SpriteBatch batch, String text) {
        BitmapFont bitmapFont = SymbiontMain.skin.getFont("default-font");
        float fontX = GameConstants.VIRTUAL_WIDTH / 2 - bitmapFont.getBounds(text).width/2;
        float fontY = 50;
        bitmapFont.draw(batch, text, fontX, fontY);
    }
    
    //Draws an image on screen center. Image width is (1/1.5) times screen width. 
    // aspect ratio of image maintained
    private void drawImageCentered(SpriteBatch batch, String imageName) {
        TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas(imageName);
        float width = GameConstants.VIRTUAL_WIDTH/1.5f;
        float height = width * atlasRegion.packedHeight/atlasRegion.packedWidth;
        float x = GameConstants.VIRTUAL_WIDTH / 2 - width/2;
        float y = GameConstants.VIRTUAL_HEIGHT / 2 - height/2;
        batch.draw(atlasRegion, x, y, width, height);
    }

    /*
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
