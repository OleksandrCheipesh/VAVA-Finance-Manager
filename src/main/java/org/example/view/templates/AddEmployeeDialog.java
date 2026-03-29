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
import org.example.model.database.entity.Employee;
import org.example.model.database.service.validation.EmployeeValidator;
import org.example.view.templates.StateButton;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class AddEmployeeDialog {

    public static void show(Stage owner, Consumer<Employee> onSuccess) {
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
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(450);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // Header & 'X' Close Button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("New Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: " + Themes.BG_FIELD + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0;"
        );

        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);

        // --- Form Fields utilizing UIFactory for the green style ---
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("Enter name");
        VBox nameBox = createLabeledField("Name", nameField);

        TextField surnameField = UIFactory.inputField("Enter surname");
        VBox surnameBox = createLabeledField("Surname", surnameField);

        // Split Name and Surname into one row
        HBox splitName = new HBox(15);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        HBox.setHgrow(surnameBox, Priority.ALWAYS);
        splitName.getChildren().addAll(nameBox, surnameBox);

        TextField positionField = UIFactory.inputField("e.g. Developer");
        VBox positionBox = createLabeledField("Position", positionField);

        TextField ageField = UIFactory.inputField("Age");
        VBox ageBox = createLabeledField("Age", ageField);

        TextField salaryField = UIFactory.inputField("0.00");
        VBox salaryBox = createLabeledField("Salary", salaryField);

        // Split Age and Salary into one row
        HBox splitDetails = new HBox(15);
        HBox.setHgrow(ageBox, Priority.ALWAYS);
        HBox.setHgrow(salaryBox, Priority.ALWAYS);
        splitDetails.getChildren().addAll(ageBox, salaryBox);

        form.getChildren().addAll(splitName, positionBox, splitDetails);

        // ONLY Save Button (Cancel removed)
        StateButton saveBtn = new StateButton("Save", StateButton.ButtonType.PRIMARY);
        saveBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty()) {
                nameField.setStyle(nameField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                return;
            }

            saveBtn.setLoading(true);
            new Thread(() -> {
                try { Thread.sleep(800); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> {
                    try {
                        String name = nameField.getText();
                        String surname = surnameField.getText();
                        int age = ageField.getText().isEmpty() ? 0 : Integer.parseInt(ageField.getText());
                        String pos = positionField.getText();
                        BigDecimal salary = salaryField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(salaryField.getText());

                        Employee newEmp = new Employee(1, name, surname, name.toLowerCase() + "@company.com", age, salary, pos, OffsetDateTime.now());
                        EmployeeValidator.validate(newEmp);
                        onSuccess.accept(newEmp);
                        ToastManager.showSuccess(owner, "Employee added successfully!");
                        closeWithAnimation(modal, shadowWrapper);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        ageField.setStyle(ageField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                        salaryField.setStyle(salaryField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                    }
                });
            }).start();
        });

        root.getChildren().addAll(header, form, saveBtn);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);
        modal.setScene(scene);

        // Animations
        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
        slideUp.setToY(0);

        ParallelTransition entranceAnimation = new ParallelTransition(fadeIn, slideUp);

        modal.show();
        entranceAnimation.play();
    }

    private static VBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 13px;");
        return new VBox(5, label, field);
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }
}