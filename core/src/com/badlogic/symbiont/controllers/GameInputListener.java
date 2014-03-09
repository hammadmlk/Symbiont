package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.symbiont.SymbiontMain;

public class GameInputListener extends InputListener {

    @Override
    public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
        SymbiontMain.gameState.startIfWaiting();
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            System.out.println(String.format("{\"x\": %f, \"y\": %f}", screenX, screenY));
        } else if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            System.out.println(String.format("%f, %f,", screenX, screenY));
        }
        touchDragged(event, screenX, screenY, pointer);
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
        if(pointer < 2){
            SymbiontMain.gameState.deflectorEndpoints[pointer].active = false;
        }
        SymbiontMain.gameState.deflectorEndpoints[0].resetParticleEffect();
        SymbiontMain.gameState.deflectorEndpoints[1].resetParticleEffect();
    }

    @Override
    public void touchDragged(InputEvent event, float screenX, float screenY, int pointer) {
        if(pointer < 2){
            SymbiontMain.gameState.setDeflectorEndpoint(screenX, screenY, pointer);
        }
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        touchDragged(event, x, y, 1);
        return true;
    }
}
