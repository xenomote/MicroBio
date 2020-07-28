package old;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;
import processing.sound.SoundFile;

import java.util.*;

public class Old extends PApplet {
    public static void main(String[] args) {
        new Old().runSketch();
    }

    final float
            WHITE = 255,
            GREY = 125,
            DARK_GREY = 25,
            BLACK = 0;

    final int
            BLUE = color(0, 0, 255),
            RED = color(255, 0, 0);

    final float INFINITY = Float.POSITIVE_INFINITY;
    final float MAP_RADIUS = 2500;

    final int ATTACK_FRAMES = 10,

    STARTING_CELLS = 10,
            OPPOSITION_AIS = 50,
            ENERGY_SOURCES = 100;

    SoundFile[] splat;
    SoundFile music;

    Mouse mouse;
    Camera camera;

    boolean pause;
    boolean ended;
    boolean winner;
    boolean acknowledge;

    int max_cells;
    int min_cells;

    PVector map_center;

    PlayerController player;
    HashSet<Controller> controllers;
    HashSet<EnergySource> sources;

    ArrayList<ArrayList<PVector>> attacks;

    public void settings() {
        size(displayWidth, displayHeight, P2D);
        smooth(8);
    }

    public void setup() {
        noCursor();
        frameRate(1000);
        PFont arial = createFont("Lucida Sans", 100, true);
        textFont(arial);

        splat = new SoundFile[]{
                new SoundFile(this, "splat01.wav"),
                new SoundFile(this, "splat02.wav"),
                new SoundFile(this, "splat03.wav"),
                new SoundFile(this, "splat04.wav"),
                new SoundFile(this, "splat05.wav"),
                new SoundFile(this, "splat06.wav"),
                new SoundFile(this, "splat07.wav"),
                new SoundFile(this, "splat08.wav"),
                new SoundFile(this, "splat09.wav"),
                new SoundFile(this, "splat10.wav"),
                new SoundFile(this, "splat11.wav")
        };

        music = new SoundFile(this, "leaving-home-by-kevin-macleod.wav");
        music.loop();

        map_center = new PVector(0, 0);

        mouse = new Mouse();
        camera = new Camera();

        reset();
    }

    public void draw() {

        if (!pause) {
            mouse.update();
            camera.update();
            updateControllers();
            updateCells();
            updateSources();
        }

        background(DARK_GREY);
        drawCameraView();
        mouse.selector().drawCursor();
        printStatistics();

        if (pause) {
            textAlign(CENTER, TOP);
            text("Paused", width/2f, 0);
        }

        if (ended && !acknowledge) {
            if (winner) gameWon();
            else  gameLost();

            if (keys[' ']) acknowledge = true;
        }
        else checkWinCondition();

        mouseReleased = false;
    }

    void checkWinCondition() {
        if (!ended) {
            if (!controllers.contains(player)) {
                winner = false;
                ended = true;
            }

            else if (controllers.size() == 1) {
                winner = true;
                ended = true;
            }
        }
    }

    void reset() {
        pause = false;
        ended = false;
        winner = false;
        acknowledge = false;

        attacks = new ArrayList<>();
        for (int i = 0; i < ATTACK_FRAMES; i++)
            attacks.add(new ArrayList<>());

        controllers = new HashSet<>();
        sources = new HashSet<>();

        for (int i = 0; i < ENERGY_SOURCES; i++) {
            float radius = random(CELL_RADIUS, MAP_RADIUS/10);
            PVector position = randomPosition();

            spawnEnergySource(position, radius);
        }

        for (int i = 0; i < OPPOSITION_AIS; i++) {
            PVector position = randomPosition();

            EnergySource source = nearestSource(position);

            position = PVector
                    .fromAngle(random(TWO_PI))
                    .setMag(random(source.radius))
                    .add(source.position);

            spawnAggressor(position);
        }


        PVector position = randomPosition();
        player = spawnPlayer(position);
        spawnEnergySource(position, random(CELL_RADIUS, MAP_RADIUS/10));
        camera.position = position.copy();

        max_cells = cells.size();
        min_cells = cells.size();
    }

    void gameWon() {
        push();
        textAlign(CENTER, CENTER);
        textSize(100);
        text("You Win", width/2f, height/2f);
        pop();
    }

    void gameLost() {
        push();
        textAlign(CENTER, CENTER);
        textSize(100);
        text("You Lose", width/2f, height/2f);
        pop();
    }

    void updateControllers() {
        for (Controller controller : controllers)  controller.update();

        filterInactiveControllers();
    }


    void filterInactiveControllers() {
        HashSet<Controller> active = new HashSet<>();
        for (Controller controller : controllers) if (!controller.cells.isEmpty()) active.add(controller);
        controllers = active;
    }

    void drawCameraView() {
        push();

        camera.pan();

        drawMap();
        drawSources();

        if (toggle['r']) {
            highlightColonyRegions();
            highlightSquadronRegions();
        }

        highlightPlayerCells();

        drawAttacks();
        drawCells();

        if (toggle['e']) drawEnergyLevels();
        if (toggle['g']) SPACE.draw();
        if (mouse.clicked()) mouse.selector().drawSelection();

        pop();
    }

    void drawMap() {
        push();

        fill(BLACK);
        stroke(0,0);

        circle(map_center, 2 * MAP_RADIUS);
        fill(WHITE);

        pop();
    }

    void drawSources() {
        for (EnergySource source : sources)
            source.draw();
    }

    void updateSources() {
        for (EnergySource source : sources)
            source.update();
    }

    void drawEnergyLevels() {
        push();

        fill(WHITE);
        stroke(0,0);

        for (Cell cell : cells)
            rect(cell.nucleus.position.x - 2, cell.nucleus.position.y, 4, -cell.energy);
        pop();
    }

    void highlightColonyRegions() {
        for (Controller controller : controllers)
            for (Colony colony : controller.getColonies())
                colony.draw();
    }

