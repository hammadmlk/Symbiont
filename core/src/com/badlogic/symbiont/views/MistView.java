package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/*
    mad props to
    https://gist.github.com/mattdesl/6076849
 */
public class MistView {
  
	ShapeRenderer shapes;
    ParticleEffect mistEffect;

    public MistView(Camera camera) {
        shapes = new ShapeRenderer();
        shapes.setProjectionMatrix(camera.combined);
        mistEffect = new ParticleEffect();
        FileHandle particleDir = Gdx.files.internal("particles");
        mistEffect.load(Gdx.files.internal("particles/mist.p"), particleDir);
    }

	public void render(SpriteBatch batch) {
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

		shapes.circle(5, 8, 3, 100);

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
        mistEffect.draw(batch, 1 / 60f);

		//end/flush your batch
		batch.end();

        Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	}
	
	public void dispose() {
		shapes.dispose();
	}
}