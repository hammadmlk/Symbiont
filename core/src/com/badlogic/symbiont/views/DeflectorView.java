package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.Util;
import com.badlogic.symbiont.models.DeflectorEndpoint;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;
import com.badlogic.symbiont.models.PhysicsEntityModel;

public class DeflectorView {

    private final TextureAtlas.AtlasRegion atlasRegion = Assets.loadAtlas("deflector");

    /**
     * render the deflector
     * 
     * @param batch
     */
    public void render(SpriteBatch batch, GameState gameState, boolean elastic) {
        if (gameState.deflector()) {
        	
        	gameState.deflectorTime++;
        	if(gameState.deflectorTime<=0){
        		gameState.deflectorTime=1;
        	}
            
        	if(!elastic){   
	            DeflectorEndpoint pointStart = new DeflectorEndpoint(gameState.deflectorEndpoints[0]); //start point
	            DeflectorEndpoint pointEnd = new DeflectorEndpoint(gameState.deflectorEndpoints[1]); //end point
	            
	            // swap points so pointA is always on LEFT (bug fix)
	            if (pointStart.x > pointEnd.x) {
	                DeflectorEndpoint temp = new DeflectorEndpoint(pointStart);
	                pointStart = pointEnd;
	                pointEnd = temp;
	            }
	            
	            DeflectorEndpoint pointCenter = pointAtThisFraction(pointStart, pointEnd, (float)0.5, true); //center point
	            
	            //the based on time since deflector tap
	            DeflectorEndpoint pointB = pointAtThisTime(gameState, pointStart, pointCenter, true);
	            DeflectorEndpoint pointC = pointAtThisTime(gameState, pointCenter, pointEnd, false);
	            
	            //draw the electric lines
	            drawElectric(batch, pointStart, pointB, 0, 3, 0);
	            drawElectric(batch, pointC, pointEnd, 0, 3, 0);
	            //drawline(batch,atlasRegion.originalHeight, pointStart.x,pointStart.y, pointB.x, pointB.y);	            
	            //drawline(batch,atlasRegion.originalHeight, pointC.x,pointC.y, pointEnd.x, pointEnd.y);
	            
            } else {
            	// TODO: Perhaps find alien only once? 
            	// find the alien in entities array
            	for(PhysicsEntityModel e: gameState.entities){
            		if (e.entityType == PhysicsEntityModel.Type.ALIEN) {
            			//ALIEN FOUND, call the elastic method
            			drawElastic(gameState, batch, gameState.deflectorEndpoints[0], 
            					gameState.deflectorEndpoints[1],
            					e);
            		}
            	 }            
            }
        	
            for (DeflectorEndpoint deflectorEndpointInfo : gameState.deflectorEndpoints) {
                deflectorEndpointInfo.getParticleEffect().setPosition(deflectorEndpointInfo.x, deflectorEndpointInfo.y);
                deflectorEndpointInfo.getParticleEffect().draw(batch);
            }
            
            
        } else{
            gameState.deflectorTime=0; //comment
        }
    }
    
    //comment
    private void drawElectric(SpriteBatch batch, DeflectorEndpoint pointA, DeflectorEndpoint pointB, int currGen, int maxGen, int dist){
    	if(currGen >= maxGen){
    		float width = (float)(((float)(maxGen-dist)/(float)maxGen)/4.0 + 0.25 );
    		//drawline(batch,atlasRegion.originalHeight*width, pointA.x,pointA.y, pointB.x, pointB.y);
    		drawline(batch,GameConstants.DEFLECTOR_THICKNESS*GameConstants.PIXELS_PER_METER*width, pointA.x,pointA.y, pointB.x, pointB.y);
    		return;
    	}
    	
    	//random movement
    	DeflectorEndpoint mid = pointAtThisFraction(pointA, pointB, (float)0.5, true);
    	mid.y = mid.y + (float)((Math.random()-0.5)*20);
    	
    	//branch
    	if(Math.random()<0.5){
    		DeflectorEndpoint branch = pointAtThisFraction(mid, pointB, (float)0.75, true);
    		branch.y = branch.y + (float)((Math.random()-0.5)*10);
    		drawElectric(batch, mid, branch, currGen+1, maxGen, dist+1);
    	}
    	
    	//recurse
    	drawElectric(batch, pointA, mid, currGen+1, maxGen, dist);
    	drawElectric(batch, mid, pointB, currGen+1, maxGen, dist+1);
    	
    }
    
