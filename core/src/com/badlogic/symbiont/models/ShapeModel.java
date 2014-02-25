package com.badlogic.symbiont.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/**
 * Can't serialize box2d shapes due to C++ bindings,
 * instead we just make our own shapes/fixtures
 */
public class ShapeModel {

    public Shape.Type type;
    public Vector2 position;
    public float radius;
    public Vector2[] vertices;

    /**
     * don't forget to dispose it!
     */
    public Shape makeShape() {
        switch(type) {
            case Circle:
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(radius);
                circleShape.setPosition(position);
                return circleShape;
            case Edge:
                throw new UnsupportedOperationException("not implemented)");
            case Polygon:
                throw new UnsupportedOperationException("not implemented)");
            case Chain:
                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setRadius(radius);
                polygonShape.set(vertices);
                return polygonShape;
        }
        return null;
    }
}
