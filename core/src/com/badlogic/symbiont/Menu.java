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
import com.badlogic.symbiont.models.GameState;

public class Menu {
    public static Actor createMenu(Skin skin) {
        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table().top().left().padTop(20);
        table.setFillParent(true);

        final CheckBox debugCheckBox = new CheckBox("Debug", skin);
        table.add(debugCheckBox);
        final CheckBox editCheckBox = new CheckBox("Edit", skin);
        table.add(editCheckBox);
        final TextButton saveFileButton = new TextButton("Save", skin);
        table.add(saveFileButton);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(skin.get(Button.ButtonStyle.class));
        style.imageUp = new TextureRegionDrawable(Assets.loadAtlas("restart"));
        ImageButton loadFileButton = new ImageButton(style);
        table.add(loadFileButton).width(32).height(32);
        final TextField levelPath = new TextField(SymbiontMain.currentLevelFileName, skin);
        table.add(levelPath);
        levelPath.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char key) {
                SymbiontMain.currentLevelFileName = textField.getText();
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

        return table;
    }
}
