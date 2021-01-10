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
    public static final int MAX_DIVISION_COOLDOWN = 1000;
    public static final int MIN_DIVISION_COOLDOWN = 500;

    private int divisionCooldown;

    private final Colony colony;

    public Colonist(Cell cell, Colony colony) {
        super(cell);
        this.divisionCooldown = randomDivisionCooldown();
        this.colony = colony;
    }

    public void update() {
        if (divisionCooldown > 0) {
            divisionCooldown--;
        }

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

            if (canDivide()) {
                divide();
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

    // TODO: 10/01/2021 spawn new cells with energy evenly divided
    private void divide() {
        cell.getCommander().spawn(new Cell(cell.getCommander(), cell.getGroup(), PVector.random2D().add(cell.getPosition())));

        divisionCooldown = randomDivisionCooldown();
    }

    private boolean canDivide() {
        return divisionCooldown == 0 && cell.energy().stored() > DIVISION_MINIMUM;
    }

    private boolean displaced() {
        return cell.getAttachments().size() < 2 || dist(cell.getPosition(), colony.center) > colony.radius();
    }

    private static int randomDivisionCooldown() {
        return MIN_DIVISION_COOLDOWN + (int) (Math.random() * (MAX_DIVISION_COOLDOWN - MIN_DIVISION_COOLDOWN));
    }
}