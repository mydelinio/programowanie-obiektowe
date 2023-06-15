package com.example.demo4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadManage extends Thread {

    private final Socket socket;
    private final Controller controller;

    public ThreadManage(String address, int port, Controller controller) throws IOException {
        this.socket = new Socket(address, port);
        this.controller = controller;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = reader.readLine()) != null) {
                controller.appendWord(message);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
