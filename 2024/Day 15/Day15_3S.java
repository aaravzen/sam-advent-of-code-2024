import java.io.FileReader;
import java.util.Scanner;

public class Day15_3S {
    public static final char WALL = '#', BOX = 'O', EMPTY = '.', ROBOT = '@';
    public static final char LBOX = '[', RBOX = ']';
    public static final char UP = '^', DOWN = 'v', RIGHT = '>', LEFT = '<';

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new FileReader("./day15.a.in"));
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
        }
        // displayGrid(grid);
        System.out.println(value(grid));
    }

    public static void part2(char[][] grid, String moves) {
        int robo_r = -1, robo_c = -1; // location of robot
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
                        robo_r = i; robo_c = j * 2;
                        gridb[i][j * 2 + 1] = EMPTY;
                        break;
                }
            }
        }

        for (String move : moves.split("")) { // iterate thru each move
            char move_char = move.charAt(0);
            if (canPush(gridb, move_char, robo_r, robo_c)) {
                push(gridb, move_char, robo_r, robo_c);
                robo_r = getMovedRow(move_char, robo_r);
                robo_c = getMovedCol(move_char, robo_c);
            }
        }
        
        // displayGrid(gridb);
        System.out.println(value(gridb));
    }

    private static boolean canPush(char[][] grid, char direction, int r, int c) {
        int r_new = getMovedRow(direction, r);
        int c_new = getMovedCol(direction, c);
        switch (grid[r_new][c_new]) {
            case WALL:
                return false;
            case EMPTY:
                return true;
            case LBOX:
                if (direction == DOWN || direction == UP) {
                    return canPush(grid, direction, r_new, c_new) && canPush(grid, direction, r_new, c_new+1);
                }
                return canPush(grid, direction, r_new, c_new);
            case RBOX:
                if (direction == DOWN || direction == UP) {
                    return canPush(grid, direction, r_new, c_new-1) && canPush(grid, direction, r_new, c_new);
                }
                return canPush(grid, direction, r_new, c_new);
            case BOX:
                return canPush(grid, direction, r_new, c_new);
            default:
                System.out.println("push is cooked, as they say");
                return false;
        }
    }

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
            case LBOX:
                if (direction == DOWN || direction == UP) {
                    push(grid, direction, r_new, c_new+1);
                }
                push(grid, direction, r_new, c_new);
                grid[r_new][c_new] = grid[r][c];
                grid[r][c] = EMPTY;
                return true;
            case RBOX:
                if (direction == DOWN || direction == UP) {
                    push(grid, direction, r_new, c_new-1);
                }
                push(grid, direction, r_new, c_new);
                grid[r_new][c_new] = grid[r][c];
                grid[r][c] = EMPTY;
                return true;
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
