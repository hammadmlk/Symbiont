package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;

public class EnergyBarView {

	private static final int barWidth = GameConstants.VIRTUAL_WIDTH/2;
	private static final int barHeight = 20;
	
	//the energy bar region
    private final Rectangle region = new Rectangle(
    		GameConstants.VIRTUAL_WIDTH/2 - barWidth/2, 
    		GameConstants.VIRTUAL_HEIGHT - barHeight, 
    		barWidth, 
    		barHeight);

    private final TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("energybar");

    /**
     * Draws the energy bar and the glowing particles on right corner
     * @param batch
     * @param energyFraction 0 <= energyFraction <= 1
     */
    public void render(SpriteBatch batch, GameState gameState) {
        float energyFraction = gameState.currentEnergy / gameState.totalEnergy;
        assert (0 <= energyFraction && energyFraction <= 1);
        //draw the bar
        if (gameState.currentEnergy > 0) {
            batch.draw(
                    atlasRegion,
                    region.x,
                    region.y,
                    region.width* energyFraction,
                    region.height);
        }
        
        //draw the glowing particle on the right corner of the energy bar
        gameState.energyBarParticleEffect.setPosition(
        		region.x + region.width*energyFraction ,  // 
        		region.y+region.height/2);
        gameState.energyBarParticleEffect.draw(batch);
    }

}
