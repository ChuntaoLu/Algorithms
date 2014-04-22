import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by lu on 4/21/14.
 * word net data type
 */
public class WordNet {
    private HashMap<Integer, String> idToSynset;        // an id has exactly one synset
    private HashMap<String, HashSet<Integer>> nounToId; // a noun many have many ids
    private Digraph g;
    private SAP s;

    /**
     * constructor takes the name of the two input files
     */
    public WordNet(String synsets, String hypernyms) {
        int counter = 0;
        int id;
        String line;
        String[] fields;
        String[] nouns;
        DirectedCycle dc;
        // add ids to rootCheck when parsing synsets, and remove
        // ids when parsing hypernyms, rooted DAG should have one id left
        HashSet<Integer> rootCheck = new HashSet<Integer>();
        idToSynset = new HashMap<Integer, String>();
        nounToId = new HashMap<String, HashSet<Integer>>();

        // read in synsets and build two hash maps
        In syn = new In(synsets);
        In hyp = new In(hypernyms);
        while ((line = syn.readLine()) != null) {
            fields = line.split(",");
            id = Integer.parseInt(fields[0]);
            rootCheck.add(id);
            idToSynset.put(id, fields[1]);
            nouns = fields[1].split("\\s+");
            for (int i = 0; i < nouns.length; i++) {
                String noun = nouns[i];
                if (nounToId.containsKey(noun)) {
                    nounToId.get(noun).add(id);
                } else {
                    HashSet<Integer> h = new HashSet<Integer>();
                    h.add(id);
                    nounToId.put(noun, h);
                }
            }
            counter++;
        }

        // read in hypernyms and build digraph
        g = new Digraph(counter);
        while ((line = hyp.readLine()) != null) {
            fields = line.split(",");
            id = Integer.parseInt(fields[0]);
            rootCheck.remove(id); // only remove the head id
            for (int i = 1; i < fields.length; i++) {
                g.addEdge(id, Integer.parseInt(fields[i]));
            }
        }

        // check if given digraph is a rooted DAG
        dc = new DirectedCycle(g);
        if (dc.hasCycle()) throw new IllegalArgumentException("Given graph has a cycle");
        if (rootCheck.size() != 1) throw new IllegalArgumentException("Given graph not rooted");

        // build sap
        s = new SAP(g);
    }

    /**
     * the set of nouns (no duplicates), returned as an Iterable
     */
    public Iterable<String> nouns() {
        return nounToId.keySet();
    }

    /**
     * is the word a WordNet noun?
     */
    public boolean isNoun(String word) {
        return nounToId.containsKey(word);
    }

    // helper for distance and sap
    private void checkNoun(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Argument not a WordNet noun");
    }

    /**
     * distance between nounA and nounB
     */
    public int distance(String nounA, String nounB) {
        checkNoun(nounA, nounB);
        return s.length(nounToId.get(nounA), nounToId.get(nounB));
    }

    /**
     * a synset that is the common ancestor of nounA and nounB
     * in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        checkNoun(nounA, nounB);
        return idToSynset.get(s.ancestor(nounToId.get(nounA), nounToId.get(nounB)));
    }

    /**
     * unit test
     */
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        StdOut.println(33 == wn.distance("Black_Plague", "black_marlin"));
        StdOut.println(27 == wn.distance ("American_water_spaniel", "histology"));
        StdOut.println(29 == wn.distance("Brown_Swiss", "barrel_roll"));
    }
}
