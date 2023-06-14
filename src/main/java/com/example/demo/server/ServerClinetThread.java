package com.example.demo.server;

import java.io.*;
import java.net.Socket;

public class ServerClinetThread extends Thread {

    private final Socket socket;
    private final ServerInstance server;

    private final BufferedReader reader;
    private final BufferedWriter writer;

    public ServerClinetThread(Socket socket, ServerInstance server) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.server = server;
    }

    @Override
    public void run() {
        try {
            sendMessage("Hello from client");

            String message;
            while ((message = readFromClient()) != null) {
                // Akcje związane z klientem (np. wysłanie wiadomości do wszystkich klientów)
                System.out.println("received " + message);
                server.broadcast(message);
            }
        } catch (IOException e) {
            server.removeClient(this);
            cleanUpConnection();
            throw new RuntimeException(e);
        }
    }

    public String readFromClient() throws IOException {
        return this.reader.readLine();
    }

    private void cleanUpConnection() {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        this.writer.write(message);
        this.writer.newLine();
        this.writer.flush();
    }
}
