package com.roahacha.gogame;

import com.roahacha.gogame.Common.*;

import java.io.IOException;
import java.net.Socket;
import com.roahacha.gogame.Common.*;


// Connects Users via Server
public class GameSession implements Runnable {

    private final PlayerController    firstPlayer;
    private final PlayerController    secondPlayer;
    private GameBoard gameBoard;

    public GameSession(Socket firstPlayer, Socket secondPlayer) throws IOException {
        this.firstPlayer = new PlayerController(firstPlayer, Stone.BLACK);
        this.secondPlayer = new PlayerController(secondPlayer, Stone.WHITE);
        this.gameBoard = new GameBoard();
    }

    @Override
    public void run() {
        try (firstPlayer; secondPlayer) {
            // Informacje o kamieniach dla gracza
            // 1 = Czarne, 2 = Białe
            //firstPlayer.sendAction(GameAction.GAME_STONE_BLACK);
            //secondPlayer.sendAction(GameAction.GAME_STONE_WHITE);
            firstPlayer.sendAction(GameAction.GAME_YOUR_TURN);

            boolean gameLoop =  true;
            boolean isBlackTurn = true;
            int playerPassCount = 0;

            // Main game loop
            while (gameLoop){
                PlayerController currentPlayer =    isBlackTurn ? firstPlayer : secondPlayer;
                PlayerController opponentPlayer =   isBlackTurn ? secondPlayer : firstPlayer;

                GameAction action = currentPlayer.reciveAction();

                switch (action) {
                    case PLAYER_MOVE:
                        playerPassCount = 0;
                        if (handleMove(currentPlayer, opponentPlayer))
                            isBlackTurn = !isBlackTurn;
                        break;
                    case PLAYER_PASS:
                        playerPassCount++;
                        opponentPlayer.sendAction(GameAction.GAME_YOUR_TURN);
                        isBlackTurn = !isBlackTurn;
                        break;
                    case PLAYER_SURRENDER:
                        playerPassCount = 0;
                        opponentPlayer.sendAction(GameAction.GAME_END_WIN);
                        currentPlayer.sendAction(GameAction.GAME_END_LOSS);
                        gameLoop = false;
                        break;
                    default:    // QUIT or UNKNOWN
                        opponentPlayer.sendAction(GameAction.GAME_END_WIN);
                        gameLoop = false;
                        break;
                }
                if (playerPassCount >= 2) {
                    gameLoop = false;
                    int points[] = gameBoard.calculatePoints();
                    if (points[0] > points[1]) {
                        firstPlayer.sendAction(GameAction.GAME_END_WIN);
                        secondPlayer.sendAction(GameAction.GAME_END_LOSS);
                    } else if (points[1] > points[0]) {
                        firstPlayer.sendAction(GameAction.GAME_END_LOSS);
                        secondPlayer.sendAction(GameAction.GAME_END_WIN);
                    } else {
                        firstPlayer.sendAction(GameAction.GAME_END_DRAW);
                        secondPlayer.sendAction(GameAction.GAME_END_DRAW);
                    }
                }
               
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    // returns true on success
    // false otherwise
    private boolean handleMove(PlayerController current, PlayerController opponent) {
        int[] cords = current.reciveMove();
        int row = cords[0];
        int col = cords[1];

        MoveCommand command = new MoveCommand(gameBoard, row, col, current.getStone());
        command.execute();

        if (command.isSuccessful()) {
            current.sendAction(GameAction.GAME_SEND_GRID);
            current.sendBoardState(gameBoard);
            opponent.sendAction(GameAction.GAME_SEND_GRID);
            opponent.sendBoardState(gameBoard);
            opponent.sendAction(GameAction.GAME_YOUR_TURN);
            return true;
        }
        current.sendAction(GameAction.GAME_INCORRENT_MOVE);
        return false;
    }

}
