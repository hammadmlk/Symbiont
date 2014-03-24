package com.badlogic.symbiont.models;

public class GameConstants {
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 800;
    public static final float PIXELS_PER_METER = 50;
    
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    
    public static final float DEFLECTOR_THICKNESS = 10 / PIXELS_PER_METER;
    public static final float DEFLECTOR_IMPULSE = 0.05f;
    public static final float DEFLECTOR_ENERGY = 0.001f;
    
    public static final float powerupSpeed = 25;
    public static final float powerupScale = 0.6f;
    
    //rope simulation constants
    public static final int noOfJointsInRope = 20;
    
}
