package com.roahacha.gogame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int port = 12543;
    private static Server instance;

    private Server() {}

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public static void main(String[] args) {
        Server.getInstance().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started at socket " + port);

            while (true) {
                System.out.println("Waiting for players to join...");

                //connection to player1
                Socket firstPlayer = serverSocket.accept();
                System.out.println("Player 1 joined. Player 1's IP address: " + firstPlayer.getInetAddress().getHostAddress());

                //connection to player2
                Socket secondPlayer = serverSocket.accept();
                System.out.println("Player 2 joined. Player 2's IP address: " + secondPlayer.getInetAddress().getHostAddress());

                GameSession task = new GameSession(firstPlayer, secondPlayer);
                Thread t1 = new Thread(task);
                t1.start();
            }
        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
}
