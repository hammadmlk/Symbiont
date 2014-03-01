package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

public class Plant extends PhysicsEntity {

    /**
     * use to find which mist belongs to this plant
     */
    public int[] mistIDs;

    private transient List<Mist> myMists = new ArrayList<Mist>();

    public Plant() {
        entityType = Type.PLANT;
    }

    @Override
    public void cleanUP() {
        for (Mist mist : myMists) {
            mist.startFading();
        }
    }

    private boolean mine(int id) {
        for (int i : mistIDs) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    public void makeMistReferences(List<Mist> mists) {
        for (Mist mist : mists) {
            if (mine(mist.id)) {
                myMists.add(mist);
            }
        }
    }
}