    void highlightSquadronRegions() {
        for (Controller controller : controllers)
            for (Squadron squadron : controller.getSquadrons())
                squadron.draw();
    }

    void highlightPlayerCells() {
        push();

        fill(WHITE, 255/4f);
        stroke(0,0);

        for (Cell cell : player.cells) {
            if (player.selection.contains(cell)) {
                fill(WHITE, (255 * 3) / 4f);
                circle(cell.nucleus.position, 100);
                fill(WHITE, 255/4f);
            }

            else circle(cell.nucleus.position, 100);
        }

        pop();
    }

    void drawAttacks() {
        push();
        fill(WHITE);
        stroke(0,0);
        for (int i = 0; i < attacks.size(); i++)
            for (PVector position : attacks.get(i))
                circle(position, 5 * CELL_RADIUS * i / ATTACK_FRAMES);

        for (int i = attacks.size() - 1; i > 0; i--)
            attacks.set(i, attacks.get(i-1));

        attacks.set(0, new ArrayList<>());

        pop();
    }

    void printStatistics() {
        if (toggle['i']) {
            max_cells = max(max_cells, cells.size());
            min_cells = min(min_cells, cells.size());

            push();
            textAlign(LEFT, TOP);
            textSize(18);
            text("min: " + min_cells
                    + "\nmax: " + max_cells
                    + "\ncurrent: " + cells.size()
                    + "\nquadrants: " + QUAD_INSTANCE
                    + "\nfps: " + (frameRate < 25 ? "slowed " : "") + frameRate + ")", 0, 0);
            pop();
        }

        push();
        textAlign(RIGHT, TOP);
        textSize(100);
        text(str(player.cells.size()), width, 0);

        if (!player.selection.isEmpty()) {
            float max = player.selection.size() * MAX_ENERGY;
            float total = 0;

            for (Cell cell : player.selection)
                total += cell.energy;

            fill(WHITE);
            rect(10, 10, (width - 20) * (total/max), 10);
        }
        pop();
    }

    PlayerController spawnPlayer(PVector position) {
        PlayerController player = new PlayerController();
        player.placeColony(group(position), position);

        controllers.add(player);

        return player;
    }

    void spawnAggressor(PVector position) {
        Controller controller = new AggressiveAIController();
        controller.placeColony(group(position), position);

        controllers.add(controller);

    }

    void spawnEnergySource(PVector position, float radius) {
        position.limit(MAP_RADIUS - radius);
        EnergySource source = new EnergySource(position, radius, (float) 5);
        sources.add(source);
    }

/////////////////////////////////////////////////////////////////////////////
// CAMERA CLASS
/////////////////////////////////////////////////////////////////////////////

    class Camera {
        PVector screenCenter;
        PVector position;
        float scale;

        Camera() {
            screenCenter = new PVector(width/2f, height/2f);
            position = new PVector(0, 0);
            scale = 1;
        }

        void update() {
            scale += mouse.scroll * 0.1;
            scale = constrain(scale, 0.1f, 1);

            float pan = 10/scale;

            if (key_shift) {
                position.add(PVector.sub(mouse.position, screenCenter).div(10));
            }

            else {
                if (keys['w'] || key_up)     position.y -= pan;
                if (keys['s'] || key_down)   position.y += pan;
                if (keys['a'] || key_left)   position.x -= pan;
                if (keys['d'] || key_right)  position.x += pan;
            }

            screenCenter.set(width/2f, height/2f);
            position.x = constrain(position.x, -MAP_RADIUS, MAP_RADIUS);
            position.y = constrain(position.y, -MAP_RADIUS, MAP_RADIUS);
        }

        void pan() {
            translate(screenCenter);
            scale(scale);
            translate(PVector.mult(position, -1));
        }


        PVector coordinate(PVector p) {
            return p.copy().sub(screenCenter).div(scale).add(position);
        }
    }

/////////////////////////////////////////////////////////////////////////////
// SOUND LOCALISATION
/////////////////////////////////////////////////////////////////////////////

    void play(SoundFile sound, PVector position) {
        float distance = dist(camera.position, position);
        float right = camera.position.x - position.x;

        sound.stop();
        sound.pan(constrain(atan2(-right, displayWidth/camera.scale), -1, 1));
        sound.amp(constrain(log(distance / (MAP_RADIUS * 2)) * sq(camera.scale), -1, 1) * 0.75f);
        sound.play();
    }

    boolean constrained(float x, float a, float b) {
        return a < x && x < b;
    }

    float dist(Cell a, Cell b) {
        return dist(a.nucleus, b.nucleus);
    }

    float dist(Point a, Point b) {
        return dist(a.position, b.position);
    }

    float dist(PVector a, PVector b) {
        return dist(a.x, a.y, b.x, b.y);
    }

/////////////////////////////////////////////////////////////////////////////
// COORDINATE TRANSLATIONS
/////////////////////////////////////////////////////////////////////////////


    PVector randomPosition() {
        return PVector.fromAngle(random(TWO_PI)).setMag(random(MAP_RADIUS));
    }

    final float NUCLEUS_SIZE = 10;
    final float NUCLEUS_MASS = 10;

    final float CELL_RADIUS = 20;
    final float CELL_SPRING = 0.2f;

    final float COLONY_SPRING = 1;
    final float ATTACK_RANGE = 40;

    final float MAX_ENERGY = 100;

    final float ENERGY_TRANSFER_RATE = 1;
    final float DIVISION_MINIMUM = 50;

    final float SURVIVAL_COST = 0.1f;
    final float MOVEMENT_COST = 0.2f;
    final float ATTACK_COST = 10;

    final float STARVATION_DAMAGE = 0.1f;


    final float MAX_HEALTH = 100;

    final float ATTACK_DAMAGE = 10;
    final float ATTACK_SPEED = 100;

    final float DIVISION_SPEED = 1000;

