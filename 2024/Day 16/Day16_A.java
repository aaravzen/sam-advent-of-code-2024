import java.util.*;
import java.io.*;

public class Day16_A {
    public static char SPACE = '.', START = 'S', END = 'E', WALL = '#', PATH = 'O';
    public static int[] dr = {0, 1, 0, -1}; // dirs are E/S/W/N
    public static int[] dc = {1, 0, -1, 0};

    /*
     * This is a helper class that's usable in PriorityQueues because it implements Comparable.
     * 
     * This is a necessary prerequisite to running dijkstra's algorithm.
     */
    static class Entry implements Comparable<Entry> {
        public int x;
        public int y;
        public int dir;
        public int distance;
        public Entry(int xIn, int yIn, int dirIn, int distIn) {
            x = xIn; y = yIn; dir = dirIn; distance = distIn;
        }
        public int compareTo(Entry other) {
            if (distance != other.distance) return (distance - other.distance);
            if (x != other.x) return (x - other.x);
            if (y != other.y) return (y - other.y);
            if (dir != other.dir) return (dir - other.dir);
            return 0;
        }
    }
    public static void main(String[] args) throws IOException {
        char[][] grid = inputGrid();
        solve(grid);
    }

    public static void solve(char[][] grid) {
        // Setup of variables, finding of start/end points
        int[][][] fward_distances = new int[grid.length][grid[0].length][4];
        int[][][] bward_distances = new int[grid.length][grid[0].length][4];
        PriorityQueue<Entry> frontier = new PriorityQueue<Entry>();
        int endX = -1, endY = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Arrays.fill(fward_distances[i][j], Integer.MAX_VALUE);
                Arrays.fill(bward_distances[i][j], Integer.MAX_VALUE);
                if (grid[i][j] == START) {
                    frontier.offer(new Entry(i, j, 0, 0));
                }
                if (grid[i][j] == END) {
                    endX = i;
                    endY = j;
                }
            }
        }

        // Perform dijkstra's from the start point and print the shortest path to the end.
        dijkstra(grid, frontier, fward_distances);
        int shortest_path = Integer.MAX_VALUE;
        ArrayList<Integer> ending_directions = new ArrayList<Integer>();
        for (int d = 0; d < 4; d++) {
            if (fward_distances[endX][endY][d] < shortest_path) {
                ending_directions.clear();
                ending_directions.add(d);
                shortest_path = fward_distances[endX][endY][d];
            }
            else if (fward_distances[endX][endY][d] == shortest_path) {
                ending_directions.add(d);
            }
        }
        System.out.println(shortest_path);

        // Perform dijkstra's from the end point, facing backwards.
        for (Integer dir : ending_directions) {
            frontier.offer(new Entry(endX, endY, (dir+2)%4, 0));
        }
        dijkstra(grid, frontier, bward_distances);

        // Count the locations where the distance from the end point plus the
        // distance from the start point equals the shortest path. 
        int o_count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                for (int d = 0; d < 4; d++) {
                    int forward_dist = fward_distances[i][j][d];
                    int backward_dist = bward_distances[i][j][(d+2)%4];
                    if (forward_dist + backward_dist == shortest_path) {
                        grid[i][j] = PATH;
                    }
                }
                if (grid[i][j] == PATH) {
                    o_count++;
                }
            }
        }
        // displayGrid(grid);
        System.out.println(o_count);
    }

    public static void dijkstra(char[][] grid, PriorityQueue<Entry> frontier, int[][][] distances) {
        while (!frontier.isEmpty()) {
            Entry visit = frontier.poll();
            if (distances[visit.x][visit.y][visit.dir] <= visit.distance) continue;
            
            distances[visit.x][visit.y][visit.dir] = visit.distance;
            
            ArrayList<Entry> neighbors = getNeighbors(grid, visit);
            for (Entry n : neighbors) {
                if (distances[n.x][n.y][n.dir] < n.distance) continue;
                frontier.offer(n);
            }
        }
    }

    /*
     * This returns the possible next steps in a path, either by moving forward or turning 90˚
     */
    private static ArrayList<Entry> getNeighbors(char[][] grid, Entry e) {
        ArrayList<Entry> ret = new ArrayList<Entry>();
        // can it move forward?
        int newX = e.x + dr[e.dir];
        int newY = e.y + dc[e.dir];
        if (newX >= 0 && newX < grid.length) {
            if (newY >= 0 && newY < grid[newX].length) {
                if (grid[newX][newY] != WALL) {
                    ret.add(new Entry(newX, newY, e.dir, e.distance+1));
                }
            }
        }
        // it can definitely turn 90˚
        ret.add(new Entry(e.x, e.y, (e.dir+1)%4, e.distance+1000));
        ret.add(new Entry(e.x, e.y, (e.dir+3)%4, e.distance+1000));
        return ret;
    }

    /*
     * This input method works a bit more flexibly than previously; any rectangle will work.
     */
    private static char[][] inputGrid() throws IOException {
        Scanner in = new Scanner(new FileReader("./day16.in"));
        ArrayList<char[]> lines = new ArrayList<char[]>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            lines.add(line.toCharArray());
        }
        in.close();
        char[][] grid = new char[lines.size()][lines.get(0).length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = lines.get(i)[j];
            }
        }
        return grid;
    }

    public static void displayGrid(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                sb.append(grid[i][j]);
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }
}
