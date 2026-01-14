package com.roahacha.gogame.Common;

public class MoveCommand implements Command {
    private GameBoard board;
    private int row, col;
    private Stone stone;
    private boolean success;

    public MoveCommand(GameBoard board, int row, int col, Stone stone) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.stone = stone;
    }

    @Override
    public void execute() {
        success = board.placeStone(row, col, stone, 1);
    }

    public boolean isSuccessful() {
        return success;
    }
    
}
