package com.roahacha.gogame;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server extends JFrame {
    final int port = 12543;

    public static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;

    public static void main(String[] args) {
        Server display = new Server();
    }


    public Server() {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        setSize(550, 300);
        setTitle("Gogame server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            textArea.append(new Date() + ":     Server started at socket"+port+"\n");
            int sessionNum = 1;
            while (true) {
                textArea.append(new Date() + ":     Waiting for players to join session " + sessionNum + "\n");

                //connection to player1
                Socket firstPlayer = serverSocket.accept();
                textArea.append(new Date() + ":     Player 1 joined session " + sessionNum + ". Player 1's IP address " + firstPlayer.getInetAddress().getHostAddress() + "\n");

                //connection to player2
                Socket secondPlayer = serverSocket.accept();
                textArea.append(new Date() + ":     Player 2 joined session " + sessionNum + ". Player 2's IP address " + secondPlayer.getInetAddress().getHostAddress() + "\n");


                //starting the thread for two players
                textArea.append(new Date() + ":     Starting a thread for session " + sessionNum++ + "...\n");
                GameSession task = new GameSession(firstPlayer, secondPlayer);
                Thread t1 = new Thread(task);
                t1.start();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }
}
