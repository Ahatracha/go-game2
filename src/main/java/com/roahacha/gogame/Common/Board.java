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

    public Board() {
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = Stone.NONE;
    }

    public Stone getStone(int height, int length) {
        return grid[height][length];
    }

    public int getSize() {
        return gridWidth;
    }

    // updates grid[][] after any player moves
    // recives new grid from GameBoard
    public void updateGrid(Stone arr[][]) {
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = arr[i][j];
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
