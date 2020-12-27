package commander;

import cell.Cell;
import game.EnergySource;
import group.Colony;
import group.Squadron;
import processing.core.PVector;
import space.Space;

public class AggressiveAICommander extends Commander {
    private final int minimum;
    private final float aggression;

    public AggressiveAICommander(Space<Cell> cell_map, Space<EnergySource> source_map, int colour, int minimum, float aggression) {
        super(cell_map, source_map, colour);
        this.minimum = minimum;
        this.aggression = aggression;
    }

    @Override
    public void update() {
        super.update();

        for (Squadron squadron : getSquadrons())
            if (squadron.idle())
                squadron.rally.add(PVector.random2D());

        for (Colony colony : getColonies()) {
            int spare = (int) ((colony.size() - minimum) * aggression);

            if (spare > 0) place_squadron(colony.take(spare), colony.center.copy());
        }
    }
}