    HashSet<Cell> cells = new HashSet<>();

    class Cell {

        Stack<Group> groups;

        int fill;
        float radius;

        float health;
        float energy;
        float speed;
        int attackCooldown;
        int divisionCooldown;

        boolean attacking;
        boolean dividing;
        boolean moving;

        PVector target;

        Point membrane;
        Point nucleus;

        HashSet<Cell> neighbours;
        HashSet<Cell> attachments;

        Cell(PVector center) {
            groups = new Stack<>();

            this.fill = color(random(255/2f, 255), random(255/2f, 255), random(255/2f, 255));
            this.radius = CELL_RADIUS;

            this.health = MAX_HEALTH;
            this.energy = MAX_ENERGY;
            this.speed = 10;

            this.attackCooldown = (int) random(ATTACK_SPEED);
            this.divisionCooldown = (int) random(DIVISION_SPEED);

            this.neighbours = new HashSet<>();
            this.attachments = new HashSet<>();

            this.membrane = new Point(center.copy());
            this.nucleus = new Point(center.copy(), NUCLEUS_MASS, 0.5f);

            SPACE = SPACE.place(this);
        }

        boolean canAttack() {
            return attackCooldown == 0 && energy > ATTACK_COST;
        }

        boolean canDivide() {
            return !attachments.isEmpty() && divisionCooldown == 0 && energy > DIVISION_MINIMUM;
        }

        boolean isDead() {
            return health <= 0;
        }

        void attack(Cell target) {
            if (canAttack()) {
                nucleus.force(nucleus.to(target.nucleus).setMag(10));
                target.nucleus.force(nucleus.to(target.nucleus).setMag(50));
                target.health -= ATTACK_DAMAGE;

                energy -= ATTACK_COST;
                attackCooldown = (int) random(ATTACK_SPEED);

                SoundFile sound = splat[(int) random(splat.length)];

                PVector position = PVector.add(target.nucleus.position, nucleus.position).div(2);
                play(sound, position);
                attacks.get(0).add(position);

                attacking = true;
            }
        }

        void divide() {
            if (canDivide() && !dividing) {
                dividing = true;

                divisionCooldown = (int) random(DIVISION_SPEED);
            }
        }

        void moveTowards(PVector target) {
            this.moving = true;
            this.target = target;
        }

        void stop() {
            this.moving = false;
        }

        void attach(Cell cell) {
            if (neighbours.contains(cell)) attachments.add(cell);
        }

        void detach(HashSet<Cell> cells) {
            for (Cell cell : cells) detach(cell);
        }

        void detach(Cell cell) {
            this.attachments.remove(cell);
            cell.attachments.remove(this);
        }

        void detach() {
            List<Cell> cells = new ArrayList<>(attachments);
            for (Cell cell : cells) detach(cell);
        }

        void integrate() {
            SPACE.remove(this);

            energy -= SURVIVAL_COST;
            if (energy <= 0) {
                energy = 0;
                health -= STARVATION_DAMAGE;
                detach();
            }

            if (attackCooldown > 0) attackCooldown--;
            if (divisionCooldown > 0) divisionCooldown--;
            attacking = false;
            dividing = false;

            if (moving && energy > speed) {
                nucleus.force(PVector.sub(target, nucleus.position).limit(speed));
                energy -= MOVEMENT_COST;
            }

            HashSet<Cell> detached = new HashSet<>();
            for (Cell cell : attachments) if (!neighbours.contains(cell)) detached.add(cell);
            attachments.removeAll(detached);

            spring(nucleus, membrane, 0, CELL_SPRING);

            for (Cell cell : neighbours) if (colliding(cell)) {
                spring(cell.nucleus, this.nucleus, this.radius + cell.radius, COLONY_SPRING);
            }

            transferEnergy();

            membrane.integrate();
            nucleus.integrate();

            SPACE = SPACE.place(this);
        }

        void draw() {

            push();
            noStroke();

            float f = health/MAX_HEALTH;
            fill(color(red(fill) * f, green(fill) * f, blue(fill) * f));
            square(membrane.position, 2 * CELL_RADIUS);

            fill(WHITE *  energy/MAX_ENERGY);
            square(nucleus.position, NUCLEUS_SIZE);

            pop();
        }

        void transferEnergy() {
            HashSet<Cell> transfers = new HashSet<>();
            for (Cell cell : attachments) {

                SPACE.remove(cell);
                rod(this.nucleus, cell.nucleus, this.radius + cell.radius);
                SPACE = SPACE.place(cell);

                if (cell.energy < this.energy - ENERGY_TRANSFER_RATE) {
                    transfers.add(cell);
                }
            }

            float rate = ENERGY_TRANSFER_RATE / transfers.size();
            for (Cell cell : transfers) {
                this.energy -= rate;
                cell.energy += rate;
            }
        }

        void backTo(Group group) {
            assert groups.contains(group);
            while (groups.peek() != group) groups.peek().take(this);
        }

        boolean neighbouring(Cell cell) {
            return dist(this.nucleus, cell.nucleus) < this.radius + cell.radius + 10;
        }

        boolean colliding(Cell cell) {
            return dist(this.nucleus, cell.nucleus) < this.radius + cell.radius;
        }

        PVector position() {
            return nucleus.position.copy();
        }

        EnergySource closestSource() {
            return nearestSource(position());
        }
    }

    HashSet<Cell> group(PVector center) {
        HashSet<Cell> group = new HashSet<>();

        float space = 2 * CELL_RADIUS;
        int side = ceil(sqrt((float) STARTING_CELLS)) - 1;
        int x = 0, y = 0;

        PVector corner = PVector.sub(center, new PVector(side/2f * space, side/2f * space));

        for (int i = 0; i < STARTING_CELLS; i++) {
            PVector offset = new PVector(space * x, space * y);
            PVector position = PVector.add(corner, offset);

            group.add(new Cell(position));

            if (x < side) x++;
            else {
                y++;
                x = 0;
            }
        }

        cells.addAll(group);
        born.addAll(group);

        return group;
    }

/////////////////////////////////////////////////////////////////////////////
// GLOBAL UPDATE METHODS
/////////////////////////////////////////////////////////////////////////////

