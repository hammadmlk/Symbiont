package com.badlogic.symbiont.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.models.GameConstants;

public class LevelSelectView extends Table {

    public LevelSelectView(final GameEngine gameEngine) {
        
        // Create a table (mainTable) consisting of upperTable and menuwindow 
        // that fills the screen. Everything else will go inside this table.
        // A good table guide at: 
        //      https://github.com/EsotericSoftware/tablelayout
        // TODO padding needs to get updated on resize
        
        //========== upperTable
        TextureRegionDrawable blackBg = new TextureRegionDrawable(Assets.loadAtlas("blacksmall"));
        this.setBackground(blackBg);
        this.setPosition(0,0);
        this.setWidth(GameConstants.VIRTUAL_WIDTH);
        this.setHeight(GameConstants.VIRTUAL_HEIGHT);
        this.left().top(); //internal left top align
        this.pad(20);
        
        
        // === add things to upperTable
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final int index = i*4 + j;
                TextureRegionDrawable lvlImg = new TextureRegionDrawable(Assets.loadAtlas("lv"+(index+1)));
                final ImageButton lvlButton = new ImageButton(lvlImg);
                lvlButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        gameEngine.loadLevel(index);
                    }
                });
                this.add(lvlButton).width(GameConstants.LVL_IMAGE_SIZE).height(GameConstants.LVL_IMAGE_SIZE);
            }
            this.row();
        }
    }

}
