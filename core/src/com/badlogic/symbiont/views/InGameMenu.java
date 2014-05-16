package com.badlogic.symbiont.views;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.symbiont.Assets;
import com.badlogic.symbiont.controllers.GameEngine;
import com.badlogic.symbiont.models.GameConstants;
import com.badlogic.symbiont.models.GameState;

public class InGameMenu extends Table {
    
    /**
     * is the menu visible
     */
    private boolean menuIsVisible = false;

    private EnergyBarView energyBarView;
    private GameEngine gameEngine;
    
    private final Table menuWindow;

    public InGameMenu(Skin skin, final GameEngine gameEngine) {
        super(skin);
        
        this.gameEngine = gameEngine;
        energyBarView = new EnergyBarView();
        
        // Create a table (mainTable) consisting of upperTable and menuwindow 
        // that fills the screen. Everything else will go inside this table.
        // A good table guide at: 
        //      https://github.com/EsotericSoftware/tablelayout
        // TODO padding needs to get updated on resize
        
        
        menuWindow = new Table();
        final Table upperTable = new Table();
        
        //========== upperTable
        TextureRegionDrawable blackBg = new TextureRegionDrawable(Assets.loadAtlas("blacksmall"));
        upperTable.setBackground(blackBg);
        upperTable.setPosition(0,GameConstants.VIRTUAL_HEIGHT-GameConstants.MENU_BAR_HEIGHT);
        upperTable.left().top(); //internal left top align
        
        
        // === add things to upperTable
        //menu button
        TextureRegionDrawable menuImageUp = new TextureRegionDrawable(Assets.loadAtlas("menu"));
        final ImageButton menuImageButton = new ImageButton(menuImageUp);
        upperTable.add(menuImageButton).width(100).height(GameConstants.MENU_BAR_HEIGHT).expandX().left();
        
        //reload button --- TODO: different image down.
        TextureRegionDrawable reloadImageUp = new TextureRegionDrawable(Assets.loadAtlas("reload"));
        TextureRegionDrawable reloadImageDown = new TextureRegionDrawable(Assets.loadAtlas("reload"));
        ImageButton reloadButton = new ImageButton(reloadImageUp, reloadImageDown);
        upperTable.add(reloadButton).width(GameConstants.MENU_BAR_HEIGHT).height(GameConstants.MENU_BAR_HEIGHT);
        
        //=== upperTable listeners
        menuImageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleMenu();
            }
        });

        reloadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameEngine.reloadLevel();
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
        menuWindow.setVisible(menuIsVisible);
        
        //===add things to menuwindow
//        final CheckBox elasticDeflectorCheckBox = new CheckBox("ElasticDeflector", skin);
//        menuWindow.add(elasticDeflectorCheckBox);
//        final CheckBox debugCheckBox = new CheckBox("Debug", skin);
//        menuWindow.add(debugCheckBox);
//        menuWindow.row();
//        final CheckBox editCheckBox = new CheckBox("Edit", skin);
//        menuWindow.add(editCheckBox);
//        menuWindow.row();
//        final TextButton saveFileButton = new TextButton("Save", skin);
//        menuWindow.add(saveFileButton);
//        menuWindow.row();
        
        final LevelSelectView levelSelect = new LevelSelectView(gameEngine, this);
        menuWindow.add(levelSelect);
        menuWindow.row();
        
        
//        elasticDeflectorCheckBox.addListener(new ChangeListener() {
//            public void changed(ChangeEvent event, Actor actor) {
//            	gameEngine.elasticDeflector = !gameEngine.elasticDeflector;
//            }
//        });
//
//        debugCheckBox.addListener(new ChangeListener() {
//            public void changed(ChangeEvent event, Actor actor) {
//                gameEngine.debug = !gameEngine.debug;
//            }
//        });
//
//        editCheckBox.addListener(new ChangeListener() {
//            public void changed(ChangeEvent event, Actor actor) {
//                gameEngine.edit = !gameEngine.edit;
//                if (!gameEngine.edit) {
//                    if (gameEngine.world != null) {
//                        gameEngine.world.dispose();
//                    }
//                    gameEngine.world = new World(new Vector2(0, -10), true);
//                    gameEngine.world.setContactListener(new GameContactListener(gameEngine));
//                    gameEngine.gameState = GameState.fromJSON(gameEngine.levelEditor.editorGameState.toJSON());
//                    gameEngine.gameState.addToWorld(gameEngine.world);
//                    gameEngine.gameView.clearListeners();
//                    gameEngine.gameView.addListener(gameEngine.gameInputListener);
//                } else {
//                    gameEngine.gameView.clearListeners();
//                    gameEngine.gameView.addListener(gameEngine.levelEditor);
//                }
//            }
//        });
//        
//        saveFileButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                FileHandle fileHandle = Gdx.files.local("levels/"
//                        + gameEngine.getCurrentLevelName() + ".json");
//                fileHandle.writeString(
//                        gameEngine.levelEditor.editorGameState.toJSON(),
//                        false);
//            }
//        });
        //======== end menuWindow
        
        this.setWidth(GameConstants.VIRTUAL_WIDTH);
        this.setHeight(GameConstants.VIRTUAL_HEIGHT);
        this.setPosition(0,0);
        this.left().top(); //internal left top align
        
        int w = GameConstants.VIRTUAL_WIDTH;
        this.add(upperTable).width(w).height(GameConstants.MENU_BAR_HEIGHT);
        this.row();
        this.add(menuWindow).width(w).expandY();
    }
    
    
    public void toggleMenu() {
        menuIsVisible = !menuIsVisible;
        menuWindow.setVisible(menuIsVisible);
    }
    
    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        
        GameState gameState = gameEngine.edit ? gameEngine.levelEditor.editorGameState : gameEngine.gameState;
        energyBarView.render(batch, gameState);
    }
}
