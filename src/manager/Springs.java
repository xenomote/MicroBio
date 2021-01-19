package manager;

import processing.core.PVector;

public class Springs {
    private final PhysicsStore a;
    private final PhysicsStore b;
    private final float k;

    public Springs(PhysicsStore a, PhysicsStore b, float k) {
        this.a = a;
        this.b = b;
        this.k = k;
    }

    public void update() {
        ReadList<PVector> ap = a.getPositions();
        ReadList<PVector> af = a.getForces();

        ReadList<PVector> bp = b.getPositions();
        ReadList<PVector> bf = b.getForces();

        assert(ap.size() == af.size());
        assert(af.size() == bp.size());
        assert(bp.size() == bf.size());

        for (int i = 0; i < ap.size(); i++) {
            PVector delta = PVector.sub(ap.get(i), bp.get(i));

            af.get(i).add(PVector.mult(delta, k));
            bf.get(i).add(PVector.mult(delta, -k));
        }
    }
}
