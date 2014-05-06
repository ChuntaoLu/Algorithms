import java.util.*;

/**
 * Created by lu on 5/4/14.
 * data type for baseball elimination
 */
public class BaseballElimination {
    private int numberOfTeams;
    private int[] win;
    private int[] loss;
    private int[] left;
    private int[][] against;
    private Queue<String> subsetR;
    private ArrayList<String> teams;
    private String cachedIsEliminatedCall;

    /**
     * create a baseball division from a given filename
     */
    public BaseballElimination(String filename) {
        In file = new In(filename);
        numberOfTeams = file.readInt();

        win = new int[numberOfTeams];
        loss = new int[numberOfTeams];
        left = new int[numberOfTeams];
        against = new int[numberOfTeams][numberOfTeams];
        teams = new ArrayList<String>(numberOfTeams);

        for (int i = 0; i < numberOfTeams; i++) {
            teams.add(file.readString());
            win[i] = file.readInt();
            loss[i] = file.readInt();
            left[i] = file.readInt();
            for (int j = 0; j < numberOfTeams; j++)
                against[i][j] = file.readInt();
        }
    }

    /**
     * number of teams
     */
    public int numberOfTeams() {
        return numberOfTeams;
    }

    /**
     * all teams
     */
    public Iterable<String> teams() {
        return teams;
    }

    // helper for handle illegal argument
    private void handleIlleagalArgument(String team) {
        if (!teams.contains(team)) throw new IllegalArgumentException();
    }

    /**
     * number of wins for given team
     */
    public int wins(String team) {
        handleIlleagalArgument(team);
        return win[teams.indexOf(team)];
    }

    /**
     * number of losses for given team
     */
    public int losses(String team) {
        handleIlleagalArgument(team);
        return loss[teams.indexOf(team)];
    }

    /**
     * number of remaining games for given team
     */
    public int remaining(String team) {
        handleIlleagalArgument(team);
        return left[teams.indexOf(team)];
    }

    /**
     * number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        handleIlleagalArgument(team1);
        handleIlleagalArgument(team2);
        return against[teams.indexOf(team1)][teams.indexOf(team2)];
    }

    /**
     * is given team eliminated?
     */
    public boolean isEliminated(String team) {
        handleIlleagalArgument(team);
        cachedIsEliminatedCall = team;
        if (numberOfTeams == 1) return false;
        return isTrivialEliminated(team) || isNontrivialEliminated(team);
    }

    // helper for isEliminated, trivial case
    private boolean isTrivialEliminated(String team) {
        int maxWins = wins(team) + remaining(team);
        for (int i = 0; i < numberOfTeams; i++) {
            if (maxWins < win[i]){
                subsetR = new Queue<String>();
                subsetR.enqueue(teams.get(i));
                return true;
            }
        }
        return false;
    }

    // helper for isEliminated, nontrivial case
    private boolean isNontrivialEliminated(String team) {
        Double inf = Double.POSITIVE_INFINITY;
        int id = teams.indexOf(team);
        int numberOfGameVertices = (numberOfTeams - 2) * (numberOfTeams - 1) / 2;
        int numberOfTeamVertices = numberOfTeams - 1;
        int networkSize = 1 + numberOfGameVertices + numberOfTeamVertices + 1;
        FlowNetwork fn = new FlowNetwork(networkSize);
        // minimum of the team vertices is always bigger than the maximum of team ids,
        // so it is OK to build a single two way hash between team vertices and team ids
        HashMap<Integer, Integer> idTeamVertexBiMap = new HashMap<Integer, Integer>(2 * numberOfTeamVertices);

        // build bidirectional map between team id and team vertex
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != id){
                int vertex;
                if (i > id) vertex = numberOfGameVertices + i;
                else        vertex = numberOfGameVertices + i + 1;
                idTeamVertexBiMap.put(i, vertex);
                idTeamVertexBiMap.put(vertex, i);
            }
        }

        // add edges from source to game vertex, and edges from game vertex to team vertex
        int sourceVertex = 0;
        int gameVertex = 1;
        double fullCapacity = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i != id && j != id) {
                    fullCapacity += against[i][j];
                    fn.addEdge(new FlowEdge(sourceVertex, gameVertex, against[i][j]));
                    fn.addEdge(new FlowEdge(gameVertex, idTeamVertexBiMap.get(i), inf));
                    fn.addEdge(new FlowEdge(gameVertex, idTeamVertexBiMap.get(j), inf));
                    gameVertex++;
                }
            }
        }

        // add edges from team vertex to target
        int targetVertex = networkSize - 1;
        for (int i = numberOfGameVertices + 1; i < networkSize - 1; i++) {
            int teamId = idTeamVertexBiMap.get(i); // from vertex to id
            double capacity = win[id] + left[id] - win[teamId];
            fn.addEdge(new FlowEdge(i, targetVertex, capacity));
        }

        FordFulkerson ff = new FordFulkerson(fn, sourceVertex, targetVertex);
        if (ff.value() != fullCapacity) {
            subsetR = new Queue<String>();
            for (int i = numberOfGameVertices + 1; i < networkSize - 1; i++) {
                if (ff.inCut(i)) subsetR.enqueue(teams.get(idTeamVertexBiMap.get(i)));
            }
            return true;
        }
        subsetR = null; // explicitly set to null, because subsetR is possibly cached
        return false;
    }

    /**
     * subset of teams that eliminates given team; null if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        handleIlleagalArgument(team);
        if (!team.equals(cachedIsEliminatedCall)) isEliminated(team);
        return subsetR;
    }

    /**
     * unit test
     */
    public static void main(String[] args) {
//        // input file is "teams4.txt"
        BaseballElimination x = new BaseballElimination(args[0]);
//
//        StdOut.println(x.numberOfTeams() == 4);
//        StdOut.println(x.wins("Atlanta") == 83);
//        StdOut.println(x.losses("Atlanta") == 71);
//        StdOut.println(x.remaining("Atlanta") == 8);
//        StdOut.println(x.against("Atlanta", "Atlanta") == 0);
//        StdOut.println(x.against("Atlanta", "Philadelphia") == 1);
//        StdOut.println(x.against("Atlanta", "New_York") == 6);
//        StdOut.println(x.against("Atlanta", "Montreal") == 1);
//
//        StdOut.println("===============");
        StdOut.println(x.isEliminated("Atlanta") == false);
        StdOut.println(x.isEliminated("Philadelphia") == true);
        StdOut.println(x.isEliminated("New_York") == false);
        StdOut.println(x.isEliminated("Montreal") == true);
        StdOut.println(x.certificateOfElimination("New_York"));


//        BaseballElimination division = new BaseballElimination(args[0]);
//        for (String team : division.teams()) {
//            if (division.isEliminated(team)) {
//                StdOut.print(team + " is eliminated by the subset R = { ");
//                for (String t : division.certificateOfElimination(team))
//                    StdOut.print(t + " ");
//                StdOut.println("}");
//            }
//            else {
//                StdOut.println(team + " is not eliminated");
//            }
//        }
    }
}
