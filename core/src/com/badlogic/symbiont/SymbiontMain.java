package com.badlogic.symbiont;

import java.util.Iterator;

import javax.swing.text.html.parser.Entity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class SymbiontMain extends ApplicationAdapter implements InputProcessor {
    // Shouts out to http://www.gamefromscratch.com/post/2013/10/24/LibGDX-Tutorial-5-Handling-Input-Touch-and-gestures.aspx
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    
    int screenWidth;
    int screenHeight;

    private World world;

    class TouchInfo {
        public Vector3 vector = new Vector3();
        public boolean touched = false;
    }
    
    class Ball {
    	public Vector3 position = new Vector3();
    	public Vector3 velocity = new Vector3();
    	public Texture img = new Texture("ball.png");
    }

    private TouchInfo[] touches = new TouchInfo[2];
    private Ball alien;

    @Override
    public void create() {
        // shouts out to http://stackoverflow.com/questions/16514152/libgdx-coordinate-system-differences-between-rendering-and-touch-input

        // Create a full-screen camera:
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Set it to an orthographic projection with "y down" (the first boolean parameter)
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        // Create a full screen sprite renderer and use the above camera
        batch = new SpriteBatch(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        world = new World(new Vector2(0, -10), true);

        Texture.setEnforcePotImages(false);
        Gdx.input.setInputProcessor(this);
        for(int i = 0; i < 2; i++){
            touches[i] = new TouchInfo(); 
        }

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        
        alien = new Ball();

        setUpPhysics();
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
        bodyDef.linearVelocity.set(50f, 0f);
        Body body = world.createBody(bodyDef);
        body.setUserData(alien);

        // Create a circle shape and set its radius to 6
        CircleShape circle = new CircleShape();
        circle.setRadius(alien.img.getWidth() / 2);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);
        // Create our body definition

        BodyDef groundBodyDef = new BodyDef();
        // Set its world position
        groundBodyDef.position.set(new Vector2(0, 10));

        // Create a body from the definition and add it to the world
        Body groundBody = world.createBody(groundBodyDef);

        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(camera.viewportWidth, 10.0f);
        // Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(new Vector2(10,0));
        Body leftWallBody = world.createBody(leftWallDef);
        PolygonShape leftWallBox = new PolygonShape();
        leftWallBox.setAsBox(10f, camera.viewportHeight);
        leftWallBody.createFixture(leftWallBox, 0f);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(new Vector2(camera.viewportWidth - 10,0));
        Body rightWallBody = world.createBody(rightWallDef);
        PolygonShape rightWallBox = new PolygonShape();
        rightWallBox.setAsBox(10f, camera.viewportHeight);
        rightWallBody.createFixture(rightWallBox, 0f);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        debugRenderer.dispose();
        world.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(world, camera.combined);

        if (touches[0].touched && touches[1].touched) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.line(
                    touches[0].vector.x,
                    touches[0].vector.y,
                    touches[1].vector.x,
                    touches[1].vector.y
            );
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line( 5,5,5,screenHeight);//left wall
        shapeRenderer.line(screenWidth-5, 5,screenWidth-5,screenHeight); //rigth wall
        shapeRenderer.end();
        
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        for (Body b : bodies) {
        	Ball o = (Ball) b.getUserData();
        	if (o != null) {
        		batch.begin();
        		batch.draw(o.img, b.getPosition().x - o.img.getWidth()/2, b.getPosition().y - o.img.getHeight()/2);
        		batch.end();
        	}
        }
        
        world.step(1/60f, 6, 2);
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
