package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color; // Added for Color.TRANSPARENT
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.database.entity.Employee;
import org.example.view.templates.StateButton;

import java.util.function.Consumer;

public class AddEmployeeDialog {

    public static void show(Stage owner, Consumer<Employee> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- CHAINED DARK BLUR EFFECT ON BACKGROUND ---
        javafx.scene.Node backgroundRoot = owner.getScene().getRoot();

        GaussianBlur blur = new GaussianBlur(30);
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.4);
        darken.setInput(blur);

        backgroundRoot.setEffect(darken);
        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        StackPane modalContainer = new StackPane();
        modalContainer.setStyle("-fx-background-color: transparent;");

        // --- THE FIX: ADD PADDING SO THE SHADOW DOES NOT CLIP ---
        // A shadow with radius 40 and offset 15 needs at least 55 pixels of breathing room!
        modalContainer.setPadding(new Insets(60));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setPrefWidth(450);
        root.setMaxWidth(450);

        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 0;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 40, 0, 0, 15);"
        );

        modalContainer.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        modalContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == modalContainer) {
                modal.close();
            }
        });

        // Header
        Label title = new Label("New Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Form Fields
        FormField nameField = FormField.createTextField("Name", "Name");
        FormField surnameField = FormField.createTextField("Surname", "Surname");
        FormField ageField = FormField.createNumericField("Age", "Age");
        FormField positionField = FormField.createTextField("Position", "e.g. Developer");
        FormField salaryField = FormField.createNumericField("Salary", "0.00");

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        StateButton cancelBtn = new StateButton("Cancel", StateButton.ButtonType.SECONDARY);
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        cancelBtn.setOnAction(e -> modal.close());

        saveBtn.setOnAction(e -> {
            saveBtn.setLoading(true);

            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}

                javafx.application.Platform.runLater(() -> {
                    if (((javafx.scene.control.TextField)nameField.getControl()).getText().isEmpty()) {
                        nameField.setError("Name is required");
                        saveBtn.setLoading(false);
                        ToastManager.showError(owner, "Please fill in all required fields.");
                        return;
                    }

                    ToastManager.showSuccess(owner, "Employee added successfully!");
                    modal.close();
                });
            }).start();
        });

        root.getChildren().addAll(title, nameField, surnameField, ageField, positionField, salaryField, buttonBox);

        Scene scene = new Scene(modalContainer);
        // THE FIX 2: Safely ensure the Scene itself is rendering completely transparently
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();
    }
}