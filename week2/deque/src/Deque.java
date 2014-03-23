/**
 * Created by lu on 3/11/14.
 *
 * Deque implemented with a doubly linked list.
 *
 */

import java.util.Iterator;
import java.util.NoSuchElementException;


public class Deque<Item> implements Iterable<Item> {
    private int N;              // size of the deque
    private Node<Item> head;    // head sentinel before first item
    private Node<Item> tail;    // tail sentinel after last item

    // helper doubly linked list class
    private static class Node<Item> {
        private Item item;
        private Node<Item> prev;
        private Node<Item> next;
    }

    /**
     * Initializes an empty deque.
     */
    public Deque() {
        head = new Node<Item>();
        tail = new Node<Item>();
        head.next = tail;
        tail.prev = head;
        N = 0;
    }


    /**
     * Is the deque empty?
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Returns the size of the deque.
     */
    public int size() {
        return N;
    }

    /**
     * Inserts the item at the front.
     */
    public void addFirst(Item item) {
        if (item == null) { throw new NullPointerException(); }
        Node<Item> oldFirst = head.next;
        Node<Item> first = new Node<Item>();
        first.item = item;
        first.prev = head;
        first.next = oldFirst;
        oldFirst.prev = first;
        head.next = first;
        N++;
    }

    /**
     * Inserts the item at the end.
     */
     public void addLast(Item item) {
         if (item == null) { throw new NullPointerException(); }
         Node<Item> oldLast = tail.prev;
         Node<Item> last = new Node<Item>();
         last.item = item;
         last.prev = oldLast;
         last.next = tail;
         oldLast.next = last;
         tail.prev = last;
         N++;
     }

    /**
     * Deletes and return the item at the front.
     */
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque underflow");
        Item item = head.next.item;
        head.next = head.next.next;
        head.next.prev = head;
        N--;
        return item;
    }

    /**
     * Deletes and return the item at the end.
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque underflow");
        Item item = tail.prev.item;
        tail.prev = tail.prev.prev;
        tail.prev.next = tail;
        N--;
        return item;
    }

    /**
     * Returns an iterator over items in order from front to end.
     */
    public Iterator<Item> iterator() {
        return new ListIterator<Item>(head.next);
    }

    // an iterator
    private class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;
        private int counter = 0;

        public ListIterator(Node<Item> first) {
            current = first;
        }
        public boolean hasNext() { return counter < N; }
        public void remove() { throw new UnsupportedOperationException(); }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            current = current.next;
            counter++;
            return item;
        }
    }

    /**
     * Unit test
     */
    public static void main(String[] args) {
        Deque<String> stringDeque = new Deque<String>();
        StdOut.println("--New empty deque created--");
        StdOut.println("size: " + stringDeque.size() + " should be size 0");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be true");

        StdOut.println("\nEdge test(head):");
        stringDeque.addFirst("a");
        StdOut.println("--String 'a' added after head--");
        StdOut.println("size: " + stringDeque.size() + " should be size 1");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be false");
        StdOut.print("Test iterator: ");
        for (String i : stringDeque) { StdOut.print(i + " "); }
        StdOut.println("");
        stringDeque.removeFirst();
        StdOut.println("--First element removed--");
        StdOut.println("size: " + stringDeque.size() + " should be size 0");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be true");

        StdOut.println("\nEdge test(tail):");
        stringDeque.addLast("z");
        StdOut.println("--String 'z' added before tail--");
        StdOut.println("size: " + stringDeque.size() + " should be size 1");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be false");
        StdOut.print("Test iterator: ");
        for (String i : stringDeque) { StdOut.print(i + " "); }
        StdOut.println("");
        stringDeque.removeLast();
        StdOut.println("--Last element removed--");
        StdOut.println("size: " + stringDeque.size() + " should be size 0");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be true");

        StdOut.println("\nTest common cases:(add)");
        stringDeque.addFirst("b");
        stringDeque.addFirst("a");
        stringDeque.addLast("c");
        stringDeque.addLast("d");
        StdOut.println("size: " + stringDeque.size() + " should be size 4");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be false");
        StdOut.print("Test iterator: ");
        for (String i : stringDeque) { StdOut.print(i + " "); }
        StdOut.println("");

        StdOut.println("\nTest common cases:(remove)");
        stringDeque.removeFirst();
        StdOut.println("--First element removed--");
        stringDeque.removeLast();
        StdOut.println("--Last element removed--");
        StdOut.println("size: " + stringDeque.size() + " should be size 2");
        StdOut.println("isEmpty?: " + stringDeque.isEmpty() + " should be false");
        StdOut.print("Test iterator: ");
        for (String i : stringDeque) { StdOut.print(i + " "); }
        StdOut.println("");

        StdOut.println("\nTest order:(asc)");
        Deque<Integer> intDeque = new Deque<Integer>();
        for (int i = 0; i < 10; i++) { intDeque.addFirst(i); }
        StdOut.println("Below should be 0 to 9:");
        for (int i = 0; i < 10; i++) { StdOut.print(intDeque.removeLast() + " "); }
        StdOut.println("");

        StdOut.println("\nTest order:(desc)");
        for (int i = 0; i < 10; i++) { intDeque.addLast(i); }
        StdOut.println("Below should be 9 to 0:");
        for (int i = 0; i < 10; i++) { StdOut.print(intDeque.removeLast() + " "); }
        StdOut.println("");
    }
}
