package com.badlogic.symbiont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.controllers.GameContactListener;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;

public class Menu {
    /**
     * is the menu visible
     */
    public static boolean menuIsVisible = false;

    /**
     * create the menu
     * @param skin
     * @return
     */
    public static Actor createMenu(Skin skin) {
        // Create a table (mainTable) consisting of upperTable and menuwindow 
    	// that fills the screen. Everything else will go inside this table.
        // A good table guide at: 
    	//  	https://github.com/EsotericSoftware/tablelayout
    	// TODO padding needs to get updated on resize
    	
    	
    	final Window menuWindow = new Window("Menu", skin);
        final Table upperTable = new Table();
        
        
    	//========== upperTable
        upperTable.setWidth(GameConstants.VIRTUAL_WIDTH);
        upperTable.setHeight(GameConstants.VIRTUAL_HEIGHT);
        upperTable.setPosition(0,0);
        upperTable.left().top(); //internal left top align
        
        // === add things to upperTable
        //menu button
        TextureRegionDrawable menuImageUp = new TextureRegionDrawable(Assets.loadAtlas("menu"));
        final ImageButton menuImageButton = new ImageButton(menuImageUp);
        upperTable.add(menuImageButton).width(100).height(32).expandX().left();
        
        //reload button --- TODO: different image down.
        TextureRegionDrawable reloadImageUp = new TextureRegionDrawable(Assets.loadAtlas("reload"));
        TextureRegionDrawable reloadImageDown = new TextureRegionDrawable(Assets.loadAtlas("reload"));
        ImageButton reloadButton = new ImageButton(reloadImageUp, reloadImageDown);
        upperTable.add(reloadButton).width(32).height(32);
        
        //=== upperTable listeners
        menuImageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuIsVisible = true;
                upperTable.setVisible(!menuIsVisible);
                menuWindow.setVisible(menuIsVisible);
                menuWindow.setModal(menuIsVisible);
            }
        });

        reloadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.reloadLevel();
            }
        });
        //============ end upperTable
        
        
        // ====== menuWindow
        //menuWindow.setFillParent(true);
        menuWindow.setWidth(GameConstants.VIRTUAL_WIDTH);
        menuWindow.setHeight(GameConstants.VIRTUAL_HEIGHT);
        menuWindow.setPosition(0,0);
        menuWindow.center(); //internal left top align
        
        //menuWindow.setPosition(0, 0);
        menuWindow.setMovable(false);
        menuWindow.setVisible(menuIsVisible);
        menuWindow.setModal(menuIsVisible);
        
        //===add things to menuwindow
        final CheckBox elasticDeflectorCheckBox = new CheckBox("ElasticDeflector", skin);
        menuWindow.add(elasticDeflectorCheckBox);
        final CheckBox debugCheckBox = new CheckBox("Debug", skin);
        menuWindow.add(debugCheckBox);
        menuWindow.row();
        final CheckBox editCheckBox = new CheckBox("Edit", skin);
        menuWindow.add(editCheckBox);
        menuWindow.row();
        final TextButton saveFileButton = new TextButton("Save", skin);
        menuWindow.add(saveFileButton);
        menuWindow.row();
        final List levelPath = new List(Assets.constantsConfigLoader.listOfLevels, skin);
        menuWindow.add(levelPath);
        menuWindow.row();
        final TextButton returnTextButton = new TextButton("Return", skin);
        menuWindow.add(returnTextButton);
        menuWindow.row();
        
        // === MenuWindow Listeners         
        returnTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuIsVisible = false;
                upperTable.setVisible(!menuIsVisible);
                menuWindow.setVisible(menuIsVisible);
                menuWindow.setModal(menuIsVisible);
            }
        });
        
        levelPath.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.loadLevel(levelPath.getSelectedIndex());
            }
        });
        
        elasticDeflectorCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
            	SymbiontMain.elasticDeflector = !SymbiontMain.elasticDeflector;
            }
        });

        debugCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.debug = !SymbiontMain.debug;
            }
        });

        editCheckBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.edit = !SymbiontMain.edit;
                if (!SymbiontMain.edit) {
                    if (SymbiontMain.world != null) {
                        SymbiontMain.world.dispose();
                    }
                    SymbiontMain.world = new World(new Vector2(0, -10), true);
                    SymbiontMain.world.setContactListener(new GameContactListener());
                    SymbiontMain.gameState = GameState.fromJSON(SymbiontMain.levelEditor.editorGameState.toJSON());
                    SymbiontMain.gameState.addToWorld(SymbiontMain.world);
                    SymbiontMain.gameView.clearListeners();
                    SymbiontMain.gameView.addListener(SymbiontMain.gameInputListener);
                } else {
                    SymbiontMain.gameView.clearListeners();
                    SymbiontMain.gameView.addListener(SymbiontMain.levelEditor);
                }
            }
        });
        
        saveFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                FileHandle fileHandle = Gdx.files.local("levels/"
                        + SymbiontMain.getCurrentLevelName() + ".json");
                fileHandle.writeString(
                        SymbiontMain.levelEditor.editorGameState.toJSON(),
                        false);
            }
        });
        //======== end menuWindow
        

        
        //=== mainTable
        Table mainTable = new Table();
        
        mainTable.setWidth(GameConstants.VIRTUAL_WIDTH);
        mainTable.setHeight(GameConstants.VIRTUAL_HEIGHT);
        mainTable.setPosition(0,0);
        mainTable.left().top(); //internal left top align
        
        int w=GameConstants.VIRTUAL_WIDTH;
        mainTable.add(upperTable).width(w).height(50);
        mainTable.row();
        mainTable.add(menuWindow).width(w).expandY();
        
        return mainTable;
        //return upperTable;
    }
}
