package com.roahacha.gogame.Common;

// Made for server
// Handle game rules and applies them on board
public class GameBoard extends Board {
    // tells, if tile at [i][j] was interacted with
    boolean[][] tileUsed = new boolean[gridWidth][gridWidth];

    // previous state of the board for KO rule
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
                        grid[height][length] = Stone.NONE;
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

    // Returns true if the move violates the KO rule (invalid move), false otherwise
    private boolean checkKO(int height, int length, Stone stone) {
        // [DIAGNOSTYKA] Sprawdzamy czy mamy historię
        if (morePreviousGrid == null) {
            System.out.println("KO CHECK: Brak historii (morePreviousGrid is null). Ruch dozwolony.");
            return false;
        }

        // 1. Create a DEEP COPY of the current grid to simulate the move
        Stone[][] simulationGrid = new Stone[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridWidth; j++) {
                simulationGrid[i][j] = this.grid[i][j];
            }
        }

        Stone[][] realGrid = this.grid; // Backup real grid

        // Swap in simulation grid to use existing logic safely
        this.grid = simulationGrid;

        try {
            // Simulate placing the stone
            this.grid[height][length] = stone;

            // Simulate capturing opponents
            if (height - 1 >= 0)            removeIfInvalid(height - 1, length, stone.oppositeStone());
            if (length + 1 < gridWidth)     removeIfInvalid(height, length + 1, stone.oppositeStone());
            if (height + 1 < gridWidth)     removeIfInvalid(height + 1, length, stone.oppositeStone());
            if (length - 1 >= 0)            removeIfInvalid(height, length - 1, stone.oppositeStone());

            // 4. Compare the simulation result with the state from 2 moves ago
            for (int i = 0; i < gridWidth; i++) {
                for (int j = 0; j < gridWidth; j++) {
                    if (this.grid[i][j] != morePreviousGrid[i][j]) {
                        // [DIAGNOSTYKA] Jeśli znajdzie różnicę, wypisze to w konsoli serwera

                        return false; // The layout is different -> No KO -> Move allowed
                    }
                }
            }

            System.out.println("KO CHECK: Wykryto powtórzenie planszy! (KO VIOLATION)");
            return true; // The layout is identical -> KO -> Move forbidden!

        } finally {
            // 5. VERY IMPORTANT: Restore the real board
            this.grid = realGrid;
        }
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
        int blackPoints = 0;
        int whitePoints = 0;
        boolean[][] visited = new boolean[gridWidth][gridWidth];
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++)
                visited[i][j] = false;

        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridWidth; j++) {
                if (visited[i][j]) continue;
                if (grid[i][j] == Stone.BLACK) {
                    blackPoints++;
                } else if (grid[i][j] == Stone.WHITE) {
                    whitePoints++;
                } else {
                    // empty tile, check surrounding stones
                    boolean touchesBlack = false;
                    boolean touchesWhite = false;
                    int territorySize = 0;

                    java.util.Stack<int[]> stack = new java.util.Stack<>();
                    stack.push(new int[] {i, j});
                    visited[i][j] = true;
                    territorySize++;

                    while (!stack.isEmpty()) {
                        int[] pos = stack.pop();
                        int height = pos[0];
                        int length = pos[1];
                        int[][] directions = { {-1,0}, {0,1}, {1,0}, {0,-1} }; // up, right, down, left

                        for (int[] dir : directions) {
                            int newHeight = height + dir[0];
                            int newLength = length + dir[1];
                            if (newHeight >= 0 && newHeight < gridWidth &&
                                newLength >= 0 && newLength < gridWidth) {
                                    if (grid[newHeight][newLength] == Stone.BLACK) {
                                        touchesBlack = true;
                                    } else if (grid[newHeight][newLength] == Stone.WHITE) {
                                        touchesWhite = true;
                                    } else  if (!visited[newHeight][newLength]) {
                                        visited[newHeight][newLength] = true;
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
