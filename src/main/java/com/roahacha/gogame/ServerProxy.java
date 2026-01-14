package com.roahacha.gogame;

import java.io.*;
import java.net.Socket;

import com.roahacha.gogame.Common.GameAction;
import com.roahacha.gogame.Common.SocketFacade;
import com.roahacha.gogame.Common.Stone;

import javafx.application.Platform;


public class ServerProxy implements AutoCloseable {
    private static final int gridSize = 19;
    private final SocketFacade connection;
    private final GameClientObserver observer;

    public ServerProxy(SocketFacade connection, GameClientObserver observer) {
        this.connection = connection;
        this.observer = observer;
        new Thread(this::listenLoop).start();
    }

    public void sendAction(GameAction action) {
        connection.sendInt(action.getIndex());
    }

    public GameAction reciveAction() {
        int actionIndex = connection.readInt();
        return GameAction.fromIndex(actionIndex);
    }

    public void sendMove(int row, int col) {
        sendAction(GameAction.PLAYER_MOVE);
        connection.sendInt(row);
        connection.sendInt(col);
    }

    public Stone[][] reciveBoardState(int size) {
        Stone[][] grid = new Stone[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int stoneValue = connection.readInt();
                if (stoneValue == 0) {
                    grid[row][col] = Stone.NONE;
                } else if (stoneValue == 1) {
                    grid[row][col] = Stone.BLACK;
                } else if (stoneValue == 2) {
                    grid[row][col] = Stone.WHITE;
                }
            }
        }
        return grid;
    }

    private void listenLoop() {
        try {
            GameAction startAction = reciveAction();
            System.out.println(startAction);
            //GameAction startAction = reciveAction();

            Stone myStone;
            switch (startAction) {
                case GAME_STONE_BLACK:
                    myStone = Stone.BLACK;
                    break;
                case GAME_STONE_WHITE:
                    myStone = Stone.WHITE;
                    break;
                default:
                    myStone = null;
                    break;
            }
            Platform.runLater(() -> observer.onGameStart(myStone));

            while(true) {
                GameAction action = reciveAction();
                switch (action) {
                    case GAME_END_DRAW:
                    case GAME_END_WIN:
                    case GAME_END_LOSS:
                        Platform.runLater(() -> observer.onGameAction(action));
                        return;
                    case GAME_SEND_GRID:
                        Stone[][] grid = reciveBoardState(gridSize);
                        Platform.runLater(() -> observer.onBoardUpdate(grid));
                        break;
                    case GAME_YOUR_TURN:
                        // TODO: join with javafx to get move input
                        Platform.runLater(() -> observer.onGameAction(action));
                        break;
                    case GAME_STONE_BLACK:
                    case GAME_STONE_WHITE:
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> observer.onGameAction(GameAction.PLAYER_QUIT));
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
