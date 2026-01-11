package com.roahacha.gogame.Common;

// Made for players
// Allow viewing board and checking if move is valid
//
//              length
//          \ 0 1 2
//          0 ○
// height   1   ●
//          2 ○   ○
//
public class Board {
    public static final int gridWidth = 19;
    Stone[][] grid = new Stone[gridWidth][gridWidth];
    // tells, if tile at [i][j] was interacted with
    boolean[][] tileUsed = new boolean[gridWidth][gridWidth];

    public Board() {
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++) {
                grid[i][j] = Stone.NONE;
                tileUsed[i][j] = false;
            }
    }

    public Stone getStone(int height, int length) {
        return grid[height][length];
    }

    // calculates number of breaths for chain including stone at grid[height][length]
    // after invoking first recursion of funtion, set whole tileUsed[][] to false
    protected int numOfBreaths(int height, int length, Stone stone) {
        if (tileUsed[height][length]) return -1;
        tileUsed[height][length] = true;

        int breaths = 4;
        if (height - 1 >= 0) {     // Stone above
            if (grid[height - 1][length] != stone)  breaths--;
            else    breaths += numOfBreaths(height - 1, length, stone);
            if (grid[height - 1][length] == Stone.NONE && !tileUsed[height - 1][length]) {
                tileUsed[height - 1][length] = true;
                breaths++;
            }
        } else breaths--;

        if (length + 1 < gridWidth) {   // Stone to the right
            if (grid[height][length + 1] != stone)   breaths--;
            else    breaths += numOfBreaths(height, length + 1, stone);
            if (grid[height][length + 1] == Stone.NONE && !tileUsed[height][length + 1]) {
                tileUsed[height][length + 1] = true;
                breaths++;
            }
        } else breaths--;

        if (height + 1 < gridWidth) {   // Stone below
            if (grid[height + 1][length] != stone)   breaths--;
            else    breaths += numOfBreaths(height + 1, length, stone);
            if (grid[height + 1][length] == Stone.NONE && !tileUsed[height + 1][length]) {
                tileUsed[height + 1][length] = true;
                breaths++;
            }
        } else breaths--;

        if (length - 1 >= 0) {          // Stone to the left
            if (grid[height][length - 1] != stone)   breaths--;
            else    breaths += numOfBreaths(height - 1, length, stone);
            if (grid[height][length - 1] == Stone.NONE && !tileUsed[height][length - 1]) {
                tileUsed[height][length - 1] = true;
                breaths++;
            }
        } else breaths--;

        return breaths;
    }

    // lesser version of numOfBreaths(...)
    protected int countFreeSpaces(int height, int length) {
        int breaths = 4;
        if (height - 1 >= 0 && grid[height - 1][length] != Stone.NONE)
            breaths--;
        if (height - 1 < 0) breaths--;
        if (length + 1 < gridWidth && grid[height][length + 1] != Stone.NONE)
            breaths--;
        if (length + 1 >= gridWidth) breaths--;
        if (height + 1 < gridWidth && grid[height + 1][length] != Stone.NONE)
            breaths--;
        if (height + 1 >= gridWidth) breaths--;
        if (length - 1 >= 0 && grid[height][length - 1] != Stone.NONE)
            breaths--;
        if (length - 1 < 0) breaths--;

        return breaths;
    }

    // sprawdza, czy gracz może postawić kamień na pozycji grid[width,length]
    public boolean checkMoveValidity(int height, int length, Stone stone) {
        if (height < 0 || height >= gridWidth)      return false;
        if (length < 0 || length >= gridWidth)      return false;
        if (grid[height][length] != Stone.NONE)     return false;
        //boolean public checkCapture(width, length);
        if (countFreeSpaces(height, length) == 0)   return false;
        return true;
    }

    
    private void removeIfInvalid(int height, int length) {
        if (countFreeSpaces(height, length) == 0) {
            grid[height][length] = Stone.NONE;
            tileUsed[height][length] = true;
        }
    }

    public boolean placeStone(int height, int length, Stone stone) {
        if (countFreeSpaces(height, length) == 0)   return false;
        grid[height][length] = stone;
        tileUsed[height][length] = true;

        if (height - 1 >= 0)            removeIfInvalid(height - 1, length);
        if (length + 1 < gridWidth)     removeIfInvalid(height, length + 1);
        if (height + 1 < gridWidth)     removeIfInvalid(height + 1, length);
        if (length - 1 >= 0)            removeIfInvalid(height, length - 1);


        return true;
    }

    @Override
    public String toString() {
        final String changeColor = "\033[0;30m\033[43m";
        final String revert = "\33[0m";

        String output = "" + changeColor;
        output += "╔";
        for (int i = 0; i <= 2 * gridWidth; i++) output += "═";
        output += "╗" + revert + "\n";
        for (int i = 0; i < gridWidth; i++) {
            output += changeColor + "║ ";
            for (int j = 0; j < gridWidth; j++) {
                switch (grid[i][j]) {
                    case WHITE:
                        output += "○ ";     // ○ ◯
                        break;
                    case BLACK:
                        output += "● ";     // ● ⬤
                        break;
                    default:
                        output += "  ";
                        break;
                }
            }
            output += "║" + revert + "\n";
        }
        output += changeColor + "╚";
        for (int i = 0; i <= 2 * gridWidth; i++) output += "═";
        output += "╝" + revert + "\n";

        return output;
    }
}
