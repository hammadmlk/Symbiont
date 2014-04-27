package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Animator {

    private AnimationModel defaultAnimation;
    private int currentFrame = 0;
    private AnimationModel newAnimation = null;
    private float timeElapsed = 0;


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

    public void overrideAnimation(AnimationModel newAnimation) {
        assert newAnimation.frames != null;
        this.newAnimation = newAnimation;
    }

}
