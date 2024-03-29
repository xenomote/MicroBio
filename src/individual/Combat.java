package individual;

import cell.Attack;
import cell.Cell;
import game.EnergySource;
import group.Squadron;
import processing.core.PVector;

import java.util.List;
import java.util.Optional;

import static cell.Cell.MAX_ENERGY;
import static processing.core.PVector.dist;

public class Combat extends Individual {
    public static final float DETECTION_RANGE = 1000;
    public static final float ATTACK_RANGE = 40;
    public static final float ATTACK_COST = 10;
    public static final float ATTACK_DAMAGE = 25;

    public static final int ATTACK_SPEED = 100;

    private int attackCooldown;
    private boolean recharging;

    private Cell target;

    private final Squadron squadron;

    public Combat(Cell cell, Squadron squadron) {
        super(cell);
        this.squadron = squadron;
        this.recharging = false;
        this.target = null;
    }

    // TODO: 29/12/2020 prevent repeated adding of attacker for instant damage
    // TODO: 14/01/2021 attack any target in range if not targeting
    public void update() {
        if (attackCooldown > 0) {
            attackCooldown--;
        }

        if (cell.energy().stored() < MAX_ENERGY/2 || recharging) {
            List<EnergySource> sources = squadron.getCommander().sourceMap().get(cell.getPosition(), DETECTION_RANGE);

            Optional<EnergySource> closest = Optional.empty();
            float min_dist = Float.POSITIVE_INFINITY;

            for (EnergySource energySource : sources) {
                float dist = dist(energySource.getPosition(), cell.getPosition()) - energySource.getRadius();

                if (dist < min_dist) {
                    closest = Optional.of(energySource);
                    min_dist = dist;
                }
            }

            if (closest.isPresent()) {
                cell.seek(closest.get().getPosition());
            } else {
                cell.stop();
            }

            recharging = !cell.energy().full();
        } else if (dist(cell.getPosition(), squadron.rally) > squadron.radius() * 2) {
            findTarget();
        } else {
            if (target != null) {
                if (target.health().empty() || target.energy().empty()) {
                    findTarget();
                } else {
                    if (inAttackRange(target) && canAttack()) {
                        attack(target);
                    }
                    cell.seek(target.getPosition());
                }
            } else {
                findTarget();
            }
        }
    }

    private void findTarget() {
        target = closestTarget();

        if (!inRallyRange(cell)) {
            cell.seek(squadron.rally);
        } else {
            cell.stop();
        }
    }

    private Cell closestTarget() {
        Cell closest = null;
        float min_dist = DETECTION_RANGE;

        for (Cell target : squadron.getTargets()) {
            float dist = dist(cell.getPosition(), target.getPosition());
            if (dist < min_dist) {
                closest = target;
                min_dist = dist;
            }
        }

        return closest;
    }

    private boolean inRallyRange(Cell cell) {
        return dist(cell.getPosition(), squadron.rally) < squadron.radius();
    }

    private boolean inAttackRange(Cell cell) {
        return dist(this.cell.getPosition(), cell.getPosition()) <= ATTACK_RANGE;
    }

    private void attack(Cell target) {
        cell.energy().sub(ATTACK_COST);
        attackCooldown = ATTACK_SPEED;
        target.health().sub(ATTACK_DAMAGE);

        PVector direction = PVector.sub(target.getPosition(), cell.getPosition());
        cell.force(direction.setMag(10));
        target.force(direction.setMag(50));

        PVector position = PVector.add(target.getPosition(), cell.getPosition()).div(2);

        cell.getAttacks().add(new Attack(position));
    }

    private boolean canAttack() {
        return attackCooldown == 0 && cell.energy().stored() > ATTACK_COST;
    }
}
