package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.PhysicsEntity;

public class PhysicsEntityView {
    public static void render(SpriteBatch batch, PhysicsEntity entity) {
        Vector2 origin = entity.getOrigin();
        float adjustedX = entity.position.x - origin.x;
        float adjustedY = entity.position.y - origin.y;
        batch.draw(
                entity.getImg(),                                   // Texture texture
                adjustedX,                                         // float x
                adjustedY,                                         // float y
                origin.x,                                          // float originX
                origin.y,                                          // float originY
                entity.getImg().getWidth(),                        // float width
                entity.getImg().getHeight(),                       // float height
                entity.scale,                                      // float scaleX
                entity.scale,                                      // float scaleY
                (float) Math.toDegrees(entity.angle),              // float rotation
                0,                                                 // int srcX
                0,                                                 // int srcY
                entity.getImg().getWidth(),                        // int srcWidth
                entity.getImg().getHeight(),                       // srcHeight
                entity.flipHorizontal,                             // boolean flipX
                entity.flipVertical                                // boolean flipY
        );
    }
}
