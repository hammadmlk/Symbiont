package com.badlogic.symbiont.controllers.levelEditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class LevelEditor extends InputListener{

    PhysicsEntityModel selectedPhysicsEntityModel;
    public GameState editorGameState;
    public World editorWorld;

    private final float aabb_delta = 5 / SymbiontMain.PIXELS_PER_METER;

    public LevelEditor(GameState editorGameState) {
        this.editorGameState = editorGameState;
        editorWorld = new World(new Vector2(-10, 0), true);
        this.editorGameState.addToWorld(editorWorld);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        float physicsX = x / SymbiontMain.PIXELS_PER_METER;
        float physicsY = y / SymbiontMain.PIXELS_PER_METER;
        editorWorld.QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                selectedPhysicsEntityModel = (PhysicsEntityModel) fixture.getBody().getUserData();
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
        float physicsX = x / SymbiontMain.PIXELS_PER_METER;
        float physicsY = y / SymbiontMain.PIXELS_PER_METER;
        selectedPhysicsEntityModel.position.set(x, y);
        selectedPhysicsEntityModel.body.setTransform(physicsX, physicsY, selectedPhysicsEntityModel.body.getAngle());
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        selectedPhysicsEntityModel = null;
    }

    public void dispose() {
        editorWorld.dispose();
    }
}
