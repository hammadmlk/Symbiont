package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.symbiont.Assets;

public class TutorialModel {
    public String animation;

    private transient Animator animator;

    private Animator getAnimator() {
        if (animator == null) {
            animator = new Animator(Assets.loadAnimation(animation));
        }
        return animator;
    }

    public void update(float delta) {
        getAnimator().update(delta);
    }

    public TextureAtlas.AtlasRegion getFrame() {
        return getAnimator().getCurrentFrame();
    }
}
