package com.roahacha.gogame.Common;

import java.io.IOException;
import java.net.Socket;

public class PlayerController implements AutoCloseable {
    private final SocketFacade connection;
    private final Stone stone;

    private final int CMD_MOVE = 1;
    private final int CMD_PASS = 2;
    private final int CMD_SURRENDER = 3;

    public PlayerController(Socket socket, Stone stone) throws IOException {
        this.connection = new SocketFacade(socket);
        this.stone = stone;
        if (stone == Stone.BLACK) {
            sendAction(GameAction.GAME_STONE_BLACK);
        } else {
            sendAction(GameAction.GAME_STONE_WHITE);
        }
    }

    public GameAction waitForDecision() {
        try {
            int commandId = connection.readInt();
            switch (commandId) {
                case CMD_MOVE:      return GameAction.PLAYER_MOVE;
                case CMD_PASS:      return GameAction.PLAYER_PASS;
                case CMD_SURRENDER: return GameAction.PLAYER_SURRENDER;
                default:            return GameAction.UNKNOWN;
            }
        } catch (Exception e) {
            return GameAction.PLAYER_QUIT;   // Błąd sieci = wyjście
        }
    }

    public void sendAction(GameAction action) {
        connection.sendInt(action.getIndex());
    }

    public GameAction reciveAction() {
        int actionIndex = connection.readInt();
        return GameAction.fromIndex(actionIndex);
    }

    public void sendBoardState(GameBoard board) {
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Stone stone = board.getStone(row, col);
                int stoneValue = (stone == null) ? 0 : (stone == Stone.BLACK ? 1 : 2);
                connection.sendInt(stoneValue);
            }
        }
    }

    public int[] reciveMove() {
        int x = connection.readInt();
        int y = connection.readInt();
        return new int[] {x, y};
    }

    // used once, at the start of the match
    public void sendStoneInfo(int playerId) {
        connection.sendInt(playerId);
    }

    public Stone getStone() {
        return stone;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
