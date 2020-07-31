package controller;

public class Button {
    private boolean pressed;
    private boolean released;
    private int held;

    public void update() {
        if (pressed) held++;
        if (released) {
            held = 0;
            released = false;
        }
    }

    void press() {
        pressed = true;
        released = false;
    }

    void release() {
        pressed = false;
        released = true;
    }

    public boolean pressed() {
        return pressed;
    }

    public int held() {
        return held;
    }

    public boolean released() {
        return released;
    }
}
