module com.example.kol2021 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;

    opens com.example.kol2021 to javafx.fxml;
    exports com.example.kol2021;
}