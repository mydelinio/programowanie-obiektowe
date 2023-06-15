package com.example.demo4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new ArrayList<>();

    private WordBag wordBag;

    public Server(int port, WordBag wordBag) {
        this.wordBag = wordBag;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSending() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcast(wordBag.get());
            }
        }, 10, 100);
    }

    public void run(){
        while(true){
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                ClientThread thread = new ClientThread(clientSocket, this);
                clients.add(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeClient(ClientThread client) {
        clients.remove(client);
        System.out.println("removed");
    }

    public void broadcast(String message){
        for(var client : clients)
            client.send(message);
    }

}
