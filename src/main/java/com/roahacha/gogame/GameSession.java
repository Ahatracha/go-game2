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

    public static final int CONTINUE = 4;
    public static final int PLAYER1_WON = 1;
    public static final int PLAYER2_WON = 2;


    public GameSession(Socket firstPlayerSocket, Socket secondPlayerSocket) throws IOException {
        this.firstPlayer = new PlayerController(firstPlayerSocket, Stone.BLACK);
        this.secondPlayer = new PlayerController(secondPlayerSocket, Stone.WHITE);
        this.gameBoard = new GameBoard();

    }
    @Override
    public void run() {
        try (firstPlayer; secondPlayer) {
            // Informacje o kamieniach dla gracza
            // 1 = Czarne, 2 = Białe
            firstPlayer.sendStoneInfo(1);
            secondPlayer.sendStoneInfo(2);

            firstPlayer.getOut().writeInt(1);

            boolean gameLoop =  true;
            boolean isBlackTurn = true;

            // Main game loop
            while (gameLoop) {
                PlayerController currentPlayer = isBlackTurn ? firstPlayer : secondPlayer;
                PlayerController opponentPlayer = isBlackTurn ? secondPlayer : firstPlayer;

                PlayerAction action = currentPlayer.waitForDecision();

                switch (action) {
                    case MOVE:
                        if (handleMove(currentPlayer, opponentPlayer)) {
                            isBlackTurn = !isBlackTurn;
                        }
                        break;
                    case PASS:
                        isBlackTurn = !isBlackTurn;
                        break;
                    case SURRENDER:
                        // TODO: Display that [opponentPlayer] won
                        sendGameOver(currentPlayer == firstPlayer ? PLAYER2_WON : PLAYER1_WON);
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
            try {
                // DODANO: Kluczowa zmiana dla GUI.
                // Serwer musi wysłać potwierdzenie do OBU graczy po udanym ruchu.

                // Powiadomienie gracza, który wykonał ruch
                p1.getOut().writeInt(CONTINUE);
                p1.getOut().writeInt(row);
                p1.getOut().writeInt(col);
                p1.getOut().flush();

                // Powiadomienie przeciwnika o ruchu
                p2.getOut().writeInt(CONTINUE);
                p2.getOut().writeInt(row);
                p2.getOut().writeInt(col);
                p2.getOut().flush();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: wysłać informację o błędnym ruchu tylko do currentPlayer
        }
        return false;
    }

        private void sendGameOver ( int status) throws IOException {
            firstPlayer.getOut().writeInt(status);
            secondPlayer.getOut().writeInt(status);
        }
    }
}
