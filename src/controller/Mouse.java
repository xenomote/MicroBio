package controller;

import processing.core.PVector;
import processing.event.MouseEvent;

import static processing.core.PConstants.*;

public class Mouse {
    private final Button left;
    private final Button middle;
    private final Button right;

    private int scroll;

    private final ScreenTranslation mapping;
    private final PVector coordinates;

    private final PVector position;
    private final PVector delta;

    private boolean fresh;

    public Mouse(Camera camera) {
        this.left = new Button();
        this.middle = new Button();
        this.right = new Button();

        this.coordinates = new PVector();
        this.position = new PVector();
        this.delta = new PVector();

        this.mapping = new ScreenTranslation(camera, coordinates);
    }

    public void update() {
        left.update();
        middle.update();
        right.update();

        mapping.set(position);

        if (fresh) {
            fresh = false;
        } else {
            delta.set(0, 0);
            scroll = 0;
        }
    }

    public void handleMouseEvent(MouseEvent event) {
        Button button = switch (event.getButton()) {
            case LEFT -> left;
            case RIGHT -> right;
            case CENTER -> middle;
            default -> new Button();
        };

        switch (event.getAction()) {
            case MouseEvent.PRESS -> button.press();
            case MouseEvent.RELEASE -> button.release();
            case MouseEvent.WHEEL -> scroll = event.getCount();
        }

        PVector mouse = new PVector(event.getX(), event.getY());

        delta.set(PVector.sub(position, mouse));

        position.set(mouse);

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
