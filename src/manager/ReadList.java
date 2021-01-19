package manager;

import java.util.ArrayList;
import java.util.Iterator;

public class ReadList<T> implements Iterable<T> {
    private final ArrayList<T> list;

    public ReadList(ArrayList<T> list) {
        this.list = list;
    }

    public T get(int i) {
        return list.get(i);
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
