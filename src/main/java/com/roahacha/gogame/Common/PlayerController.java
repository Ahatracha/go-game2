package com.roahacha.gogame.Common;

import com.roahacha.gogame.Common.*;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class PlayerController implements AutoCloseable {
    private final SocketFacade connection;
    private final Stone stone;

    private final int CMD_MOVE = 1;
    private final int CMD_PASS = 2;
    private final int CMD_SURRENDER = 3;

    public PlayerController(Socket socket, Stone stone) throws IOException {
        this.connection = new SocketFacade(socket);
        this.stone = stone;
    }

    public PlayerAction waitForDecision() {
        try {
            int commandId = connection.readInt();
            switch (commandId) {
                case CMD_MOVE:      return PlayerAction.MOVE;
                case CMD_PASS:      return PlayerAction.PASS;
                case CMD_SURRENDER: return PlayerAction.SURRENDER;
                default:            return PlayerAction.UNKNOWN;
            }
        } catch (Exception e) {
            return PlayerAction.QUIT;   // Błąd sieci = wyjście
        }
    }

    public int[] getMovePlace() {
        return connection.reciveMove();
    }

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
