package com.example.demo4;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;

public class Controller {

    @FXML
    public ListView<String> wordList = new ListView<>();

    @FXML
    public Label wordCountLabel = new Label();

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
    public void onConnectClicked(ActionEvent actionEvent) {
        try {
            ThreadManage manage = new ThreadManage("localhost", 5000,this );
            manage.start();
            } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendWord(String word) {
        Platform.runLater(() -> {
            wordList.getItems().add(word);
            ++wordCount;
            wordCountLabel.setText(String.valueOf(wordCount));
        });

    }
}

