package com.roahacha.gogame;

import com.roahacha.gogame.Common.*;
import javafx.application.Platform;
import java.io.IOException;
import java.net.Socket;

public class Client implements GameClientObserver {
    final int port = 12543;
    final String host = "localhost";

    private ServerProxy server;
    private GUI gui; // Referencja do okna
    private Stone myStone;
    private boolean myTurn = false;

    // Konstruktor łączy logikę z widokiem
    public Client(GUI gui) {
        this.gui = gui;
    }

    //method which starts connection
    public void connectToServer() {
        try {
            Socket socket = new Socket(host, port);
            server = new ServerProxy(new SocketFacade(socket), this);
        } catch (IOException e) {
            System.err.println("Błąd połączenia: " + e.getMessage());
            e.printStackTrace();
            // Informujemy GUI o błędzie
            gui.updateInfo("Błąd połączenia!");
            gui.updateStatus("Nie można połączyć z serwerem.");
        }
    }

    // sending move
    public void sendMove(int row, int col) {
        // Pozwalamy na ruch tylko, jeśli mamy połączenie i jest nasza tura
        if (server != null && myTurn) {
            server.sendMove(row, col);

            myTurn = false;
            gui.updateStatus("Weryfikacja ruchu...");
        }
    }

    public void close() {
        try {
            if (server != null) server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // reaction for comunicats from server

    @Override
    public void onGameStart(Stone myColor) {
        this.myStone = myColor;
        this.myTurn = (myColor == Stone.BLACK); // Czarne zaczynają

        gui.updateInfo("Grasz jako: " + (myColor == Stone.BLACK ? "CZARNE" : "BIAŁE"));
        gui.updateStatus(myTurn ? "Twoja tura! Postaw kamień." : "Ruch przeciwnika...");
    }

    @Override
    public void onStonePlaced(int row, int col, Stone stone) {

    }

    @Override
    public void onBoardUpdate(Stone[][] grid) {
        // Upadeting board
        gui.refreshBoard(grid);
    }

    @Override
    public void onGameAction(GameAction action) {
        // scenarios
        Platform.runLater(() -> {
            switch (action) {
                case GAME_YOUR_TURN:
                    myTurn = true;
                    gui.updateStatus("TWOJA TURA! Wykonaj ruch.");
                    break;

                case GAME_INCORRENT_MOVE:
                    myTurn = true;
                    gui.updateStatus("Ruch niedozwolony! Spróbuj w innym miejscu.");
                    break;

                case GAME_END_WIN:
                    myTurn = false;
                    gui.updateStatus("WYGRANA!");
                    gui.showEndMessage("Gratulacje! Wygrałeś grę.");
                    break;

                case GAME_END_LOSS:
                    myTurn = false;
                    gui.updateStatus("PRZEGRANA...");
                    gui.showEndMessage("Niestety, przegrałeś.");
                    break;

                case GAME_END_DRAW:
                    myTurn = false;
                    gui.updateStatus("REMIS.");
                    gui.showEndMessage("Gra zakończona remisem.");
                    break;

                case PLAYER_QUIT:
                    myTurn = false;
                    gui.updateStatus("Przeciwnik rozłączył się.");
                    gui.showEndMessage("Przeciwnik opuścił grę.");
                    break;

                default:
                    System.out.println("Nieobsłużona akcja: " + action);
                    break;
            }
        });
    }
}