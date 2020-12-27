package controller;

import processing.core.PGraphics;
import processing.core.PVector;

public class Camera {
    private final PVector position;
    private float zoom;

    private final PVector size;

    public Camera(PVector position) {

        this.position = position;
        this.zoom = 1;

        this.size = new PVector();
    }

    public void transform(PGraphics g) {
        g.translate(g.width/2f, g.height/2f);
        g.scale(zoom);
        g.translate(-position.x, -position.y);

        size.set(g.width, g.height);
    }

    public ScreenTranslation translate(PVector position) {
        return new ScreenTranslation(position);
    }

    public void zoom(float zoom) {
        this.zoom = zoom;
    }

    public float getZoom() {
        return zoom;
    }

    public PVector getPosition() {
        return position;
    }

    public class ScreenTranslation {
        private final PVector position;

        private ScreenTranslation(PVector position) {
            this.position = position;
        }

        public void set(PVector position) {
            PVector camera = Camera.this.position;
            this.position.set((position.x - size.x/2) / zoom + camera.x, (position.y - size.y/2) / zoom + camera.y);
        }
    }
}
