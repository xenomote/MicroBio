package cell;

import commander.Commander;
import group.Group;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Area;
import space.Spatial;

import java.util.ArrayList;
import java.util.List;

import static game.Colours.*;

public class Cell implements Spatial {
    public static final float NUCLEUS_RADIUS = 10;
    public static final float MEMBRANE_RADIUS = 25;

    public static final float NUCLEUS_MASS = 5;
    public static final float MEMBRANE_MASS = 1;

    public static final float CELL_SPEED = 20;
    public static final float MOVEMENT_DAMPING = 0.5f;
    public static final float SPRING_CONSTANT = 0.5f;

    public static final float MAX_HEALTH = 100;
    public static final float STARVATION_DAMAGE = 0.1f;

    public static final float MAX_ENERGY = 100;
    public static final float SURVIVAL_COST = 0.1f;
    public static final float MOVEMENT_COST = 0.2f;

    // TODO: 30/12/2020 refactor radius into the Area class
    private final float radius;
    private final Resource health;
    private final Resource energy;

    private final Commander commander;

    private final PointMass nucleus;
    private final PointMass membrane;

    private final Area<Cell> area;
    private final Movement movement;

    private final PVector cache_position;
    private final PVector target;

    private final List<Cell> attachments;
    private final List<Cell> collisions;

    private Group group;

    public Cell(Commander commander, Group group, PVector position) {
        this.commander = commander;
        this.commander.cells().add(this);

        this.group = group;
        this.group.attach(this);

        this.attachments = new ArrayList<>();
        this.collisions = new ArrayList<>();

        this.nucleus = new PointMass(position.copy(), NUCLEUS_MASS, MOVEMENT_DAMPING);
        this.membrane = new PointMass(position.copy(), MEMBRANE_MASS, MOVEMENT_DAMPING);
        this.area = new Area<>(this, commander.cellMap(), collisions);

        this.cache_position = position.copy();
        this.target = position.copy();

        this.movement = new Movement(target, nucleus, CELL_SPEED);

        this.radius = MEMBRANE_RADIUS;

        this.health = new Resource(MAX_HEALTH, MAX_HEALTH);
        this.energy = new Resource(MAX_ENERGY, MAX_ENERGY);
    }

    public boolean dead() {
        return health.empty();
    }

    public void kill() {
        this.commander.cells().remove(this);
        this.group.detach(this);
    }

    // TODO: 10/01/2021 ensure collisions and neighbours are only updated from one side
    public void update() {
        PointMass.spring(nucleus, membrane, 0, SPRING_CONSTANT);

        collisions.forEach(cell -> PointMass.spring(this.nucleus, cell.nucleus, this.getRadius() + cell.getRadius(), SPRING_CONSTANT));
        attachments.forEach(cell -> PointMass.spring(this.nucleus, cell.nucleus, (this.getRadius() + cell.getRadius()) * 0.9f , SPRING_CONSTANT));

        energy.sub(SURVIVAL_COST);

        if (energy.empty()) {
            movement.setForce(0);
            health.sub(STARVATION_DAMAGE);
        } else if (movement.moving()) {
            movement.setForce(CELL_SPEED);
            energy.sub(MOVEMENT_COST);
        }
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Resource energy() {
        return energy;
    }

    public Resource health() {
        return health;
    }

    public PointMass getNucleus() {
        return nucleus;
    }

    public PointMass getMembrane() {
        return membrane;
    }

    public void seek(PVector position) {
        target.set(position);
    }

    public void stop() {
        target.set(getPosition());
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
        float x = health.stored() / MAX_HEALTH;
        float rd = g.red(commander.getColour()) * x;
        float gr = g.green(commander.getColour()) * x;
        float bl = g.blue(commander.getColour()) * x;
        // TODO: 30/07/2020 investigate arithmetic colour tinting

        g.fill(g.color(rd, gr, bl));
        g.square(a.x, a.y, MEMBRANE_RADIUS);

        PVector b = nucleus.getPosition();
        g.fill(255 * energy.stored() / MAX_ENERGY);
        g.square(b.x, b.y, NUCLEUS_RADIUS);
    }

    public void highlight(PGraphics g) {
        PVector a = membrane.getPosition();

        g.fill(WHITE,  256/2f);
        g.circle(a.x, a.y, MEMBRANE_RADIUS * 4);
    }

    public Movement getMovement() {
        return movement;
    }

    public Area<Cell> getCollider() {
        return area;
    }
}
