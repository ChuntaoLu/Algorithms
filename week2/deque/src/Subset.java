/**
 * Created by lu on 3/12/14.
 * Client class using RandomizedQueue
 */
public class Subset {
    public static void main(String[] args) {
        int k =Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            rq.enqueue(s);
        }
        for (int i = 0; i < k; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}
