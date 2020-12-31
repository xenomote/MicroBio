package space;

import processing.core.PVector;

public interface Spatial {
    PVector getPosition();
    float getRadius();
    // TODO: 31/12/2020 move radius to appropriate class
}
