package com.badlogic.symbiont;

import com.badlogic.gdx.Game;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.controllers.MainMenuScreen;

public class SymbiontMain extends Game {

    private GameEngine gameScreen;
    private MainMenuScreen mainMenuScreen;

    @Override
    public void create() {
        gameScreen = new GameEngine(this);
        mainMenuScreen = new MainMenuScreen(this);
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
    }
    
    public void showGameScreen() {
        this.setScreen(gameScreen);
    }
    
    public void showMainMenuScreen() {
        this.setScreen(mainMenuScreen);
    }

}
