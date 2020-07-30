package cell;

import commander.Commander;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Spatial;

import java.util.ArrayList;
import java.util.List;

import static game.Game.WHITE;
import static processing.core.PApplet.max;
import static processing.core.PApplet.min;

public class Cell implements Spatial {
    public static final float NUCLEUS_RADIUS = 10;
    public static final float MEMBRANE_RADIUS = 25;

    public static final float NUCLEUS_MASS = 10;
    public static final float MEMBRANE_MASS = 10;

    public static final float CELL_SPEED = 5;

    public static final float MAX_HEALTH = 100;
    public static final float STARVATION_DAMAGE = 0.1f;

    public static final float MAX_ENERGY = 100;
    public static final float SURVIVAL_COST = 0.1f;
    public static final float MOVEMENT_COST = 0.2f;

    private float radius;
    private float health;
    private float energy;

    private Commander commander;

    private Physics nucleus;
    private Physics membrane;

    private Collider<Cell> collider;
    private Movement movement;

    private PVector cache_position;
    private PVector target;

    private List<Cell> attachments;
    private List<Cell> collisions;

    public Cell(Commander commander, PVector position) {
        this.commander = commander;

        this.attachments = new ArrayList<>();
        this.collisions = new ArrayList<>();

        this.nucleus = new Physics(position.copy(), NUCLEUS_MASS, 0.5f);
        this.membrane = new Physics(position.copy(), MEMBRANE_MASS, 0.5f);
        this.collider = new Collider<>(this, commander.cellMap(), collisions);

        this.cache_position = position.copy();
        this.target = position.copy();

        this.movement = new Movement(target, nucleus, CELL_SPEED);

        this.radius = MEMBRANE_RADIUS;

        this.health = MAX_HEALTH;
        this.energy = MAX_ENERGY;
    }

    public boolean dead() {
        return health <= 0;
    }

    public void update() {
        Physics.spring(nucleus, membrane, 0, 0.5f);

        collisions.forEach(cell -> Physics.spring(this.nucleus, cell.nucleus, this.getRadius() + cell.getRadius(), 1));
        attachments.forEach(cell -> Physics.spring(this.nucleus, cell.nucleus, (this.getRadius() + cell.getRadius()) * 0.9f , 1));

        useEnergy(SURVIVAL_COST);
        if (movement.moving()) useEnergy(MOVEMENT_COST);

        if (energy == 0) damage(STARVATION_DAMAGE);
    }

    @Override
    public PVector getPosition() {
        return cache_position;
    }

    public void updatePosition() {
        cache_position.set(nucleus.getPosition());
    }

    public Commander getCommander() {
        return commander;
    }

    public void useEnergy(float cost) {
        energy = max(energy - cost, 0);
    }

    public float getEnergy() {
        return energy;
    }

    public void addEnergy(float extra) {
        energy = min(energy + extra, MAX_ENERGY);
    }

    public Physics getNucleus() {
        return nucleus;
    }

    public Physics getMembrane() {
        return membrane;
    }

    public void seek(PVector position) {
        target.set(position);
    }

    public void stop() {
        target.set(getPosition());
    }

    public void damage(float damage) {
        health = health < damage ? 0 : health - damage;
    }


    public void force(PVector force) {
        nucleus.force(force);
    }

    public float getRadius() {
        return radius;
    }

    public List<Cell> getCollisions() {
        return collisions;
    }

    public List<Cell> getAttachments() {
        return attachments;
    }

    public void draw(PGraphics g) {
        g.noStroke();

        PVector a = membrane.getPosition();
        float x = health / MAX_HEALTH;
        float rd = g.red(commander.getColour()) * x;
        float gr = g.green(commander.getColour()) * x;
        float bl = g.blue(commander.getColour()) * x;
        // TODO: 30/07/2020 investigate arithmetic colour tinting

        g.fill(g.color(rd, gr, bl));
        g.square(a.x, a.y, MEMBRANE_RADIUS);

        PVector b = nucleus.getPosition();
        g.fill(255 * getEnergy() / MAX_ENERGY);
        g.square(b.x, b.y, NUCLEUS_RADIUS);
    }

    public void highlight(PGraphics g) {
        PVector a = membrane.getPosition();

        g.fill(WHITE,  255/4f);
        g.circle(a.x, a.y, MEMBRANE_RADIUS * 4);
    }

    public Movement getMovement() {
        return movement;
    }

    public Collider<Cell> getCollider() {
        return collider;
    }
}
