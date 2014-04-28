package com.badlogic.symbiont;

import com.badlogic.gdx.Game;
import com.badlogic.symbiont.controllers.GameEngine;

public class SymbiontMain extends Game {

    private GameEngine gameScreen;

    @Override
    public void create() {
        gameScreen = new GameEngine();
        this.setScreen(gameScreen);
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

}
