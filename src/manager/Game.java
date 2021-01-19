package manager;

import processing.core.PApplet;

import java.util.ArrayList;

public class Game extends PApplet {
    public static void main(String[] args) {
        new Game().runSketch();
    }

    private Cells cells;

    @Override
    public void settings() {
        size(1000, 500);
    }

    @Override
    public void setup() {
        int n = 100;

        ArrayList<Integer> deletions = new ArrayList<>();

        cells = new Cells(n, deletions);

        for (int i = 0; i < n; i++) {
            int colour = color(random(255), random(255), random(255));
            cells.create(random(width), random(height), 100, colour);
        }
    }

    @Override
    public void draw() {
        cells.update(frameRateLastNanos / 1000.0f);
        cells.draw(this.g);
    }
}
