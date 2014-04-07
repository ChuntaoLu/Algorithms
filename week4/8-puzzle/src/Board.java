import java.util.Arrays;

/**
 * Created by lu on 4/5/14.
 * a NxN slider game tiles
 */
public class Board {
    private final int N;            // tiles size
    private final int[][] tiles;    // current tiles

    /**
     * construct a tiles from an N-by-N array of tiles
     */
    public Board(int[][] blocks) {
        N = blocks.length;
        tiles = deepCopy(blocks, N);
    }

    // helper for deep copy a 2D array
    private int[][] deepCopy(int[][] y, int M) {
        int[][] out = new int[M][M];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                out[i][j] = y[i][j];
            }
        }
        return out;
    }

    /**
     * tiles dimension N
     */
    public int dimension() {
        return N;
    }

    /**
     * number of tiles out of place
     */
    public int hamming() {
        int numOfDiffBlocks = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != j + i * N  + 1) {
                    numOfDiffBlocks++;
                }
            }
        }
        return --numOfDiffBlocks; // exclude the blank block
    }

    // helper to compute the Manhattan distance for a single block
    private int singleMan(int i, int j) {
        int goalI = tiles[i][j] / N;
        int goalJ = tiles[i][j] % N;
        if (goalJ == 0) {
            goalI -= 1;
            goalJ = N - 1;
        } else {
            goalJ -= 1;
        }
        return Math.abs(i - goalI) + Math.abs(j - goalJ);
    }

    /**
     * sum of Manhattan distance between tiles and goal
     */
    public int manhattan() {
        int manDist = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != j + i * N  + 1 && tiles[i][j] != 0) {
                    manDist += singleMan(i, j);
                }
            }
        }
        return manDist;
    }

    /**
     * is this tiles the goal tiles?
     */
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // helper for swapping two elements in a 2D array
    private void swapBlocks(int[][] x, int i, int j, int m, int n) {
        int swap;
        swap = x[i][j];
        x[i][j] = x[m][n];
        x[m][n] = swap;
    }

    /**
     * a tiles obtained by exchanging two adjacent tiles in the same row
     */
    public Board twin() {
        int[][] aTwin = deepCopy(tiles, N);
        if (aTwin[0][0] * aTwin[0][1] == 0) {
            swapBlocks(aTwin, 1, 0, 1, 1);
        } else {
            swapBlocks(aTwin, 0, 0, 0, 1);
        }
        return new Board(aTwin);
    }

    /**
     * does this tiles equals y?
     * @param y another tiles
     */
    public boolean equals(Object y) {
        //check for reference equality
        if (y == this) return true;

        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any super type, and not just one class;
        //2. it renders an explicit check for "y == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if (!(y instanceof Board)) return false;

        //let the compiler know y is a "Board" instead of an "Object"
        Board that = (Board) y;
        return (this.N == that.N) && (Arrays.deepEquals(this.tiles, that.tiles));

    }

    // helper method to push neighbor board onto a stack
    private void pushBoardOntoStack(Stack<Board> s, int x, int y, int m, int n) {
        int[][] neighborTiles = deepCopy(tiles, N);
        swapBlocks(neighborTiles, x, y, m, n);
        s.push(new Board(neighborTiles));
    }

    /**
     * all neighboring boards
     */
    public Iterable<Board> neighbors() {
        Stack<Board> boards = new Stack<Board>();
        boolean blankFound = false;
        for (int i = 0; i < N && !blankFound; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] == 0) {
                    if (i > 0) pushBoardOntoStack(boards, i - 1, j, i, j);
                    if (j > 0) pushBoardOntoStack(boards, i, j - 1, i, j);
                    if (i < N - 1) pushBoardOntoStack(boards, i + 1, j, i, j);
                    if (j < N - 1) pushBoardOntoStack(boards, i, j + 1, i, j);
                    blankFound = true;
                    break;
                }
            }
        }
        return boards;
    }

    /**
     * string representation of the tiles
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }




    public static void main(String[] args) {
//        int[][] tiles = {{1, 3, 7}, {0, 2, 8}, {4, 6, 5}};
//        int[][] tiles = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        int[][] blocks = {{8, 1, 3}, {4, 2, 0}, {7, 6, 5}};
//        int[][] tiles = {{1, 0}, {2, 3}};
        Board a = new Board(blocks);
        StdOut.println(a.dimension());
        StdOut.println(a.hamming());
        StdOut.println(a.manhattan());
        StdOut.println(a);
        StdOut.println(a.twin());
        StdOut.println(a);
        for (Board board: a.neighbors()) {
            StdOut.println(board);
        }
    }
}
