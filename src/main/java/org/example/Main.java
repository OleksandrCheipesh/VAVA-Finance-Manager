package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.logging.AppLog;
import org.example.view.authentication.LoginView;
import org.example.view.authentication.RegistrationView;
import org.example.model.database.ConnectionProvider;
import org.example.model.database.DatabaseConfig;
import org.example.model.database.Migrations;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        new LoginView().show(stage);
    }

    public static void main(String[] args) {
        var logger = AppLog.getLogger(Main.class);

        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            logger.error("Unhandled exception in thread {}", thread.getName(), ex);
        });

        DatabaseConfig config = DatabaseConfig.fromClasspath("application.properties");
        try {
            Migrations.migrate(config);
        } catch (Exception e) {
            logger.error("Application startup aborted due to migration failure", e);
            System.exit(1);
        }

        ConnectionProvider.init(config);
        launch();
    }
}