package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class Scale {
    private final ReadList<PVector> as;
    private final ReadList<Float> ns;
    private final ReadList<PVector> bs;

    Scale(ReadList<PVector> as, ReadList<Float> ns, ReadList<PVector> bs) {
        this.as = as;
        this.ns = ns;
        this.bs = bs;
    }

    void update() {
        assert(as.size() == ns.size() && ns.size() == bs.size());

        for (int i = 0; i < as.size(); i++) {
            PVector a = as.get(i);
            float n = ns.get(i);
            PVector b = bs.get(i);

            b.set(a.x * n, a.y * n);
        }
    }
}
