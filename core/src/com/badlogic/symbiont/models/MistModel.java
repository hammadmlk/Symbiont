package com.badlogic.symbiont.models;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.symbiont.Assets;


public class MistModel {

    /**
     * rectangle the mist covers
     */
    public Rectangle rectangle;

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
    public float fadeTime = 2;

    /**
     * how many seconds until the mist is gone?
     * (if it's fading)
     */
    public float secondsLeft = 2;

    private transient ParticleEffect mistEffect;

    public static final Color NORMAL_COLOR = new Color(0.44f, 0.28f, 0.79f, 0.10f);
    public static final Color FEEDBACK_COLOR = new Color(0.74f, 0.18f, 0.29f, 0.25f);

    public Color color = NORMAL_COLOR;

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

    public boolean contains(float x, float y) {
        return rectangle.contains(x, y);
    }

    public void update(float delta) {
        getMistEffect().update(delta);
    }
}
