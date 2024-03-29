package com.badlogic.symbiont.models;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class MovableObjectModel extends PhysicsEntityModel {
    
    public List<Vector2> movingPath;
    public float movingSpeed;
    public int pathPos;

    private Vector2 curPos = new Vector2();
    private Vector2 vel = new Vector2();
    private float lastDist = 0;

    @Override
    public void update(float delta) {
        curPos.set(
                body.getPosition().x * GameConstants.PIXELS_PER_METER,
                body.getPosition().y * GameConstants.PIXELS_PER_METER
        );
        
        float dist = curPos.dst(movingPath.get(pathPos));

        if (dist >= lastDist) {
            curPos = movingPath.get(pathPos);
            pathPos++;
            if (pathPos >= movingPath.size()) {
                pathPos = 0;
            }
            vel.set(movingPath.get(pathPos));
            vel.sub(curPos).nor().scl(movingSpeed);
            body.setLinearVelocity(vel);
            lastDist = Float.MAX_VALUE;
        } else {
            lastDist = dist;
        }
        
        super.update(delta);
    }
}
