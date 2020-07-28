package controller;

public class Toggle {
    private Button button;
    private boolean toggle;

    public Toggle(Button button) {
        this.button = button;
        this.toggle = false;
    }

    public void update() {
        if (button.held() == 1) toggle = !toggle;
    }

    public boolean pressed() {
        return toggle;
    }
}
