/**
 * Created by lu on 4/21/14.
 * Given a list of WordNet nouns, decide which is least related to the others
 */
public class Outcast {
    private WordNet wn;

    /**
     * constructor takes a WordNet object
     */
    public Outcast(WordNet wordnet) {
        wn = wordnet;
    }

    // helper for outcast
    private int distSum(String noun, String[] nouns) {
        int sum = 0;
        for (int i = 0; i < nouns.length; i++) {
            sum += wn.distance(noun, nouns[i]);
        }
        return sum;
    }

    /**
     * given an array of WordNet nouns, return an outcast
     */
    public String outcast(String[] nouns) {
        String maxNoun = "";
        int currentDist;
        int maxDist = Integer.MIN_VALUE;
        for (int i = 0; i < nouns.length; i++) {
            currentDist = distSum(nouns[i], nouns);
            if (currentDist > maxDist) {
                maxDist = currentDist;
                maxNoun = nouns[i];
            }
        }
        return maxNoun;
    }

    /**
     * unit test
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
