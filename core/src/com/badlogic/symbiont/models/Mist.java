package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.Assets;

public class Mist {

    /**
     * plants have a lists of ids of mist they own
     */
    public int id;

    /**
     * vertices of the polygon the mist covers
     */
    public float[] vertices;

    /**
     * position the mist emanates from.
     */
    public Vector2 position;

    /**
     * is this mist fading out?
     */
    public boolean fading = false;

    /**
     * how long does it take for the mist to fade?
     */
    public float fadeTime = 5;

    /**
     * how many seconds until the mist is gone?
     * (if it's fading)
     */
    public float secondsLeft = 5;

    private transient ParticleEffect mistEffect;

    public ParticleEffect getMistEffect() {
        if (mistEffect != null) {
            return mistEffect;
        }
        mistEffect = Assets.getParticleEffect("mist");
        mistEffect.setPosition(position.x, position.y);
        return mistEffect;
    }

    public void startFading() {
        fading = true;
        secondsLeft = fadeTime;
        // no more emitting
        for (ParticleEmitter particleEmitter : getMistEffect().getEmitters()) {
            particleEmitter.setContinuous(false);
        }
    }
}
