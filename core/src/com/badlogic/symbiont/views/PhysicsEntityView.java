package com.badlogic.symbiont.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class PhysicsEntityView {
	public static void create(PhysicsEntityModel entity){
		if(entity.entityType==PhysicsEntityModel.Type.ALIEN){
			entity.atlasAnimRegion=entity.getAnimImg();
	    	entity.createAlienAnimation(); 
		}
	}
	
	public static void render(SpriteBatch batch, PhysicsEntityModel entity) {
        Vector2 origin = entity.getOrigin();
        float adjustedX = entity.position.x - origin.x;
        float adjustedY = entity.position.y - origin.y;
        TextureAtlas.AtlasRegion atlasRegion = entity.getImg();
        boolean oldX = atlasRegion.isFlipX();
        boolean oldY = atlasRegion.isFlipY();

        // flip if necessary
        atlasRegion.flip(oldX != entity.flipHorizontal, oldY != entity.flipVertical);
        
       
       
        if(entity.entityType==PhysicsEntityModel.Type.ALIEN){
        	entity.stateTime+=Gdx.graphics.getDeltaTime();
        	TextureAtlas.AtlasRegion currentFrame=(AtlasRegion) entity.alienAnimation.getKeyFrame(entity.stateTime,true);
        	batch.draw(currentFrame,  
        			adjustedX,                                         // float x
                    adjustedY,                                         // float y
                    origin.x,                                          // float originX
                    origin.y,                                          // float originY
                    atlasRegion.originalWidth,                         // float width
                    atlasRegion.originalHeight,                        // float height
                    entity.scale,                                      // float scaleX
                    entity.scale,                                      // float scaleY
                    (float) Math.toDegrees(entity.angle)               // float rotation);
            );
        }
        if(entity.entityType !=PhysicsEntityModel.Type.ALIEN){
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
        }
        
        // flip back if necessary
        atlasRegion.flip(oldX != entity.flipHorizontal, oldY != entity.flipVertical);
    }
}
