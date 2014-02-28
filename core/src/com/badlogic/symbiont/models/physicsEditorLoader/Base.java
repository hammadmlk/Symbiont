package com.badlogic.symbiont.models.physicsEditorLoader;

/**
 * The rigidBodies are found in one giant object in the JSON, so this is that object
 */
public class Base {

    /*
     * I think we can ignore dynamicBodies, as they're unimplemented in the editor
     */

    public RigidBody[] rigidBodies;

}
