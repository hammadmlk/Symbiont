package com.badlogic.symbiont;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

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

    private Texture texture;
    private Texture backgroundTexture;

    int screenWidth;
    int screenHeight;

    private World world;

    class TouchInfo {
        public Vector3 vector = new Vector3();
        public boolean touched = false;
    }
    
    class Ball {
    	public Texture img = new Texture("ball.png");
    	public float scale = 0.5f;
    }

    private TouchInfo[] touches = new TouchInfo[2];
    private Ball alien;

    @Override
    public void create() {
        // shouts out to http://stackoverflow.com/questions/16514152/libgdx-coordinate-system-differences-between-rendering-and-touch-input

        // Create a full-screen camera:
        camera = new OrthographicCamera();
        // Set it to an orthographic projection with "y down" (the first boolean parameter)
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        camera.update();
        // Create a full screen sprite renderer and use the above camera
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        batch.setProjectionMatrix(camera.combined);

        loadTextures();       

        world = new World(new Vector2(0, -10), true);

        //Texture.setEnforcePotImages(false);

        Gdx.input.setInputProcessor(this);
        for(int i = 0; i < 2; i++){
            touches[i] = new TouchInfo(); 
        }

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        alien = new Ball();

        setUpPhysics();
    }
    
    private void loadTextures() {
        Texture.setEnforcePotImages(false);

        texture = new Texture(Gdx.files.internal("ball.png"));

    	backgroundTexture = new Texture(Gdx.files.internal("background.png"));
    }
    
    private void renderBackground() {
    	batch.draw(backgroundTextureRegion,0,0);
    }

    private void setUpPhysics() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector3 middle = new Vector3(
        		screenWidth / 2,
        		screenHeight / 2,
                0
        );
        camera.unproject(middle);
        bodyDef.position.set(middle.x, middle.y);
        bodyDef.linearVelocity.set(50f, 250f);
        Body body = world.createBody(bodyDef);
        body.setUserData(alien);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(alien.img.getWidth() * alien.scale / 2);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 1f; // Make it bounce a lot

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
        // Create our body definition

        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(0, 0));

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(camera.viewportWidth, 0f);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0f);
        
        BodyDef topWallDef = new BodyDef();
        topWallDef.position.set(new Vector2(0, camera.viewportHeight));
        Body topWallBody = world.createBody(topWallDef);
        PolygonShape topWallBox = new PolygonShape();
        topWallBox.setAsBox(camera.viewportWidth, 0f);
        topWallBody.createFixture(topWallBox, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(0,0));
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(0f, camera.viewportHeight);
        leftWallBody.createFixture(leftWallBox, 0f);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(camera.viewportWidth, 0));
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(0f, camera.viewportHeight);
        rightWallBody.createFixture(rightWallBox, 0f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

    @Override
    public void render() {
		// update camera
		camera.update();
		camera.apply(Gdx.gl10);

		// set viewport
		Gdx.gl.glViewport((int) viewport.x, (int) viewport.y,
				(int) viewport.width, (int) viewport.height);

		// clear the window
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        batch.begin();
        renderBackground();
        batch.end();

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body b : bodies) {
        	Ball o = (Ball) b.getUserData();
        	if (o != null) {
        		batch.begin();
        		float originX = b.getPosition().x - o.img.getWidth()/2;
        		float originY = b.getPosition().y - o.img.getHeight()/2;
        		batch.draw(o.img, originX, originY, o.img.getWidth()/2, o.img.getHeight()/2,
        				o.img.getWidth(), o.img.getHeight(), o.scale, o.scale, (float) (b.getAngle()*180/Math.PI), 
        				0, 0, o.img.getWidth(), o.img.getHeight(), false, false);
        		/*
        		Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
        		float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY*/
        		batch.end();
        	}
        }
        
        Body trampolineBody = null;
        if (touches[0].touched && touches[1].touched &&
                new Vector2(
                    touches[1].vector.x - touches[0].vector.x,
                    touches[1].vector.y - touches[0].vector.y
                ).len() > 1) {
            trampolineBody = setUpTrampoline();
        }
        debugRenderer.render(world, camera.combined);
        world.step(1/30f, 6, 2);
        if (trampolineBody != null)
            tearDownTrampoline(trampolineBody);
    }

    public Body setUpTrampoline() {
        BodyDef trampolineDef = new BodyDef();
        trampolineDef.type = BodyDef.BodyType.StaticBody;
        trampolineDef.position.set(new Vector2(touches[0].vector.x, touches[0].vector.y));
        Vector2[] points = new Vector2[4];
        float trampoline_width = 10;
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
        return trampolineBody;
    }

    public void tearDownTrampoline(Body trampoline) {
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
