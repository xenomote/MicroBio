package controller;

import processing.core.PVector;

public class ScreenTranslation {
    private final Camera camera;
    private final PVector position;

    ScreenTranslation(Camera camera, PVector position) {
        this.camera = camera;
        this.position = position;
    }

    public void set(PVector position) {
        PVector view = camera.getPosition();
        PVector size = camera.getSize();
        float zoom = camera.getZoom();

        this.position.set(
                (position.x - size.x/2) / zoom + view.x,
                (position.y - size.y/2) / zoom + view.y
        );
    }
}
