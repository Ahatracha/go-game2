package com.roahacha.gogame;

import com.roahacha.gogame.Common.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


// Connects Users via Server
public class GameSession implements Runnable {

    private final PlayerController    firstPlayer;
    private final PlayerController    secondPlayer;
    private GameBoard gameBoard;

    
    // public static int PLAYER1_WON = 3;
    // public static int PLAYER2_WON = 4;
    // public static int DRAW = 5;
    // public static int CONTINUE = 6;

    public GameSession(Socket firstPlayer, Socket secondPlayer) throws IOException {
        this.firstPlayer = new PlayerController(secondPlayer, Stone.BLACK);
        this.secondPlayer = new PlayerController(secondPlayer, Stone.WHITE);
        this.gameBoard = new GameBoard();

    }
    @Override
    public void run() {
        try (firstPlayer; secondPlayer) {
            // Informacje o kamieniach dla gracza
            // 1 = Czarne, 2 = Białe
            firstPlayer.sendStoneInfo(1);
            secondPlayer.sendStoneInfo(2);

            boolean gameLoop =  true;
            boolean isBlackTurn = true;

            // Main game loop
            while (gameLoop){
                PlayerController currentPlayer =    isBlackTurn ? firstPlayer : secondPlayer;
                PlayerController opponentPlayer =   isBlackTurn ? secondPlayer : firstPlayer;

                PlayerAction action = currentPlayer.waitForDecision();

                switch (action) {
                    case MOVE:
                        if (handleMove(currentPlayer, opponentPlayer))
                            isBlackTurn = !isBlackTurn;
                        break;
                    case PASS:
                        isBlackTurn = !isBlackTurn;
                        break;
                    case SURRENDER:
                        // TODO: Display that [opponentPlayer] won
                        gameLoop = false;
                        break;
                    default:    // QUIT or UNKNOWN
                        // TODO: Display [currentPlayer] lost connection
                        gameLoop = false;
                        break;
                }

               
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    // returns true on success
    // false otherwise
    private boolean handleMove(PlayerController p1, PlayerController p2) {
        int[] cords = p1.getMovePlace();
        int row = cords[0];
        int col = cords[1];

        MoveCommand command = new MoveCommand(gameBoard, row, col, p1.getStone());
        command.execute();

        if (command.isSuccessful()) {
            // TODO: update grids

            return true;
        }
        return false;
    }

}
