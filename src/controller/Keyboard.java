package controller;

import processing.event.KeyEvent;

import java.util.HashMap;

import static processing.core.PConstants.CODED;

public class Keyboard {
    final HashMap<Character, Button> buttons;
    final HashMap<Integer, Button> special_buttons;

    public Keyboard() {
        this.buttons = new HashMap<>();
        this.special_buttons = new HashMap<>();
    }

    // TODO: 30/12/2020 use time based and event based updates
    public void update() {
        buttons.values().forEach(Button::update);
        special_buttons.values().forEach(Button::update);
    }

    public void handleKeyEvent(KeyEvent event) {
        Button button = event.getKey() == CODED
                ? code(event.getKeyCode())
                : key(event.getKey());

        if (event.getAction() == KeyEvent.RELEASE) button.release();
        else button.press();

    }

    public Button key(char key) {
        return buttons.computeIfAbsent(key, k -> new Button());
    }

    public Button code(int key) {
        return special_buttons.computeIfAbsent(key, k -> new Button());
    }
}
