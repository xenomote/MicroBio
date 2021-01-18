package controller;

import cell.PointMass;
import processing.core.PVector;

import java.util.List;

import static processing.core.PApplet.constrain;
import static processing.core.PConstants.*;

public class CameraControls {
    public static final float ZOOM_STEP = 0.1f;
    public static final float MIN_ZOOM = 0.1f;
    public static final float MAX_ZOOM = 0.9f;
    
    private final Mouse mouse;
    private final Keyboard keyboard;
    private final Camera camera;
    private final PointMass pointMass;
    private final float speed;

    // TODO: 30/12/2020 refactor buttons into controller class
    private final List<Button> W;
    private final List<Button> A;
    private final List<Button> S;
    private final List<Button> D;

    public CameraControls(Mouse mouse, Keyboard keyboard, Camera camera, PointMass pointMass, float speed) {
        this.mouse = mouse;
        this.keyboard = keyboard;

        this.camera = camera;
        this.pointMass = pointMass;
        this.speed = speed;

        this.W = List.of(keyboard.key('w'), keyboard.code(UP));
        this.A = List.of(keyboard.key('a'), keyboard.code(LEFT));
        this.S = List.of(keyboard.key('s'), keyboard.code(DOWN));
        this.D = List.of(keyboard.key('d'), keyboard.code(RIGHT));
    }

    // TODO: 14/01/2021 smooth out zoom scrolling, possibly use time delta
    public void update() {
        float zoom = camera.getZoom();
        int step = -mouse.scroll();

        if (keyboard.key('+').held() == 1) {
            step++;
        }

        if (keyboard.key('-').held() == 1) {
            step--;
        }

        camera.zoom(constrain(zoom + step * ZOOM_STEP, MIN_ZOOM, MAX_ZOOM));

        if (mouse.middle().pressed()) {
            pointMass.force(PVector.div(mouse.delta(), zoom));
        }

        float scale = keyboard.code(SHIFT).pressed() ? zoom / 5 : zoom;

        if (W.stream().anyMatch(Button::pressed)) {
            pointMass.force(new PVector(0, -speed).div(scale));
        }

        if (A.stream().anyMatch(Button::pressed)) {
            pointMass.force(new PVector(-speed, 0).div(scale));
        }

        if (S.stream().anyMatch(Button::pressed)) {
            pointMass.force(new PVector(0, speed).div(scale));
        }

        if (D.stream().anyMatch(Button::pressed)) {
            pointMass.force(new PVector(speed, 0).div(scale));
        }
    }
}
