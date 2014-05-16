package com.badlogic.symbiont;

import com.badlogic.symbiont.models.DeflectorEndpoint;

public class Util {

    public static float distance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float)Math.sqrt(x * x + y * y);
    }
    
    public static float distance(DeflectorEndpoint p1, DeflectorEndpoint p2){
    	return distance(p1.x, p1.y, p2.x, p2.y);
    }

}
