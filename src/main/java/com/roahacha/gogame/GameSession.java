package com.roahacha.gogame;

import com.roahacha.gogame.Common.Board;
import com.roahacha.gogame.Common.GameBoard;
import com.roahacha.gogame.Common.Stone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


// Connects Users via Server
public class GameSession implements Runnable {

    private Socket firstPlayer;
    private Socket secondPlayer;
    private GameBoard gameBoard;
    boolean isEnded=false;

    public static int PLAYER1_WON = 1;
    public static int PLAYER2_WON = 2;
    public static int DRAW = 3;
    public static int CONTINUE = 4;

    public GameSession(Socket firstPlayer, Socket secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;

        this.gameBoard = new GameBoard();

    }
    @Override
    public void run() {
        try{
            DataInputStream fromPlayer1 = new DataInputStream(firstPlayer.getInputStream());
            DataOutputStream toPlayer1 = new DataOutputStream(firstPlayer.getOutputStream());
            DataInputStream fromPlayer2 = new DataInputStream(secondPlayer.getInputStream());
            DataOutputStream toPlayer2 = new DataOutputStream(secondPlayer.getOutputStream());
            //powiadomienie dla gracza 1 że dołączył
            toPlayer1.writeInt(1);

            toPlayer2.writeInt(2);
            //powiadomienie dla gracza 1 że gracz 2 dołączył
            toPlayer1.writeInt(1);

            //main game loop
            while(true){
                //tura czarnego ----
                boolean moveMade = false;
                while (!moveMade) {

                    int row = fromPlayer1.readInt();
                    int col = fromPlayer1.readInt();
                    if (gameBoard.checkMoveValidity(row, col, Stone.BLACK)){
                        if (gameBoard.placeStone(row, col, Stone.BLACK)) {
                            moveMade = true;
                            toPlayer1.writeInt(CONTINUE);
                            toPlayer2.writeInt(CONTINUE);

                            sendMove(toPlayer1, row, col);
                            sendMove(toPlayer2, row, col);

                            //checkAndSendCaptures(toPlayer1, toPlayer2);
                        } else {
                            System.out.println("Błąd logiczny przy placeStone dla P1");
                        }

                    } else {
                        System.out.println("Nieprawidłowy ruch gracza 1: " + row + ", " + col);
                    }

                }
                //-------Tura białego
                moveMade = false;
                while (!moveMade) {
                    int row = fromPlayer2.readInt();
                    int col = fromPlayer2.readInt();
                    if(gameBoard.checkMoveValidity(row, col, Stone.WHITE)){
                        if (gameBoard.placeStone(row, col, Stone.WHITE)) {
                            moveMade = true;
                            toPlayer2.writeInt(CONTINUE);
                            toPlayer1.writeInt(CONTINUE);
                            sendMove(toPlayer2, row, col);
                            sendMove(toPlayer1, row, col);

                            //checkAndSendCaptures(toPlayer1, toPlayer2);
                        } else {
                            System.out.println("Błąd logiczny przy placeStone dla P2");
                        }

                    }else {
                        System.out.println("Nieprawidłowy ruch gracza 2: " + row + ", " + col);
                    }

                }

            }
        }catch(IOException ex) {
            System.err.println("exeption");
        }


    }
//    private boolean isEnded(char c) {
//
//    }
    private void sendMove(DataOutputStream out, int row, int col) throws IOException {
        out.writeInt(row);
        out.writeInt(col);
    }
    private void checkAndSendCaptures(DataOutputStream out1, DataOutputStream out2) throws IOException{

    }
    
}
