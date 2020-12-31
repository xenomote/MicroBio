package controller;

// TODO: 30/12/2020 potentially remove this abstract class
public abstract class Controller {
    protected final Mouse mouse;
    protected final Keyboard keyboard;

    public Controller(Mouse mouse, Keyboard keyboard) {
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    public abstract void update();
}
