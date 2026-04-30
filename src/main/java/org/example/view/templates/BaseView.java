package org.example.view.templates;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        scene.setFill(javafx.scene.paint.Color.web("#F5F7FA"));
        if (!stage.isShowing()) {
            javafx.geometry.Rectangle2D screenBounds =
                    javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
        } else {
            // Reuse existing scene to avoid white flash from native window repaint.
            // Just swap the root content and stylesheets.
            javafx.scene.Scene currentScene = stage.getScene();
            javafx.scene.Parent newRoot = scene.getRoot();
            // Detach root from new scene so it can be attached to the current one
            scene.setRoot(new javafx.scene.layout.Region());
            currentScene.getStylesheets().setAll(scene.getStylesheets());
            currentScene.setRoot(newRoot);
            // Update stage title from the new scene's stage configuration
            // (title is already set by setContent() before this method is called)
        }
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

    // Covers the main content area (header + content, NOT the sidebar) with a blur + dark overlay.
    // Sidebar stays interactive so the user can navigate away.
    // Call from setLogic() when the user lacks permission, then return immediately.
    protected void showAccessDenied() {
        BorderPane root = (BorderPane) scene.getRoot();
        javafx.scene.Node center = root.getCenter();
        center.setEffect(new GaussianBlur(12));

        ImageView lockIcon = IconFactory.getWhiteIcon("lock", 52);

        Label msg = new Label("Access Denied");
        msg.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: white;");

        Label sub = new Label("You don't have permission to view this screen.");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.65);");

        VBox content = new VBox(14, lockIcon, msg, sub);
        content.setAlignment(Pos.CENTER);

        StackPane overlay = new StackPane(content);
        overlay.setStyle("-fx-background-color: rgba(15,23,42,0.60);");

        // Wrapping center detaches it from root automatically in JavaFX
        StackPane wrapper = new StackPane(center, overlay);
        root.setCenter(wrapper);
    }
}