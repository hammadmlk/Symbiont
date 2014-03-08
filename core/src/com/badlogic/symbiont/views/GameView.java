package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class GameView extends Actor {

    private MistView mistView = new MistView();
    private DeflectorView deflectorView = new DeflectorView();
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
        for (PhysicsEntityModel entity : SymbiontMain.gameState.entities) {
            PhysicsEntityView.render(batch, entity);
        }

        mistView.render(batch, SymbiontMain.gameState);

        deflectorView.render(batch);

        if (SymbiontMain.gameState.state == GameState.State.WON) {
            drawTextCentered(batch, "YOU WON!");
        } else if (SymbiontMain.gameState.state == GameState.State.LOST) {
            drawTextCentered(batch, "YOU LOST!");
        }

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

    private void drawTextCentered(SpriteBatch batch, String text) {
        BitmapFont bitmapFont = SymbiontMain.skin.getFont("default-font");
        float fontX = SymbiontMain.VIRTUAL_WIDTH / 2 - bitmapFont.getBounds(text).width/2;
        float fontY = SymbiontMain.VIRTUAL_HEIGHT / 2 - bitmapFont.getBounds(text).height/2;
        bitmapFont.draw(batch, text, fontX, fontY);
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
        deflectorView.dispose();
        debugRenderer.dispose();
    }
}
