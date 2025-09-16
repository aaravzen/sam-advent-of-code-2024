import java.util.*;
import java.io.*;

public class Day16_A {
    public static char SPACE = '.', START = 'S', END = 'E', WALL = '#';

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
        displayGrid(grid);
        System.out.println(dijkstra(grid));
    }

    /*
     * Dijkstra's Algorithm - https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
     * 
     * This is a fast single-source shortest-path algorithm. It's algorithmic complexity
     * is something like O(E + VlogV) where E is the number of edges and V is the number of nodes.
     */
    public static int dijkstra(char[][] grid) {
        int[][][] distances = new int[grid.length][grid[0].length][4];
        Queue<Entry> frontier = new PriorityQueue<Entry>();
        int endX = -1, endY = -1;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Arrays.fill(distances[i][j], Integer.MAX_VALUE);
                if (grid[i][j] == START) {
                    frontier.offer(new Entry(i, j, 0, 0));
                }
                if (grid[i][j] == END) {
                    endX = i;
                    endY = j;
                }
            }
        }

        while (!frontier.isEmpty()) {
            Entry visit = frontier.poll();
            if (visit.x == endX && visit.y == endY) return visit.distance;
            if (distances[visit.x][visit.y][visit.dir] <= visit.distance) continue;
            
            distances[visit.x][visit.y][visit.dir] = visit.distance;
            // System.out.println(visit.distance + " to x,y,d = " + visit.x + "," + visit.y + "," + visit.dir);

            ArrayList<Entry> neighbors = getNeighbors(grid, visit);
            for (Entry n : neighbors) {
                if (distances[n.x][n.y][n.dir] < n.distance) continue;
                frontier.offer(n);
            }
        }
        return -1;
    }

    /*
     * This returns the possible next steps in a path, either by moving forward or turning 90˚
     */
    private static ArrayList<Entry> getNeighbors(char[][] grid, Entry e) {
        ArrayList<Entry> ret = new ArrayList<Entry>();
        // can it move forward? dirs are E/S/W/N.
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int newX = e.x + dr[e.dir];
        int newY = e.y + dc[e.dir];
        if (newX >= 0 && newX < grid.length) {
            if (newY >= 0 && newY < grid[newX].length) {
                if (grid[newX][newY] != WALL) {
                    ret.add(new Entry(newX, newY, e.dir, e.distance+1));
                }
            }
        }
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
