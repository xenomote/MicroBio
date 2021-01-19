package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class Integrate {
    private final ArrayList<PVector> as;
    private final ArrayList<PVector> bs;

    public Integrate(ArrayList<PVector> as, ArrayList<PVector> bs) {
        this.as = as;
        this.bs = bs;
    }

    public void update(float time) {
        assert(as.size() == bs.size());

        for (int i = 0; i < bs.size(); i++) {
            PVector a = as.get(i);
            PVector b = bs.get(i);

            b.add(a.x * time, a.y * time);
        }
    }
}
