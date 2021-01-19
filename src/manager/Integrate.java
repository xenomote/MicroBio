package manager;

import processing.core.PVector;

public class Integrate {
    private final ReadList<PVector> as;
    private final ReadList<PVector> bs;

    public Integrate(ReadList<PVector> as, ReadList<PVector> bs) {
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
