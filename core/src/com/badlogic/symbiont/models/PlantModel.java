package com.badlogic.symbiont.models;

import java.util.ArrayList;
import java.util.List;

public class PlantModel extends PhysicsEntityModel {

    public List<MistModel> mistModels = new ArrayList<MistModel>();

    public PlantModel() {
        entityType = Type.PLANT;
    }

    @Override
    public void cleanUP() {
        for (MistModel mistModel : mistModels) {
            mistModel.startFading();
        }
    }

    @Override
    public void setPositionFromLevelEditor(float x, float y) {
        for (MistModel mistModel : mistModels) {
            mistModel.position.set(x, y);
        }
        super.setPositionFromLevelEditor(x, y);
    }
}
