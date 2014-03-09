package com.badlogic.symbiont.controllers.levelEditor;

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
    GameState editorGameState;
    World editorWorld;

    private final float aabb_delta = 5 / SymbiontMain.PIXELS_PER_METER;

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
        float physicsX = x / SymbiontMain.PIXELS_PER_METER;
        float physicsY = y / SymbiontMain.PIXELS_PER_METER;
        selectedPhysicsEntityModel.position.set(physicsX, physicsY);
        selectedPhysicsEntityModel.body.setTransform(physicsX, physicsY, selectedPhysicsEntityModel.body.getAngle());
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        selectedPhysicsEntityModel = null;
    }
}
