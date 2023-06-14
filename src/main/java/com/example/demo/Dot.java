package com.example.demo;

import javafx.scene.paint.Color;

public record Dot(double x, double y, double radius, Color color) {
    public String toMessage() {
        return String.format("x:%f;y:%f;r:%f;c:%x", x, y, radius, color.hashCode());
    }

    public static Dot fromMessage(String message) {
        double x = 0, y = 0, rad = 0;
        Color color = null;
        for (String s : message.split(";")) {
            char definer = s.charAt(0);
            switch (definer) {
                case 'x' -> {
                    String[] values = s.split(":");
                    x = Double.parseDouble(values[1]);
                }
                case 'y' -> {
                    String[] values = s.split(":");
                    y = Double.parseDouble(values[1]);
                }
                case 'r' -> {
                    String[] values = s.split(":");
                    rad = Double.parseDouble(values[1]);
                }
                case 'c' -> {
                    String[] values = s.split(":");
                    color = Color.valueOf(values[1]);
                }
            }
        }
        return new Dot(x, y, rad, color);
    }
}
