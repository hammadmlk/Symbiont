package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class PhysicsEntityView {
    /**
     * render a physics entity
     * @param batch
     * @param entity
     */
    public static void render(SpriteBatch batch, PhysicsEntityModel entity) {
        Vector2 origin = entity.getOrigin();
        Vector2 shakerDisplacement = entity.getAnimator().getShakerDisplacement();

        float adjustedX = entity.position.x + shakerDisplacement.x - origin.x;
        float adjustedY = entity.position.y + shakerDisplacement.y - origin.y;

        TextureAtlas.AtlasRegion atlasRegion = entity.getImg();

        boolean oldX = atlasRegion.isFlipX();
        boolean oldY = atlasRegion.isFlipY();

        // flip if necessary
        atlasRegion.flip(oldX != entity.flipHorizontal, oldY != entity.flipVertical);

        //push to the batch
        Color oldColor = batch.getColor();

        batch.setColor(1, 1, 1, entity.getAnimator().getAlpha());

        batch.draw(
                atlasRegion,                                       // Texture atlasRegion
                adjustedX,                                         // float x
                adjustedY,                                         // float y
                origin.x,                                          // float originX
                origin.y,                                          // float originY
                atlasRegion.originalWidth,                         // float width
                atlasRegion.originalHeight,                        // float height
                entity.scale,                                      // float scaleX
                entity.scale,                                      // float scaleY
                (float) Math.toDegrees(entity.angle)               // float rotation
        );

        batch.setColor(oldColor);

        // flip back if necessary
        atlasRegion.flip(oldX != entity.flipHorizontal, oldY != entity.flipVertical);
    }
}
