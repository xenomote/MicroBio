package controller;

import controller.Camera.ScreenTranslation;
import processing.core.PVector;
import processing.event.MouseEvent;

import static processing.core.PConstants.*;

public class Mouse {
    private Button left;
    private Button middle;
    private Button right;

    private int scroll;

    private ScreenTranslation mapping;
    private PVector coordinates;
    private PVector position;
    private PVector delta;

    private boolean fresh;

    public Mouse(Camera camera) {
        this.left = new Button();
        this.middle = new Button();
        this.right = new Button();

        this.coordinates = new PVector();
        this.position = new PVector();
        this.delta = new PVector();

        this.mapping = camera.translate(coordinates);
    }

    public void update() {
        left.update();
        middle.update();
        right.update();

        if (fresh) fresh = false;
        else {
            delta.set(0, 0);
            scroll = 0;
        }
    }

    public void handleMouseEvent(MouseEvent event) {
        Button button;
        switch (event.getButton()) {
            case LEFT:
                button = left;
                break;

            case RIGHT:
                button = right;
                break;

            case CENTER:
                button = middle;
                break;

            default:
                button = new Button();
        }

        switch (event.getAction()) {
            case MouseEvent.PRESS:
                button.press();
                break;

            case MouseEvent.RELEASE:
                button.release();
                break;

            case MouseEvent.WHEEL:
                scroll = event.getCount();
        }

        PVector mouse = new PVector(event.getX(), event.getY());

        delta.set(PVector.sub(position, mouse));

        position.set(mouse);
        mapping.set(mouse);

        fresh = true;
    }

    public Button left() {
        return left;
    }

    public Button right() {
        return right;
    }

    public Button middle() {
        return middle;
    }

    public PVector position() {
        return position;
    }

    public int scroll() {
        return scroll;
    }

    public PVector delta() {
        return delta;
    }

    public PVector coordinates() {
        return coordinates;
    }
}
