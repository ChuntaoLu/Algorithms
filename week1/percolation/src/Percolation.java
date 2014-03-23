/**
 * Created by lu on 2/25/14.
 * percolation
 */
public class Percolation {
    private boolean[][] grid;             // the grid
    private int gridSize;                 // grid size
    private WeightedQuickUnionUF wquf;    // union find object
    private int virtualTop;               // the virtual top
    private boolean[] connectedBottom;    // if site connected to bottom

    /**
     * Initializes a percolation grid with all sites blocked.
     * @param N the grid size
     */
    public Percolation(int N) {
        gridSize = N;
        wquf = new WeightedQuickUnionUF(N * N + 1); // extra one for the virtual top
        virtualTop = 0;
        grid = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grid[i][j] = false;
            }
        }
        connectedBottom = new boolean[N * N + 1];   //extra one for the virtual top
        for (int i = 0; i <= N * (N-1); i++) {
            connectedBottom[i] = false;
        }
        for (int i = N * (N - 1) + 1; i <= N * N; i++) {
            connectedBottom[i] = true;
        }
    }

    /**
     * if indices are valid
     */
    private boolean isValidIndices(int i, int j) {
        return (i > 0 && i <= gridSize && j > 0 && j <= gridSize);
    }

    /**
     * validates the indices of the site
     */
    private void validateIndices(int i, int j) {
        if (!isValidIndices(i, j)) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
    }

    /**
     * get the neighbors for each site
     */
    private int[][] getNeighbors(int i, int j) {
        return new int[][]{{i - 1, j}, {i, j + 1}, {i + 1, j}, {i, j - 1}};
    }

    /**
     * get the union find array index
     */
    private int getUnionFindIndex(int i, int j) {
        return 1 + (i - 1) * gridSize + (j - 1);
    }

    /**
     * open site grid[i][j] if not already open
     */
    public void open(int i, int j) {
        validateIndices(i, j);
        if (!isOpen(i, j)) {
            grid[i - 1][j - 1] = true;
            int thisUFIndex = getUnionFindIndex(i, j);
            if (i == 1) wquf.union(thisUFIndex, virtualTop);
            for (int[] row : getNeighbors(i, j)) {
                if (isValidIndices(row[0], row[1]) && isOpen(row[0], row[1])) {
                    int thatUFIndex = getUnionFindIndex(row[0], row[1]);
                    boolean connectBottom = connectedBottom[wquf.find(thisUFIndex)]
                            || connectedBottom[wquf.find(thatUFIndex)];
                    wquf.union(thisUFIndex, thatUFIndex);
                    if (connectBottom) connectedBottom[wquf.find(thisUFIndex)] = true;
                }
            }
        }
    }

    /**
     * is site grid[i][j] open?
     */
    public boolean isOpen(int i, int j) {
        return grid[i - 1][j - 1];
    }

    /**
     * is grid[i][j] full?
     */
    public boolean isFull(int i, int j) {
        validateIndices(i, j);
        return wquf.connected(virtualTop, getUnionFindIndex(i, j));
    }

    /**
     * does the system percolate?
     */
    public boolean percolates() {
        return connectedBottom[wquf.find(0)];
    }

    /**
     * prints the current grid.
     */
    public static void main(String[] args) {
        Percolation p = new Percolation(5);
        StdOut.println(p.isFull(1, 1));
        StdOut.println(p.isOpen(2, 1));
        StdOut.println(p.isFull(2, 1));
        p.open(3, 4);
        StdOut.println(p.isOpen(3, 4));
        StdOut.println(p.isFull(3, 4));
        StdOut.println(p.percolates());
        StdOut.println(p.isValidIndices(6, 9));
//        p.validateIndices(6, 9);
    }
}
