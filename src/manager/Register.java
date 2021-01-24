package manager;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Register<T> {
    private final ArrayList<T> items;
    private final ArrayDeque<T> additions;
    private final ArrayList<Integer> deletions;

    private final ReadList<T> read;

    Register(ArrayList<T> items, ArrayList<Integer> deletions) {
        this.items = items;
        this.additions = new ArrayDeque<>();
        this.deletions = deletions;
        this.read = new ReadList<>(items);
    }

    void update() {
        assert(deletions.size() <= items.size());

        for (int i = deletions.size() - 1; i >= 0; i--) {
            int index = deletions.get(i);
            assert (index < items.size());

            T last = items.remove(items.size() - 1);

            if (index < items.size()) {
                items.set(index, last);
            }
        }

        while(!additions.isEmpty()) {
            items.add(additions.poll());
        }
    }

    void add(T item) {
        additions.push(item);
    }

    public ReadList<T> read() {
        return read;
    }
}
