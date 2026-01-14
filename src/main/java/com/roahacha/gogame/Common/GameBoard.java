package com.roahacha.gogame.Common;

// Made for server
// Handle game rules and applies them on board
public class GameBoard extends Board {
    // tells, if tile at [i][j] was interacted with
    boolean[][] tileUsed = new boolean[gridWidth][gridWidth];

    // previous state of the board for KOI rule
    private Stone[][] previousGrid = null;

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

        int breaths = 0;
        int[][] directions = { {-1,0}, {0,1}, {1,0}, {0,-1} }; // up, right, down, left

        for (int[] dir : directions) {
            int newHeight = height + dir[0];
            int newLength = length + dir[1];

            if (newHeight >= 0 && newHeight < gridWidth &&
                newLength >= 0 && newLength < gridWidth) {
                    if (grid[newHeight][newLength] != stone) breaths--;
                    else breaths += numOfBreaths(newHeight, newLength, stone);
                    if (grid[newHeight][newLength] == Stone.NONE && !tileUsed[newHeight][newLength]) {
                        tileUsed[newHeight][newLength] = true;
                        breaths++;
                    }
                }
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

    // return if can't place stone because of KO rule
    // false otherwise
    private boolean checkKO(int height, int length, Stone stone) {
        if (previousGrid == null) return false;
        Stone[][] holdGrid = grid;
        grid = new Stone[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = holdGrid[i][j];
        // place stone temporarily
        grid[height][length] = stone;
        // remove captured stones temporarily
        placeStone(height, length, stone, 0);
        // check if current grid equals previousGrid
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                if (grid[i][j] != previousGrid[i][j]) {
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
        // grid[height][length] = Stone.NONE;
        // tileUsed[height][length] = true;

        clearTileUsed();
    }

    // KOIRecursionGuard to prevent infinite recursion
    // returns true on success, false on failure
    public boolean placeStone(int height, int length, Stone stone, int KORecursionGuard) {
        if (!checkMoveValidity(height, length, stone))                  return false;
        if (KORecursionGuard < 0 && checkKO(height, length, stone))     return false;
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
        int blackPoints = 0;
        int whitePoints = 0;
        boolean[][] visited = new boolean[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                visited[i][j] = false;

        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++) {
                if (visited[i][j]) continue;
                visited[i][j] = true;
                if (grid[i][j] == Stone.BLACK) {
                    blackPoints++;
                } else if (grid[i][j] == Stone.WHITE) {
                    whitePoints++;
                } else {
                    // empty tile, check surrounding stones
                    boolean touchesBlack = false;
                    boolean touchesWhite = false;
                    int territorySize = 1;
                    java.util.Stack<int[]> stack = new java.util.Stack<>();
                    stack.push(new int[] {i, j});
                    while (!stack.isEmpty()) {
                        int[] pos = stack.pop();
                        int height = pos[0];
                        int length = pos[1];
                        int[][] directions = { {-1,0}, {0,1}, {1,0}, {0,-1} }; // up, right, down, left
                        for (int[] dir : directions) {
                            int newHeight = height + dir[0];
                            int newLength = length + dir[1];
                            if (newHeight >= 0 && newHeight < gridWidth &&
                                newLength >= 0 && newLength < gridWidth &&
                                !visited[newHeight][newLength]) {
                                    visited[newHeight][newLength] = true;
                                    if (grid[newHeight][newLength] == Stone.BLACK) {
                                        touchesBlack = true;
                                    } else if (grid[newHeight][newLength] == Stone.WHITE) {
                                        touchesWhite = true;
                                    } else {
                                        territorySize++;
                                        stack.push(new int[] {newHeight, newLength});
                                    }
                                }
                        }
                    }
                    if (touchesBlack && !touchesWhite) {
                        blackPoints += territorySize;
                    } else if (touchesWhite && !touchesBlack) {
                        whitePoints += territorySize;
                    }
                }
            }
        return new int[] {blackPoints, whitePoints};
    }

}
