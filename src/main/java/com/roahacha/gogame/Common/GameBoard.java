package com.roahacha.gogame.Common;

// Made for server
// Handle game rules and applies them on board
public class GameBoard extends Board {
    // tells, if tile at [i][j] was interacted with
    boolean[][] tileUsed = new boolean[gridWidth][gridWidth];

    // previous state of the board for KOI rule
    private Stone[][] previousGrid = null;
    private Stone[][] morePreviousGrid = null;

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

    private void updatePreviousGrids() {
        morePreviousGrid = previousGrid;
        previousGrid = getGrid();
    }

    // calculates number of breaths for chain including stone at grid[height][length]
    // after invoking first recursion of funtion, set whole tileUsed[][] to false
    public int numOfBreaths(int height, int length, Stone stone) {

        if (height < 0 || height >= gridWidth || length < 0 || length >= gridWidth)
            return 0;

        if (tileUsed[height][length]) return 0;

        tileUsed[height][length] = true;

        if (grid[height][length] == Stone.NONE) {
            return 1;
        }

        if (grid[height][length] != stone) {
            return 0;
        }

        int breaths = 0;
        int[][] directions = { {-1,0}, {0,1}, {1,0}, {0,-1} };

        for (int[] dir : directions) {
            int newHeight = height + dir[0];
            int newLength = length + dir[1];

            breaths += numOfBreaths(newHeight, newLength, stone);
        }

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
        int[][] directions = { {-1,0}, {0,1}, {1,0}, {0,-1} }; // up, right, down, left
        for (int[] dir : directions) {
            int newHeight = height + dir[0];
            int newLength = length + dir[1];
            if (newHeight >= 0 && newHeight < gridWidth &&
                newLength >= 0 && newLength < gridWidth) {
                    if (numOfBreaths(newHeight, newLength, stone.oppositeStone()) == 0) {
                        grid[newHeight][newLength] = Stone.NONE;
                        return true;
                    }
            }
            clearTileUsed();
        }
        grid[height][length] = Stone.NONE;
        return false;
    }

    // sprawdza, czy gracz może postawić kamień na pozycji grid[width,length]
    private boolean checkMoveValidity(int height, int length, Stone stone) {
        if (height < 0 || height >= gridWidth)      return false;
        if (length < 0 || length >= gridWidth)      return false;
        if (grid[height][length] != Stone.NONE)     return false;

        grid[height][length] = stone;
        if (numOfBreaths(height, length, stone) == 0 && !checkForCapture(height, length, stone)) {
            clearTileUsed();
            grid[height][length] = Stone.NONE;
            return false;
        }
        grid[height][length] = Stone.NONE;
        clearTileUsed();
        return true;
    }

    // return if can't place stone because of KOI rule
    // false otherwise
    private boolean checkKO(int height, int length, Stone stone) {
        if (morePreviousGrid == null) return false;
        Stone[][] holdGrid = grid;
        grid = new Stone[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = holdGrid[i][j];
        // place stone temporarily
        grid[height][length] = stone;
        // remove captured stones temporarily
        placeStone(height, length, stone, 0);
        // check if current grid equals morePreviousGrid
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                if (grid[i][j] != morePreviousGrid[i][j]) {
                    grid = holdGrid;
                    return false;
                }
        grid = holdGrid;
        return true;
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

        clearTileUsed();
    }

    // returns true on success, false on failure
    public boolean placeStone(int height, int length, Stone stone, int KORecursionGuard) {
        if (KORecursionGuard > 0 &&!checkMoveValidity(height, length, stone))   return false;
        if (KORecursionGuard > 0 && checkKO(height, length, stone))             return false;
        grid[height][length] = stone;

        if (height - 1 >= 0)            removeIfInvalid(height - 1, length, stone.oppositeStone());
        if (length + 1 < gridWidth)     removeIfInvalid(height, length + 1, stone.oppositeStone());
        if (height + 1 < gridWidth)     removeIfInvalid(height + 1, length, stone.oppositeStone());
        if (length - 1 >= 0)            removeIfInvalid(height, length - 1, stone.oppositeStone());

        if (KORecursionGuard > 0) updatePreviousGrids();
        return true;
    }

    // returns int[2], where first value is points of
    // black stones player, and second of white stones
    public int[] calculatePoints() {
        // TODO: make this
        return new int[] {-1, -1};
    }

}
