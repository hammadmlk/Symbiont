package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.symbiont.models.*;

public class SymbiontMain extends ApplicationAdapter implements InputProcessor {
	// Big ups to http://www.acamara.es/blog/2012/02/keep-screen-aspect-ratio-with-different-resolutions-using-libgdx
	private static final int VIRTUAL_WIDTH = 480;
    private static final int VIRTUAL_HEIGHT = 800;
	private static final float ASPECT_RATIO = (float) VIRTUAL_WIDTH
			/ (float) VIRTUAL_HEIGHT;

    private Rectangle viewport;

    // Shouts out to http://www.gamefromscratch.com/post/2013/10/24/LibGDX-Tutorial-5-Handling-Input-Touch-and-gestures.aspx
    private SpriteBatch batch;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private int screenWidth;
    private int screenHeight;

    private World world;

    class TouchInfo {
        public Vector3 vector = new Vector3();
        public boolean touched = false;
    }
    
    private TouchInfo[] touches = new TouchInfo[2];

    private GameState gameState;

    private static final float PIXELS_PER_METER = 50f;

    @Override
    public void create() {
        // shouts out to http://stackoverflow.com/questions/16514152/libgdx-coordinate-system-differences-between-rendering-and-touch-input


        // Create a full-screen camera:
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH / PIXELS_PER_METER, VIRTUAL_HEIGHT / PIXELS_PER_METER);
        camera.update();
        // Create a full screen sprite renderer and use the above camera
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        batch.setProjectionMatrix(camera.combined);

        world = new World(new Vector2(0, -10), true);

        Gdx.input.setInputProcessor(this);
        for(int i = 0; i < 2; i++){
            touches[i] = new TouchInfo(); 
        }

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        FileHandle gamestateFile = Gdx.files.internal("levels/gamestate.json");
        String rawGameStateJSON = gamestateFile.readString();
        gameState = GameState.fromJSON(rawGameStateJSON);
        gameState.addToWorld(world);
    }
    
    private void renderBackground() {
        batch.draw(Assets.backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
    }

    @Override
    public void dispose() {
        batch.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

    @Override
    public void render() {
        // set up trampoline
        Body trampolineBody = null;
        if (touches[0].touched && touches[1].touched &&
                new Vector2(
                        touches[1].vector.x - touches[0].vector.x,
                        touches[1].vector.y - touches[0].vector.y
                ).len() > 1 / PIXELS_PER_METER) {
            trampolineBody = setUpTrampoline();
        }

        // step physics engine
        world.step(1/60f, 6, 2);

        // update game state
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body b : bodies) {
            if (b.getUserData() instanceof PhysicsEntity) {
                PhysicsEntity o = (PhysicsEntity) b.getUserData();
                o.update(b);
            }
        }

		// update camera
		camera.update();
		camera.apply(Gdx.gl10);

		// set viewport
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
				(int) viewport.width, (int) viewport.height);

		// clear the window
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // render background
        batch.begin();
        renderBackground();
        batch.end();

        // render game state
        float originX = gameState.alien.position.x - gameState.alien.getImg().getWidth()/2;
        float originY = gameState.alien.position.y - gameState.alien.getImg().getHeight()/2;
        batch.begin();
        batch.draw(gameState.alien.getImg(), originX, originY, gameState.alien.getImg().getWidth()/2, gameState.alien.getImg().getHeight()/2,
                gameState.alien.getImg().getWidth(), gameState.alien.getImg().getHeight(), gameState.alien.scale / PIXELS_PER_METER, gameState.alien.scale / PIXELS_PER_METER, (float) (gameState.alien.angle*180/Math.PI),
                0, 0, gameState.alien.getImg().getWidth(), gameState.alien.getImg().getHeight(), false, false);
        /*
        Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
        float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY*/
        batch.end();

        // debug render
        debugRenderer.render(world, camera.combined);

        // tear down trampoline
        if (trampolineBody != null)
            tearDownTrampoline(trampolineBody);
    }

    private Body setUpTrampoline() {
        BodyDef trampolineDef = new BodyDef();
        trampolineDef.type = BodyDef.BodyType.StaticBody;
        trampolineDef.position.set(new Vector2(touches[0].vector.x, touches[0].vector.y));
        Vector2[] points = new Vector2[4];
        float trampoline_width = 10 / PIXELS_PER_METER;
        points[0] = new Vector2(0,0);
        points[1] = new Vector2(touches[1].vector.x - touches[0].vector.x, touches[1].vector.y - touches[0].vector.y);
        Vector2 normal = new Vector2(-points[1].y, points[1].x);
        normal.nor();
        normal.x *= trampoline_width;
        normal.y *= trampoline_width;
        points[2] = new Vector2(points[1].x + normal.x, points[1].y + normal.y);
        points[3] = normal;
        Body trampolineBody = world.createBody(trampolineDef);
        PolygonShape trampolineBox = new PolygonShape();
        trampolineBox.set(points);
        trampolineBody.createFixture(trampolineBox, 0f);
        trampolineBox.dispose();
        return trampolineBody;
    }

    private void tearDownTrampoline(Body trampoline) {
        world.destroyBody(trampoline);
    }

    @Override
    public void resize(int width, int height) {
    	// calculate new viewport
        float aspectRatio = (float)width/(float)height;
        float scale = 1f;
        Vector2 crop = new Vector2(0f, 0f);
        
        if(aspectRatio > ASPECT_RATIO)
        {
            scale = (float)height/(float)VIRTUAL_HEIGHT;
            crop.x = (width - VIRTUAL_WIDTH*scale)/2f;
        }
        else if(aspectRatio < ASPECT_RATIO)
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
            crop.y = (height - VIRTUAL_HEIGHT*scale)/2f;
        }
        else
        {
            scale = (float)width/(float)VIRTUAL_WIDTH;
        }

        float w = (float)VIRTUAL_WIDTH*scale;
        float h = (float)VIRTUAL_HEIGHT*scale;
        viewport = new Rectangle(crop.x, crop.y, w, h);
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
        return touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer < 2){
            touches[pointer].vector.x = 0;
            touches[pointer].vector.y = 0;
            touches[pointer].touched = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(pointer < 2){
            touches[pointer].vector.x = screenX;
            touches[pointer].vector.y = screenY;
            camera.unproject(touches[pointer].vector);
            touches[pointer].touched = true;
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return touchDragged(screenX, screenY, 1);
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
