package com.roahacha.gogame.Common;

// Made for server
// Handle game rules and applies them on board
public class GameBoard extends Board {
    // tells, if tile at [i][j] was interacted with
    boolean[][] tileUsed = new boolean[gridWidth][gridWidth];

    public GameBoard() {
        clearTileUsed();
    }

    public Stone[][] getGrid() {
        Stone[][] arr = new Stone[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth * gridWidth; i++)
            arr[i/gridWidth][i%gridWidth] = grid[i/gridWidth][i%gridWidth];
        return arr;
    }

    private void clearTileUsed() {
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                tileUsed[i][j] = false;
    }

    // calculates number of breaths for chain including stone at grid[height][length]
    // after invoking first recursion of funtion, set whole tileUsed[][] to false
    private int numOfBreaths(int height, int length, Stone stone) {
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

    // return true if Stone [stone] placed on 
    // grid[height][length] can capture oposite stones
    // otherwise return false
    private boolean checkForCapture(int height, int length, Stone stone) {
        clearTileUsed();
        // set temporarily to count breaths of opponent's chains
        // if this move were to happen
        grid[height][length] = stone;
        if (height - 1 >= 0 && numOfBreaths(height - 1, length, stone.oppositeStone()) == 0) {
            grid[height][length] = Stone.NONE;
            return true;
        } clearTileUsed();
        if (length + 1 < gridWidth && numOfBreaths(height, length + 1, stone.oppositeStone()) == 0) {
            grid[height][length] = Stone.NONE;
            return true;
        } clearTileUsed();
        if (height + 1 < gridWidth && numOfBreaths(height + 1, length, stone.oppositeStone()) == 0) {
            grid[height][length] = Stone.NONE;
            return true;
        } clearTileUsed();
        if (length - 1 >= 0 && numOfBreaths(height, length - 1, stone.oppositeStone()) == 0) {
            grid[height][length] = Stone.NONE;
            return true;
        } clearTileUsed();
        return false;
    }

    // sprawdza, czy gracz może postawić kamień na pozycji grid[width,length]
    private boolean checkMoveValidity(int height, int length, Stone stone) {
        if (height < 0 || height >= gridWidth)      return false;
        if (length < 0 || length >= gridWidth)      return false;
        if (grid[height][length] != Stone.NONE)     return false;
        //boolean public checkCapture(width, length);
        if (numOfBreaths(height, length, stone) == 0 && !checkForCapture(height, length, stone))
            return false;
        return true;
    }

    // return if can't place stone because of KOI rule
    // false otherwise
    private boolean checkKOI(int height, int length, Stone stone) {
        // TODO: make this
        return false;
    }

    // remove captured pieces if they have no breaths
    private void removeIfInvalid(int height, int length, Stone stone) {
        if (grid[height][length] != stone) return;

        // chain is still valid
        if (numOfBreaths(height, length, stone) != 0) {
            clearTileUsed();
            return;
        }

        // chain has 0 breaths
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++) {
                if (tileUsed[i][j] && grid[i][j] == stone)
                    grid[i][j] = Stone.NONE;
            }
        // grid[height][length] = Stone.NONE;
        // tileUsed[height][length] = true;

        clearTileUsed();
    }

    // returns true on success, false on failure
    public boolean placeStone(int height, int length, Stone stone) {
        if (!checkMoveValidity(height, length, stone))  return false;
        grid[height][length] = stone;
        // tileUsed[height][length] = true;

        if (height - 1 >= 0)            removeIfInvalid(height - 1, length, stone.oppositeStone());
        if (length + 1 < gridWidth)     removeIfInvalid(height, length + 1, stone.oppositeStone());
        if (height + 1 < gridWidth)     removeIfInvalid(height + 1, length, stone.oppositeStone());
        if (length - 1 >= 0)            removeIfInvalid(height, length - 1, stone.oppositeStone());

        return true;
    }

    // returns int[2], where first value is points of
    // black stones player, and second of white stones
    public int[] calculatePoints() {
        // TODO: make this
        return new int[] {-1, -1};
    }

}
