import java.util.HashSet;

/**
 * Created by lu on 4/20/14.
 * shortest ancestral path data type
 */
public class SAP {
    private static final int INF = Integer.MAX_VALUE;
    private Digraph g;
    private int cachedV, cachedW;
    private Iterable<Integer> cachedIterableV, cachedIterableW;
    private int cachedAncestor, cachedLength;
    private int cachedIterableAncestor, cachedIterableLength;

    /**
     * constructor takes a digraph
     */
    public SAP(Digraph G) {
        g = new Digraph(G);     // deep copy so that SAP is immutable
        cachedV = INF;
        cachedW = INF;
    }

    // helper for length and ancestor, update cached values
    private void sap(int v, int w) {
        cachedV = v;
        cachedW = w;
        cachedLength = INF;
        cachedAncestor = -1;
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(g, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(g, w);
        int distV, distW, dist;
        for (int i = 0; i < g.V(); i++) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                distV = vBFS.distTo(i);
                if (distV >= cachedLength) continue;
                distW = wBFS.distTo(i);
                if (distW >= cachedLength) continue;
                dist = distV + distW;
                if (dist >= cachedLength) continue;
                cachedLength = dist;
                cachedAncestor = i;
            }
        }
        if (cachedLength == INF) cachedLength = -1;
    }

    // helper for length and ancestor to handle out of bound exception
    private void checkBounds(int v, int w) {
        if (v < 0 || v > g.V() - 1 || w < 0 || w > g.V() - 1)
            throw new IndexOutOfBoundsException("Invalid input argument.");
    }

    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(int v, int w) {
        checkBounds(v, w);
        if (v == cachedV && w == cachedW) return cachedLength;
        sap(v, w);
        return cachedLength;
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(int v, int w) {
        checkBounds(v, w);
        if (v == cachedV && w == cachedW) return cachedAncestor;
        sap(v, w);
        return cachedAncestor;
    }

    // helper for length and ancestor, update cached values
    private void sap(Iterable<Integer> v, Iterable<Integer> w) {
        cachedIterableV = v;
        cachedIterableW = w;
        cachedIterableLength = INF;
        cachedIterableAncestor = -1;
        BreadthFirstDirectedPaths vBFS = new BreadthFirstDirectedPaths(g, v);
        BreadthFirstDirectedPaths wBFS = new BreadthFirstDirectedPaths(g, w);

        int distV, distW, dist;
        for (int i = 0; i < g.V(); i++) {
            if (vBFS.hasPathTo(i) && wBFS.hasPathTo(i)) {
                distV = vBFS.distTo(i);
                if (distV >= cachedIterableLength) continue;
                distW = wBFS.distTo(i);
                if (distW >= cachedIterableLength) continue;
                dist = distV + distW;
                if (dist >= cachedIterableLength) continue;
                cachedIterableLength = dist;
                cachedIterableAncestor = i;
            }
        }
        if (cachedIterableLength == INF) cachedIterableLength = -1;
    }

    // helper for length and ancestor to handle out of bound exception
    private void checkBounds(Iterable<Integer> v, Iterable<Integer> w) {
        for (int i : v) {
            if (i < 0 || i > g.V() - 1) throw new IndexOutOfBoundsException("Invalid input argument.");
        }
        for (int i : w) {
            if (i < 0 || i > g.V() - 1) throw new IndexOutOfBoundsException("Invalid input argument.");
        }
    }

    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkBounds(v, w);
        if (v == cachedIterableV && w == cachedIterableW) return cachedIterableLength;
        sap(v, w);
        return cachedIterableLength;
    }

    /**
     * a common ancestor that participates in a shortest ancestral path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkBounds(v, w);
        if (v == cachedIterableV && w == cachedIterableW) return cachedIterableAncestor;
        sap(v, w);
        return cachedIterableAncestor;
    }

    /**
     * unit test
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
