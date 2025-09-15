import java.util.*;
import java.io.*;

// Day 15 REDO
public class Day15_2 {
    public static final char WALL = '#', BOX = 'O', EMPTY = '.', ROBOT = '@';
    public static final char LBOX = '[', RBOX = ']';
    public static final String UP = "^", DOWN = "v", RIGHT = ">", LEFT = "<";

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
        // part1(grid, moves);
        part2(grid, moves);
        in.close();
    }

    /*
     * Let's discuss algorithmic complexity of part1 here.
     * Let the grid be size m*n, let x be the number of moves.
     * 
     * In essence, the time it takes to run this method is:
     * For each move [x]:
     * Iterate through the array to rotate [m*n]
     * Iterate through the array to find robot [m*n]
     * Iterate through the line ~4 times to substring/search [max(m,n)]
     * Perform switch [O(1)]
     * Iterate through the array to rotate [m*n]
     * 
     * Ergo, it takes O(x*(3*m*n + 4*max(m,n))) ≈ O(x*m*n)
     */
    public static void part1(char[][] grid, String moves) {
        int robo_r = -1, robo_c = -1; // location of robot
        int wall_i = Integer.MAX_VALUE, box_i = Integer.MAX_VALUE, empty_i = Integer.MAX_VALUE; // index of certain items
        for (String move : moves.split("")) { // iterate thru each move
            grid = rotate(grid, move, false);
            for (int i = 0; i < grid.length; i++) { // update location of ROBOT
                for (int j = 0; j < grid.length; j++) {
                    if (grid[i][j] == ROBOT) {
                        robo_r = i;
                        robo_c = j;
                        break;
                    }
                }
            }
            String load = new String(grid[robo_r]).substring(robo_c); // what we're pushing
            wall_i = (load.indexOf(WALL) != -1) ? load.indexOf(WALL) : Integer.MAX_VALUE;
            box_i = (load.indexOf(BOX) != -1) ? load.indexOf(BOX) : Integer.MAX_VALUE;
            empty_i = (load.indexOf(EMPTY) != -1) ? load.indexOf(EMPTY) : Integer.MAX_VALUE;

            // System.out.println(load);

            if (wall_i < empty_i) {
            } // NO SPACE DO NOTHING; // @_#_ -> @_#_
            else if (empty_i < Math.min(box_i, wall_i)) { // @._ -> .@_
                grid[robo_r][robo_c] = EMPTY;
                grid[robo_r][robo_c + 1] = ROBOT;
            } else if (box_i < empty_i) { // @O.# -> .@O#
                grid[robo_r][robo_c] = EMPTY;
                grid[robo_r][robo_c + 1] = ROBOT;
                for (int i = 2; i <= Math.min(wall_i, empty_i); i++) {
                    grid[robo_r][robo_c + i] = BOX;
                }
            }
            grid = rotate(grid, move, true);
            // System.out.println("push " + move);
            // display(grid);
        }
        System.out.println(value(grid, BOX));
    }

    public static void part2(char[][] grid, String moves) {
        int robo_r = -1, robo_c = -1; // location of robot
        int wall_i = Integer.MAX_VALUE, box_i = Integer.MAX_VALUE, empty_i = Integer.MAX_VALUE; // index of certain items
        char[][] gridb = new char[grid.length][grid[0].length * 2]; // make bigger grid
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                switch (grid[i][j]) {
                    case WALL:
                        gridb[i][j * 2] = WALL;
                        gridb[i][j * 2 + 1] = WALL;
                        break;
                    case BOX:
                        gridb[i][j * 2] = LBOX;
                        gridb[i][j * 2 + 1] = RBOX;
                        break;
                    case EMPTY:
                        gridb[i][j * 2] = EMPTY;
                        gridb[i][j * 2 + 1] = EMPTY;
                        break;
                    case ROBOT:
                        gridb[i][j * 2] = ROBOT;
                        gridb[i][j * 2 + 1] = EMPTY;
                        break;
                }
            }
        }
        for (String move : moves.split("")) { // iterate thru each move
            display(gridb);
            gridb = rotate(gridb, move, false);
            for (int i = 0; i < gridb.length; i++) { // update location of ROBOT
                for (int j = 0; j < gridb[i].length; j++) {
                    if (gridb[i][j] == ROBOT) {
                        robo_r = i;
                        robo_c = j;
                        break;
                    }
                }
            }
            /*
             * Here's where we push boxes.
             * 
             * The issue present here is, given the current layout of the method, that
             * you're
             * moving 2x1 boxes into a potentially 1x2 context; the operation is no longer
             * symmetric.
             * 
             * To understand this intuitively, think of the case moving a box left/right
             * versus up/down. Left/right movement can only move a single line of boxes.
             * Up/down movement can move a triangle of expanding boxes (1 box pushes 2;
             * 2 boxes push 3; ...).
             * 
             * This adds complexity to the current method of rotate -> push -> unrotate.
             */
            gridb = push(gridb, move, robo_r, robo_c);
            gridb = rotate(gridb, move, true);
            System.out.println("push " + move);
        }
        // System.out.println(value(grid, LBOX));
    }

    public static char[][] push(char[][] grid, String move, int row, int col) {
        if (move.equals(RIGHT) || move.equals(LEFT)) { // easy cases, basically same as p1
            int wall_i = Integer.MAX_VALUE, box_i = Integer.MAX_VALUE, empty_i = Integer.MAX_VALUE; // index of certain
                                                                                                    // items
            String load = new String(grid[row]).substring(col); // what we're pushing
            wall_i = (load.indexOf(WALL) != -1) ? load.indexOf(WALL) : Integer.MAX_VALUE;
            if (move.equals(LEFT))
                box_i = (load.indexOf(RBOX) != -1) ? load.indexOf(RBOX) : Integer.MAX_VALUE;
            else
                box_i = (load.indexOf(LBOX) != -1) ? load.indexOf(LBOX) : Integer.MAX_VALUE;
            empty_i = (load.indexOf(EMPTY) != -1) ? load.indexOf(EMPTY) : Integer.MAX_VALUE;

            if (wall_i < empty_i) {
            } // NO SPACE DO NOTHING; // @_#_ -> @_#_
            else if (empty_i < Math.min(box_i, wall_i)) { // @._ -> .@_
                grid[row][col] = EMPTY;
                grid[row][col + 1] = ROBOT;
            } else if (box_i < empty_i) { // @O.# -> .@O#
                grid[row][col] = EMPTY;
                grid[row][col + 1] = ROBOT;
                for (int i = 2; i <= Math.min(wall_i, empty_i); i += 2) {
                    if (move.equals(RIGHT)) {
                        grid[row][col + i] = LBOX;
                        grid[row][col + i + 1] = RBOX;
                    } else if (move.equals(LEFT)) {
                        grid[row][col + i] = RBOX;
                        grid[row][col + i + 1] = LBOX;
                    } else {
                        System.out.println("RLpush cooked");
                        System.exit(1);
                    }
                }
            }
        } else { // harder cases, recursive prolly

        }

        /*
         * char token = grid[row][col];
         * if(token == WALL) return grid;
         * 
         * // if(token == EMPTY)
         * char[][] ret = new char[grid.length][grid[0].length];
         * String area = new String(grid[row]).substring(col);
         * System.out.println(area);
         */

        return grid;
    }

    /*
     * rotate is a relatively elegant solution to modularize movement.
     * the algorithmic complexity of this (ie. the amount of time it takes to run)
     * is:
     * i * j, aka for a matrix of size m*n, it would take O(m*n) to run one full
     * matrix rotation
     * 
     * This makes intuitive sense since you're iterating through each element and
     * then doing a
     * simple assignment. It takes the length of time required to iterate through
     * array once.
     */
    public static char[][] rotate(char[][] grid, String dir, boolean reverse) { // rotates based on direction s.t. always moving right
        char[][] ret = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (dir.equals(RIGHT))
                    ret[i][j] = grid[i][j];
                else if (!reverse && dir.equals(DOWN) || reverse && dir.equals(UP))
                    ret[grid[i].length - j - 1][i] = grid[i][j];
                else if (dir.equals(LEFT))
                    ret[grid.length - i - 1][grid[i].length - j - 1] = grid[i][j];
                else if (!reverse && dir.equals(UP) || reverse && dir.equals(DOWN))
                    ret[j][grid.length - i - 1] = grid[i][j];
                else
                    System.out.println("rotate cooked");
            }
        }
        return ret;
    }

    public static long value(char[][] grid, char val) { // calculates final value
        long tot = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == val)
                    tot += 100 * i + j;
            }
        }
        return tot;
    }

    public static void display(char[][] grid) { // prints 2d array neatly
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }
}