    void updateCells() {
        deaths();
        births();
        collisions();

        for (Cell cell : cells) cell.integrate();

        SPACE.clean();
    }

    void drawCells() {
        for (Cell cell : cells) cell.draw();
    }

    HashSet<Cell> born = new HashSet<>();
    void births() {
        born.clear();

        for (Cell cell : cells) {
            if (cell.dividing && cell.neighbours.size() <= 4 && random(1) > 0.5) {
                Cell birth = new Cell(PVector.random2D().add(cell.nucleus.position));

                if (player.selection.contains(cell))
                    player.selection.add(birth);

                birth.fill = cell.fill;

                float energy = cell.energy;
                birth.energy = energy/2;
                cell.energy = energy/2;

                for (Group group : cell.groups) group.put(birth);

                born.add(birth);

                cell.dividing = false;
            }
        }

        cells.addAll(born);
    }

    HashSet<Cell> dead = new HashSet<>();
    void deaths() {
        dead.clear();

        for (Cell cell : cells) if (cell.isDead()) {
            dead.add(cell);
            SPACE.remove(cell);
        }

        cells.removeAll(dead);
    }

    void collisions() { ////////////////////////////////////////////////////////////////////////////////// CHECK COLLISIONS EFFICIENTLY WITHOUT DUPLICATION
        for (Cell cell : cells) {
            cell.neighbours.clear();
            PVector position = cell.position();
            float range = CELL_RADIUS * 2;
            LinkedList<Cell> neighbours = SPACE.get_neighbours(position, range);

            for (Cell candidate : neighbours)
                if (candidate != cell && cell.neighbouring(candidate))
                    cell.neighbours.add(candidate);
        }
    }


    Cell closestActive(List<Cell> targets, PVector p) {
        if (targets.isEmpty()) return null;

        Cell closest = null;
        float closest_dist = INFINITY;
        LinkedList<Cell> candidates = SPACE.get_neighbours(p, DETECTION_RANGE);

        for (Cell candidate : candidates) {
            if (targets.contains(candidate) && dist(candidate.position(), p) < closest_dist) {
                closest = candidate;
                closest_dist = dist(candidate.position(), p);
            }
        }

        return closest;
    }

/////////////////////////////////////////////////////////////////////////////
// COLLISION METHODS
/////////////////////////////////////////////////////////////////////////////

    public QuadSpace SPACE = new QuadSpaceRegion(new PVector(0, 0), MAP_RADIUS);

    int QUAD_INSTANCE = 0;

    abstract class QuadSpace {
        static final int CELLS_PER_REGION = 10;

        final PVector min;
        final PVector max;
        final PVector mid;

        int n_cells;

        boolean fresh = true;

        QuadSpace(PVector min, PVector max) {
            this.min = min;
            this.max = max;
            this.mid = PVector.add(min, max).div(2);

            this.n_cells = 0;

            QUAD_INSTANCE++;
        }

        boolean contains(PVector point) {
            return min.x <= point.x - (float) 0
                    && max.x > point.x + (float) 0
                    && min.y <= point.y - (float) 0
                    && max.y > point.y + (float) 0
                    ;
        }

        boolean intersects(PVector point, float range) {
            //return (min.x <= point.x && point.x < max.x && min.y <= point.y && point.y < max.y);
            //return !(max.x <= point.x - range && min.x > point.x + range) && !(max.y <= point.y - range && min.y > point.y + range);

            // If one rectangle is on left side of other
            if (min.x >= point.x + range || point.x - range >= max.x) {
                return false;
            }

            // If one rectangle is above other
            return !(min.y >= point.y + range) && !(point.y - range >= max.y);
        }

        boolean out_of_bounds(Cell cell) {
            if (!contains(cell.position())) {
                print(" cell out of bounds, " + cell.position() + " outside " + min + " " + max);
                return true;
            }

            return false;
        }

        abstract void draw();

        abstract QuadSpace place(Cell cell);
        abstract void remove(Cell cell);
        abstract QuadSpace clean();

        abstract LinkedList<Cell> get_cells();
        abstract LinkedList<Cell> get_neighbours(PVector point, float range);
    }

    class QuadSpaceGroup extends QuadSpace {
        QuadSpace TL;
        QuadSpace TR;
        QuadSpace BL;
        QuadSpace BR;

        QuadSpaceGroup(QuadSpaceRegion region) {
            super(region.min, region.max);

            TL = new QuadSpaceRegion(min, mid);
            TR = new QuadSpaceRegion(new PVector(mid.x, min.y), new PVector(max.x, mid.y));
            BL = new QuadSpaceRegion(new PVector(min.x, mid.y), new PVector(mid.x, max.y));
            BR = new QuadSpaceRegion(mid, max);

            for (Cell cell : region.cells) {
                if ((place(cell) != this)) throw new AssertionError();
            }
        }

        QuadSpace place(Cell cell) {
            PVector position = cell.position();
            if (out_of_bounds(cell)) throw new Error();

            if (position.x < mid.x) {
                if (position.y < mid.y) {
                    TL = TL.place(cell);
                }

                else {
                    BL = BL.place(cell);
                }
            }

            else {
                if (position.y < mid.y) {
                    TR = TR.place(cell);
                }

                else {
                    BR = BR.place(cell);
                }
            }

            n_cells++;

            return this;
        }

        void remove(Cell cell) {
            PVector position = cell.position();
            if (out_of_bounds(cell)) throw new Error();

            if (position.x < mid.x) {
                if (position.y < mid.y) {
                    TL.remove(cell);
                }

                else {
                    BL.remove(cell);
                }
            }

            else {
                if (position.y < mid.y) {
                    TR.remove(cell);
                }

                else {
                    BR.remove(cell);
                }
            }

            n_cells--;
        }

