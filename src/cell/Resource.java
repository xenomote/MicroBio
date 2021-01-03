package cell;

public class Resource {
    private float stored;
    private final float limit;

    public Resource(float limit) {
        this(0, limit);
    }

    public Resource(float stored, float limit) {
        this.stored = stored;
        this.limit = limit;
    }

    public float stored() {
        return stored;
    }

    public boolean empty() {
        return stored == 0;
    }

    public boolean full() {
        return stored == limit;
    }

    public float add(float x) {
        float excess = (stored + x) - limit;

        if (excess > 0) {
            stored = limit;
            return excess;
        }

        stored += x;
        return 0;
    }

    public float sub(float x) {
        float deficit = x - stored;

        if (deficit > 0) {
            stored = 0;
            return deficit;
        }

        stored -= x;
        return 0;
    }
}
