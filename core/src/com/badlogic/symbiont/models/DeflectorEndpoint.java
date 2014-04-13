package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.symbiont.Assets;

public class DeflectorEndpoint {
    public float x;
    public float y;
    /**
     * whether this endpoint is being touched or not
     */
    public boolean active = false;

    private ParticleEffect particleEffect;

    public ParticleEffect getParticleEffect() {
        if (particleEffect != null) {
            return particleEffect;
        }
        particleEffect = Assets.getParticleEffect("deflector");
        return particleEffect;
    }

    public void resetParticleEffect() {
        getParticleEffect().reset();
    }

    /**
     *
     * @param delta time passed since last animation frame
     */
    public void update(float delta) {
        getParticleEffect().update(delta);
    }
}
