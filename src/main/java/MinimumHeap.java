import java.util.Comparator;

public class MinimumHeap<T>  {
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    Object[] heap; // non-private to simplify nested class access
    private int size = 0;
    private final Comparator<? super T> comparator; // In this heap, comparator must be given
    public MinimumHeap(Comparator<? super T> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    public MinimumHeap(int initialCapacity,
                       Comparator<? super T> comparator) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.heap = new Object[initialCapacity];
        this.comparator = comparator;
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Extend capacity of heap
     */
    private void grow() {
        int oldCapacity = heap.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        Object[] newQueue = new Object[newCapacity];
        for (int i = 0; i < heap.length; i++) {
            newQueue[i] = heap[i];
        }
        heap = newQueue;
    }


    public boolean add(T t) {
        if (t == null)
            throw new NullPointerException();
        int i = size;
        if (i >= heap.length)
            grow();
        size = i + 1;
        if (i == 0)
            heap[0] = t;
        else
            siftUp(i, t);
        return true;
    }

    public T peek() {
        return (size == 0) ? null : (T) heap[0];
    }

    public int size() {
        return size;
    }


    /**
     * Return the minimum elements in the heap
     * @return
     */
    public T poll() {
        if (size == 0)
            return null;
        int s = --size;
        T result = (T) heap[0];
        T backup = (T) heap[s];
        heap[s] = null; // delete
        if (s != 0)
            siftDown(0, backup);
        return result;
    }


    private void siftUp(int k, T x) {
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            Object e = heap[parent];
            if (comparator.compare(x, (T) e) >= 0)
                break;
            heap[k] = e;
            k = parent;
        }
        heap[k] = x;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftDown(int k, T x) {
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = heap[child];
            int right = child + 1;
            if (right < size &&
                    comparator.compare((T) c, (T) heap[right]) > 0)
                c = heap[child = right];
            if (comparator.compare(x, (T) c) <= 0)
                break;
            heap[k] = c;
            k = child;
        }
        heap[k] = x;
    }
}
