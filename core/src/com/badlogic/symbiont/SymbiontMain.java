package com.badlogic.symbiont;

import com.badlogic.gdx.Game;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.controllers.MainMenuScreen;
import com.badlogic.symbiont.controllers.StoryScreen;

public class SymbiontMain extends Game {

    private GameEngine gameScreen;
    private MainMenuScreen mainMenuScreen;
    private StoryScreen storyScreen;

    @Override
    public void create() {
        gameScreen = new GameEngine(this);
        mainMenuScreen = new MainMenuScreen(this);
        storyScreen = new StoryScreen(this);
        showMainMenuScreen();
    }

    /**
     * main game loop. called once per animation frame
     */
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        gameScreen.dispose();
        mainMenuScreen.dispose();
    }
    
    public void showStoryScreen() {
        this.setScreen(storyScreen);
    }
    
    public void showGameScreen() {
        this.setScreen(gameScreen);
    }
    
    public void showMainMenuScreen() {
        this.setScreen(mainMenuScreen);
    }

}
