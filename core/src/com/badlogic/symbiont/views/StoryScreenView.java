package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.models.GameConstants;

public class StoryScreenView extends Actor {
    
    private int curPage;
    
    private TextureRegionDrawable[] storyImages = new TextureRegionDrawable[GameConstants.NUM_STORY_PAGES];
    
    public StoryScreenView() {
        for (int i = 0; i < GameConstants.NUM_STORY_PAGES; i++) {
            storyImages[i] = new TextureRegionDrawable(Assets.loadAtlas("intro_story"+i));
        }
        curPage = 0;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.draw(storyImages[curPage].getRegion(), 0, 0, getWidth(), getHeight());
    }
    
    /**
     * Increments the page counter
     * @return the index of the next page (after incrementing)
     */
    public int nextPage() {
        if (curPage < GameConstants.NUM_STORY_PAGES) {
            curPage++;
        }
        return curPage;
    }
}
