package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.Mist;
import com.badlogic.symbiont.models.PhysicsEntity;
import com.badlogic.symbiont.models.Plant;

/*
    mad props to
    https://gist.github.com/mattdesl/6076849
 */
public class MistView {

	final ShapeRenderer shapes;

    public MistView(Camera camera) {
        shapes = new ShapeRenderer();
        shapes.setProjectionMatrix(camera.combined);
    }

	public void render(SpriteBatch batch, GameState gameState) {
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

        for (PhysicsEntity e : gameState.entities) {
            if (e.entityType == PhysicsEntity.Type.PLANT) {
                Plant plant = (Plant) e;
                for (Mist mist : plant.mist) {
                    float x1 = mist.vertices[0];
                    float y1 = mist.vertices[1];
                    for (int i = 2; i + 3 < mist.vertices.length; i+=2) {
                        shapes.triangle(x1, y1, mist.vertices[i], mist.vertices[i+1], mist.vertices[i+2], mist.vertices[i+3]);
                    }
                }
            }
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
        for (PhysicsEntity e : gameState.entities) {
            if (e.entityType == PhysicsEntity.Type.PLANT) {
                Plant plant = (Plant) e;
                for (Mist mist : plant.mist) {
                    mist.getMistEffect().draw(batch, 1 / 60f);
                }
            }
        }

		//end/flush your batch
		batch.end();

        // turn off masking so that the rest of the scene doesn't get nuked
        Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
	public void dispose() {
		shapes.dispose();
	}
}