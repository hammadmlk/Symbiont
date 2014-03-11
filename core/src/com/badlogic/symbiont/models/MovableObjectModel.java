package com.badlogic.symbiont.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.SymbiontMain;

import java.util.List;

public class MovableObjectModel extends PhysicsEntityModel {
    
    public List<Vector2> movingPath;
    public float movingSpeed;
    public int pathPos;
    
    @Override
    public void update() {
        Vector2 curPos = new Vector2(
                body.getPosition().x * SymbiontMain.PIXELS_PER_METER,
                body.getPosition().y * SymbiontMain.PIXELS_PER_METER
        );
        
        // Some small distance that signals that the body has reached the next point
        if (curPos.dst(movingPath.get(pathPos)) < 0.1) {
            pathPos++;
            if (pathPos >= movingPath.size()) {
                pathPos = 0;
            }
            Vector2 vel = new Vector2(movingPath.get(pathPos));
            vel.sub(curPos).nor().scl(movingSpeed);
            body.setLinearVelocity(vel);
        }
        
        super.update();
    }
}
