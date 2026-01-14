package com.roahacha.gogame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.roahacha.gogame.Common.Board;
import com.roahacha.gogame.Common.GameBoard;
import com.roahacha.gogame.Common.Stone;

public class BoardTest {

    @Test
    public void testMovePlacement() {
        GameBoard gameBoard = new GameBoard();

        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);
        gameBoard.placeStone(1, 2, Stone.WHITE, 1);
        gameBoard.placeStone(2, 1, Stone.WHITE, 1);
        // . ● .
        // ● . ●
        // . ● .

        assertEquals(gameBoard.placeStone(1, 1, Stone.BLACK, 1), false);
        assertEquals(gameBoard.placeStone(0, 2, Stone.BLACK, 1), true);
    }

    @Test
    public void testUpdateGrid() {
        GameBoard gameBoard = new GameBoard();
        Board board = new Board();

        gameBoard.placeStone(0, 0, Stone.BLACK, 1);
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);

        board.updateGrid(gameBoard.getGrid());

        assertEquals(gameBoard.toString(), board.toString());
    }

    @Test
    public void testBreathsCalculation() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(1,1, Stone.BLACK, 1);
        gameBoard.placeStone(1,2, Stone.BLACK, 1);
        gameBoard.placeStone(1,3, Stone.BLACK, 1);
        gameBoard.placeStone(2,1, Stone.BLACK, 1);
        gameBoard.placeStone(2,3, Stone.BLACK, 1);
        // . . . . .
        // . ○ ○ ○ .
        // . ○ . ○ .
        // . . . . .
        //System.out.println(gameBoard.numOfBreaths(2, 2, Stone.BLACK));
        assertEquals(gameBoard.numOfBreaths(2, 1, Stone.BLACK), 10);

    }

    @Test
    public void testCaptureStone() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);
        gameBoard.placeStone(1, 2, Stone.WHITE, 1);
        gameBoard.placeStone(1, 1, Stone.BLACK, 1);
        // . ● .
        // ● ○ ●
        // . . .

        assertEquals(gameBoard.placeStone(2, 1, Stone.WHITE, 1), true);
        assertEquals(gameBoard.getStone(1, 1), Stone.NONE);
    }

    @Test
    public void testCaptureStoneInCorner() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(0, 0, Stone.BLACK, 1);
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        // ○ ● .
        // . . .
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);
        // . ● .
        // ● . .

        assertEquals(gameBoard.getStone(0, 0), Stone.NONE);
    }

    @Test
    public void testCaptureStoneAtEdge() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(0, 2, Stone.WHITE, 1);
        gameBoard.placeStone(1, 1, Stone.BLACK, 1);
        gameBoard.placeStone(1, 2, Stone.BLACK, 1);
        gameBoard.placeStone(0, 3, Stone.BLACK, 1);
        // . ● ● ○ .
        // . ○ ○ . .
        System.out.println("SSSSTOP");
        gameBoard.placeStone(0, 0, Stone.BLACK, 1);
        // ○ . . ○ .
        // . ○ ○ . .

        assertEquals(gameBoard.getStone(0, 0), Stone.BLACK);
        assertEquals(gameBoard.getStone(0, 1), Stone.NONE);
        assertEquals(gameBoard.getStone(0, 2), Stone.NONE);

    }

    @Test void testCaptureWhileSurrounded() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);
        gameBoard.placeStone(1, 2, Stone.WHITE, 1);
        gameBoard.placeStone(2, 1, Stone.WHITE, 1);
        gameBoard.placeStone(0, 2, Stone.BLACK, 1);
        gameBoard.placeStone(1, 3, Stone.BLACK, 1);
        gameBoard.placeStone(2, 2, Stone.BLACK, 1);
        // . ● ○ .
        // ● . ● ○
        // . ● ○ .

        assertEquals(gameBoard.placeStone(1, 1, Stone.BLACK, 1), true);
        assertEquals(gameBoard.getStone(1, 1), Stone.BLACK);
        assertEquals(gameBoard.getStone(1, 2), Stone.NONE);
    }

    @Test
    public void testKoiRule() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeStone(0, 1, Stone.WHITE, 1);
        gameBoard.placeStone(1, 0, Stone.WHITE, 1);
        gameBoard.placeStone(1, 2, Stone.WHITE, 1);
        gameBoard.placeStone(2, 1, Stone.WHITE, 1);
        gameBoard.placeStone(0, 2, Stone.BLACK, 1);
        gameBoard.placeStone(1, 3, Stone.BLACK, 1);
        gameBoard.placeStone(2, 2, Stone.BLACK, 1);
        // . ● ○ .
        // ● . ● ○
        // . ● ○ .

        gameBoard.placeStone(1, 1, Stone.BLACK, 1);

        // . ● ○ .
        // ● ○ . ○
        // . ● ○ .

        assertEquals(gameBoard.placeStone(1, 2, Stone.WHITE, 1), false);
    }
}
