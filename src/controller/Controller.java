package controller;

public abstract class Controller {
    protected final Mouse mouse;
    protected final Keyboard keyboard;

    public Controller(Mouse mouse, Keyboard keyboard) {
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    public abstract void update();
}
