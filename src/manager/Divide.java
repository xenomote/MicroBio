package manager;

import processing.core.PVector;

public class Divide {
    private final ReadList<PVector> as;
    private final ReadList<Float> ms;
    private final ReadList<PVector> bs;

    Divide(ReadList<PVector> as, ReadList<Float> ms, ReadList<PVector> bs) {
        this.as = as;
        this.ms = ms;
        this.bs = bs;
    }

    void update() {
        assert(as.size() == ms.size() && ms.size() == bs.size());

        for (int i = 0; i < as.size(); i++) {
            PVector a = as.get(i);
            float m = ms.get(i);
            PVector b = bs.get(i);

            b.set(a.x / m, a.y / m);
        }
    }
}
