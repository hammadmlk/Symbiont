package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;

public class EnergyBarView {
	
	private static int barWidth = GameConstants.VIRTUAL_WIDTH/2;
	private static int barHeight = 20;
	private static int barStartX = GameConstants.VIRTUAL_WIDTH/2 - barWidth/2;
	private static int barStartY = GameConstants.VIRTUAL_HEIGHT - barHeight;
			
    private Rectangle region = new Rectangle(
    		barStartX, 
    		barStartY, 
    		barWidth, 
    		barHeight);

    private TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    public EnergyBarView() {
    }

    /**
     * @param batch
     * @param energyFraction 0 <= energyFraction <= 1
     */
    public void draw(SpriteBatch batch, float energyFraction) {
        assert (0 <= energyFraction && energyFraction <= 1);
        
        batch.draw(
        		atlasRegion, 
        		region.x, 
        		region.y, 
        		region.width* energyFraction, 
        		region.height);

        SymbiontMain.gameState.energyBarParticleEffect.setPosition(
        		region.x + region.width*energyFraction ,  // 
        		region.y);
        SymbiontMain.gameState.energyBarParticleEffect.draw(batch);
    }

}
