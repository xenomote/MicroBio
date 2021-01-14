package individual;

import cell.Cell;
import cell.Resource;
import group.Colony;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.min;
import static processing.core.PVector.dist;

public class Colonist extends Individual {
    public static final float DIVISION_MINIMUM = 50;
    public static final float ENERGY_TRANSFER_RATE = 1f;
    public static final int MAX_GROWTH_PERIOD = 1000;
    public static final int MIN_GROWTH_PERIOD = 500;

    private int growthPeriod;

    private final Colony colony;

    public Colonist(Cell cell, Colony colony) {
        super(cell);
        this.growthPeriod = randomGrowthPeriod();
        this.colony = colony;
    }

    public void update() {
        List<Cell> attachments = cell.getAttachments();
        attachments.clear();

        for (Cell collision : cell.getCollisions()) {
            if (colony.cells().contains(collision)) {
                attachments.add(collision);
            }
        }

        if (displaced()) {
            cell.seek(colony.center);
        } else {
            cell.stop();

            if (canGrow()) {
                if (growthPeriod > 0) {
                    growthPeriod--;
                } else {
                    divide();
                    growthPeriod = randomGrowthPeriod();
                }
            }
        }

        transfer();
    }

    private void transfer() {
        Resource source = cell.energy();
        List<Resource> recipients = new ArrayList<>();
        for (Cell other : cell.getAttachments()) {
            if (other.energy().stored() < source.stored()) {
                recipients.add(other.energy());
            }
        }

        float rate = min(source.stored(), ENERGY_TRANSFER_RATE) / recipients.size();
        for (Resource other : recipients) {
            source.sub(rate);
            source.add(other.add(rate)); // return excess to the source
        }
    }

    private void divide() {
        float energy = cell.energy().stored() / 2;
        cell.energy().sub(energy);

        cell.getCommander().spawn(
                new Cell(
                        cell.getCommander(),
                        cell.getGroup(),
                        PVector.random2D().add(cell.getPosition()),
                        energy
                )
        );
    }

    private boolean canGrow() {
        int attachments = cell.getAttachments().size();
        return cell.energy().stored() > DIVISION_MINIMUM && 0 < attachments && attachments < 4;
    }

    private boolean displaced() {
        return cell.getAttachments().size() < 2 || dist(cell.getPosition(), colony.center) > colony.radius();
    }

    private static int randomGrowthPeriod() {
        return MIN_GROWTH_PERIOD + (int) (Math.random() * (MAX_GROWTH_PERIOD - MIN_GROWTH_PERIOD));
    }
}