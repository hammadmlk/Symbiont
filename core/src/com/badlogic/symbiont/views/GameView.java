package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.PhysicsEntity;

public class GameView extends Actor {

    private MistView mistView = new MistView();
    private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        // render background
        // disable blending optimization as recommended here
        // https://github.com/libgdx/libgdx/wiki/Spritebatch%2C-textureregions%2C-and-sprite#wiki-blending
        batch.disableBlending();
        batch.draw(SymbiontMain.gameState.getBackgroundTexture(), 0, 0, getWidth(), getHeight());
        batch.enableBlending();

        // render game state
        for (PhysicsEntity entity : SymbiontMain.gameState.entities) {
            PhysicsEntityView.render(batch, entity);
        }

        mistView.render(batch, SymbiontMain.gameState);

        // debug render
        if (SymbiontMain.debug) {
            batch.end();
            debugRenderer.render(SymbiontMain.world, batch.getProjectionMatrix().cpy().scale(
                    SymbiontMain.PIXELS_PER_METER,
                    SymbiontMain.PIXELS_PER_METER,
                    SymbiontMain.PIXELS_PER_METER
                )
            );
            batch.begin();
        }
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
