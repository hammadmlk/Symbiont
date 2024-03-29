package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;

/*
    mad props to
    https://gist.github.com/mattdesl/6076849
 */
public class MistView {

	private final ShapeRenderer shapes = new ShapeRenderer();

    private final TextureAtlas.AtlasRegion mistLayer = Assets.loadAtlas("mistlayer");

    /**
     * render all the mist in gamestate
     * @param batch
     * @param gameState
     */
    public void render(SpriteBatch batch, GameState gameState) {
        for (MistModel mistModel : gameState.mistModels) {
            renderMist(batch, mistModel);
        }
    }

    /**
     * render one mist
     * @param batch
     * @param mistModel
     */
	private void renderMist(SpriteBatch batch, MistModel mistModel) {
        batch.end();

        shapes.setProjectionMatrix(batch.getProjectionMatrix());
		//2. clear our depth buffer with 1.0
		Gdx.gl.glClearDepthf(1f);
		Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);

		//3. set the function to LESS
		Gdx.gl.glDepthFunc(GL10.GL_LESS);

		//4. enable depth writing
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);

		//5. Enable depth writing
		Gdx.gl.glDepthMask(true);

        // enable blending: thanks http://stackoverflow.com/questions/14700577/drawing-transparent-shaperenderer-in-libgdx
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		///////////// Draw mask shape(s)
		
		//6. render your primitive shapes
		shapes.begin(ShapeType.Filled);

        shapes.setColor(mistModel.color.r, mistModel.color.g, mistModel.color.b, 
                mistModel.color.a * (mistModel.fading ? mistModel.secondsLeft / mistModel.fadeTime : 1));

        shapes.rect(mistModel.rectangle.x, mistModel.rectangle.y, mistModel.rectangle.width, mistModel.rectangle.height);

		shapes.end();

        // disable blending
        Gdx.gl.glDisable(GL10.GL_BLEND);

		///////////// Draw sprite(s) to be masked
		batch.begin();

		//8. Enable RGBA color writing
		//   (SpriteBatch.begin() will disable depth mask)
		Gdx.gl.glColorMask(true, true, true, true);

		//9. Make sure testing is enabled.
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);

		//10. Now depth discards pixels outside our masked shapes
		Gdx.gl.glDepthFunc(GL10.GL_EQUAL);

		//push to the batch
        Color oldColor = batch.getColor();

        batch.setColor(mistModel.color.r, mistModel.color.g, mistModel.color.b, 0.5f * (mistModel.fading ? mistModel.secondsLeft / mistModel.fadeTime : 1));

        batch.draw(mistLayer, mistModel.rectangle.x, mistModel.rectangle.y, mistModel.rectangle.width, mistModel.rectangle.height);

        batch.setColor(oldColor);

        // turn off masking so that the rest of the scene doesn't get nuked
        Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);

        mistModel.getMistEffect().draw(batch);
	}
	
	public void dispose() {
		shapes.dispose();
	}
}
