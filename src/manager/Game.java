package manager;

import processing.core.PApplet;

import java.util.ArrayList;

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

        ArrayList<Integer> deletions = new ArrayList<>();

        cells = new Cells(n, deletions);

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

        fill(0xFFFFFFFF);
        rect(0, 0, 50, 20);
        fill(0);
        text(frameRate, 0, 10);
    }
}