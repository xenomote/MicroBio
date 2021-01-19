package manager;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Register<T> {
    private final ArrayList<T> items;
    private final ArrayDeque<T> additions;
    private final ArrayDeque<Integer> deletions;

    Register(ArrayList<T> items, ArrayDeque<Integer> deletions) {
        this.items = items;
        this.additions = new ArrayDeque<>();
        this.deletions = deletions;
    }

    void update() {
        assert(deletions.size() < items.size());

        while(!deletions.isEmpty()) {
            int i = deletions.poll();
            assert(i < items.size());

            T last = items.remove(items.size() - 1);
            items.set(i, last);
        }

        while(!additions.isEmpty()) {
            items.add(additions.poll());
        }
    }

    void add(T item) {
        additions.push(item);
    }
}
