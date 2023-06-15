package com.example.demo4;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.text.Collator;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    public ListView<String> wordList = new ListView<>();
@FXML
public TextField filterField;
    @FXML
    public Label wordCountLabel = new Label();
    List<String> strings= new ArrayList<>();
    private ObservableList<String> originalList = FXCollections.observableArrayList();

    private int wordCount = 0;

    @FXML
    public void onStartServerClicked(ActionEvent actionEvent) {
        WordBag wordBag=new WordBag();
        wordBag.populate();
        Server server = new Server(5000, wordBag);
        server.start();
        server.startSending();
    }
    @FXML
    public void initialize() {
        originalList.addAll(wordList.getItems());
    }
    @FXML
    public void onConnectClicked(ActionEvent actionEvent) {
        try {
            ThreadManage manage = new ThreadManage("localhost", 5000,this );
            manage.start();
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static String stripPolishCharacters(String input) {
        return input.replaceAll("[ęĘ]", "e")
                .replaceAll("[óÓ]", "o")
                .replaceAll("[ąĄ]", "a")
                .replaceAll("[śŚ]", "s")
                .replaceAll("[łŁ]", "l")
                .replaceAll("[żŻ]", "z")
                .replaceAll("[źŹ]", "z")
                .replaceAll("[ćĆ]", "c")
                .replaceAll("[ńŃ]", "n");
    }



    public void appendWord(String word) {
        String temp = String.valueOf(filterField.getCharacters());
        LocalTime currentTime = LocalTime.now();
        String newWord = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + word;
        strings.add(newWord);
        if (temp.equals("")) {
            Platform.runLater(() -> {
                List<String> filteredString = strings.stream()
                        .map(s -> stripPolishCharacters(s))
                        .sorted((o1, o2) -> {
                            String[] parts1 = o1.split(" ");
                            String[] parts2 = o2.split(" ");
                            return stripPolishCharacters(parts1[1]).compareTo(stripPolishCharacters(parts2[1]));
                        })
                        .collect(Collectors.toList());
                wordList.getItems().setAll(filteredString);
                ++wordCount;
                wordCountLabel.setText(String.valueOf(wordCount));
            });
        }
        else{
            Platform.runLater(() -> {
                List<String> filteredString = strings.stream()
                        .filter(s -> {
                            int spaceIndex = s.indexOf(" ");
                            if (spaceIndex != -1 && spaceIndex < s.length() - 1) {
                                String remainingSubstring = s.substring(spaceIndex + 1);
                                return remainingSubstring.startsWith(temp);
                            }
                            return false;
                        })
                        .map(s -> {
                            int spaceIndex = s.indexOf(" ");
                            String firstSubstring = s.substring(0, spaceIndex + 1);
                            return firstSubstring  + s.substring(spaceIndex + 1);
                        })
                        .map(s -> stripPolishCharacters(s))
                        .sorted((o1, o2) -> {
                            String[] parts1 = o1.split(" ");
                            String[] parts2 = o2.split(" ");
                            return stripPolishCharacters(parts1[1]).compareTo(stripPolishCharacters(parts2[1]));
                        })
                        .collect(Collectors.toList());
                wordList.getItems().setAll(filteredString);
                ++wordCount;
                wordCountLabel.setText(String.valueOf(wordCount));
            });
        }
        }
    }


