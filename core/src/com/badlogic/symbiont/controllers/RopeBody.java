package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.symbiont.models.GameConstants;

public class RopeBody {
	
    static final float batDensity = 1000;
    static final float batFriction = 0.2F;
    static final float batRestitution= 1F;
    static final float DEGTORAD = 0.0174532925F;
	
	PolygonShape boxShape;
	FixtureDef fixtureDef;
	BodyDef bodyDef;
	RevoluteJointDef revoluteJointDef;
	Body body;
	Body link;
	
	public Body[] MakeRope(float ropeStartX, float ropeStartY, float ropeEndX, float ropeEndY, World world){
		
		Body[] bodyArray = new Body[GameConstants.noOfJointsInRope];              
		// rope
	    for (int i = 0; i < GameConstants.noOfJointsInRope; i++) {
		  // rope segment
		  
		  bodyDef = new BodyDef();
		  if (i==0 || i==GameConstants.noOfJointsInRope-1){
			  bodyDef.type = BodyType.StaticBody;
		  }else{
			  bodyDef.type = BodyType.DynamicBody;
		  }
		  
		  float diffx = Math.abs(ropeEndX - ropeStartX)/GameConstants.noOfJointsInRope;
		  float diffy = Math.abs(ropeEndY - ropeStartY)/GameConstants.noOfJointsInRope;
		  
		  bodyDef.position.set(ropeStartX+(i*diffx*2),
				  			   ropeStartY+(i*diffy*2));
		  bodyDef.angle = (float) Math.atan(diffy/diffx);
		  bodyArray[i] = world.createBody(bodyDef);
		  boxShape = new PolygonShape();
		  boxShape.setAsBox(diffx, 10);
		  
		  fixtureDef=new FixtureDef();
		  fixtureDef.shape=boxShape;
		  fixtureDef.density=batDensity;
		  fixtureDef.friction=batFriction;
		  fixtureDef.restitution=batRestitution;
		  bodyArray[i].createFixture(fixtureDef);
		  //bodyArray[i].setTransform( bodyArray[i].getPosition(),
		  //(float) Math.atan(diffy/diffx) );
		  //link
		  if(i!=0){
		      revoluteJointDef = new RevoluteJointDef();
		      revoluteJointDef.bodyA = link;
		      revoluteJointDef.bodyB = bodyArray[i];
		      
		      revoluteJointDef.localAnchorA.set(+diffx,0);
		      revoluteJointDef.localAnchorB.set(-diffx,0);
		      
		      //revoluteJointDef.collideConnected = true;
		
		  //revoluteJointDef.enableLimit = true;
		  //revoluteJointDef.lowerAngle = -5 * DEGTORAD;
		  //revoluteJointDef.upperAngle =  5 * DEGTORAD;
		  
		  //revoluteJointDef.enableMotor = true;
		  //revoluteJointDef.motorSpeed = 10;
		  //revoluteJointDef.maxMotorTorque = 200;
		  world.createJoint(revoluteJointDef);
		  //body.setMassFromShapes();
		  }
		  link=bodyArray[i];
  
	    }
	    return bodyArray;
	}
	
	
	
	
}
