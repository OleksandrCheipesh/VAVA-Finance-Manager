package org.example.view.templates;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


// Base abstract class for all VIEWS -- DO NOT CHANGE !!!
public abstract class BaseView {

    protected Stage stage;
    protected Scene scene;

    // Template method -- the order of method calls is FIXED
    public final void show(Stage stage) {
        this.stage = stage;
        setContent();
        setStyle();
        setLogic();
        applyScene();
    }

    // NECESSARY to IMPLEMENT!!!
    // getContent -- all view elements (PRE-INITIALIZED in the class BODY !!!)
    // getStyles -- the entire visual appearance of the view is defined
    // getLogic -- the main button actions, ViewModel calls, and other
    //
    // CONTENT
    protected abstract void setContent();
    // STYLES
    protected abstract void setStyle();
    // LOGIC
    protected abstract void setLogic();

    // Base logic for all Views -- DO NOT CHANGE AND OVERWRITE !!!
    private void applyScene() {
        stage.setScene(scene);
        stage.show();

        // get size of screen
        javafx.geometry.Rectangle2D screenBounds =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.setMaximized(true);
    }

    // View Title
    protected Label buildContentTitle(String text) {
        Label title = new Label(text);
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #1A1F2B;");
        return title;
    }

    // Creates Sidebar
    protected void buildSidebar(BorderPane root) {
        Sidebar sidebar = new Sidebar(stage);

        root.setLeft(sidebar.getSidebar());
    }

    // A helper that can be used by all views for quick navigation between views
    // Just specify the target CLASS to navigate to !!!
    protected void navigateTo(BaseView view) {
        view.show(stage);
    }
}