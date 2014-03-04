package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.MistModel;

/*
    mad props to
    https://gist.github.com/mattdesl/6076849
 */
public class MistView {

	final ShapeRenderer shapes;

    public MistView() {
        shapes = new ShapeRenderer();
    }

    public void render(SpriteBatch batch, GameState gamestate) {
        for (MistModel mistModel : gamestate.mistModels) {
            renderMist(batch, mistModel);
        }
    }

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
		
		//5. Enable depth writing, disable RGBA color writing 
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glColorMask(false, false, false, false);
		
		///////////// Draw mask shape(s)
		
		//6. render your primitive shapes
		shapes.begin(ShapeType.Filled);

        float x1 = mistModel.vertices[0];
        float y1 = mistModel.vertices[1];
        for (int i = 2; i + 3 < mistModel.vertices.length; i+=2) {
            shapes.triangle(x1, y1, mistModel.vertices[i], mistModel.vertices[i+1], mistModel.vertices[i+2], mistModel.vertices[i+3]);
        }

		shapes.end();
		
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
        mistModel.getMistEffect().draw(batch, 1 / 60f);

        // turn off masking so that the rest of the scene doesn't get nuked
        Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
	public void dispose() {
		shapes.dispose();
	}
}