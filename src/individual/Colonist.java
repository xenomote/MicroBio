package individual;

import cell.Cell;
import group.Colony;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PVector.dist;

public class Colonist extends Individual {
    public static final float DIVISION_MINIMUM = 50;
    public static final float ENERGY_TRANSFER_RATE = 1f;
    public static final int DIVISION_COOLDOWN = 1000;

    private int divisionCooldown;

    private final Colony colony;

    public Colonist(Cell cell, Colony colony) {
        super(cell);
        this.divisionCooldown = DIVISION_COOLDOWN;
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
        }

        else {
            cell.stop();
            transfer();

            if (canDivide()) {
                divide();
            }
        }

        transfer();
    }

    private void transfer() {
        List<Cell> recipients = new ArrayList<>();
        for (Cell other : cell.getAttachments()) {
            if (other.getEnergy() < cell.getEnergy()) {
                recipients.add(other);
            }
        }

        // TODO: 25/07/2020 dirty bit for energy updating between ticks
        float rate = recipients.size() / ENERGY_TRANSFER_RATE;
        for (Cell other : recipients) {
            cell.useEnergy(rate);
            other.addEnergy(rate);
        }
    }

    private void divide() {
        cell.getCommander().spawn(new Cell(cell.getCommander(), cell.getGroup(), PVector.random2D().add(cell.getPosition())));

        divisionCooldown = DIVISION_COOLDOWN;
    }

    // TODO: 30/12/2020 randomise division rate
    private boolean canDivide() {
        return divisionCooldown == 0;// && cell.getEnergy() > DIVISION_MINIMUM;
    }

    private boolean displaced() {
        return cell.getAttachments().size() < 2 || dist(cell.getPosition(), colony.center) > colony.radius();
    }
}