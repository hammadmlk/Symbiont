package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.Assets;

public class Mist {

    /**
     * vertices of the polygon the mist covers
     */
    public float[] vertices;

    /**
     * position the mist emanates from.
     */
    public Vector2 position;

    private transient ParticleEffect mistEffect;

    public ParticleEffect getMistEffect() {
        if (mistEffect != null) {
            return mistEffect;
        }
        mistEffect = Assets.getParticleEffect("mist");
        mistEffect.setPosition(position.x, position.y);
        return mistEffect;
    }
}
