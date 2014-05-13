package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.models.Animator;

public class SplashScreenView extends Actor {

    private Animator backgroundAnimation = new Animator(Assets.loadAnimation("homescreen_animation/mainscreen"));

    @Override
    public void act(float delta) {
        backgroundAnimation.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.draw(backgroundAnimation.getCurrentFrame(), 0, 0, getWidth(), getHeight());
    }
}
