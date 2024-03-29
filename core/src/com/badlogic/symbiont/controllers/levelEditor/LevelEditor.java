package com.badlogic.symbiont.controllers.levelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class LevelEditor extends InputListener{

    PhysicsEntityModel selectedPhysicsEntityModel;

    Vector2 selectedOffset = new Vector2();
    Vector2 selectedInitialLocation = new Vector2();
    float selectedInitalAngle;

    public GameState editorGameState;
    public World editorWorld;

    private final float aabb_delta = 5 / GameConstants.PIXELS_PER_METER;

    public LevelEditor(GameState editorGameState) {
        this.editorGameState = editorGameState;
        editorWorld = new World(new Vector2(), true);
        this.editorGameState.addToWorld(editorWorld);
    }

    @Override
    public boolean touchDown(InputEvent event, final float x, final float y, int pointer, int button) {
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            System.out.println(String.format("%f, %f,", x, y));
        }
        float physicsX = x / GameConstants.PIXELS_PER_METER;
        float physicsY = y / GameConstants.PIXELS_PER_METER;
        editorWorld.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                selectedPhysicsEntityModel = (PhysicsEntityModel) fixture.getBody().getUserData();
                selectedOffset.set(
                        x - selectedPhysicsEntityModel.position.x,
                        y - selectedPhysicsEntityModel.position.y
                );
                selectedInitialLocation.set(x, y);
                selectedInitalAngle = selectedPhysicsEntityModel.angle;
                return false;
            }
        }, physicsX - aabb_delta, physicsY - aabb_delta, physicsX + aabb_delta, physicsY + aabb_delta);
        return true;
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (selectedPhysicsEntityModel == null) {
            return;
        }
        float angle_rate = (float) Math.PI / 180 * 2;
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            selectedPhysicsEntityModel.angle = selectedInitalAngle + (y - selectedInitialLocation.y) * angle_rate;
            if (selectedPhysicsEntityModel.body != null) {
                float bodyX = selectedPhysicsEntityModel.body.getPosition().x;
                float bodyY = selectedPhysicsEntityModel.body.getPosition().y;
                selectedPhysicsEntityModel.body.setTransform(bodyX, bodyY, selectedPhysicsEntityModel.angle);
            }
        } else {
            x -= selectedOffset.x;
            y -= selectedOffset.y;
            float physicsX = x / GameConstants.PIXELS_PER_METER;
            float physicsY = y / GameConstants.PIXELS_PER_METER;
            selectedPhysicsEntityModel.setPositionFromLevelEditor(x, y);
            if (selectedPhysicsEntityModel.body != null) {
                selectedPhysicsEntityModel.body.setTransform(physicsX, physicsY, selectedPhysicsEntityModel.body.getAngle());
            }
        }
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        selectedPhysicsEntityModel = null;
    }

    public void dispose() {
        editorWorld.dispose();
    }
}
