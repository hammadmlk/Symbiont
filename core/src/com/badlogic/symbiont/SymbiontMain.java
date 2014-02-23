package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class SymbiontMain extends ApplicationAdapter implements InputProcessor {
    // Shouts out to http://www.gamefromscratch.com/post/2013/10/24/LibGDX-Tutorial-5-Handling-Input-Touch-and-gestures.aspx
    private SpriteBatch batch;
    private OrthographicCamera camera;
	Texture img;

    class TouchInfo {
        public Vector3 vector = new Vector3();
        public boolean touched = false;
    }

    private TouchInfo[] touches = new TouchInfo[5];

    @Override
    public void create() {
        // shouts out to http://stackoverflow.com/questions/16514152/libgdx-coordinate-system-differences-between-rendering-and-touch-input

        // Create a full-screen camera:
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Set it to an orthographic projection with "y down" (the first boolean parameter)
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        // Create a full screen sprite renderer and use the above camera
        batch = new SpriteBatch(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);

        Texture.setEnforcePotImages(false);
		img = new Texture("ball.png");
        Gdx.input.setInputProcessor(this);
        for(int i = 0; i < 5; i++){
            touches[i] = new TouchInfo();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        batch.begin();

        for (TouchInfo touch : touches) {
            if(touch.touched) {
                batch.draw(img, touch.vector.x - img.getWidth() / 2 , touch.vector.y - img.getWidth() / 2);
            }
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(pointer < 5){
            touches[pointer].vector.x = screenX;
            touches[pointer].vector.y = screenY;
            camera.unproject(touches[pointer].vector);
            touches[pointer].touched = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer < 5){
            touches[pointer].vector.x = 0;
            touches[pointer].vector.y = 0;
            touches[pointer].touched = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
