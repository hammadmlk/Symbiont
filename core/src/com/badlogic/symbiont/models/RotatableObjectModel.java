package com.badlogic.symbiont.models;

public class RotatableObjectModel extends PhysicsEntityModel {
    
    public float rotatingVelocity, startAngle, endAngle;

    @Override
    public void update() {
        float curAngle = body.getAngle();
        if (body.getAngularVelocity() == 0 || curAngle < startAngle) {
            body.setAngularVelocity(rotatingVelocity);
        } else if (curAngle > endAngle) {
            body.setAngularVelocity(-rotatingVelocity);
        }
        
        super.update();
    }
}
