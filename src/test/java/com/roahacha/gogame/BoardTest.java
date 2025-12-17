package com.roahacha.gogame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.roahacha.gogame.Common.GameBoard;
import com.roahacha.gogame.Common.Stone;

public class BoardTest {
    
    @Test
    public void testMovePlacement() {
        GameBoard gameBoard = setupGameBoard();

        gameBoard.placeStone(0, 1, Stone.WHITE);
        gameBoard.placeStone(1, 0, Stone.WHITE);
        gameBoard.placeStone(1, 2, Stone.WHITE);
        gameBoard.placeStone(2, 1, Stone.WHITE);
        // . ● .
        // ● . ●
        // . ● .

        assertEquals(gameBoard.placeStone(1, 1, Stone.BLACK), false);
        assertEquals(gameBoard.checkMoveValidity(0, 2, Stone.BLACK), true);
        assertEquals(gameBoard.placeStone(0, 2, Stone.BLACK), true);
    }

    @Test
    public void testCaptureStone() {
        GameBoard gameBoard = setupGameBoard();
        gameBoard.placeStone(0, 1, Stone.WHITE);
        gameBoard.placeStone(1, 0, Stone.WHITE);
        gameBoard.placeStone(1, 2, Stone.WHITE);
        gameBoard.placeStone(1, 1, Stone.BLACK);
        // . ● .
        // ● ○ ●
        // . . .

        assertEquals(gameBoard.placeStone(2, 1, Stone.WHITE), true);
        assertEquals(gameBoard.getStone(1, 1), Stone.NONE);

    }

    private GameBoard setupGameBoard() {
        GameBoard gameBoard = new GameBoard();

        return gameBoard;
    }
}
