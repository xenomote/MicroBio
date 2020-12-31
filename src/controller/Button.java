package controller;

public class Button {
    private boolean pressed;
    private boolean released;
    private int held;

    // TODO: 30/12/2020 use event based updating and time counting instead of tick counting
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
