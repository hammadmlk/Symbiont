package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

public class Plant extends PhysicsEntity {

    public List<Mist> myMists = new ArrayList<Mist>();

    public Plant() {
        entityType = Type.PLANT;
    }

    @Override
    public void cleanUP() {
        for (Mist mist : myMists) {
            mist.startFading();
        }
    }

}
