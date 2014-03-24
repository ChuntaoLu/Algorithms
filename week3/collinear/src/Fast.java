import java.awt.*;
import java.util.Arrays;

/**
 * Created by lu on 3/23/14.
 * Collinear - a fast approach
 */
public class Fast {
    public static void main(String[] args) {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.show();

        String filename = args[0];
        In in = new In(filename);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            Point p = new Point(x, y);
            points[i] = p;
        }

        Arrays.sort(points);

        Point pivot;          // the start point of a potential line
        Point[] pointsCopy;   // copy of the naturally sorted point array
        int nextToPivotIndex; // the index of the point next to pivot of a potential line
        double lastSlope;     // slope between pivot and the next point of a potential line
        double currentSlope;  // slope between pivot and the current point
        int counter;          // counts the number of points having the same slope as lastSlope

        for (int i = 0; i < N; i++) {
            // restore natural order, copy is faster than sort
            pointsCopy = Arrays.copyOf(points, N);

            // sort by slope
            Arrays.sort(pointsCopy, points[i].SLOPE_ORDER);

            // set the current pivot point
            pivot = points[i];
            pivot.draw();
            if (N < 4) continue;

            // slope-sorted array always starts with pivot point,
            // so the point next to pivot initially has index 1
            nextToPivotIndex = 1;

            // includes the pivot and the point next to pivot
            counter = 2;

            // the slope between pivot and the next point of a potential line,
            // updates when line stops growing
            lastSlope = pivot.slopeTo(pointsCopy[nextToPivotIndex]);

            // count the number of points with the same slope,
            // line found if counter > 2: pivot, nextToPivot and the points counted.
            for (int j = 2; j < N; j++) {
                currentSlope = pivot.slopeTo(pointsCopy[j]);
                if (currentSlope == lastSlope) {
                    counter++;
                } else {
                    // only outputs when pivot is the start point of a line
                    // avoid printing permutations
                    if (counter >= 4 && (pivot.compareTo(pointsCopy[nextToPivotIndex]) < 0)) {
                        StdOut.print(pivot + " -> ");
                        for (int k = nextToPivotIndex; k < j - 1; k++) {
                            StdOut.print(pointsCopy[k] + " -> ");
                        }
                        StdOut.print(pointsCopy[j - 1] + "\n");
                        pivot.drawTo(pointsCopy[j - 1]);
                    }
                    counter = 2;
                    nextToPivotIndex = j;
                    lastSlope = currentSlope;
                }
            }

            // take care of edge case when last point is in a line
            if (counter >= 4 && (pivot.compareTo(pointsCopy[nextToPivotIndex]) < 0)) {
                StdOut.print(pivot + " -> ");
                for (int k = nextToPivotIndex; k < N - 1; k++) {
                    StdOut.print(pointsCopy[k] + " -> ");
                }
                StdOut.print(pointsCopy[N - 1] + "\n");
                pivot.drawTo(pointsCopy[N - 1]);
            }
        }
    }
}
