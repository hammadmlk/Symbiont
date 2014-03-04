package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.symbiont.SymbiontMain;

public class GameInputListener extends InputListener {

    public GameInputListener() {
    }

    @Override
    public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
        SymbiontMain.gameState.started = true;
        touchDragged(event, screenX, screenY, pointer);
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
        if(pointer < 2){
            SymbiontMain.gameState.touches[pointer].x = 0;
            SymbiontMain.gameState.touches[pointer].y = 0;
            SymbiontMain.gameState.touches[pointer].touched = false;
        }
        SymbiontMain.gameState.touches[0].resetParticleEffect();
        SymbiontMain.gameState.touches[1].resetParticleEffect();
    }

    @Override
    public void touchDragged(InputEvent event, float screenX, float screenY, int pointer) {
        if(pointer < 2){
            SymbiontMain.gameState.touches[pointer].x = screenX;
            SymbiontMain.gameState.touches[pointer].y = screenY;
            SymbiontMain.gameState.touches[pointer].touched = true;
        }
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        touchDragged(event, x, y, 1);
        return true;
    }
}
