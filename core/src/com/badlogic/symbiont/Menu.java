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
    public static boolean menuIsVisible = false;

    public static Actor createMenu(Skin skin) {
        // Create a table that fills the screen. Everything else will go inside this table.
        Table mainTable = new Table().top().left();
        mainTable.setFillParent(true);
        mainTable.debug();
        final Table upperTable = new Table();

        final Window menuWindow = new Window("Menu", skin);
        menuWindow.setMovable(false);
        menuWindow.setVisible(menuIsVisible);
        menuWindow.setModal(menuIsVisible);

        final TextButton menuTextButton = new TextButton("Menu", skin);
        menuTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuIsVisible = true;
                upperTable.setVisible(!menuIsVisible);
                menuWindow.setVisible(menuIsVisible);
                menuWindow.setModal(menuIsVisible);
            }
        });

        final TextButton returnTextButton = new TextButton("Return", skin);
        returnTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuIsVisible = false;
                upperTable.setVisible(!menuIsVisible);
                menuWindow.setVisible(menuIsVisible);
                menuWindow.setModal(menuIsVisible);
            }
        });

        upperTable.add(menuTextButton);

        final CheckBox debugCheckBox = new CheckBox("Debug", skin);
        menuWindow.add(debugCheckBox);
        menuWindow.row();
        final CheckBox editCheckBox = new CheckBox("Edit", skin);
        menuWindow.add(editCheckBox);
        menuWindow.row();
        final TextButton saveFileButton = new TextButton("Save", skin);
        menuWindow.add(saveFileButton);
        menuWindow.row();
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(Button.ButtonStyle.class));
        style.imageUp = new TextureRegionDrawable(Assets.loadAtlas("restart"));
        ImageButton loadFileButton = new ImageButton(style);
        upperTable.add(loadFileButton).width(32).height(32);
        final List levelPath = new List(new String[] {"first", "second"}, skin);
        menuWindow.add(levelPath);
        menuWindow.row();

        levelPath.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.currentLevelFileName = levelPath.getSelection();
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

        loadFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SymbiontMain.loadFile();
            }
        });

        saveFileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                FileHandle fileHandle = Gdx.files.local("levels/" + SymbiontMain.currentLevelFileName + ".json");
                fileHandle.writeString(SymbiontMain.levelEditor.editorGameState.toJSON(), false);
            }
        });

        menuWindow.row();
        menuWindow.add(returnTextButton);

        mainTable.add(upperTable);
        mainTable.row();
        mainTable.add(menuWindow).width(GameConstants.VIRTUAL_WIDTH);

        return mainTable;
    }
}
