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

    public static final float DETECTION_RANGE = 100;
    public static final float ATTACK_RANGE = 40;
    public static final float ATTACK_COST = 10;
    public static final float ATTACK_DAMAGE = 10;

    public static final int ATTACK_SPEED = 100;

    private int attackCooldown;
    private boolean recharging;


    private Squadron squadron;

    public Combat(Cell cell, Squadron squadron) {
        super(cell);
        this.squadron = squadron;
        this.recharging = false;
    }

    public void update() {
        if (attackCooldown > 0) attackCooldown--;

        if (cell.getEnergy() < MAX_ENERGY/2 || recharging) {
            List<EnergySource> sources = squadron.getCommander().sourceMap().get(cell.getPosition(), DETECTION_RANGE);

            Optional<EnergySource> source = sources.stream().min((a, b) -> {
                float x = dist(cell.getPosition(), a.getPosition());
                float y = dist(cell.getPosition(), b.getPosition());

                return Float.compare(x, y);
            });

            source.filter(s -> dist(cell.getPosition(), s.getPosition()) > s.getRadius());

            if (source.isPresent()) cell.seek(source.get().getPosition());
            else cell.stop();

            recharging = cell.getEnergy() != MAX_ENERGY;
        }

        else if (!inRallyRange(cell)) {
            cell.seek(squadron.rally);
        }

        else {
            Optional<Cell> target = closestTarget();

            if (target.isPresent()) {
                Cell victim = target.get();
                cell.seek(victim.getPosition());
                if (inAttackRange(victim) && canAttack()) attack(victim);
            }

            else cell.stop();
        }
    }

    private Optional<Cell> closestTarget() {
        List<Cell> cells = squadron.getCommander().cellMap().get(cell.getPosition(), DETECTION_RANGE);

        return cells.stream()
                .filter(target -> target.getCommander() != cell.getCommander())
                .filter(this::inRallyRange)
                .min((a, b) -> {
                    float x = dist(cell.getPosition(), a.getPosition());
                    float y = dist(cell.getPosition(), b.getPosition());

                    return Float.compare(x, y);
                });
    }

    private boolean inRallyRange(Cell cell) {
        return dist(cell.getPosition(), squadron.rally) < squadron.radius();
    }

    private boolean inAttackRange(Cell cell) {
        return dist(this.cell.getPosition(), cell.getPosition()) <= ATTACK_RANGE;
    }

    private void attack(Cell target) {
        cell.useEnergy(ATTACK_COST);
        attackCooldown = ATTACK_SPEED;
        target.damage(ATTACK_DAMAGE);

        PVector direction = PVector.sub(target.getPosition(), cell.getPosition());
        cell.force(direction.setMag(10));
        target.force(direction.setMag(50));

        PVector position = PVector.add(target.getPosition(), cell.getPosition()).div(2);

        // TODO: 25/07/2020 collect attacks to global list
        new Attack(position);
    }

    private boolean canAttack() {
        return attackCooldown == 0 && cell.getEnergy() > ATTACK_COST;
    }
}
