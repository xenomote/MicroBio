package game;

public class Counter {
    private int max;
    private int min;
    private int total;
    private int current;

    public Counter(int start) {
        this.current = start;
        this.total = start;
        this.max = start;
        this.min = start;
    }

    public void inc() {
        current++;
        total++;

        if (current > max) {
            max = current;
        }
    }

    public void dec() {
        if (current > 0) {
            current--;
        }

        if (current < min) {
            min = current;
        }
    }

    public int getCurrent() {
        return current;
    }

    public int getTotal() {
        return total;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    @Override
    public String toString() {
        return "{" +
                "current=" + getCurrent() +
                ", total=" + getTotal() +
                ", max=" + getMax() +
                ", min=" + getMin() +
                '}';
    }
}
