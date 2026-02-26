package org.example;

import javafx.application.Application;

import javafx.stage.Stage;
import org.example.view.authentication.RegistrationView;
// точка входу — окремий клас, більше нічого не робить
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        new RegistrationView().show(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}