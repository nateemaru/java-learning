import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BlockingQueue<T> {
    private final int maxSize;
    private final Deque<T> tasks;
    private final Object monitor = new Object();

    public BlockingQueue(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        this.maxSize = size;
        this.tasks = new ArrayDeque<>();
    }

    public void enqueue(T item) throws InterruptedException {
        Objects.requireNonNull(item, "Item must not be null");

        synchronized (monitor) {
            while (tasks.size() == maxSize) {
                monitor.wait();
            }

            tasks.addLast(item);
            monitor.notifyAll();
        }
    }

    public T dequeue() throws InterruptedException {
        synchronized (monitor) {
            while (tasks.isEmpty()) {
                monitor.wait();
            }

            T item = tasks.removeFirst();
            monitor.notifyAll();
            return item;
        }
    }

    public int size() {
        synchronized (monitor) {
            return tasks.size();
        }
    }
}
