package com.roahacha.gogame;

import com.roahacha.gogame.Common.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javafx.application.Application;

public class Client extends Application implements GameClientObserver {
    final int port = 12543;
    final int gridSize;
    final String host = "localhost";

    private ServerProxy server;
    private Board board;
    private Stone myStone;
    //private boolean myTurn = true;

    @Override
    public void start(Stage stage) {
        board = new Board();
        gridSize = board.getSize();
        // TODO: GUI

        try {
            server = new ServerProxy(new SocketFacade(new Socket(host, port)), this);
        } catch (IOException e) {
            System.err.println("Nie można połączyć się z serwerem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onGameStart(GameAction action) {
        switch (action) {
            case GAME_STONE_BLACK:
                myStone = Stone.BLACK;
                //myTurn = true;
                break;
            case GAME_STONE_WHITE:
                myStone = Stone.WHITE;
                //myTurn = false;
                break;
            default:
                break;
        }
        // TODO: Update GUI
    }

    @Override
    public void onStonePlaced(int row, int col, Stone stone) {
        // TODO: Update GUI
    }

    @Override
    public void onBoardUpdate(Stone[][] grid) {
        board.updateGrid(grid);
        // TODO: Update GUI
    }

    @Override
    public void onGameAction(GameAction action) {
        switch (action) {
            case GAME_END_WIN:
                // TODO: Update GUI
                break;
            case GAME_END_LOSS:
                // TODO: Update GUI
                break;
            case GAME_END_DRAW:
                // TODO: Update GUI
                break;
            case GAME_YOUR_TURN:
                //myTurn = true;
                // TODO: Update GUI
                break;
        }
    }

    @Override
    public void stop() throws Exception {
        if (server != null) {
            server.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