        QuadSpace clean() {
            if (n_cells <= CELLS_PER_REGION) {
                return new QuadSpaceRegion(this);
            }

            TL = TL.clean();
            TR = TR.clean();
            BL = BL.clean();
            BR = BR.clean();

            return this;
        }

        LinkedList<Cell> get_cells() {
            LinkedList<Cell> cells = TL.get_cells();
            cells.addAll(TR.get_cells());
            cells.addAll(BL.get_cells());
            cells.addAll(BR.get_cells());
            return cells;
        }

        LinkedList<Cell> get_neighbours(PVector point, float range) {
            LinkedList<Cell> neighbours = new LinkedList<>();

            if (TL.intersects(point, range)) neighbours.addAll(TL.get_neighbours(point, range));
            if (TR.intersects(point, range)) neighbours.addAll(TR.get_neighbours(point, range));
            if (BL.intersects(point, range)) neighbours.addAll(BL.get_neighbours(point, range));
            if (BR.intersects(point, range)) neighbours.addAll(BR.get_neighbours(point, range));

            return neighbours;
        }

        void draw() {
            TL.draw();
            TR.draw();
            BL.draw();
            BR.draw();
        }
    }

    class QuadSpaceRegion extends QuadSpace {
        ArrayList<Cell> cells;

        QuadSpaceRegion(PVector point, float range) {
            this(new PVector(point.x - range, point.y - range), new PVector(point.x + range, point.y + range));
        }

        QuadSpaceRegion(PVector min, PVector max) {
            super(min, max);
            cells = new ArrayList<>(CELLS_PER_REGION);
            n_cells = cells.size();
        }

        QuadSpaceRegion(QuadSpaceGroup group) {
            this(group.min, group.max);

            cells.addAll(group.get_cells());
        }

        QuadSpace place(Cell cell) {
            if (out_of_bounds(cell)) throw new Error();

            if (cells.size() == CELLS_PER_REGION) {
                QuadSpaceGroup group = new QuadSpaceGroup(this);
                if ((group.place(cell) != group)) throw new AssertionError();
                return group;
            }

            cells.add(cell);
            n_cells = cells.size();
            return this;

        }

        void remove(Cell cell) {
            if (out_of_bounds(cell))  throw new Error();

            cells.remove(cell);
            n_cells = cells.size();
        }

        QuadSpace clean() {
            return this;
        }

        LinkedList<Cell> get_cells() {
            return new LinkedList<>(this.cells);
        }

        LinkedList<Cell> get_neighbours(PVector point, float range) {
            assert(intersects(point, range));
            return get_cells();
        }


        void draw(){
            push();

            if (fresh)
                fill(RED);
            else if (intersects(mouse.coordinates(), 10))
                fill(WHITE, 100);
            else
                noFill();

            fresh = false;

            stroke(255);
            strokeWeight(10);
            rect(min, max);

            fill(WHITE);
            textSize(50);
            text(str(n_cells), mid);
            pop();
        }
    }

    abstract class Controller extends Group {
        HashSet<Squadron> squadrons;
        HashSet<Colony> colonies;
        int fill;

        Controller() {
            super(null);
            this.controller = this;
            this.colonies = new HashSet<>();
            this.squadrons = new HashSet<>();
            this.fill = color(random(255), random(255), random(255));
        }

        HashSet<Colony> getColonies() {
            return colonies;
        }

        HashSet<Squadron> getSquadrons() {
            return squadrons;
        }

        void placeSquadron(HashSet<Cell> cells, PVector position) {
            ungroup(cells);

            for (Squadron squadron : squadrons)
                if (dist(position, squadron.rallyPoint) < squadron.radius()) {
                    squadron.put(cells);
                    return;
                }

            Squadron squadron = new Squadron(this, position);
            squadron.put(cells);

            HashSet<Cell> enemies = new HashSet<>();
            for (Controller controller : controllers)
                if (controller != this)
                    enemies.addAll(controller.cells);

            squadron.attack(enemies);
            squadrons.add(squadron);
        }

        void placeColony(HashSet<Cell> cells, PVector position) {
            put(cells);
            ungroup(cells);

            for (Colony colony : colonies) {
                if (dist(position, colony.center) < colony.radius()) {
                    colony.put(cells);
                    return;
                }
            }

            Colony colony = new Colony(this, position);
            colony.put(cells);
            colonies.add(colony);
        }

        void ungroup(HashSet<Cell> cells) {
            for (Cell cell : cells)
                cell.backTo(this);
        }

        void onPut(Cell cell) {
            cell.fill = fill;
        }

        void onTake(Cell cell) {
            for (Squadron squadron : squadrons)
                if(squadron.contains(cell)) {
                    squadron.take(cell);
                    return;
                }

            for (Colony colony : colonies)
                if (colony.contains(cell)) {
                    colony.take(cell);
                    return;
                }
        }

        void filterColonies() {
            HashSet<Colony> active = new HashSet<>();
            for (Colony colony : colonies) if (!colony.cells.isEmpty()) active.add(colony);
            colonies = active;
        }

        void filterSquadrons() {
            HashSet<Squadron> active = new HashSet<>();
            for (Squadron squadron : squadrons) if (!squadron.cells.isEmpty()) active.add(squadron);
            squadrons = active;
        }

        void addTargets() {
            for (Cell birth : born) {
                for (Controller controller : controllers) {
                    if (controller != this) {
                        if (controller.cells.contains(birth))
                            for (Squadron squadron : squadrons) {
                                squadron.attack(birth);
                                break;
                            }
                    }
                }
            }
        }

        void updateColonies() {
            for (Colony colony : colonies)
                colony.update();

        }

