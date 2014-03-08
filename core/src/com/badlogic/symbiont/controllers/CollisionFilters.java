package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.physics.box2d.Filter;

public class CollisionFilters {
    public static final Filter BROKEN = new Filter();
    public static final Filter GROUND = new Filter();
    static {
        BROKEN.categoryBits = 0x0001;
        BROKEN.maskBits     = 0x0002; // only collides with ground

        GROUND.categoryBits = 0x0002;
    }
}
