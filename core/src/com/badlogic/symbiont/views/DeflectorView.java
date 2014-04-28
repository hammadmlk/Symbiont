package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.DeflectorEndpoint;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class DeflectorView {

    private TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    /**
     * render the deflector
     * @param batch
     */
    public void render(SpriteBatch batch) {
        if (SymbiontMain.edit) {
            return;
        }
        if (SymbiontMain.gameState.deflector()) {
        	
        	//if not-elastic
        	if(!SymbiontMain.elasticDeflector){
	        	float x1 = SymbiontMain.gameState.deflectorEndpoints[0].x;
	            float y1 = SymbiontMain.gameState.deflectorEndpoints[0].y;
	            float x2 = SymbiontMain.gameState.deflectorEndpoints[1].x;
	            float y2 = SymbiontMain.gameState.deflectorEndpoints[1].y;
	            
	            drawline(batch, x1,y1, x2, y2);

            }
            // if elastic
            else{
            	// TODO: Perhaps find alien only once? 
            	// find the alien in entities array
            	for(PhysicsEntityModel e: SymbiontMain.gameState.entities){
            		if (e.entityType == PhysicsEntityModel.Type.ALIEN) {
            			//ALIEN FOUND, call the elastic method
            			elastic(batch, SymbiontMain.gameState.deflectorEndpoints[0], 
            					SymbiontMain.gameState.deflectorEndpoints[1],
            					e);
            		}
            	 }            
            }
            for (DeflectorEndpoint deflectorEndpointInfo : SymbiontMain.gameState.deflectorEndpoints) {
                deflectorEndpointInfo.getParticleEffect().setPosition(deflectorEndpointInfo.x, deflectorEndpointInfo.y);
                deflectorEndpointInfo.getParticleEffect().draw(batch);
            }
        }
    }
    
    private void elastic(SpriteBatch batch, DeflectorEndpoint pointA, DeflectorEndpoint pointB, PhysicsEntityModel alien ){    	
    	// Ball radius
    	float r = alien.body.getFixtureList().first()
    			.getShape().getRadius() * GameConstants.PIXELS_PER_METER;
    	
    	// swap points so pointA is always on right (bug fix)
    	if(pointA.x< pointB.x){
    		DeflectorEndpoint temp = pointA;
    		pointA = pointB;
    		pointB = temp;
    	}
    	
    	// pointA coords
    	float x1 = pointA.x;
    	float y1 = pointA.y;
    	
    	// pointB coords
    	float x2 = pointB.x;
    	float y2 = pointB.y;
    	
    	// Ball coords
    	float cx = alien.position.x;
    	float cy = alien.position.y;
    	
    	// Gives you the angle between pointA and pointB
    	float angleAB = (float) Math.atan2(y2 - y1, x2 - x1);
    	
    	// Precalculate cosine and sine
    	float cosine = (float) Math.cos( angleAB );
    	float sine = (float) Math.sin( angleAB );

    	// Distances between ball and elastic centre
    	float dbex = alien.position.x - (x2 + x1) / 2;
    	float dbey = alien.position.y - (y2 + y1) / 2;
    	
    	// Find position of ball relative to the elastic (and it's rotation)
    	float px1 = cosine * dbex + sine * dbey;
    	float py1 = cosine * dbey - sine * dbex;
    	
    	float maxPerpDist = (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)) / 2;
    	
    	
    	// Check if distance to line surface is less than ball radius
	    	if ( -py1 < r && px1 > -maxPerpDist && px1 < maxPerpDist  ) {
	    	// Distances from ball to pointA
	    	float dx1 = cx - x1;
	    	float dy1 = cy - y1;

	    	// Distances from ball to pointB
	    	float dx2 = cx - x2;
	    	float dy2 = cy - y2;

	    	// Calculate exact distances (LEFT)
	    	float h1 = (float) Math.sqrt(dx1*dx1 + dy1*dy1);
	    	float p1 = (float) Math.sqrt(h1*h1 - r*r);

	    	// Calculate exact distances (RIGHT)
	    	float h2 = (float) Math.sqrt(dx2*dx2 + dy2*dy2);
	    	float p2 = (float) Math.sqrt(h2*h2 - r*r);

	    	// Dont continue if the ball touches points
			if ( h1 < r || h2 < r ) {
				return;
			}

	    	// Calculate angles (LEFT)
	    	float a1 = (float) Math.atan2(dy1, dx1);
	    	float b1 = (float) Math.asin(r / h1);

	    	// Calculate angles (RIGHT)
	    	// Small adjustment needed to find tangent
	    	// on opposite side of ball (hense -r = opposite radius)
	    	float a2 = (float) Math.atan2(dy2, dx2);
	    	float b2 = (float) Math.asin(-r / h2); 

	    	// Tangent/Ball intersection coordinates (LEFT)
	    	float ix1 = (float) Math.cos(a1+b1) * p1 + x1;
	    	float iy1 = (float) Math.sin(a1+b1) * p1 + y1;

	    	// Tangent/Ball intersection coordinates (RIGHT)
	    	float ix2 = (float) Math.cos(a2+b2) * p2 + x2;
	    	float iy2 = (float) Math.sin(a2+b2) * p2 + y2;

	    	//Midpoint between the two tangent points (ix1,iy1) and (ix2,iy2)
	    	float mpx = (ix1 + ix2) / 2;
	    	float mpy = (iy1 + iy2) / 2;
	    	// Angle of acceleration
	    	float accAngle = (float) Math.atan2(mpy - cy, mpx - cx);

	    	//distance between pointA and pointB
	    	float d = (float) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));

	    	// Power is dependent on how far the elastic's been stretched 
	    	//	and the distance between pointA and pointB 
	    	float power = py1/50 ;//* (1+(10*d/GameConstants.VIRTUAL_WIDTH));
	    	//System.out.println("power1: "+py1 /50);

	    	//Change in velocity x and y 
	    	float dvx = (float) Math.cos(accAngle) * power;
	    	float dvy = (float) Math.sin(accAngle) * power;

	    	//
	    	Vector2 velo = alien.body.getLinearVelocity();
	    	velo.x-=dvx;
	    	velo.y-=dvy;
	    	alien.body.setLinearVelocity(velo.x, velo.y);

	    	/*
	    	//Impulse (changeInVelocity*mass)
	    	Vector2 impulse = new Vector2( 
	    			-dvx*alien.body.getMass(), 
	    			-dvy*alien.body.getMass()
			);	    	
	    	alien.body.applyLinearImpulse(
	    			impulse, alien.body.getWorldCenter(), true);
	    	*/

	    	// Draw lines
	    	drawline(batch, x1, y1, ix1, iy1);   	
	    	drawline(batch, x2, y2, ix2, iy2);
    	} else{
    		// No collision detected... simply draw a line from pointA to pointB
    		drawline(batch, x1, y1, x2, y2);
    	}
    }
    
    //draws a line between the two points (x1,y1) and (ix1, iy1).
    private void drawline(SpriteBatch batch, float x1, float y1, float ix1, float iy1){
    	float angle = (float) Math.toDegrees(Math.atan2(iy1 - y1, ix1 - x1));
        
        batch.draw(
                atlasRegion,                                                         // Texture atlasRegion
                x1,                                                                  // float x
                y1 - atlasRegion.originalHeight / 2,                                 // float y
                0,                                                                   // float originX
                atlasRegion.originalHeight / 2,                                      // float originY
                (float) Math.sqrt((x1 - ix1)*(x1 - ix1) + (y1 - iy1)*(y1 - iy1)),        // float width
                atlasRegion.originalHeight/2,                                          // float height
                1,                                                                   // float scaleX
                1,                                                                   // float scaleY
                angle                                                                // float rotation
        );
        
    }

}
