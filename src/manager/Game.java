package manager;

import processing.core.PApplet;

import static manager.Cells.MAX_ENERGY;

public class Game extends PApplet {
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 500;

    public static void main(String[] args) {
        new Game().runSketch();
    }

    private Cells cells;
    private long last;

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        colorMode(HSB);

        int n = 1000;

        cells = new Cells(n);

        for (int i = 0; i < n; i++) {
            int colour = color(random(255), random(200, 255), 255);
            cells.create(random(width), random(height), MAX_ENERGY, colour);
        }

        last = System.nanoTime();
    }

    @Override
    public void draw() {
        background(1);

        long next = System.nanoTime();
        float delta = 3 * (next - last) / 1E9f;

        System.out.println(delta);

        cells.update(delta);
        cells.draw(this.g);

        last = next;

        int n = cells.getPositions().size() - 1;

        for (int i = 0; i < n; i++) {
            if (random(1) < 0.001) {
                cells.delete(i);
            }
        }

        for (int i = 0; i < n; i++) {
            if (random(1) < 0.001) {
                cells.clone(i);
            }
        }

        fill(0xFFFFFFFF);
        rect(0, 0, 65, 45);
        fill(0);
        double scale = Math.pow(10, 3);
        double frames = Math.round(frameRate * scale) /scale;
        text(delta + "\n" + frames + "\n" + n, 5, 10);
    }
}
