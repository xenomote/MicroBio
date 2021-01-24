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
    }

    @Override
    public void draw() {
        background(1);

        cells.update(frameRateLastNanos / 5_000_000_000_000_000f);
        cells.draw(this.g);

        int n = cells.getEnergies().read().size() - 1;

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
        rect(0, 0, 45, 25);
        fill(0);
        double scale = Math.pow(10, 3);
        double frames = Math.round(frameRate * scale) /scale;
        text(cells.getEnergies().read().size() + "\n" + frames, 5, 10);
    }
}
