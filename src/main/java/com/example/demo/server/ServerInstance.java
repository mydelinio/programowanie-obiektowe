package com.example.demo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerInstance extends Thread {

    private final List<ServerClinetThread> clients = new ArrayList<>();

    private final ServerSocket server;

    public ServerInstance(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Socket socket = server.accept();
                final ServerClinetThread client = new ServerClinetThread(socket, this);
                addClient(client);
                client.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addClient(final ServerClinetThread client) {
        this.clients.add(Objects.requireNonNull(client, "client cannot be null"));
    }

    public void removeClient(final ServerClinetThread client) {
        this.clients.remove(Objects.requireNonNull(client, "client cannot be null"));
    }

    public void broadcast(final String message) throws IOException {
        for (ServerClinetThread client : this.clients) {
            client.sendMessage(message);
        }
    }
}
