import java.awt.*;
import java.util.Arrays;

/**
 * Created by lu on 3/23/14.
 * Collinear - Brute Force approach
 */
public class Brute {
    public static void main(String[] args) {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.setPenColor(Color.RED);
        StdDraw.setPenRadius(0.03);
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
        Point ph;
        Point pi;
        Point pj;
        Point pk;
        for (int h = 0; h < N; h++) {
            ph = points[h];
            ph.draw();
            for (int i = h + 1; i < N - 2; i++) {
                pi = points[i];
                double slopeHToI = ph.slopeTo(pi);
                for (int j = i + 1; j < N - 1; j++) {
                    pj = points[j];
                    if (slopeHToI == pi.slopeTo(pj)) {
                        for (int k = j + 1; k < N; k++) {
                            pk = points[k];
                            if (slopeHToI == pj.slopeTo(pk)) {
                                StdOut.println(ph + " -> " + pi+ " -> " + pj + " -> " + pk);
                                StdDraw.setPenColor(Color.BLUE);
                                StdDraw.setPenRadius(0.01);
                                ph.drawTo(pk);
                                StdDraw.setPenColor(Color.RED);
                                StdDraw.setPenRadius(0.03);
                            }
                        }
                    }
                }
            }
        }
        StdDraw.show();
    }
}
