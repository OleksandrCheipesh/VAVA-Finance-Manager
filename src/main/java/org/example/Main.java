package org.example;

import javafx.application.Application;

import javafx.stage.Stage;
import org.example.view.authentication.RegistrationView;
import org.example.model.database.ConnectionProvider;
import org.example.model.database.DatabaseConfig;
import org.example.model.database.Migrations;

// точка входу — окремий клас, більше нічого не робить
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        new RegistrationView().show(stage);
    }

    public static void main(String[] args) {
        DatabaseConfig config = DatabaseConfig.fromClasspath("application.properties");
        Migrations.migrate(config);
        ConnectionProvider.init(config);
        launch();
    }
}