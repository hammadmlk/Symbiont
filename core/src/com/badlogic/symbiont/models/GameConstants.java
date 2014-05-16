package com.badlogic.symbiont.models;

/**
 * keep all constants not loaded from json in one location
 */
public class GameConstants {
    public static final int VIRTUAL_WIDTH = 480;
    public static final int VIRTUAL_HEIGHT = 800;
    public static final float PIXELS_PER_METER = 50;
    
    public static final int MENU_BAR_HEIGHT = 32;
    
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    
    public static final float DEFLECTOR_THICKNESS = 15 / PIXELS_PER_METER;
    public static final float DEFLECTOR_IMPULSE = 0.05f;
    public static final float DEFLECTOR_ENERGY = 0.001f;
    public static final float DEFLECTOR_DRAW_TIME = 15; // the number of frames it takes to draw the deflector
    
    public static final float powerupSpeed = 25;
    public static final float powerupScale = 0.6f;

    public static String PREFERENCES = "symbiont-preferences";
    
    public static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    
    public static final int NUM_STORY_PAGES = 5;
}
