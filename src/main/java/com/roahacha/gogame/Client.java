package com.roahacha.gogame;

import com.roahacha.gogame.Common.Board;
import com.roahacha.gogame.Common.GameBoard;
import com.roahacha.gogame.Common.Stone;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
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
    private GameBoard board;

    private boolean continueToPlay = true;
    private Scanner inputScanner = new Scanner(System.in);

    //czekam na system po jakiemu to wprowadzamy
    private int rowSelected;
    private int columnSelected;

    private Socket socket;
    private DataInputStream fromServer;
    private DataOutputStream toServer;


    private boolean waiting = true;

    public Client(){
        board = new GameBoard();

        board.connectPlayerBoards(new Board(), new Board());
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
    public static void main(String[] args) {
        new Client().connectToServer();
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


                //myTurn = true;
            }
            //if second player then game can start
            else if (player == PLAYER2) {
                mystone = Stone.WHITE;
                hisstone = mystone.oppositeStone();
                System.out.println("Jesteś GRACZEM 2 (Białe).");
                System.out.println("Oczekiwanie na ruch przeciwnika (Czarne)...");

            }
            printBoard();

            while (continueToPlay) {
                if (player == PLAYER1) {
                    doMove();
                    waitForPlayerAction();

                }
                else if (player == PLAYER2) {
                    waitForPlayerAction();
                    doMove();
                }
            }
        }
        catch (IOException ex) {
            System.err.println("Rozłączono z serwerem.");
        }
    }
    private void doMove() throws IOException{
        if (!continueToPlay) return;
        System.out.println("TWÓJ RUCH (" + (mystone == Stone.BLACK ? "●" : "○") + ")");
        boolean validLocalInput = false;
        int row = -1, col = -1;

        while (!validLocalInput){
            System.out.print("Podaj rząd i kolumnę (np. 3 4): ");
            try{
                if (inputScanner.hasNextInt()) {
                    row = inputScanner.nextInt();
                    col = inputScanner.nextInt();
                    if (board.checkMoveValidity(row, col, mystone)) {
                        validLocalInput = true;
                    } else {
                        System.out.println("Ruch niedozwolony (zajęte pole lub brak oddechu). Spróbuj ponownie.");
                    }
                } else {
                    inputScanner.next(); // wyczyszczenie bufora
                }


            }catch (Exception ex){
                System.out.println("Błąd wprowadzania. Podaj dwie liczby.");
                inputScanner.nextLine();
            }
        }
        toServer.writeInt(row);
        toServer.writeInt(col);

        int status = fromServer.readInt();
        receiveMoveUpdate(status, mystone);
    }
    private void waitForPlayerAction() throws IOException {
        if (!continueToPlay) return;
        System.out.println("Czekam na ruch przeciwnika...");

        int status = fromServer.readInt();

        receiveMoveUpdate(status, hisstone);
    }


    private void receiveMoveUpdate(int status, Stone stone) throws IOException {
        if (status == PLAYER1_WON){
            continueToPlay = false;
            if (mystone == Stone.BLACK) System.out.println("WYGRAŁEŚ! (Przeciwnik się poddał lub wygrałeś)");
            else System.out.println("PRZEGRAŁEŚ.");
        } else if (status == PLAYER2_WON) {
            continueToPlay = false;
            if (mystone == Stone.WHITE) System.out.println("WYGRAŁEŚ!");
            else System.out.println("PRZEGRAŁEŚ.");
        } else if (status == DRAW) {
            continueToPlay = false;
            System.out.println("REMIS.");
        } else {
            //dla status == continiue
            int r = fromServer.readInt();
            int c = fromServer.readInt();

            board.placeStone(r, c, stone);
            printBoard();


        }
    }
    private void printBoard() {
        System.out.println("\n-----------------------------");
        System.out.println(board.toString());
        System.out.println("-----------------------------");
    }

}
