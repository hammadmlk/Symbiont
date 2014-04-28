package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;

public class MainMenuScreen implements Screen {
    
    private final SymbiontMain game;
    private final Stage stage;
    
    public MainMenuScreen(final SymbiontMain game) {
        this.game = game;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        stage.addActor(getMenuActor());
    }
    
    private Actor getMenuActor() {
        final Table upperTable = new Table();
        
        
        //========== upperTable
        upperTable.setWidth(GameConstants.VIRTUAL_WIDTH);
        upperTable.setHeight(GameConstants.VIRTUAL_HEIGHT);
        upperTable.setPosition(0,0);
        upperTable.center();
        
        // === add things to upperTable
        //menu button
        TextureRegionDrawable menuImageUp = new TextureRegionDrawable(Assets.loadAtlas("mainmenu"));
        final ImageButton menuImageButton = new ImageButton(menuImageUp);
        upperTable.add(menuImageButton);
        
        //=== upperTable listeners
        menuImageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.showGameScreen();
            }
        });
        
        return upperTable;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(GameConstants.VIRTUAL_WIDTH,
                GameConstants.VIRTUAL_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(),
                -stage.getGutterHeight(), 0);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

}
