package com.example.kol2021;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ListenOnClient implements Runnable {

    private final Socket client;
    private final Canvas canvas;
    private final HelloController controller;
    private String selectedColor;

    public ListenOnClient(Socket client, Canvas canvas, HelloController helloController) {
        this.client = client;
        this.canvas = canvas;
        this.controller = helloController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String message = in.readLine();
                String jsonData = message.split(";")[1];
                final JSONObject data = new JSONObject(jsonData);

                if (data.has("color")) {
                    this.selectedColor = data.getString("color");
                    canvas.getGraphicsContext2D().setStroke(Color.valueOf(this.selectedColor));
                }

                double xStart = 0.0;
                double yStart = 0.0;
                double xEnd = 0.0;
                double yEnd = 0.0;

                if (data.has("x_start") && data.has("y_start") && data.has("x_end") && data.has("y_end")) {
                    xStart = data.getDouble("x_start");
                    yStart = data.getDouble("y_start");
                    xEnd = data.getDouble("x_end");
                    yEnd = data.getDouble("y_end");
                }

                canvas.getGraphicsContext2D().strokeLine(xStart, yStart, xEnd, yEnd);
                controller.addLine(xStart, yStart, xEnd, yEnd, this.selectedColor == null ? "#000" : this.selectedColor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
