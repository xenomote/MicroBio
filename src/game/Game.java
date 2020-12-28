package game;

import cell.*;
import group.Colony;
import group.Group;
import space.*;
import commander.*;
import controller.*;
import processing.core.*;
import processing.event.*;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.List;

import static cell.Cell.MEMBRANE_RADIUS;
import static space.QuadSpace.QUAD_INSTANCE;
import static game.Colours.*;

public class Game extends PApplet {
    public static final float MAP_RADIUS = 2500;

    public static final int STARTING_CELLS = 10;
    public static final int OPPOSITION_AIS = 30;
    public static final int ENERGY_SOURCES = 20;

    SoundFile[] splat;
    SoundFile music;

    private Mouse mouse;
    private Keyboard keyboard;
    private Camera camera;
    private PointMass camera_body;
    private CameraControls camera_controls;

    private UIControl ui;

    private boolean ended;

    int max_cells;
    int min_cells;

    PVector center;

    PlayerCommander player;
    List<Commander> commanders;

    private List<Attack> attacks;
    private List<PointMass> physics;
    private List<Movement> movement;
    private List<Area<Cell>> areas;

    private List<Cell> cells;
    private List<EnergySource> sources;

    private Space<Cell> cell_map;
    private Space<EnergySource> source_map;

    public static void main(String[] args) {
        new Game().runSketch();
    }

    public PVector randomPosition() {
        return PVector.random2D().mult(random(0, MAP_RADIUS));
    }


    //##################################################################################################################
    // SETUP METHODS
    //##################################################################################################################


    @Override
    public void settings() {
        size(1000, 500, P2D);
        //smooth(8);
    }

    @Override
    public void setup() {
        noCursor();
        frameRate(60);

        rectMode(RADIUS);

        // PFont arial = createFont("Lucida Sans", 100, true);
        // textFont(arial);

        // splat = new SoundFile[]{
        //         new SoundFile(this, "splat01.wav"),
        //         new SoundFile(this, "splat02.wav"),
        //         new SoundFile(this, "splat03.wav"),
        //         new SoundFile(this, "splat04.wav"),
        //         new SoundFile(this, "splat05.wav"),
        //         new SoundFile(this, "splat06.wav"),
        //         new SoundFile(this, "splat07.wav"),
        //         new SoundFile(this, "splat08.wav"),
        //         new SoundFile(this, "splat09.wav"),
        //         new SoundFile(this, "splat10.wav"),
        //         new SoundFile(this, "splat11.wav")
        // };

        //music = new SoundFile(this, "leaving-home-by-kevin-macleod.wav");
        //music.loop();

        attacks = new ArrayList<>();
        commanders = new ArrayList<>();
        sources = new ArrayList<>();
        cells = new ArrayList<>();

        physics = new ArrayList<>();
        movement = new ArrayList<>();
        areas = new ArrayList<>();

        center = new PVector();

        PVector position = new PVector();

        camera = new Camera(position);
        camera_body = new PointMass(position, 1, 0.9f);
        camera.zoom(0.5f);

        mouse = new Mouse(camera);
        keyboard = new Keyboard();

        camera_controls = new CameraControls(mouse, keyboard, camera, camera_body, 1);

        ui = new UIControl(keyboard);

        reset();
    }

    void reset() {
        ended = false;

        attacks = new ArrayList<>();
        cells = new ArrayList<>();
        commanders = new ArrayList<>();
        sources = new ArrayList<>();

        cell_map = new QuadSpaceGroup<>(new PVector(0, 0), MAP_RADIUS);
        source_map = new QuadSpaceGroup<>(new PVector(0, 0), MAP_RADIUS);

        for (int i = 0; i < ENERGY_SOURCES; i++) {
            float radius = random(MEMBRANE_RADIUS, MAP_RADIUS/10);

            PVector position = randomPosition().limit(MAP_RADIUS - radius);
            EnergySource source = new EnergySource(position, radius, 5f, cell_map);
            areas.add(source.getCollider());

            sources.add(source);
        }

        commanders.add(player = new PlayerCommander(cell_map, source_map, randomColor(), mouse, keyboard));

        for (int i = 0; i < OPPOSITION_AIS; i++) {
            commanders.add(new AggressiveAICommander(cell_map, source_map, randomColor(), STARTING_CELLS, random(0.5f, 1)));
        }

        commanders.forEach(commander -> {
            EnergySource source = randomSource();
            PVector position = PVector.random2D().mult(source.getRadius()).add(source.getPosition());
            Colony colony = new Colony(position);

            spawn_group(commander, colony, position, STARTING_CELLS);

            commander.getColonies().add(colony);
        });


        camera.getPosition().set(player.getColonies().get(0).center);

        max_cells = cells.size();
        min_cells = cells.size();
    }

    //##################################################################################################################
    // UPDATE METHODS
    //##################################################################################################################

    @Override
    public void draw() {

        if (!ui.paused()) update_step();

        mouse.update();
        keyboard.update();

        camera_controls.update();
        camera_body.update();

        ui.update();

        if (!ended && commanders.size() == 1) {
            ui.message(commanders.contains(player) ? "you win" : "you lose");
            ended = true;
        }

        render();

        if (keyboard.key('\n').held() == 1) reset();
    }

