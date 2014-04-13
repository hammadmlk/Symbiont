package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;

public class EnergyBarView {

    private Rectangle region = new Rectangle(0, 0, 20, GameConstants.VIRTUAL_HEIGHT);

    private TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    public EnergyBarView() {
    }

    /**
     * @param batch
     * @param energyFraction 0 <= energyFraction <= 1
     */
    public void draw(SpriteBatch batch, float energyFraction) {
        assert (0 <= energyFraction && energyFraction <= 1);

        float x1 = region.x + region.width / 2;
        float y1 = region.y;
        float x2 = region.x + region.width / 2;
        float y2 = region.height * energyFraction;

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

        SymbiontMain.gameState.energyBarParticleEffect.setPosition(region.x + region.width / 2, region.height * energyFraction);
        SymbiontMain.gameState.energyBarParticleEffect.draw(batch);
    }

}
