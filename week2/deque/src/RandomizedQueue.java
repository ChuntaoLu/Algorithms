/**
 * Created by lu on 3/11/14.
 *
 * Randomized queue
 *
 */
import java.util.Iterator;
import java.util.NoSuchElementException;



public class RandomizedQueue<Item> implements Iterable<Item> {
    private int N;                          // size of the queue
    private Item[] queue;                   // array of items

    /**
     * Constructs an empty randomized queue
     */
    public RandomizedQueue() {
        queue = (Item[]) new Object[2];
    }

    /**
     * Is the queue empty?
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Returns the number of items on the queue
     */
    public int size() {
        return N;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= N;
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++) { temp[i] = queue[i]; }
        queue = temp;
    }

    /**
     * Adds the item
     */
    public void enqueue(Item item) {
        if (item == null) throw new NullPointerException();
        if (N == queue.length) resize(2 * N);
        queue[N++] = item;
    }

    /**
     * Deletes and returns a random item
     */
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int randIndex = StdRandom.uniform(N);
        Item item = queue[randIndex];
        queue[randIndex] = queue[N - 1];                 
        queue[N-1] = null;              // avoid loitering
        N--;
        if (N > 0 && N == queue.length / 4) resize(queue.length / 2);
        return item;
    }

    /**
     * Returns (but does not delete) a random item
     */
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();
        return queue[StdRandom.uniform(N)];
    }

    /**
     * Returns an independent iterator over times in random order
     */
    public Iterator<Item> iterator() {
        return new QueueIterator();
    }

    // an iterator
    private class QueueIterator implements Iterator<Item> {
        private int i;
        private int[] indexArray;

        private void shuffle() {
            for (int i = 0; i < N; i++) {
                int randIndex = i + StdRandom.uniform(N - i);
                int swap = indexArray[randIndex];
                indexArray[randIndex] = indexArray[i];
                indexArray[i] = swap;
            }
        }

        public QueueIterator() {
            indexArray = new int[N];
            for (int j = 0; j < N; j++) indexArray[j] = j;
            shuffle();
            i = N;
        }

        public boolean hasNext() {
            return i > 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return queue[indexArray[--i]];
        }
    }

    /**
     * Unit test
     */
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        for (int i = 0; i < 10; i++) rq.enqueue(i);
        StdOut.println("Randomized queue of size 10 (0 to 9) created:");
        StdOut.println("isEmpty?: " + rq.isEmpty() + " should be false");
        StdOut.println("size: " + rq.size() + " should be 10");

        StdOut.println("\nTest iterator:(sequence below should have random order)");
        for (int i : rq) StdOut.print(i + " ");
        StdOut.println();
        for (int i : rq) StdOut.print(i + " ");
        StdOut.println();
        for (int i : rq) StdOut.print(i + " ");
        StdOut.println();

        StdOut.println("\nDequeue an item: " + rq.dequeue());
        StdOut.println("size: " + rq.size() + " should be 9");
        StdOut.println("\nDequeue an item: " + rq.dequeue());
        StdOut.println("size: " + rq.size() + " should be 8");


        StdOut.println("\nSample an item: " + rq.sample());
        StdOut.println("size: " + rq.size() + " should be 8");
        StdOut.println("\nSample an item: " + rq.sample());
        StdOut.println("size: " + rq.size() + " should be 8");
    }
}
