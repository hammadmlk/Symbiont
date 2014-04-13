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
	
	//the energy bar region
    private Rectangle region = new Rectangle(
    		GameConstants.VIRTUAL_WIDTH/2 - barWidth/2, 
    		GameConstants.VIRTUAL_HEIGHT - barHeight, 
    		barWidth, 
    		barHeight);

    private TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    public EnergyBarView() {
    }

    /**
     * Draws the energy bar and the glowing particles on right corner
     * @param batch
     * @param energyFraction 0 <= energyFraction <= 1
     */
    public void draw(SpriteBatch batch, float energyFraction) {
        assert (0 <= energyFraction && energyFraction <= 1);
        //draw the bar
        batch.draw(
        		atlasRegion, 
        		region.x, 
        		region.y, 
        		region.width* energyFraction,
        		region.height);
        
        //draw the glowing particle on the right corner of the energy bar
        SymbiontMain.gameState.energyBarParticleEffect.setPosition(
        		region.x + region.width*energyFraction ,  // 
        		region.y+region.height/2);
        SymbiontMain.gameState.energyBarParticleEffect.draw(batch);
    }

}