        void updateSquadrons() {
            for (Squadron squadron : squadrons)
                squadron.update();
        }
    }

/////////////////////////////////////////////////////////////////////////////
// PLAYER CONTROLLER
/////////////////////////////////////////////////////////////////////////////

    class PlayerController extends Controller {
        HashSet<Cell> selection;

        PlayerController() {
            this.selection = new HashSet<>();
        }

        void onUpdate() {
            if (mouse.selector().available())
                selection = mouse.selector().selection(this.cells);

            else if (keys[' '])
                selection = new HashSet<>(cells);

            updateSelection();

            updateColonies();
            updateSquadrons();

            filterColonies();
            filterSquadrons();

            addTargets();
        }

        void updateSelection() {
            selection.removeAll(dead);

            if (!selection.isEmpty() && keyPressed) {
                if (keys['c']) placeColony(selection, mouse.coordinates());
                else if (keys['x']) placeSquadron(selection, mouse.coordinates());
            }
        }

    }

/////////////////////////////////////////////////////////////////////////////
// AI CONTROLLERS
/////////////////////////////////////////////////////////////////////////////

    class AggressiveAIController extends Controller {
        int minimum;
        float squad_ratio;

        AggressiveAIController() {
            this.minimum = (int) random(3, 25);
            this.squad_ratio = random(0, 1);
        }

        void onUpdate() {
            // if there are no targets then move the squadron
            for (Squadron squadron : squadrons)
                if (squadron.isIdle()) {
                    squadron.rallyPoint.set(randomPosition());
                }


            // if the colony has free members then add them to the squadron
            for (Colony colony : colonies) {
                HashSet<Cell> attackers = new HashSet<>();
                while (colony.colonists.size() > cells.size() - (cells.size() - minimum) * squad_ratio) {
                    attackers.add(colony.take(colony.cells.iterator().next()));
                }

                placeSquadron(attackers, colony.center.copy());
            }



            for (Cell birth : born) {
                for (Controller controller : controllers) {
                    if (controller != this) {
                        if (controller.cells.contains(birth))
                            for (Squadron squadron : squadrons) {
                                squadron.attack(birth);
                                break;
                            }
                    }
                }
            }

            addTargets();

            updateColonies();
            updateSquadrons();

            filterColonies();
            filterSquadrons();
        }

    }

    void translate(PVector a) {
        translate(a.x, a.y);
    }

    void circle(PVector a, float r) {
        circle(a.x, a.y, r);
    }

    void square(PVector a, float r) {
        square(a.x - r/2, a.y - r/2, r);
    }

    void line(PVector a, PVector b) {
        line(a.x, a.y, b.x, b.y);
    }

    void rect(PVector a, PVector b) {
        rect(a.x, a.y, (b.x - a.x), (b.y - a.y));
    }

    void text(String s, PVector p) {
        text(s, p.x, p.y);
    }

    void text(int s, PVector p) {
        text(s, p.x, p.y);
    }

    class EnergySource {
        PVector position;
        float radius;
        float rate;

        EnergySource(PVector position, float radius, float rate) {
            this.position = position;
            this.radius = radius;
            this.rate = rate;
        }

        void update() {
            HashSet<Cell> cells = inRange();

            for (Cell cell : cells)
                cell.energy = constrain(cell.energy + rate, 0, MAX_ENERGY);
        }

        void draw() {
            push();

            fill(GREY);
            stroke(0,0);
            circle(position, radius * 2);

            pop();
        }

        HashSet<Cell> inRange() {
            return inRange(SPACE.get_neighbours(position, radius));
        }

        HashSet<Cell> inRange(List<Cell> cells) {
            HashSet<Cell> inRange = new HashSet<>();
            for (Cell cell : cells)
                if (dist(cell.nucleus.position, position) < radius)
                    inRange.add(cell);

            return inRange;
        }
    }

    EnergySource nearestSource(PVector position) {
        EnergySource closest = null;
        float closestDistance = INFINITY;

        for (EnergySource source : sources)
            if (dist(position, source.position) - source.radius < closestDistance) {
                closest = source;
                closestDistance = dist(position, source.position);
            }

        return closest;
    }

    abstract class Group {
        Controller controller;
        HashSet<Cell> cells;

        Group(Controller controller) {
            this.controller = controller;
            this.cells = new HashSet<>();
        }

        void take(HashSet<Cell> group) { assert(group != cells);
            for (Cell cell : group) take(cell);
        }

        Cell take(Cell cell) {
            if (cell.groups.contains(this)) {
                cell.backTo(this);
                cell.groups.pop();
                cells.remove(cell);

                onTake(cell);
            }
            return cell;
        }

        void put(HashSet<Cell> group) {
            for (Cell cell : group) put(cell);
        }

        void put(Cell cell) {
            if (!cell.groups.contains(this)) {
                cells.add(cell);
                cell.groups.push(this);
                onPut(cell);
            }
        }

        void update() {
            take(dead);
            onUpdate();
        }

        boolean contains(Cell cell) {
            return cells.contains(cell);
        }

        float radius() {
            return sqrt(cells.size() * sq(CELL_RADIUS)) + CELL_RADIUS;
        }

        abstract void onPut(Cell cell);
        abstract void onTake(Cell cell);
        abstract void onUpdate();
    }

    class Colony extends Group {
        PVector center;
        HashMap<Cell, Colonist> colonists;

        Colony(Controller controller, PVector center) {
            super(controller);
            this.center = center;
            this.colonists = new HashMap<>();
        }

        void onPut(Cell cell) {
            colonists.put(cell, new Colonist(cell, this));

            if (dist(center, map_center) > MAP_RADIUS - radius()) center.setMag(MAP_RADIUS - radius());
        }

        void onTake(Cell cell) {
            colonists.remove(cell);
            cell.detach(cells);
        }

        void onUpdate() {
            for (Colonist colonist : colonists.values()) colonist.update();
        }

        private void draw() {
            push();

            fill(BLUE, 255/2f);
            stroke(0,0);
            circle(center, radius() * 2);

            pop();
        }
    }

