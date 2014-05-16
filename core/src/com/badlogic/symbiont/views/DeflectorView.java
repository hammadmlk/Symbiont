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
	            drawElectricLine(batch, pointStart, pointB, 0, 3, 0);
	            drawElectricLine(batch, pointC, pointEnd, 0, 3, 0);
	            //drawline(batch,atlasRegion.originalHeight, pointStart.x,pointStart.y, pointB.x, pointB.y);	            
	            //drawline(batch,atlasRegion.originalHeight, pointC.x,pointC.y, pointEnd.x, pointEnd.y);
	            
            } else {
            	// TODO: Perhaps find alien only once? 
            	// find the alien in entities array
            	for(PhysicsEntityModel e: gameState.entities){
            		if (e.entityType == PhysicsEntityModel.Type.ALIEN) {
            			//ALIEN FOUND, call the elastic method
            			elasticController(gameState, batch, gameState.deflectorEndpoints[0], 
            					gameState.deflectorEndpoints[1],
            					e); //TODO:should not be in VIEW
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
    
    /*
     * Draws electric line between two points. 
     *	MaxGen: max subdivisions of line
     *	currGen: use 0 to start.
     *	dist: i forgot what it does. Perhaps controls the width of lines.   
     */
    private void drawElectricLine(SpriteBatch batch, DeflectorEndpoint pointA, DeflectorEndpoint pointB, int currGen, int maxGen, int dist){
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
    		drawElectricLine(batch, mid, branch, currGen+1, maxGen, dist+1);
    	}
    	
    	//recurse
    	drawElectricLine(batch, pointA, mid, currGen+1, maxGen, dist);
    	drawElectricLine(batch, mid, pointB, currGen+1, maxGen, dist+1);
    	
    }
    
    /*
     * Distance between line and point. -ve if line above point
     */
    private float getLinePointDistance(DeflectorEndpoint lineP1, DeflectorEndpoint lineP2, DeflectorEndpoint point){
    	float X = point.x; //the point
        float Y = point.y; //the point
    	//y=mx+c
        float M = (float)(lineP1.y-lineP2.y)/(float)(lineP1.x-lineP2.x);
        float C = lineP1.y-M*lineP1.x;
        //|-mx+y-c=0|/ sqrt((-m)^2+1)
        //float D = (float) (Math.abs(-M*X+Y-C)/Math.sqrt((-M)*(-M)+1)); //distance from line
        float D2 = (float) ((-M*X+Y-C)/Math.sqrt((-M)*(-M)+1)); //distance from line
    	return D2;
    }
    
    /*
     * does the physics stuff. TODO: should not be in VIEW.
     */
    private void elasticController(GameState gameState, SpriteBatch batch, DeflectorEndpoint pointA, DeflectorEndpoint pointB,
            PhysicsEntityModel alien){
    	
    	DeflectorEndpoint alienPoint = createPoint(alien.position.x, alien.position.y);
        float D2 = getLinePointDistance(pointA, pointB, alienPoint);//distance from rope
        
        float radius = alien.body.getFixtureList().first().getShape().getRadius()
        * GameConstants.PIXELS_PER_METER;
        
        //in contact
        if(Math.abs(D2)<radius){
        	gameState.deflecterContacted = true;
        }
        //contact ending.
        if(D2<-radius/2 && gameState.deflecterContacted){
        	gameState.deflected=true;
        	gameState.deflecterContacted = false;
        }
        
        if(gameState.deflected){
        	gameState.deflected = false;
        	alien.body.setLinearVelocity(0, 0);
        	//deflect now
        
        	//apply impulse
        	float xdiff = gameState.deflectorEndpoints[1].x
    				- gameState.deflectorEndpoints[0].x;
    		float ydiff = gameState.deflectorEndpoints[1].y
    				- gameState.deflectorEndpoints[0].y;
    		
    		// Always assume we're trying to bounce it upwards
    		// Weird corner case is if alien hits deflector from underneath,
    		// it will still bounce upwards
    		Vector2 impulseDir = new Vector2(ydiff, -xdiff);
    		if (impulseDir.y < 0) {
    			impulseDir.scl(-1);
    		}
    		
    		float desiredVel = impulseDir.len()*GameConstants.DEFLECTOR_IMPULSE;
    		Vector2 vel = alien.body.getLinearVelocity();

    		float velChange = desiredVel - vel.len();
    		float imp = alien.body.getMass() * velChange;

    		impulseDir.nor().scl(imp);
    		alien.body.applyLinearImpulse(impulseDir, alien.body.getWorldCenter(), true);
        }
    }
    
    
    //draw beam from deflector end points to alien tangent points    
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
        
        ///=== detect if deflector is in contact
        boolean deflectorInContact=false;
        
        DeflectorEndpoint alienPoint = createPoint(alien.position.x, alien.position.y);
        float D2 = getLinePointDistance(pointA, pointB, alienPoint);
        
        float radius = alien.body.getFixtureList().first().getShape().getRadius()
        * GameConstants.PIXELS_PER_METER;
        
        if(Math.abs(D2)<radius){
        	deflectorInContact = true;
        }
        ///===
        
        if(deflectorInContact){
            // Distances from alien to pointA
            float dx1 = alien.position.x - x1;
            float dy1 = alien.position.y - y1;

            // Distances from alien to pointB
            float dx2 = alien.position.x - x2;
            float dy2 = alien.position.y - y2;

            // Calculate exact distances (LEFT)
            float h1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
            float p1 = (float) Math.sqrt(h1 * h1 - r * r);

            // Calculate exact distances (RIGHT)
            float h2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
            float p2 = (float) Math.sqrt(h2 * h2 - r * r);

            // Dont continue if the alien touches points
            if (h1 < r || h2 < r) {
                return; //TODO: check if you want this
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

            DeflectorEndpoint b = pointAtThisTime(gameState, createPoint(x1,y1), createPoint(ix1, iy1), true);
            drawElectricLine(batch, b,createPoint(x1,y1), 0, 3, 0);
            
            //drawline(batch, atlasRegion.originalHeight / 2, x2, y2, ix2, iy2);
            DeflectorEndpoint c = pointAtThisTime(gameState, createPoint(ix2, iy2), createPoint(x2,y2), false);            
            drawElectricLine(batch, createPoint(x2, y2), c , 0, 3, 0);
            
        } else {
            // No collision detected... simply draw a line from pointA to pointB
            //drawline(batch, atlasRegion.originalHeight / 2, x1, y1, x2, y2);
        	DeflectorEndpoint b = pointAtThisTime(gameState,createPoint(x1,y1), createPoint(x2, y2), true);
        	drawElectricLine(batch, createPoint(x1,y1), b, 0, 3, 0);
        	
        	DeflectorEndpoint pointCenter = pointAtThisFraction(pointA, pointB, (float)0.5, true); //center point
            
            //the based on time since deflector tap
            DeflectorEndpoint point1 = pointAtThisTime(gameState, pointA, pointCenter, true);
            DeflectorEndpoint point2 = pointAtThisTime(gameState, pointCenter, pointB, false);
            
            //draw the electric lines
            drawElectricLine(batch, pointA, point1, 0, 3, 0);
            drawElectricLine(batch, point2, pointB, 0, 3, 0);
        	
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
    
    /* Based on the defector draw time, returns the position at which the point show be drawn.
     * this gives the animation effect of growing beam.
     */
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
