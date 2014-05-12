import java.util.HashSet;
import java.util.Stack;

/**
 * Created by lu on 5/11/14.
 * Data type to solve the boggle game
 */
public class BoggleSolver {
    private PrefixTrieST<Integer> dict; // dictionary of all valid words
    private HashSet<String> words;      // words that can be generate by given board
    private BoggleBoard bBoard;         // the given boggle board
    private int rows;                   // number of rows of the given board
    private int cols;                   // number of columns of the given board
    private Stack<Integer>[] neighbors; // cache neighbors for each element on board
//    private String lastPrefix;          // cache last prefix, search based on its result

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
        neighbors = new Stack[rows * cols];
//        lastPrefix = "";

        buildNeighbors();

        // for each character on board run depth first search
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dfs(i, j);
            }
        }
        return words;
    }

    // helper for build neighbors cache
    private void buildNeighbors() {
        int neighbor;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Stack<Integer> neighborsStack = new Stack<Integer>();
                for (int i = row - 1; i < row + 2; i++) {
                    for (int j = col - 1; j < col + 2; j++) {
                        neighbor = toOneD(i, j);
                        if (i > - 1 && i < rows && j > -1 && j < cols)
                            neighborsStack.push(neighbor);
                    }
                }
                neighbors[toOneD(row, col)] = neighborsStack;
            }
        }
    }

    // helper for getAllValidWords
    private void dfs(int row, int col) {
        Stack<Integer> letterStack = new Stack<Integer>();      // board character index in one dimension
        Stack<Integer> traceStack = new Stack<Integer>();       // tracking depth, value is the number of elements in current depth
        Stack<Integer> prefixStack = new Stack<Integer>();      // for constructing current prefix
        boolean[] inCurrentPrefix = new boolean[rows * cols];   // if character is in use, reset for every dfs

        // start from the given index
        letterStack.push(toOneD(row, col));
        traceStack.push(1);

        // trace top being zero means current depth is done, so pop the zero and trace one depth up
        // whenever tracing one depth up, prefixStack has to pop one character
        while (!letterStack.isEmpty()) {
            // backtrace if elements in current depth are exhausted
            while (traceStack.peek() == 0) {
                traceStack.pop();
                inCurrentPrefix[prefixStack.pop()] = false;
            }

            int oneDIndex = letterStack.pop();
            inCurrentPrefix[oneDIndex] = true;
            prefixStack.push(oneDIndex);
            traceStack.push(traceStack.pop() - 1);

//            StdOut.println(prefixStack);
            String prefix = currentPrefix(prefixStack);
            if (prefix.length() > 2 && dict.contains(prefix)) {
                words.add(prefix);
            }

            int counter = 0;
            // grow prefix only if current prefix is in the dictionary
//            StdOut.println(lastPrefix);
//            StdOut.println(prefix);
            if (dict.hasWordsWith(prefix)){
//                StdOut.println(oneDIndex);
//                StdOut.println(prefix);
//                for (int i = 0; i < 4; i++) {
//                    for (int j = 0; j < 4; j++) {
//                        StdOut.print("  " + inCurrentPrefix[i * 4 + j]);
//                    }
//                    StdOut.println();
//                }
                for (int i: neighbors[oneDIndex]) {
                    if (!inCurrentPrefix[i]){
                        letterStack.push(i);
                        counter++;
                    }
                }
            }
            traceStack.push(counter);
//            lastPrefix = prefix;
        }
    }


    // helper for dfs and validNeighbors
    private int toOneD(int row, int col) {
        return row * cols + col;
    }

    // helper for dfs
    private String currentPrefix(Stack<Integer> s) {
        StringBuilder out = new StringBuilder();
        for (int i: s) {
            char letter = bBoard.getLetter(i / cols, i % cols);
            out.append(letter == 'Q'? "QU" : letter);
        }
        return out.toString();
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