    class Squadron extends Group {
        int idle;
        HashMap<Cell, Attacker> attackers;
        LinkedList<Cell> targets;

        PVector rallyPoint;

        Squadron(Controller controller, PVector rallyPoint) {
            super(controller);
            this.attackers = new HashMap<>();
            this.targets = new LinkedList<>();
            this.rallyPoint = rallyPoint;

            this.idle = 0;
        }

        void onPut(Cell cell) {
            attackers.put(cell, new Attacker(cell, this));
        }

        void onTake(Cell cell) {
            attackers.remove(cell);
        }

        void onUpdate() {
            targets.clear();
            PVector drift = new PVector(0, 0);
            for (Cell target : SPACE.get_neighbours(rallyPoint, radius())) {
                if (target.energy > 0 && !controller.contains(target) && dist(target.position(), rallyPoint) < radius()) {
                    targets.add(target);
                    drift.add(PVector.sub(target.position(), rallyPoint));
                }
            }

            drift.normalize();
            rallyPoint.add(drift);


            for (Attacker attacker : attackers.values()) attacker.update();

            if (targets.isEmpty())
                idle++;
            else
                idle = 0;
        }

        void attack(Cell target) {
            targets.add(target);
        }

        void attack(HashSet<Cell> targets) {
            this.targets.addAll(targets);
        }

        boolean isIdle() {
            return idle > 100;
        }

        float radius() {
            return super.radius() * 2;
        }

        private void draw() {
            push();

            fill(RED, 255/2f);
            stroke(0,0);
            circle(rallyPoint, radius() * 2);

            stroke(WHITE);
            for (Cell target : targets) line(target.position(), rallyPoint);

            fill(WHITE, 255);
            text(idle, rallyPoint);

            pop();
        }
    }

    abstract class Individual {

        Cell cell;

        Individual(Cell cell) {
            this.cell = cell;
        }

        void seek(PVector point) {
            cell.moveTowards(point);
        }

    }

/////////////////////////////////////////////////////////////////////////////
// AIs
/////////////////////////////////////////////////////////////////////////////


    class Colonist extends Individual {
        Colony colony;
        Colonist(Cell cell, Colony colony) {
            super(cell);
            this.colony = colony;
        }

        private void update() {
            for (Cell neighbour : cell.neighbours) if (colony.cells.contains(neighbour)) cell.attach(neighbour);

            cell.divide();

            if (cell.attachments.size() < 2 || dist(cell.nucleus.position, colony.center) > colony.radius()) seek(colony.center);
            else cell.stop();
        }
    }

    final float DETECTION_RANGE = 200;

    class Attacker extends Individual {

        boolean recharging;

        Squadron squadron;

        Attacker(Cell cell, Squadron squadron) {
            super(cell);
            this.squadron = squadron;
            this.recharging = false;
        }

        private void update() {
            if (cell.energy < MAX_ENERGY/2 || recharging) {
                EnergySource source = cell.closestSource();

                if (dist(cell.nucleus.position, source.position) > source.radius)
                    seek(source.position);

                else
                    cell.stop();

                recharging = cell.energy != MAX_ENERGY;
            }

            else if (!inRallyRange(cell)) {
                seek(squadron.rallyPoint);
            }

            else {
                Cell closest = closestActive(squadron.targets, cell.nucleus.position);

                if (closest != null && inRallyRange(closest)) {
                    seek(closest.nucleus.position);

                    if (dist(cell, closest) <= ATTACK_RANGE && cell.canAttack()) cell.attack(closest);
                }


                else {
                    cell.stop();
                }
            }
        }

        boolean inRallyRange(Cell cell) {
            return dist(cell.nucleus.position, squadron.rallyPoint) < squadron.radius();
        }
    }

    boolean[] keys = new boolean[256];
    boolean[] toggle = new boolean[256];

    boolean
            key_up = false,
            key_down = false,
            key_left = false,
            key_right = false,
            key_shift = false;

    public void keyPressed() {
        if (key < 256) {
            keys[Character.toLowerCase(key)] = true;
            toggle[Character.toLowerCase(key)] ^= true;

            if (key == '+') camera.scale += 0.1;
            if (key == '-') camera.scale -= 0.1;

        }
        else if (key == CODED) {
            if (keyCode == UP) key_up = true;
            if (keyCode == DOWN) key_down = true;
            if (keyCode == LEFT) key_left = true;
            if (keyCode == RIGHT) key_right = true;
            if (keyCode == SHIFT) key_shift = true;
        }
    }

    public void keyReleased() {
        if (key =='p') pause = !pause;

        if (pause && music.isPlaying()) music.pause();
        else if (!music.isPlaying()) music.play();

        if (key < 256) {
            keys[Character.toLowerCase(key)] = false;
        }
        else if (key == CODED) {
            if (keyCode == UP) key_up = false;
            if (keyCode == DOWN) key_down = false;
            if (keyCode == LEFT) key_left = false;
            if (keyCode == RIGHT) key_right = false;
            if (keyCode == SHIFT) key_shift = false;
        }
    }

    boolean mouseReleased = false;

    class Mouse {

        PVector position;

        int heldL;
        int heldR;

        int scroll;
        boolean scrolling;


        int i;
        Selector[] selectors;

        Mouse() {
            this.position = new PVector(mouseX, mouseY);

            this.heldL = 0;
            this.heldR = 0;

            this.scroll = 0;
            this.scrolling = false;

            this.i = 0;
            this.selectors = new Selector[] {new Box(), new Lasso()};
        }

        PVector coordinates() {
            return camera.coordinate(position);
        }

        boolean clicked() {
            return heldL > 0;
        }

        boolean rightClicked() {
            return heldR > 0;
        }

        int held() {
            return heldL;
        }

        int rightHeld() {
            return heldR;
        }

        Selector selector() {
            return selectors[i];
        }

