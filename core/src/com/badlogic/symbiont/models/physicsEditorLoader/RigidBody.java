package com.badlogic.symbiont.models.physicsEditorLoader;

import com.badlogic.gdx.math.*;

/**
 * POJO mirroring the JSON schema outputted by physics editor,
 * so that we can use libgdx's swagged out json reader
 * to read our physics bodies.
 * We lose a little precision because Vector2's use floats,
 * and the JSON output is doubles, but YOLO.
 */
public class RigidBody {

    public Circle[] circles;

    /**
     * relative to android/assets, where we're reading the
     * physics.json file from
     */
    public String imagePath;

    /**
     * We'll store these in our own level files, to refer to
     * rigid bodies.
     */
    public String name;

    public Vector2 origin;

    public Vector2[][] polygons;

    /*
     * I think we can ignore shapes, as it seems to contain
     * the undecomposed polygons etc that only the editor
     * cares about
     */
}
