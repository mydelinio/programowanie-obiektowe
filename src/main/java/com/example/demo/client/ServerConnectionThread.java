package com.example.demo.client;

import com.example.demo.Controller;
import com.example.demo.Dot;

import java.io.*;
import java.net.Socket;

public class ServerConnectionThread extends Thread {

    private final Socket serverSocket;

    private final BufferedReader reader;
    private final PrintWriter writer;

    private final Controller controller;

    public ServerConnectionThread(String address, int port, Controller controller) throws IOException {
        this.serverSocket = new Socket(address, port);
        this.reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        this.writer = new PrintWriter(serverSocket.getOutputStream(), true);
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = readFromServer()) != null) {
                System.out.println("received " + message);
                controller.drawCircle(Dot.fromMessage(message));
            }
        } catch (IOException e) {
            cleanUpConnection();
            throw new RuntimeException(e);
        }
    }

    public void sendToServer(String message) throws IOException {
        this.writer.println(message);
    }

    public String readFromServer() throws IOException {
        return this.reader.readLine();
    }

    private void cleanUpConnection() {
        try {
            if (serverSocket != null) serverSocket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
