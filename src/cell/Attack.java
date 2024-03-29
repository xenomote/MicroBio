package cell;

import processing.core.PGraphics;
import processing.core.PVector;

import static game.Colours.*;

public class Attack {
    public static final int ATTACK_DURATION = 7;
    public static final float ATTACK_RADIUS = 30;

    private final PVector position;
    private int lifespan;

    public Attack(PVector position) {
        this.position = position;
        this.lifespan = ATTACK_DURATION;
    }

    public void update() {
        lifespan--;
    }

    public boolean finished() {
        return lifespan <= 0;
    }

    public void draw(PGraphics g) {
        if (lifespan > 0) {
            float radius = ATTACK_RADIUS * lifespan / ATTACK_DURATION;

            g.fill(WHITE);
            g.circle(position.x, position.y, radius * 2);
        }
    }
}
