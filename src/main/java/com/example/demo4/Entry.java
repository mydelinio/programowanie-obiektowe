package com.example.demo4;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Entry {
    private String message;
    private LocalTime time;

    public Entry(String message, LocalTime time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + message;
    }
}
