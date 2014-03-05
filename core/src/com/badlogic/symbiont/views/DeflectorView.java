package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameState;

public class DeflectorView {

    private Texture texture = Assets.load("non-git/deflector.png");

    public void render(SpriteBatch batch) {
        //push to the batch
        if (SymbiontMain.gameState.deflector()) {
            float x1 = SymbiontMain.gameState.deflectorEndpoints[0].x;
            float y1 = SymbiontMain.gameState.deflectorEndpoints[0].y;
            float x2 = SymbiontMain.gameState.deflectorEndpoints[1].x;
            float y2 = SymbiontMain.gameState.deflectorEndpoints[1].y;

            float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));

            batch.draw(
                    texture,                                                      // Texture texture
                    x1,                                                           // float x
                    y1 - texture.getHeight() / 2,                                 // float y
                    0,                                                            // float originX
                    texture.getHeight() / 2,                                      // float originY
                    (float) Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2)), // float width
                    texture.getHeight(),                                          // float height
                    1,                                                            // float scaleX
                    1,                                                            // float scaleY
                    angle,                                                        // float rotation
                    0,                                                            // int srcX
                    0,                                                            // int srcY
                    texture.getWidth(),                                           // int srcWidth
                    texture.getHeight(),                                          // srcHeight
                    false,                                                        // boolean flipX
                    false                                                         // boolean flipY
            );

            for (GameState.deflectorEndpointInfo deflectorEndpointInfo : SymbiontMain.gameState.deflectorEndpoints) {
                deflectorEndpointInfo.getParticleEffect().setPosition(deflectorEndpointInfo.x, deflectorEndpointInfo.y);
                deflectorEndpointInfo.getParticleEffect().draw(batch, 1 / 60f);
            }
        }
    }

    public void dispose() {
        texture.dispose();
    }
}
