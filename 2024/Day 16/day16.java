import java.util.*;
import java.io.*;

public class day16 {    
    public static int[] dr = {0, 1, 0, -1};
    public static int[] dc = {1, 0, -1, 0};
    public static char SPACE = '.', START = 'S', END = 'E', WALL = '#', PATH = 'O';
    public static void main(String[] args) throws IOException{
        Scanner in = new Scanner(new FileReader("./day16.in"));
        int n = 15;
        char[][] grid = new char[n][n];

        for(int i = 0; i < n; i++){
            String line = in.nextLine();
            for(int j = 0; j < n; j++){
                grid[i][j] = line.charAt(j);
            }
            System.out.println(line);
        }
        
        DO(grid);
        in.close();
    }

    public static void DO(char[][] grid){
        int rows = grid.length;
        int cols = grid[1].length;

        int startX = -1, startY = -1, endX = -1, endY = -1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == START) {
                    startX = i;
                    startY = j;
                }
                if (grid[i][j] == END) {
                    endX = i;
                    endY = j;
                }
            }
        }

        int[][][] dist = new int[rows][cols][4];
        for (int[][] row : dist) {
            for (int[] dir : row) {
                Arrays.fill(dir, Integer.MAX_VALUE);
            }
        }


        Queue<int[]> queue = new LinkedList<>();
        for (int dir = 0; dir < 4; dir++) {
            dist[startX][startY][dir] = 0;  // Starting position at 0 seconds
            queue.add(new int[] {startX, startY, dir});
        }

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], direction = current[2];

            // Move in the current direction
            int newX = x + dr[direction];
            int newY = y + dc[direction];
            if (isValid(grid, newX, newY) && dist[newX][newY][direction] == Integer.MAX_VALUE) {
                dist[newX][newY][direction] = dist[x][y][direction] + 1;
                queue.offer(new int[] {newX, newY, direction});
            }

            // Rotate to a new direction (cost of 1000 seconds)
            for (int newDirection = 0; newDirection < 4; newDirection++) {
                if (newDirection != direction && dist[x][y][newDirection] == Integer.MAX_VALUE) {
                    dist[x][y][newDirection] = dist[x][y][direction] + 1000;
                    queue.offer(new int[] {x, y, newDirection});
                }
            }
        }

        int minTime = Integer.MAX_VALUE;
        int minDir = -1;
        for (int dir = 0; dir < 4; dir++) {
            if (dist[endX][endY][dir] < minTime) {
                minTime = dist[endX][endY][dir];
                minDir = dir;
            }
        }

        int[][] path = new int[rows][cols];
        for (int[] row : path) {
            Arrays.fill(row, -1);  // -1 means not visited yet
        }

        int x = endX, y = endY;
        while (minTime > 0) {
            path[x][y] = 1;  // Mark as part of the path
            for (int dir = 0; dir < 4; dir++) {
                int prevX = x - dr[dir], prevY = y - dc[dir];
                if (isValid(grid, prevX, prevY) && dist[prevX][prevY][minDir] == minTime - 1) {
                    x = prevX;
                    y = prevY;
                    minTime--;
                    
                }
            }
            break;
            //System.out.println(minTime);
        }
        path[startX][startY] = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (path[i][j] == 1 && grid[i][j] != START && grid[i][j] != END) {
                    grid[i][j] = PATH;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(minTime);
    }

    public static boolean isValid(char[][] grid, int x, int y) {
        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && grid[x][y] != WALL;
    }
}
