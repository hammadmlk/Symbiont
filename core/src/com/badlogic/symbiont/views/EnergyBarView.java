package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.symbiont.models.GameConstants;

public class EnergyBarView {

    private Rectangle region = new Rectangle(0, 0, 20, GameConstants.VIRTUAL_HEIGHT);

    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public EnergyBarView() {
        shapeRenderer.setColor(Color.BLUE);
    }

    /**
     * @param batch
     * @param energyFraction 0 <= energyFraction <= 1
     */
    public void draw(SpriteBatch batch, float energyFraction) {
        assert (0 <= energyFraction && energyFraction <= 1);

        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.rect(region.x, region.y, region.width, region.height);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(region.x, region.y, region.width, region.height * energyFraction);

        shapeRenderer.end();

        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

}
