package org.example.view.templates;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.model.database.entity.Employee;
import javafx.scene.effect.GaussianBlur;

import java.util.function.Consumer;

public class AddEmployeeDialog {

    public static void show(Stage owner, Consumer<Employee> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL); // Blocks background
        modal.initStyle(StageStyle.TRANSPARENT); // Allows rounded corners & shadow

        javafx.scene.Node backgroundRoot = owner.getScene().getRoot();
        backgroundRoot.setEffect(new GaussianBlur(15)); // 15 is the blur radius

        // Remove the blur when the modal is closed (via X, Cancel, or Save)
        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 20, 0, 0, 10);"
        );
        root.setPrefWidth(450);

        // Header
        Label title = new Label("New Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Form Fields requested by leader
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

        // Inside AddEmployeeDialog.java -> the saveBtn.setOnAction block:

        saveBtn.setOnAction(e -> {
            saveBtn.setLoading(true);

            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}

                javafx.application.Platform.runLater(() -> {
                    // Validate (Example)
                    if (((javafx.scene.control.TextField)nameField.getControl()).getText().isEmpty()) {
                        nameField.setError("Name is required");
                        saveBtn.setLoading(false);
                        return;
                    }

                    // Extract values
                    String name = ((javafx.scene.control.TextField)nameField.getControl()).getText();
                    String surname = ((javafx.scene.control.TextField)surnameField.getControl()).getText();
                    String ageStr = ((javafx.scene.control.TextField)ageField.getControl()).getText();
                    String pos = ((javafx.scene.control.TextField)positionField.getControl()).getText();
                    String salStr = ((javafx.scene.control.TextField)salaryField.getControl()).getText();

                    // Handle conversions for YOUR specific Employee entity
                    int age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
                    java.math.BigDecimal salary = salStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(salStr);

                    // USING YOUR ENTITY CONSTRUCTOR
                    // (int companyId, String name, String surname, String email, Integer age, BigDecimal salary, String position, OffsetDateTime hiredAt)
                    org.example.model.database.entity.Employee newEmp = new org.example.model.database.entity.Employee(
                            1, // Default company ID for now
                            name,
                            surname,
                            name.toLowerCase() + "." + surname.toLowerCase() + "@company.com", // dummy email
                            age,
                            salary,
                            pos,
                            java.time.OffsetDateTime.now() // specific timestamp your backend wants
                    );

                    onSuccess.accept(newEmp);
                    ToastManager.showSuccess(owner, "Employee added successfully!");
                    modal.close();
                });
            }).start();
        });

        root.getChildren().addAll(title, nameField, surnameField, ageField, positionField, salaryField, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(null); // Required for transparent stage
        modal.setScene(scene);
        modal.showAndWait();
    }
}