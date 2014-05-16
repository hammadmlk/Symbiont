package com.badlogic.symbiont.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.SymbiontMain;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.views.SplashScreenView;

public class MainMenuScreen implements Screen {
    
    private final SymbiontMain game;
    private final Stage stage;
    
    public MainMenuScreen(final SymbiontMain game) {
        this.game = game;
        stage = new Stage();

        stage.addActor(getBackgroundActor());
        stage.addActor(getMenuActor());
    }

    private Actor getBackgroundActor() {
        final Table table = new Table();

        table.setWidth(GameConstants.VIRTUAL_WIDTH);
        table.setHeight(GameConstants.VIRTUAL_HEIGHT);
        table.setPosition(0, 0);
        table.center();

        SplashScreenView splashScreenView = new SplashScreenView();
        splashScreenView.setBounds(0, 0, GameConstants.VIRTUAL_WIDTH, GameConstants.VIRTUAL_HEIGHT);
        table.add(splashScreenView);
        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showStoryScreen();
            }
        });

        return table;
    }
    
    private Actor getMenuActor() {
        final Table table = new Table();
        
        
        //========== upperTable
        table.setWidth(GameConstants.VIRTUAL_WIDTH);
        table.setHeight(GameConstants.VIRTUAL_HEIGHT);
        table.setPosition(0, 0);
        table.center();
        
        // === add things to upperTable
        //menu button
        TextureRegionDrawable menuImageUp = new TextureRegionDrawable(Assets.loadAtlas("mainmenu"));
        final ImageButton menuImageButton = new ImageButton(menuImageUp);
        table.add(menuImageButton);
        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showStoryScreen();
            }
        });
        //=== upperTable listeners
        
        return table;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
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
        Gdx.input.setInputProcessor(stage);

        Assets.playSong("song0.mp3");
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
        if (stage != null) {
            stage.dispose();
        }
    }

}
