package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.symbiont.models.GameState;

public class GameInputListener extends InputListener {
    
    private final GameEngine gameEngine;
    
    public GameInputListener(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
        startIfWaiting();
        touchDragged(event, screenX, screenY, pointer);
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
        if(pointer < 2){
            gameEngine.gameState.deflectorEndpoints[pointer].active = false;
            gameEngine.gameState.deflectorEndpoints[0].resetParticleEffect();
            gameEngine.gameState.deflectorEndpoints[1].resetParticleEffect();
            gameEngine.gameState.resetMistColors();
        }
    }

    @Override
    public void touchDragged(InputEvent event, float screenX, float screenY, int pointer) {
        if(pointer < 2){
            gameEngine.gameState.setDeflectorEndpoint(screenX, screenY, pointer);
        }
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        touchDragged(event, x, y, 1);
        return true;
    }
    
    // TODO this probably shouldn't be done like this
    public void startIfWaiting() {
        GameState.State currentState = gameEngine.gameState.state;
        if (currentState == GameState.State.PLAYING) {
            return;
        } else if (currentState == GameState.State.WAITING_TO_START) {
            gameEngine.gameState.state = GameState.State.PLAYING;
        } else if (currentState == GameState.State.LOST) {
            gameEngine.gameState.state = GameState.State.PLAYING;
            gameEngine.reloadLevel();
        } else if (currentState == GameState.State.WON) {
            if (gameEngine.getCurrentLevelNum() + 1 < gameEngine.getNumberOfLevels()) {
                gameEngine.gameState.state = GameState.State.PLAYING;
                gameEngine.loadLevel(gameEngine.getCurrentLevelNum() + 1);
            }
        }
    }
}
