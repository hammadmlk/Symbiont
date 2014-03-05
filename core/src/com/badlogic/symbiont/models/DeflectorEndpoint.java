package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.symbiont.Assets;

public class DeflectorEndpoint {
    public float x;
    public float y;
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
}
