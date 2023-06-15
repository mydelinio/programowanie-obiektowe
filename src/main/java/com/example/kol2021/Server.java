package com.example.kol2021;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final List<ClientInstanceOnServer> clients = new ArrayList<>();

    final ServerSocket socket;

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    public static void broadcast(String message, ClientInstanceOnServer sender) {
        for (ClientInstanceOnServer client : clients) {
            if (client != sender) {
                client.sendMessage("BCAST;" + message);
            }
        }
    }

    public void listen() throws IOException {
        System.out.println("Server is running");
        while (true) {
            final Socket joinedClient = socket.accept();
            System.out.println("New client join: " + joinedClient);
            final ClientInstanceOnServer clientInstanceOnServer = new ClientInstanceOnServer(joinedClient);
            clients.add(clientInstanceOnServer);
            clientInstanceOnServer.start();
        }
    }

    public void startServer() {
        new Thread(() -> {
            try {
                listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
