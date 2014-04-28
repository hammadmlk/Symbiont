package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.models.DeflectorEndpoint;
import com.badlogic.symbiont.models.GameState;

public class DeflectorView {

    private final TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    /**
     * render the deflector
     * 
     * @param batch
     */
    public void render(SpriteBatch batch, GameState gameState) {
        //push to the batch
        if (gameState.deflector()) {
            float x1 = gameState.deflectorEndpoints[0].x;
            float y1 = gameState.deflectorEndpoints[0].y;
            float x2 = gameState.deflectorEndpoints[1].x;
            float y2 = gameState.deflectorEndpoints[1].y;

            float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

            batch.draw(
                    atlasRegion,                                                         // Texture atlasRegion
                    x1,                                                                  // float x
                    y1 - atlasRegion.originalHeight / 2,                                 // float y
                    0,                                                                   // float originX
                    atlasRegion.originalHeight / 2,                                      // float originY
                    (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)),        // float width
                    atlasRegion.originalHeight,                                          // float height
                    1,                                                                   // float scaleX
                    1,                                                                   // float scaleY
                    angle                                                                // float rotation
            );

            for (DeflectorEndpoint deflectorEndpointInfo : gameState.deflectorEndpoints) {
                deflectorEndpointInfo.getParticleEffect().setPosition(deflectorEndpointInfo.x, deflectorEndpointInfo.y);
                deflectorEndpointInfo.getParticleEffect().draw(batch);
            }
        }
    }

}
