import java.util.*;
import java.io.*;

public class Day15_3 {
    public static final char WALL = '#', BOX = 'O', EMPTY = '.', ROBOT = '@';
    public static final char LBOX = '[', RBOX = ']';
    public static final char UP = '^', DOWN = 'v', RIGHT = '>', LEFT = '<';

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new FileReader("./day15.in"));
        String line = in.nextLine();
        int size = line.length();
        char[][] grid = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = line.charAt(j);
            }
            line = in.nextLine();
        }

        String moves = "";
        while (in.hasNextLine())
            moves += in.nextLine();
        part1(grid, moves);
        // part2(grid, moves);
        in.close();
    }

    /*
     * Let's go over algorithmic complexity briefly. This code:
     * Iterates through the m*n array to find robot location
     * Then for each move [x]:
     *     We do a push operation [O(max(m,n))]
     * 
     * Since we don't have to rotate the array, it's O(x*max(m,n)).
     * 
     * running 'time java Day15_2' and 'time java Day15_3' gets:
     * java Day15_2  0.63s user 0.05s system 118% cpu 0.571 total
     * java Day15_3  0.09s user 0.03s system 116% cpu 0.098 total
     * 
     * That means for this (small) input it's ~a tenth of a second vs a half a second
     */
    public static void part1(char[][] grid, String moves) {
        int robo_r = -1, robo_c = -1; // location of robot
        for (int i = 0; i < grid.length; i++) { // update location of ROBOT
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == ROBOT) {
                    robo_r = i;
                    robo_c = j;
                }
            }
        }
        for (String move : moves.split("")) { // iterate thru each move
            char move_char = move.charAt(0);
            if (push(grid, move_char, robo_r, robo_c)) {
                robo_r = getMovedRow(move_char, robo_r);
                robo_c = getMovedCol(move_char, robo_c);
            }
            // displayGrid(grid);
        }
        System.out.println(value(grid));
    }

    /*
     * This is what we gotta implement.
     * 
     * We fixed the way we solve part1 such that we don't have to rotate!
     * This eradicates one of the major problems we ran into (ie. transforming
     * boxes from a 2x1 to a 1x2 space)
     * 
     * Additionally, left/right movement is pretty much exactly the same. We just need
     * a slight change of push to work on LBOX and RBOX as well.
     * 
     * We do, however, run into a problem with up/down movement if we execute pushes
     * the same way. Currently, we execute a push further down the line (if possible)
     * and depending on the success of that movement, we move the other boxes/robot.
     * Consider the case where there are boxes like this:
     * ##########
     * #....#...#
     * #.[][]...#
     * #..[]....#
     * #..@.....#
     * ##########
     * 
     * If the robot tries to move up, we have to ensure it doesn't partially move the
     * boxes without being able to move the full row of boxes. It should NOT do this:
     * ##########
     * #.[].#...#
     * #...[]...#
     * #..[]....#
     * #..@.....#
     * ##########
     * 
     * We can still use recursion to solve this problem, but we need to run a check on
     * the boxes to see if we can move all of them before moving any of them. This wasn't
     * a problem during part1 because if we hit an empty space at any point while traversing
     * the row (before a wall), we knew we could move all previous boxes and the robot.
     */
    public static void part2(char[][] grid, String moves) {

    }

    /*
     * TODO: implement this
     */
    private static boolean canPush(char[][] grid, char direction, int r, int c) {
        return false;
    }

    /*
     * TODO: modify this to work with the new char types introduced in part 2.
     */
    private static boolean push(char[][] grid, char direction, int r, int c) {
        int r_new = getMovedRow(direction, r);
        int c_new = getMovedCol(direction, c);
        switch (grid[r_new][c_new]) {
            case WALL:
                return false;
            case EMPTY:
                grid[r_new][c_new] = grid[r][c];
                grid[r][c] = EMPTY;
                return true;
            case BOX:
                if (push(grid, direction, r_new, c_new)) {
                    grid[r_new][c_new] = grid[r][c];
                    grid[r][c] = EMPTY;
                    return true;
                }
                else {
                    return false;
                }
            default:
                System.out.println("push is cooked, as they say");
                return false;
        }
    }

    private static int getMovedRow(char direction, int r) {
        switch (direction) {
            case DOWN:
                return r+1;
            case UP:
                return r-1;
            default:
                return r;
        }
    }

    private static int getMovedCol(char direction, int c) {
        switch (direction) {
            case LEFT:
                return c-1;
            case RIGHT:
                return c+1;
            default:
                return c;
        }
    }

    public static long value(char[][] grid) {
        long tot = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == BOX || grid[i][j] == LBOX)
                    tot += 100 * i + j;
            }
        }
        return tot;
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
