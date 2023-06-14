package com.example.demo;

import com.example.demo.client.ServerConnectionThread;
import com.example.demo.server.ServerInstance;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Controller {

    @FXML
    public TextField addressField;
    @FXML
    public TextField portField;

    @FXML
    public ColorPicker colorPicker;

    @FXML
    public Slider radiusSlider;

    @FXML
    public Canvas canvas;

    private ServerConnectionThread serverConnectionThread;

    @FXML
    public void startServerAndCreateConnection() {
        try {
            final ServerInstance instance = new ServerInstance(getPort());
            instance.start();
            System.out.println("Server started on port " + getPort());

            final String address = addressField.getText();
            final int port = getPort();
            System.out.println("Server connecting to " + address + ":" + port);

            serverConnectionThread = new ServerConnectionThread(address, port, this);
            serverConnectionThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getPort() {
        return Integer.parseInt(portField.getText());
    }

    @FXML
    public void onConnectClicked() {
        if (serverConnectionThread != null) {
            System.out.println("Already connected to server");
            return;
        }

        final String address = addressField.getText();
        final int port = getPort();
        System.out.println("Connecting to " + address + ":" + port);

        try {
            serverConnectionThread = new ServerConnectionThread(address, port, this);
            serverConnectionThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onMouseClicked(MouseEvent event) {
        if (serverConnectionThread == null) {
            System.out.println("Not connected to server");
            return;
        }

        try {
            final Dot dot = new Dot(event.getX(), event.getY(), getRadius(), colorPicker.getValue());
            serverConnectionThread.sendToServer(dot.toMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawCircle(Dot dot) {
        final GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(dot.color());
        final double x = dot.x();
        final double y = dot.y();
        final double radius = dot.radius();
        context.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    private double getRadius() {
        return radiusSlider.getValue();
    }
}