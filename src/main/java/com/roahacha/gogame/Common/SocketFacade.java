package com.roahacha.gogame.Common;

import java.io.*;
import java.net.Socket;

public class SocketFacade implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketFacade(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendInt(int value) {
        try {
            out.writeInt(value);
            out.flush();
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    public int readInt() {
        try {
            return in.readInt();
        } catch (IOException e) {
            handleConnectionError(e);
            return -1;
        }
    }

    private void handleConnectionError(IOException e) {
        System.err.println("Błąd połączenia: " + e.getMessage());
        throw new RuntimeException("Stracono połączenie", e);
    }

    @Override
    public void close() throws Exception {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
