package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Employee;
import org.example.view.templates.StateButton;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class AddEmployeeDialog {

    // Pass null for employeeToEdit if creating a NEW employee.
    public static void show(Stage owner, Employee employeeToEdit, Consumer<Employee> onSuccess) {
        boolean isEditMode = (employeeToEdit != null);

        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        javafx.scene.Node backgroundRoot = owner.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(30);
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.4);
        darken.setInput(blur);
        backgroundRoot.setEffect(darken);

        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        StackPane modalContainer = new StackPane();
        modalContainer.setStyle("-fx-background-color: transparent;");
        modalContainer.setPadding(new Insets(60));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 40, 0, 0, 15);");
        root.setPrefWidth(450);
        root.setMaxWidth(450);

        modalContainer.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        modalContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == modalContainer) closeWithAnimation(modal, root);
        });

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(isEditMode ? "Edit Employee" : "New Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle("-fx-background-color: " + Themes.BG_FIELD + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, root));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // Form Fields
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("Name");
        TextField surnameField = UIFactory.inputField("Surname");
        HBox splitName = new HBox(15);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);
        splitName.getChildren().addAll(createLabeledField("Name", nameField), createLabeledField("Surname", surnameField));

        TextField emailField = UIFactory.inputField("E-mail");
        VBox emailBox = createLabeledField("E-mail", emailField);

        TextField roleField = UIFactory.inputField("Industry");
        VBox roleBox = createLabeledField("Role", roleField);

        TextField deptField = UIFactory.inputField("Department");

        // Status ComboBox
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Active", "Inactive", "Contractor");
        statusCombo.setValue("Active");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setStyle("-fx-background-color: " + Themes.BG_FIELD_LARGE + "; -fx-border-color: " + Themes.BORDER_LARGE + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 14px; -fx-padding: 5;");

        HBox splitBottom = new HBox(15);
        HBox.setHgrow(deptField, Priority.ALWAYS);
        HBox.setHgrow(statusCombo, Priority.ALWAYS);
        splitBottom.getChildren().addAll(createLabeledField("Department", deptField), createLabeledField("Status", statusCombo));

        form.getChildren().addAll(splitName, emailBox, roleBox, splitBottom);

        // Pre-fill if editing
        if (isEditMode) {
            nameField.setText(employeeToEdit.getName());
            surnameField.setText(employeeToEdit.getSurname());
            emailField.setText(employeeToEdit.getEmail());
            roleField.setText(employeeToEdit.getPosition());
            deptField.setText(employeeToEdit.getDepartment());
            statusCombo.setValue(employeeToEdit.getStatus());
        }

        // Save Button
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                nameField.setStyle(nameField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                return;
            }
            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    Employee emp = isEditMode ? employeeToEdit : new Employee(1, "", "", "", 0, BigDecimal.ZERO, "", OffsetDateTime.now());
                    emp.setName(nameField.getText());
                    emp.setSurname(surnameField.getText());
                    emp.setEmail(emailField.getText());
                    emp.setPosition(roleField.getText());
                    emp.setDepartment(deptField.getText());
                    emp.setStatus(statusCombo.getValue());

                    onSuccess.accept(emp);
                    ToastManager.showSuccess(owner, isEditMode ? "Employee updated!" : "Employee added!");
                    closeWithAnimation(modal, root);
                });
            }).start();
        });

        root.getChildren().addAll(header, form, saveBtn);
        Scene scene = new Scene(modalContainer);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        root.setOpacity(0);
        root.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), root);
        slideUp.setToY(0);

        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");
        VBox box = new VBox(5, label, field);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);

        ParallelTransition exit = new ParallelTransition(fadeOut, slideDown);
        exit.setOnFinished(e -> modal.close());
        exit.play();
    }
}