        void update() {
            position.set(mouseX, mouseY);

            if (mousePressed)  switch (mouseButton) {
                case LEFT: mouse.heldL++; break;
                case RIGHT: mouse.heldR++; break;
            }

            if (rightHeld() == 1) i = (i + 1) % selectors.length;

            selector().update();

            if (scrolling)
                scrolling = false;
            else
                scroll = 0;


        }
    }

/////////////////////////////////////////////////////////////////////////////
// MOUSE UPDATE EVENTS
/////////////////////////////////////////////////////////////////////////////

    public void mouseWheel(MouseEvent event) {
        mouse.scroll = -event.getCount();
        mouse.scrolling = true;
    }

    public void mousePressed() {

    }

    public void mouseReleased() {
        switch (mouseButton) {
            case LEFT: mouse.heldL = 0; break;
            case RIGHT: mouse.heldR = 0; break;
        }

        mouseReleased = true;
    }

/////////////////////////////////////////////////////////////////////////////
// SELECTOR CLASSES
/////////////////////////////////////////////////////////////////////////////

    abstract class Selector {
        abstract boolean available();
        abstract HashSet<Cell> selection(HashSet<Cell> filter);

        abstract void update();
        abstract void drawCursor();
        abstract void drawSelection();
    }

    class Box extends Selector {
        PVector marker;

        Box() {
            this.marker = new PVector(mouseX, mouseY);
        }

        boolean available() {
            return mouseReleased && dist(mouse.position, marker) > CELL_RADIUS;
        }

        HashSet<Cell> selection(HashSet<Cell> filter) {
            HashSet<Cell> selection = new HashSet<>();

            PVector position = mouse.coordinates();

            for (Cell cell : filter) {
                float min_x = min(marker.x, position.x);
                float max_x = max(marker.x, position.x);

                float min_y = min(marker.y, position.y);
                float max_y = max(marker.y, position.y);

                //noinspection SuspiciousNameCombination
                if (constrained(cell.nucleus.position.x, min_x, max_x) && constrained(cell.nucleus.position.y, min_y, max_y))
                    selection.add(cell);
            }

            return selection;
        }

        void update() {
            if (mouse.held() == 1 || mouse.rightClicked()) {
                marker = mouse.coordinates();
            }
        }

        void drawCursor() {
            push();
            fill(0, 0);
            stroke(WHITE);
            square(mouse.position, CELL_RADIUS);
            stroke(BLACK);
            square(mouse.position, CELL_RADIUS + 2);
            pop();
        }

        void drawSelection() {
            push();
            fill(WHITE, 125);
            rect(marker, mouse.coordinates());
            pop();
        }
    }

    class Lasso extends Selector {
        ArrayList<PVector> lasso;
        ArrayList<Integer> ranges;

        float lasso_range;

        Lasso() {
            this.lasso = new ArrayList<>();
            this.ranges = new ArrayList<>();

            this.lasso_range = CELL_RADIUS * 5;
        }

        boolean available() {
            return mouseReleased;
        }

        HashSet<Cell> selection(HashSet<Cell> filter) {
            HashSet<Cell> selection = new HashSet<>();

            for (PVector point : lasso)
                for (Cell cell : filter) {
                    if (dist(cell.nucleus.position, point) < lasso_range)
                        selection.add(cell);
                }

            return selection;
        }

        void update() {
            lasso_range = CELL_RADIUS * 5;

            if (mouse.held() == 1 || mouse.rightClicked()) {
                lasso.clear();
                ranges.clear();
            }

            else if (mouse.clicked()) {
                PVector position = mouse.coordinates();
                lasso.add(position);
                ranges.add((int)lasso_range);
            }
        }

        void drawCursor() {
            push();

            fill(0,0);
            stroke(WHITE);
            circle(mouse.position, CELL_RADIUS);
            stroke(BLACK);
            circle(mouse.position, CELL_RADIUS + 2);

            pop();
        }

        void drawSelection() {
            push();

            fill(255, 255/2f);
            stroke(0,0);
            for (int i = 0; i < lasso.size(); i++) {
                circle(lasso.get(i), 2 * ranges.get(i));
            }

            pop();
        }
    }

    class Point {
        PVector position;
        PVector velocity;
        PVector force;

        float drag;
        float mass;

        Point(PVector position) {
            this(position, 1, 0.1f);
        }

        Point(PVector position, float mass, float drag) {
            this.position = position;

            this.velocity = new PVector();
            this.force = new PVector();

            this.mass = mass;
            this.drag = drag;
        }

        void force(PVector impulse) {
            force.add(impulse);
        }

        void integrate() {
            force(PVector.mult(velocity, -drag));

            force.div(mass);
            velocity.add(force);
            position.add(velocity);
            force.set(0, 0);

            boundsCheck();
        }

        void boundsCheck() {
            if (position.mag() > MAP_RADIUS) {
                position.setMag(MAP_RADIUS - 1);
                PVector normal = position.copy().normalize();
                float magnitude = PVector.dot(velocity, normal);
                PVector component = normal.setMag(magnitude);
                velocity.sub(component);
            }

            else if (Float.isNaN(position.x) || Float.isNaN(position.y)) {
                position.set(PVector.random2D());
                velocity.set(0, 0);
                force.set(0, 0);
            }
        }

        PVector to(Point point) {
            return to(point.position);
        }

        PVector to(PVector point) {
            return PVector.sub(point, position);
        }
    }

    void spring(Point a, Point b, float d, float k) {
        float force = (dist(a, b) - d) * k;
        a.force(a.to(b).setMag(force));
        b.force(b.to(a).setMag(force));
    }

    void rod(Point a, Point b, float distance) {
        PVector normal = a.to(b).normalize();

        float delta = distance - dist(a, b);

        normal.mult(delta/2);

        a.position.sub(normal);
        b.position.add(normal);

        a.boundsCheck();
        b.boundsCheck();
    }
}
