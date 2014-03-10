package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.DeflectorEndpoint;

public class DeflectorView {

    private TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    public void render(SpriteBatch batch) {
        if (SymbiontMain.edit) {
            return;
        }

        //push to the batch
        if (SymbiontMain.gameState.deflector()) {
            float x1 = SymbiontMain.gameState.deflectorEndpoints[0].x;
            float y1 = SymbiontMain.gameState.deflectorEndpoints[0].y;
            float x2 = SymbiontMain.gameState.deflectorEndpoints[1].x;
            float y2 = SymbiontMain.gameState.deflectorEndpoints[1].y;

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

            for (DeflectorEndpoint deflectorEndpointInfo : SymbiontMain.gameState.deflectorEndpoints) {
                deflectorEndpointInfo.getParticleEffect().setPosition(deflectorEndpointInfo.x, deflectorEndpointInfo.y);
                deflectorEndpointInfo.getParticleEffect().draw(batch, 1 / 60f);
            }
        }
    }

}
