package com.badlogic.symbiont.models;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AnimationModel {
    public int numFrames = 1;
    public float delta = 1 / 10f;

    public transient TextureAtlas.AtlasRegion[] frames;
}
