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

        batch.draw(atlasRegion, region.x, region.y, region.width, region.height * energyFraction);

        SymbiontMain.gameState.energyBarParticleEffect.setPosition(region.x + region.width / 2, region.height * energyFraction);
        SymbiontMain.gameState.energyBarParticleEffect.draw(batch);
    }

}
