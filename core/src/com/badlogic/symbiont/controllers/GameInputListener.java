package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class GameInputListener extends InputListener {

    public class TouchInfo {
        public float x;
        public float y;
        public boolean touched = false;
    }

    public TouchInfo[] touches;

    public GameInputListener() {
        touches = new TouchInfo[2];
        for(int i = 0; i < 2; i++){
            touches[i] = new TouchInfo();
        }
    }

    @Override
    public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
        touchDragged(event, screenX, screenY, pointer);
        return true;
    }

    @Override
    public void touchUp(InputEvent event, float screenX, float screenY, int pointer, int button) {
        if(pointer < 2){
            touches[pointer].x = 0;
            touches[pointer].y = 0;
            touches[pointer].touched = false;
        }
    }

    @Override
    public void touchDragged(InputEvent event, float screenX, float screenY, int pointer) {
        if(pointer < 2){
            touches[pointer].x = screenX;
            touches[pointer].y = screenY;
            touches[pointer].touched = true;
        }
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        touchDragged(event, x, y, 1);
        return true;
    }
}
