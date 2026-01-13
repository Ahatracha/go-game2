package com.roahacha.gogame;

import com.roahacha.gogame.Common.Board;
import com.roahacha.gogame.Common.GameBoard;
import com.roahacha.gogame.Common.Stone;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame implements Runnable {
    final int port = 12543;
    final String host = "localhost";

    //statusy gry
    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    public static final int PLAYER1_WON = 1;
    public static final int PLAYER2_WON = 2;
    public static final int DRAW = 3;
    public static final int CONTINUE = 4;



    private boolean myTurn = false;
    private Stone mystone = Stone.NONE;
    private Stone hisstone = Stone.NONE;
    Board board;
    //private GameBoard board;

    private boolean continueToPlay = true;
    //private Scanner inputScanner = new Scanner(System.in);

    private GUI guiBoard;


    //czekam na system po jakiemu to wprowadzamy
    private int rowSelected;
    private int columnSelected;

    private Socket socket;
    private DataInputStream fromServer;
    private DataOutputStream toServer;


    private boolean waiting = true;

    public Client(GUI gui){
        board = new GameBoard();
        this.guiBoard = gui;

        //board.connectPlayerBoards(new Board(), new Board());
    }
    private void connectToServer() {
        try {
            System.out.println("Łączenie z serwerem...");
            socket = new Socket(host, port);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException ex) {
            System.err.println(ex);
        }

        Thread thread = new Thread(this);
        thread.start();
    }
//    public static void main(String[] args) {
//        new Client().connectToServer();
//    }
    public void sendMove(int row, int col) {
        if (myTurn && continueToPlay) {
            try {
                toServer.writeInt(row);
                toServer.writeInt(col);
                toServer.flush();
            } catch (IOException ex) {
                System.err.println("Błąd wysyłania ruchu: " + ex);
            }
        }
    }

    @Override
    public void run() {
        try {
            //read which player
            int player = fromServer.readInt();

            //if first player set the token to X and wait for second player to join
            if(player == PLAYER1) {
                mystone = Stone.BLACK;
                hisstone = mystone.oppositeStone();
                System.out.println("Jesteś GRACZEM 1 (Czarne). Oczekiwanie na drugiego gracza...");


                //notification that player 2 joined
                fromServer.readInt();
                System.out.println("Gracz 2 dołączył. Gra się rozpoczyna!");
                System.out.println("Twoja tura (Czarne).");
                myTurn = true;


            }
            //if second player then game can start
            else if (player == PLAYER2) {
                mystone = Stone.WHITE;
                hisstone = mystone.oppositeStone();
                System.out.println("Jesteś GRACZEM 2 (Białe).");
                System.out.println("Oczekiwanie na ruch przeciwnika (Czarne)...");
                myTurn = false;
            }

            while (continueToPlay) {
                int status = fromServer.readInt();
                receiveMoveUpdate(status);
            }
        } catch (IOException ex) {
            System.err.println("Rozłączono z serwerem.");
        }


    }

    private void waitForPlayerAction() throws IOException {
        if (!continueToPlay) return;
        System.out.println("Czekam na ruch przeciwnika...");

        int status = fromServer.readInt();

        receiveMoveUpdate(status);
    }


    private void receiveMoveUpdate(int status) throws IOException {
        if (status == CONTINUE) {
            int r = fromServer.readInt();
            int c = fromServer.readInt();

            Stone stoneToPlace = (myTurn) ? mystone : hisstone;

            board.placeStone(r, c, stoneToPlace);

            Platform.runLater(() -> {
                Color color = (stoneToPlace == Stone.BLACK) ? Color.BLACK : Color.WHITE;
                guiBoard.MakeColor(r, c, color);
            });

            myTurn = !myTurn;
            System.out.println("Ruch na: " + r + ", " + c + " (" + stoneToPlace + ")");

        } else if (status == PLAYER1_WON || status == PLAYER2_WON || status == DRAW) {
            continueToPlay = false;
            System.out.println("Koniec gry. Status: " + status);
        }
    }
    public boolean isMyTurn() { return myTurn; }

}
