import java.util.HashSet;
import java.util.Stack;

/**
 * Created by lu on 5/11/14.
 * Data type to solve the boggle game
 */
public class BoggleSolver {
    private PrefixTrieST<Integer> dict;     // dictionary of all valid words
    private HashSet<String> words;          // words that can be generate by given board
    private BoggleBoard bBoard;             // the given boggle board
    private int rows;                       // number of rows of the given board
    private int cols;                       // number of columns of the given board
    private Stack<Integer>[][] neighbors;   // cache neighbors for each element on board
    private boolean[][] inCurrentPrefix;    // mark character in use if it's in current prefix
    private Stack<PrefixTrieST.Node> prefixNodeStack;   // tracks the node, search optimization

    /**
     * initializes dict by given array of strings
     */
    public BoggleSolver(String[] dictionary) {
        dict = new PrefixTrieST<Integer>();
        for (String s: dictionary)
            dict.put(s, s.length());
    }

    /**
     * returns the set of all valid words in the given boggle board
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        words = new HashSet<String>();
        bBoard = board;
        rows = bBoard.rows();
        cols = bBoard.cols();
        neighbors = new Stack[rows][cols];

        buildNeighbors();

        // for each character on board run depth first search
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                inCurrentPrefix = new boolean[rows][cols];       // mark character is in use, reset for every dfs
                prefixNodeStack = new Stack<PrefixTrieST.Node>();// track the last node
                prefixNodeStack.push(dict.root());               // start from the root
                dfs("", i, j);
            }
        }
        return words;
    }

    // helper for building neighbors cache
    private void buildNeighbors() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Stack<Integer> neighborsStack = new Stack<Integer>();
                for (int i = row - 1; i < row + 2; i++) {
                    for (int j = col - 1; j < col + 2; j++) {
                        if (i > - 1 && i < rows && j > -1 && j < cols)
                            neighborsStack.push(toOneD(i, j));
                    }
                }
                neighbors[row][col] = neighborsStack;
            }
        }
    }

    // helper for getAllValidWords
    private void dfs(String prefix, int row, int col) {
        // get the letter at position (row, col)
        char c = bBoard.getLetter(row, col);
        inCurrentPrefix[row][col] = true;

        // get the node in tire corresponding to the letter
        // prefixNodeStack top is the most recent node, optimized search
        PrefixTrieST.Node x = dict.nextNode(prefixNodeStack, c);

        // debug
//        StdOut.println(prefix + " -> " + c);
//        StdOut.println(x == null);
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                StdOut.print("  " + inCurrentPrefix[i][j]);
//            }
//            StdOut.println();
//        }

        // if node is not null, then update prefixNodeStack and recur
        if (x != null) {
            prefixNodeStack.push(x);
            String newPrefix;

            // deal with the special case of 'Qu'
            if (c == 'Q') {
                PrefixTrieST.Node uNode = dict.nextNode(prefixNodeStack, 'U');

                // it's possible that 'QU' makes an invalid prefix, in which case clean up and return
                if (uNode == null) {
                    prefixNodeStack.pop();
                    inCurrentPrefix[row][col] = false;
                    return;
                }

                // if 'QU' makes valid prefix, uNode should be pushed onto stack
                prefixNodeStack.push(uNode);
                newPrefix = prefix + "QU";
            } else {
                newPrefix = prefix + c;
            }

            // update words if newPrefix is a valid word
            if (newPrefix.length() > 2 && dict.contains(newPrefix))
                words.add(newPrefix);

            // for each neighbor of the node, recur if not already in prefix string
            for (int oneDIndex: neighbors[row][col]) {
                int m = oneDIndex / cols;
                int n = oneDIndex % cols;
                if (!inCurrentPrefix[m][n]) {
                    dfs(newPrefix, m, n);
                }
            }

            // upon finishing current depth, pop the node off prefixNodeStack
            prefixNodeStack.pop();
            if (c == 'Q') prefixNodeStack.pop();    // pop 'U' off the stack if special case
        }

        // upon finishing current letter, mark it as not in use
        inCurrentPrefix[row][col] = false;
    }


    // helper for dfs
    private int toOneD(int row, int col) {
        return row * cols + col;
    }

    /**
     * returns the score of the given word if it is in the dictionary, zero otherwise
     */
    public int scoreOf(String word) {
        return dict.contains(word) ? score(word) : 0;
    }

    // helper for scoreOf
    private int score(String word) {
        int len = word.length();
        if (len < 3) return 0;
        if (len < 5) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        return 11;
    }

    /**
     * unit test
     */
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int counter = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
            counter++;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Number of words = " + counter);
    }
}
