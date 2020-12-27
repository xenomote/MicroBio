package game;

import controller.Button;
import controller.Keyboard;
import controller.Toggle;
import processing.core.PGraphics;

import java.util.List;
import java.util.Stack;

import static processing.core.PConstants.CENTER;

public class UIControl {
    private final Stack<String> messages;

    private final Toggle pause;
    private final Toggle partition;
    private final Toggle groups;
    private final Toggle energy;
    private final Toggle information;

    private final Button SPACE;
    private final List<Toggle> toggles;

    public UIControl(Keyboard keyboard) {
        this.pause = new Toggle(keyboard.key('p'));
        this.partition = new Toggle(keyboard.key('r'));
        this.energy = new Toggle(keyboard.key('e'));
        this.groups = new Toggle(keyboard.key('g'));
        this.information = new Toggle(keyboard.key('i'));

        this.messages = new Stack<>();

        this.SPACE = keyboard.key(' ');

        this.toggles = List.of(pause, partition, groups, energy, information);
    }

    public void update() {
        toggles.forEach(Toggle::update);

        if (!messages.empty() && SPACE.held() == 1) messages.pop();
    }


    public void message(String s) {
        messages.push(s);
    }

    public void displayMessages(PGraphics g) {
        if (!messages.empty()) {
            g.textAlign(CENTER, CENTER);
            g.textSize(100);
            g.text(messages.peek(), g.width/2f, g.height/2f);
        }
    }

    public boolean paused() {
        return pause.pressed();
    }

    public boolean groups() {
        return groups.pressed();
    }

    public boolean energy() {
        return energy.pressed();
    }

    public boolean partition() {
        return partition.pressed();
    }

    public boolean information() {
        return information.pressed();
    }
}
