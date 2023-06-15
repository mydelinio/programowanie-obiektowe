package com.example.kol2021;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class HelloController {

    private static final double TRANSLATION_AMOUNT = 10.0;

    private final ArrayList<Line> lines = new ArrayList<>();

    @FXML
    public Canvas canvas;
    @FXML
    public ColorPicker color;
    @FXML
    public Label offset;

    private Socket clientsocket;

    private double offsetX, offsetY;

    @FXML
    private void initialize() {
        canvas.setOnKeyPressed(this::translateCanvas);
        canvas.requestFocus();
    }

    @FXML
    public void startServer() throws IOException {
        final Server server = new Server(5555);
        server.startServer();
    }

    @FXML
    public void joinToServer() throws IOException {
        Socket socket = new Socket("localhost", 5555);
        this.clientsocket = socket;
        System.out.println("Exec join to server from client");
        new Thread(new ListenOnClient(socket, canvas, this)).start();
    }

    @FXML
    public void translateCanvas(KeyEvent ev) {
        if (!ev.getCode().isArrowKey()) {
            return;
        }
        final LineTranslation translation = determineLineTranslation(ev);
        ev.consume();
        if (translation == null) {
            return;
        }

        lines.forEach(line -> applyTranslation(translation, line));

        drawsLines();
    }

    @FXML
    public void insertPoints() {
        final AtomicInteger clickCount = new AtomicInteger(0);

        final double[] startX = {0};
        final double[] startY = {0};

        canvas.setOnMouseClicked((e) -> handleDrawLine(clickCount, startX, startY, e));
    }

    @FXML
    public void changeColor() throws IOException {
        final JSONObject colorObject = new JSONObject().put("color", color.getValue());
        sendToServer(colorObject.toString());
    }

    public void addLine(double startX, double startY, double endX, double endY, String color) {
        final Line line = new Line(startX, startY, endX, endY, color);
        lines.add(line);
        System.out.println("add lines");
        drawsLines();
    }

    private void sendToServer(String message) throws IOException {
        if (this.clientsocket != null) {
            PrintWriter out = new PrintWriter(clientsocket.getOutputStream(), true);
            out.println(message);
        }
    }

    private void applyTranslation(LineTranslation translation, Line line) {
        if (Direction.Y.equals(translation.direction)) {
            line.setStartY(line.getStartY() + translation.value);
            line.setEndY(line.getEndY() + translation.value);
            this.offsetY += translation.value;
        } else {
            line.setStartX(line.getStartX() + translation.value);
            line.setEndX(line.getEndX() + translation.value);
            this.offsetX += translation.value;
        }
        offset.setText(String.format("(X,Y) = (%d,%d)",(int)offsetX, (int)offsetY));
    }

    private LineTranslation determineLineTranslation(KeyEvent ev) {
        return switch (ev.getCode()) {
            case UP -> new LineTranslation(Direction.Y, -TRANSLATION_AMOUNT);
            case DOWN -> new LineTranslation(Direction.Y, TRANSLATION_AMOUNT);
            case LEFT -> new LineTranslation(Direction.X, -TRANSLATION_AMOUNT);
            case RIGHT -> new LineTranslation(Direction.X, TRANSLATION_AMOUNT);
            default -> null;
        };
    }

    private void handleDrawLine(AtomicInteger clickCount, double[] startX, double[] startY, MouseEvent e) {
        if (clickCount.get() == 0) {
            startX[0] = e.getX();
            startY[0] = e.getY();
            clickCount.getAndIncrement();
        } else if (clickCount.get() == 1) {
            double endX = e.getX();
            double endY = e.getY();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("x_start", startX[0]);
            jsonObject.put("y_start", startY[0]);
            jsonObject.put("x_end", endX);
            jsonObject.put("y_end", endY);
            addLine(startX[0], startY[0], endX, endY, color.getValue().toString());
            try {
                sendToServer(jsonObject.toString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            clickCount.set(0);
        }
    }

    private void drawsLines() {
        final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Line line : lines) {
            graphicsContext2D.setStroke(Color.valueOf(line.getColor()));
            graphicsContext2D.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    enum Direction {
        X, Y
    }

    record LineTranslation(Direction direction, double value) {
    }
}
