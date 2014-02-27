package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.PhysicsEntity;

public class PhysicsEntityView {
    public static void render(SpriteBatch batch, PhysicsEntity entity) {
        if (entity.getImg() != null) {
            float originX = entity.position.x - entity.getImg().getWidth()/2;
            float originY = entity.position.y - entity.getImg().getHeight()/2;
            batch.draw(
                    entity.getImg(),                                   // Texture texture
                    originX,                                           // float x
                    originY,                                           // float y
                    entity.getImg().getWidth()/2,                      // float originX
                    entity.getImg().getHeight()/2,                     // float originY
                    entity.getImg().getWidth(),                        // float width
                    entity.getImg().getHeight(),                       // float height
                    entity.scale / SymbiontMain.PIXELS_PER_METER,      // float scaleX
                    entity.scale / SymbiontMain.PIXELS_PER_METER,      // float scaleY
                    (float) Math.toDegrees(entity.angle),              // float rotation
                    0,                                                 // int srcX
                    0,                                                 // int srcY
                    entity.getImg().getWidth(),                        // int srcWidth
                    entity.getImg().getHeight(),                       // srcHeight
                    false,                                             // boolean flipX
                    false                                              // boolean flipY
            );
        }
    }
}
