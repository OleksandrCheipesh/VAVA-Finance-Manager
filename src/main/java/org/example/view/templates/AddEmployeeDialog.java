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
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.SessionManager;
import org.example.logging.AppLog;
import org.example.model.database.entity.Employee;

import java.util.function.Consumer;

public class AddEmployeeDialog {

    public static void show(Stage owner, Employee employeeToEdit, Consumer<Employee> onSuccess) {
        boolean isEditMode = (employeeToEdit != null);

        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        Paint originalFill = ownerScene.getFill();
        ownerScene.setFill(Color.web(Themes.TEXT_DARK));

        Node backgroundRoot = ownerScene.getRoot();
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.3);

        GaussianBlur blur = new GaussianBlur(15);
        blur.setInput(darken);
        backgroundRoot.setEffect(blur);

        modal.setOnHidden(e -> {
            backgroundRoot.setEffect(null);
            ownerScene.setFill(originalFill);
        });

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(450);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        shadowWrapper.setOnMouseClicked(e -> {
            if (e.getTarget() == shadowWrapper) closeWithAnimation(modal, shadowWrapper);
        });

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(isEditMode ? "Edit Employee" : "New Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // Form Fields
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("Name");
        nameField.setMinHeight(44);
        nameField.setPrefHeight(44);

        TextField surnameField = UIFactory.inputField("Surname");
        surnameField.setMinHeight(44);
        surnameField.setPrefHeight(44);

        HBox splitName = new HBox(15);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(surnameField, Priority.ALWAYS);

        splitName.getChildren().addAll(createLabeledField("NAME", nameField), createLabeledField("SURNAME", surnameField));

        TextField emailField = UIFactory.inputField("E-mail");
        emailField.setMinHeight(44);
        emailField.setPrefHeight(44);
        VBox emailBox = createLabeledField("E-MAIL", emailField);

        TextField roleField = UIFactory.inputField("Industry");
        roleField.setMinHeight(44);
        roleField.setPrefHeight(44);
        VBox roleBox = createLabeledField("ROLE", roleField);

        TextField deptField = UIFactory.inputField("Department");
        deptField.setMinHeight(44);
        deptField.setPrefHeight(44);

        ComboBox<String> statusCombo = UIFactory.inputComboBox("Active");
        statusCombo.getItems().addAll("Active", "Inactive", "Contractor");
        statusCombo.setValue("Active");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setPrefWidth(0);
        statusCombo.setMinHeight(44);
        statusCombo.setPrefHeight(44);
        statusCombo.setMaxHeight(44);

        GridPane splitBottom = new GridPane();
        splitBottom.setHgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(65);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(35);

        splitBottom.getColumnConstraints().addAll(col1, col2);

        splitBottom.add(createLabeledField("DEPARTMENT", deptField), 0, 0);
        splitBottom.add(createLabeledField("STATUS", statusCombo), 1, 0);

        form.getChildren().addAll(splitName, emailBox, roleBox, splitBottom);

        if (isEditMode) {
            nameField.setText(employeeToEdit.getName());
            surnameField.setText(employeeToEdit.getSurname());
            emailField.setText(employeeToEdit.getEmail());
            roleField.setText(employeeToEdit.getPosition());
            deptField.setText(employeeToEdit.getDepartment());
            statusCombo.setValue(employeeToEdit.getStatus());
        }

        // Save Button
        StateButton saveBtn = new StateButton(isEditMode ? "Update" : "Save", StateButton.ButtonType.PRIMARY);

        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setMinHeight(50);

        saveBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()) {
                nameField.setStyle(nameField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                return;
            }

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(600); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    Employee emp = isEditMode ? employeeToEdit : new Employee();

                    if (!isEditMode) emp.setCompanyId(SessionManager.getInstance().getCurrentCompanyId());

                    emp.setName(nameField.getText().trim());
                    emp.setSurname(surnameField.getText().trim());
                    emp.setEmail(emailField.getText().trim());
                    emp.setPosition(roleField.getText().trim());
                    emp.setDepartment(deptField.getText().trim());
                    emp.setStatus(statusCombo.getValue());

                    onSuccess.accept(emp);

                    closeWithAnimation(modal, shadowWrapper);
                });
            }).start();
        });

        root.getChildren().addAll(header, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(AddEmployeeDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(AddEmployeeDialog.class);
            logger.warn("Could not load global.css for AddEmployeeDialog: {}", e.getMessage());
        }

        modal.setScene(scene);

        // Entrance Animation
        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        modal.show();
        entrance.play();
    }

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");

        VBox box = new VBox(5, label, field);

        box.setMaxWidth(Double.MAX_VALUE);
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