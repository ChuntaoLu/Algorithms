/**
 * Created by lu on 4/13/14.
 * KdTree for efficient 2D range search
 */
public class KdTree {
    private Node root;      // root of KdTree
    private int N;          // size of KdTree
    private int num;        // number of nodes visited before finding nearest

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree
    }

    /**
     * is the tree empty?
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * number of nodes in the tree
     */
    public int size() {
        return N;
    }

    /**
     * insert point p into the KdTree if it is not already there
     * @param p a 2D point
     */
    public void insert(Point2D p) {
         if (N == 0) {
            root = new Node();
            root.p = p;
            root.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
            N++;
        }
        insert(root, p, true);
    }

    // helper for insert
    private void insert(Node x, Point2D p, boolean isVertical) {
        if (x.p.equals(p)) return;
        double cmp = isVertical ? p.x() - x.p.x() : p.y() - x.p.y();
        RectHV r = x.rect;
        if (cmp < 0) {
            if (x.lb == null) {
                Node n = new Node();
                n.p = p;
                if (isVertical) {
                    n.rect = new RectHV(r.xmin(), r.ymin(), x.p.x(), r.ymax());
                } else {
                    n.rect = new RectHV(r.xmin(), r.ymin(), r.xmax(), x.p.y());
                }
                x.lb = n;
                N++;
            } else {
                insert(x.lb, p, !isVertical);
            }
        } else {
            if (x.rt == null) {
                Node n = new Node();
                n.p = p;
                if (isVertical) {
                    n.rect = new RectHV(x.p.x(), r.ymin(), r.xmax(), r.ymax());
                } else {
                    n.rect = new RectHV(r.xmin(), x.p.y(), r.xmax(), r.ymax());
                }
                x.rt = n;
                N++;
            } else {
                insert(x.rt, p, !isVertical);
            }
        }
    }

    /**
     * does the KdTree contain point p?
     * @param p a 2D point
     */
    public boolean contains(Point2D p) {
        return contains(root, p, true);
    }

    // helper for contains
    private boolean contains(Node x, Point2D p, boolean isVertical) {
        if (x == null) return false;
        if (x.p.equals(p)) return true;
        double cmp = isVertical ? p.x() - x.p.x() : p.y() - x.p.y();
        if (cmp < 0) return contains(x.lb, p, !isVertical);
        else         return contains(x.rt, p, !isVertical);
    }

    /**
     * draw all the points to standard draw
     */
    public void draw() {
        draw(root, true);
    }

    // helper for draw
    private void draw(Node n, boolean isVertical) {
        if (n == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        StdDraw.point(n.p.x(), n.p.y());
        StdDraw.setPenRadius();
        if (isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }
        draw(n.lb, !isVertical);
        draw(n.rt, !isVertical);
    }

    /**
     * all points in the set that are inside the rectangle
     */
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> pointsInRect = new Queue<Point2D>();
        range(root, rect, pointsInRect);
        return pointsInRect;
    }

    // helper for range
    private void range(Node n, RectHV r, Queue<Point2D> q) {
        if (n == null || !n.rect.intersects(r)) return;
        if (r.contains(n.p)) q.enqueue(n.p);
        range(n.lb, r, q);
        range(n.rt, r, q);
    }

    /**
     * a nearest neighbour in the set to p; null if set is empty
     */
    public Point2D nearest(Point2D p) {
        if (N == 0) return null;
        return nearest(root, p, root.p, true);
    }

    // helper for nearest
    private Point2D nearest(Node n, Point2D p, Point2D currentNear, boolean isVertical) {
        double currentMinDist = currentNear.distanceSquaredTo(p);
        if (n == null || currentMinDist <= n.rect.distanceSquaredTo(p)) return currentNear;
        if (currentMinDist > n.p.distanceSquaredTo(p)) currentNear = n.p;
        num++;
        double cmp = isVertical ? p.x() - n.p.x() : p.y() - n.p.y();
        if (cmp < 0) {
            Point2D lbNear = nearest(n.lb, p, currentNear, !isVertical);
            if (n.rt == null || lbNear.distanceSquaredTo(p) <= n.rt.rect.distanceSquaredTo(p)) {
                return lbNear;
            } else {
                Point2D rtNear = nearest(n.rt, p, currentNear, !isVertical);
                return lbNear.distanceSquaredTo(p) <= rtNear.distanceSquaredTo(p) ? lbNear : rtNear;
            }
        } else {
            Point2D rtNear = nearest(n.rt, p, currentNear, !isVertical);
            if (n.lb == null || rtNear.distanceSquaredTo(p) <= n.lb.rect.distanceSquaredTo(p)) {
                return rtNear;
            } else {
                Point2D lbNear = nearest(n.lb, p, currentNear, !isVertical);
                return lbNear.distanceSquaredTo(p) <= rtNear.distanceSquaredTo(p) ? lbNear : rtNear;
            }
        }
    }

    /**
     * test
     */
    public static void main(String[] args) {
//        KdTree t = new KdTree();
//        StdOut.println(t.isEmpty());
//        StdOut.println(t.size());
//        t.insert(new Point2D(0.7, 0.2));
//        t.insert(new Point2D(0.5, 0.4));
//        t.insert(new Point2D(0.2, 0.3));
//        t.insert(new Point2D(0.4, 0.7));
//        t.insert(new Point2D(0.9, 0.6));
//        StdOut.println(t.isEmpty());
//        StdOut.println(t.size());
//        StdOut.println(t.contains(new Point2D(0.5, 0.4)));
//        StdOut.println(t.contains(new Point2D(0.7, 0.5)));
//        StdOut.println(t.nearest(new Point2D(0.7, 0.9)));
//        t.draw();
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        kdtree.draw();
        StdOut.println(kdtree.nearest(new Point2D(.81, .30)));
        StdOut.println(kdtree.num);
    }
}