    //comment    
    private void drawElastic(GameState gameState, SpriteBatch batch, DeflectorEndpoint pointA, DeflectorEndpoint pointB,
            PhysicsEntityModel alien) {
        // Ball radius
        float r = alien.body.getFixtureList().first().getShape().getRadius()
                * GameConstants.PIXELS_PER_METER;

        // swap points so pointA is always on right (bug fix)
        if (pointA.x < pointB.x) {
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
        float cosine = (float) Math.cos(angleAB);
        float sine = (float) Math.sin(angleAB);

        // Distances between ball and elastic centre
        float dbex = alien.position.x - (x2 + x1) / 2;
        float dbey = alien.position.y - (y2 + y1) / 2;

        // Find position of ball relative to the elastic (and it's rotation)
        float px1 = cosine * dbex + sine * dbey;
        float py1 = cosine * dbey - sine * dbex;

        float maxPerpDist = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) / 2;

        // Check if distance to line surface is less than ball radius
        if (-py1 < r && px1 > -maxPerpDist && px1 < maxPerpDist) {
            // Distances from ball to pointA
            float dx1 = cx - x1;
            float dy1 = cy - y1;

            // Distances from ball to pointB
            float dx2 = cx - x2;
            float dy2 = cy - y2;

            // Calculate exact distances (LEFT)
            float h1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
            float p1 = (float) Math.sqrt(h1 * h1 - r * r);

            // Calculate exact distances (RIGHT)
            float h2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
            float p2 = (float) Math.sqrt(h2 * h2 - r * r);

            // Dont continue if the ball touches points
            if (h1 < r || h2 < r) {
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
            float ix1 = (float) Math.cos(a1 + b1) * p1 + x1;
            float iy1 = (float) Math.sin(a1 + b1) * p1 + y1;

            // Tangent/Ball intersection coordinates (RIGHT)
            float ix2 = (float) Math.cos(a2 + b2) * p2 + x2;
            float iy2 = (float) Math.sin(a2 + b2) * p2 + y2;

            // Midpoint between the two tangent points (ix1,iy1) and (ix2,iy2)
            float mpx = (ix1 + ix2) / 2;
            float mpy = (iy1 + iy2) / 2;
            // Angle of acceleration
            float accAngle = (float) Math.atan2(mpy - cy, mpx - cx);

            // distance between pointA and pointB
            float d = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

            // Power is dependent on how far the elastic's been stretched
            // and the distance between pointA and pointB
            float power = py1 / 50;// * (1+(10*d/GameConstants.VIRTUAL_WIDTH));

            // Change in velocity x and y
            float dvx = (float) Math.cos(accAngle) * power;
            float dvy = (float) Math.sin(accAngle) * power * 2;

            //
            Vector2 velo = alien.body.getLinearVelocity();
            velo.x -= dvx;
            velo.y -= dvy;
            // maximum speed limit
            velo.x = (velo.x < 0) ? Math.max(-25, velo.x) : Math.min(25, velo.x);
            velo.y = (velo.y < 0) ? Math.max(-25, velo.y) : Math.min(25, velo.y);
            alien.body.setLinearVelocity(velo.x, velo.y);

            // Draw lines
            //drawline(batch, atlasRegion.originalHeight / 2, x1, y1, ix1, iy1);
            DeflectorEndpoint b = pointAtThisTime(gameState, createPoint(x1,y1), createPoint(ix1, iy1), true);
            drawElectric(batch, b,createPoint(x1,y1), 0, 3, 0);
            
            //drawline(batch, atlasRegion.originalHeight / 2, x2, y2, ix2, iy2);
            DeflectorEndpoint c = pointAtThisTime(gameState, createPoint(ix2, iy2), createPoint(x2,y2), false);            
            drawElectric(batch, createPoint(x2, y2), c , 0, 3, 0);
            
        } else {
            // No collision detected... simply draw a line from pointA to pointB
            //drawline(batch, atlasRegion.originalHeight / 2, x1, y1, x2, y2);
        	DeflectorEndpoint b = pointAtThisTime(gameState,createPoint(x1,y1), createPoint(x2, y2), true);
        	drawElectric(batch, createPoint(x1,y1), b, 0, 3, 0);
        	
        	DeflectorEndpoint pointCenter = pointAtThisFraction(pointA, pointB, (float)0.5, true); //center point
            
            //the based on time since deflector tap
            DeflectorEndpoint point1 = pointAtThisTime(gameState, pointA, pointCenter, true);
            DeflectorEndpoint point2 = pointAtThisTime(gameState, pointCenter, pointB, false);
            
            //draw the electric lines
            drawElectric(batch, pointA, point1, 0, 3, 0);
            drawElectric(batch, point2, pointB, 0, 3, 0);
        	
        }
    }

    
    
    /*
     * returns a new point that is at a distanceFraction away from point A or Point B. 
     * Point A if growTowardsRight == true, else point B
    */
    private DeflectorEndpoint pointAtThisFraction(DeflectorEndpoint pointA, DeflectorEndpoint pointB, float distanceFraction, boolean growTowardsRight){
    	float distanceAB = Util.distance(pointA.x,pointA.y, pointB.x,pointB.y); //distance
    	float distanceAX = distanceAB*distanceFraction;
    	
    	float angleAB = (float) Math.atan2(pointB.y - pointA.y, pointB.x - pointA.x); //angle b/w the points
    	
    	DeflectorEndpoint pointX = new DeflectorEndpoint();
    	if(growTowardsRight){
    		pointX.x = (float) ((distanceAX)*Math.cos(angleAB) + pointA.x);
    		pointX.y = (float) ((distanceAX)*Math.sin(angleAB)+ pointA.y);
    	}else{
    		pointX.x = (float) ((-distanceAX)*Math.cos(angleAB) + pointB.x);
    		pointX.y = (float) ((-distanceAX)*Math.sin(angleAB)+ pointB.y);
		}
    		return pointX;
    }
    
    //based on the defector draw time, returns the position at which the point show be drawn.
    //this givces the animation effect of growing beam.
    private DeflectorEndpoint pointAtThisTime(GameState gameState, DeflectorEndpoint pointStart, DeflectorEndpoint pointEnd, boolean growToRight){
    	//the based on time since deflector tap
        float fraction = (float) Math.min(gameState.deflectorTime/GameConstants.DEFLECTOR_DRAW_TIME, 1.0);
        DeflectorEndpoint pointX = pointAtThisFraction(pointStart, pointEnd, (float)fraction, growToRight);
        return pointX;
    }
    
    // draws a line between the two points (x1,y1) and (ix1, iy1).
    private void drawline(SpriteBatch batch, float height, float x1, float y1, float ix1, float iy1) {
        float angle = (float) Math.toDegrees(Math.atan2(iy1 - y1, ix1 - x1));
        
        batch.draw(
                atlasRegion,                                                         // Texture atlasRegion
                x1,                                                                  // float x
                y1 - height,						                                 // float y
                0,                                                                   // float originX
                height,                     						                 // float originY
                (float) Math.sqrt((x1 - ix1)*(x1 - ix1) + (y1 - iy1)*(y1 - iy1)),    // float width
                height,						                                         // float height
                1,                                                                   // float scaleX
                1,                                                                   // float scaleY
                angle                                                                // float rotation
        );
    }
    
    private DeflectorEndpoint createPoint(float x, float y){
    	return new DeflectorEndpoint(x, y);
    }

}
