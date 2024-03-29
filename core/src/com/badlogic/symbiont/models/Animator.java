package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class Animator {

    private AnimationModel defaultAnimation;
    private int currentFrame = 0;
    private AnimationModel newAnimation = null;
    private float timeElapsed = 0;

    /*
     * fading stuff
     */
    class Fader {
        float duration, timeElapsed;

        boolean fading, faded;

        void update(float delta) {
            if (!fading) {
                return;
            }
            timeElapsed += delta;
            if (timeElapsed > duration) {
                fading = false;
                faded = true;
            }
        }

        float getAlpha() {
            return fading ? 1 - timeElapsed / duration : faded ? 0 : 1;
        }
    }

    /*
     * shaking stuff
     */
    class Shaker {
        float dx, dy, period, duration, timeElapsed;

        boolean stillShaking;

        Vector2 displacement = new Vector2();

        void update(float delta) {
            if (!stillShaking) {
                return;
            }
            timeElapsed += delta;
            if (timeElapsed > duration) {
                stillShaking = false;
            }
        }

        Vector2 getDisplacement() {
            if (!stillShaking) {
                displacement.x = 0;
                displacement.y = 0;
            } else {
                double theta = 2 * Math.PI * timeElapsed / period;
                displacement.x = dx * (float) Math.cos(theta);
                displacement.y = dy * (float) Math.sin(theta);
            }
            return displacement;
        }
    }

    private Shaker shaker = new Shaker();

    private Fader fader = new Fader();

    public Animator(AnimationModel defaultAnimation) {

        assert defaultAnimation.frames != null;
        this.defaultAnimation = defaultAnimation;
    }

    private AnimationModel currentAnimation() {
        if (newAnimation != null) {
            return newAnimation;
        }
        return defaultAnimation;
    }

    public TextureAtlas.AtlasRegion getCurrentFrame() {
        return currentAnimation().frames[currentFrame];
    }

    public void update(float delta) {
        shaker.update(delta);
        fader.update(delta);
        timeElapsed += delta;
        if (timeElapsed > currentAnimation().delta) {
            timeElapsed -= currentAnimation().delta;
            currentFrame++;
            if (currentFrame == currentAnimation().numFrames) {
                if (currentAnimation() == newAnimation) {
                    newAnimation = null;
                    currentFrame = 0;
                    timeElapsed = 0;
                } else {
                    currentFrame = 0;
                }
            }
        }
    }

    /**
     * start a shaking effect
     * @param dx x displacement
     * @param dy y displacement
     * @param period how long to shake once
     * @param duration how long to shake it
     */
    public void shake(float dx, float dy, float period, float duration) {
        shaker.stillShaking = true;
        shaker.timeElapsed = 0;
        shaker.dx = dx;
        shaker.dy = dy;
        shaker.period = period;
        shaker.duration = duration;
    }

    /**
     * start a fading effect
     * @param duration
     */
    public void fade(float duration) {
        fader.duration = duration;
        fader.timeElapsed = 0;
        fader.fading = true;
        fader.faded = false;
    }

    /**
     * done fading, and fade has been called at least once
     * @return
     */
    public boolean faded(){
        return fader.faded;
    }

    public float getAlpha() {
        return fader.getAlpha();
    }

    /**
     * not a new object #memorymanagement
     */
    public Vector2 getShakerDisplacement() {
        return shaker.getDisplacement();
    }

    public void overrideAnimation(AnimationModel newAnimation) {
        // can't override an animation if it's already being overridden
        if (this.newAnimation != null) {
            return;
        }
        assert newAnimation.frames != null;
        this.newAnimation = newAnimation;
        currentFrame = 0;
    }

}