    private void render() {
        background(DARK_GREY);
        drawCameraView();

        player.drawMouse(g);

        if (ui.information()) {
            printStatistics();
        }

        fill(WHITE);
        textAlign(RIGHT, TOP);
        textSize(100);
        text(str(player.size()), width, 0);

        if (ui.paused()) {
            textAlign(CENTER, TOP);
            text("Paused", width/2f, 0);
        }

        ui.displayMessages(g);
    }

    private void update_step() {
        sources.forEach(EnergySource::update);

        commanders.removeIf(commander -> {
            commander.update();
            return commander.inactive();
        });

        movement.forEach(Movement::update);
        physics.forEach(physics -> {
            physics.update();
            physics.getPosition().limit(MAP_RADIUS);
        });

        areas.forEach(Area::update);

        attacks.forEach(Attack::update);

        cells.forEach(cell -> {
            cell_map.remove(cell);
            cell.updatePosition();
            cell_map.place(cell);
        });

        cells.forEach(Cell::update);

        for (Cell cell : List.copyOf(cells)) {
            if (cell.dead()) {
                cell.kill();
                deregister(cell);
            }
        }

        commanders.forEach(commander -> {
            commander.getSpawned().forEach(this::register);
            commander.getSpawned().clear();
        });
    }

    private int randomColor() {
        return color(random(255), random(255), random(255), 255);
    }

    private EnergySource randomSource() {
        return sources.get((int) random(sources.size() - 1));
    }

    void drawCameraView() {
        pushMatrix();

        camera.transform(g);

        drawMap();

        sources.forEach(source -> source.draw(g));

        if (ui.groups()) {
            for (Commander commander : commanders) commander.drawColonies(g);
            for (Commander commander : commanders) commander.drawSquadrons(g);
        }

        player.highlightCells(g);
        player.highlightSelection(g);

        cells.forEach(cell -> cell.draw(g));
        attacks.forEach(attack -> attack.draw(g));

        if (ui.energy()) drawEnergyLevels();
        if (ui.partition()) cell_map.draw(g);

        player.drawSelection(g);

        popMatrix();
    }

    void drawMap() {
        fill(BLACK);
        stroke(0,0);
        circle(0, 0, MAP_RADIUS * 2);
    }

    void drawEnergyLevels() {
        fill(WHITE);
        stroke(0,0);

        rectMode(CORNER);
        for (Cell cell : cells) rect(cell.getPosition().x - 2, cell.getPosition().y, 4, -cell.getEnergy());
        rectMode(RADIUS);
    }

    void printStatistics() {
        max_cells = max(max_cells, cells.size());
        min_cells = min(min_cells, cells.size());

        fill(WHITE);
        textAlign(LEFT, TOP);
        textSize(18);
        text(
                "min: " + min_cells + "\n" +
                "max: " + max_cells + "\n" +
                "current: " + cells.size() + "\n" +
                "quadrants: " + QUAD_INSTANCE + "\n" +
                "fps: " + (frameRate < 25 ? "slowed " : "") + frameRate + ")",

                0, 0
        );
    }

    //##################################################################################################################
    // SPAWN METHODS
    //##################################################################################################################

    private void spawn_group(Commander commander, Group group, PVector center, int size) {
        float spacing = 2 * MEMBRANE_RADIUS;
        int side = ceil(sqrt((float)size)) - 1;
        PVector corner = PVector.sub(center, new PVector(side/2f, side/2f).mult(spacing));

        for (int i = 0; i < size; i++) {
            int x = i % side;
            int y = i / side;

            PVector offset = new PVector(x, y).mult(spacing);
            PVector position = PVector.add(corner, offset);

            register(new Cell(commander, group, position));
        }
    }

    private void register(Cell cell) {
        physics.add(cell.getNucleus());
        physics.add(cell.getMembrane());
        movement.add(cell.getMovement());
        areas.add(cell.getCollider());

        cells.add(cell);
        cell_map.place(cell);
    }

    private void deregister(Cell cell) {
        physics.remove(cell.getNucleus());
        physics.remove(cell.getMembrane());
        movement.remove(cell.getMovement());
        areas.remove(cell.getCollider());

        cells.remove(cell);
        cell_map.remove(cell);
    }

    //##################################################################################################################
    // SOUND LOCALISATION
    //##################################################################################################################


    void play(SoundFile sound, PVector position) {
        float distance = PVector.dist(camera.getPosition(), position);
        float right = camera.getPosition().x - position.x;

        sound.stop();
        sound.pan(constrain(atan2(-right, displayWidth/camera.getZoom()), -1, 1));
        sound.amp(constrain(log(distance / (MAP_RADIUS * 2)) * sq(camera.getZoom()), -1, 1) * 0.75f);
        sound.play();
    }

    @Override
    protected void handleMouseEvent(MouseEvent event) {
        super.handleMouseEvent(event);
        mouse.handleMouseEvent(event);
    }

    @Override
    protected void handleKeyEvent(KeyEvent event) {
        super.handleKeyEvent(event);
        keyboard.handleKeyEvent(event);
    }
}
