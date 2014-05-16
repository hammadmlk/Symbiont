package com.badlogic.symbiont.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.models.GameConstants;

public class LevelSelectView extends Table {
    
    private boolean muted = false;
    private final ImageButton soundOnButton;
    private final ImageButton soundOffButton;

    public LevelSelectView(final GameEngine gameEngine, final InGameMenu menu) {
        
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
        
        // Resume button
        TextureRegionDrawable resumeImg = new TextureRegionDrawable(Assets.loadAtlas("resume_title"));
        final ImageButton resumeButton = new ImageButton(resumeImg);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menu.toggleMenu();
            }
        });
        this.add(resumeButton).height(GameConstants.LVL_IMAGE_SIZE).colspan(4);
        this.row();
        
        // Level select title
        TextureRegionDrawable levelSelectImg = new TextureRegionDrawable(Assets.loadAtlas("level_select_title"));
        final Image levelSelectText = new Image(levelSelectImg);
        this.add(levelSelectText).height(GameConstants.LVL_IMAGE_SIZE).width(GameConstants.LVL_IMAGE_SIZE*4).colspan(4);
        this.row();
        
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
        // Sound button stack
        Stack soundStack = new Stack();
        // Sound on button
        TextureRegionDrawable soundOnImg = new TextureRegionDrawable(Assets.loadAtlas("sound_on"));
        soundOnButton = new ImageButton(soundOnImg);
        soundOnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleSound();
            }
        });
        soundStack.add(soundOnButton);
        
        // Sound off button
        TextureRegionDrawable soundOffImg = new TextureRegionDrawable(Assets.loadAtlas("sound_off"));
        soundOffButton = new ImageButton(soundOffImg);
        soundOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleSound();
            }
        });
        soundStack.add(soundOffButton);
        soundOffButton.setVisible(false);
        this.add(soundStack).height(GameConstants.LVL_IMAGE_SIZE).colspan(4);
        this.row();
        
        // Quit button
        TextureRegionDrawable quitImg = new TextureRegionDrawable(Assets.loadAtlas("quit_title"));
        final ImageButton quitButton = new ImageButton(quitImg);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameEngine.quitGame();
            }
        });
        this.add(quitButton).height(GameConstants.LVL_IMAGE_SIZE).colspan(4);
        this.row();
    }
    
    private void toggleSound() {
        muted = !muted;
        Assets.mute(muted);
        soundOnButton.setVisible(!muted);
        soundOffButton.setVisible(muted);
    }

}
