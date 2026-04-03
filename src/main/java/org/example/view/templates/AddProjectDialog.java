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
import org.example.model.database.entity.Project;
import org.example.model.validation.EmpValExept;
import org.example.model.validation.EmployeeValidator;
import org.example.model.validation.ProjValExept;
import org.example.model.validation.ProjectValidator;
import org.example.view.templates.StateButton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Consumer;

import static org.example.model.validation.ProjValExept.ProjErrorCode.*;

public class AddProjectDialog {

    public static void show(Stage owner, Consumer<Project> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // Background Blur & Darken
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

        // Main Container
        StackPane modalContainer = new StackPane();
        modalContainer.setStyle("-fx-background-color: transparent;");
        modalContainer.setPadding(new Insets(60));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(500);

        modalContainer.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        modalContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == modalContainer) closeWithAnimation(modal, root);
        });

        // --- Header ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label title = new Label("Add New Project");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subTitle = new Label("Define the scope and resources for your next initiative.");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subTitle);

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, root));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // --- Form Fields ---
        VBox form = new VBox(15);

        TextField nameField = UIFactory.inputField("e.g., Global Expansion phase 1");
        VBox nameBox = createLabeledField("PROJECT NAME", nameField);

        // Custom Text Area for Description
        TextArea descField = new TextArea();
        descField.setPromptText("Describe the objectives and key outcomes...");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.setStyle("-fx-background-color: #F8FAFC; -fx-control-inner-background: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'System'; -fx-font-size: 14px;");
        VBox descBox = createLabeledField("DESCRIPTION", descField);

        TextField budgetField = UIFactory.inputField("$ 0.00");
        VBox budgetBox = createLabeledField("BUDGET LIMIT", budgetField);

        DatePicker startDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        VBox startBox = createLabeledField("START DATE", startDateField);

        DatePicker endDateField = UIFactory.inputDatePicker("mm/dd/yyyy");
        VBox endBox = createLabeledField("END DATE", endDateField);

        HBox splitDates = new HBox(15);
        HBox.setHgrow(startBox, Priority.ALWAYS);
        HBox.setHgrow(endBox, Priority.ALWAYS);
        splitDates.getChildren().addAll(startBox, endBox);

        form.getChildren().addAll(nameBox, descBox, budgetBox, splitDates);

        // --- Bottom Buttons ---
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> closeWithAnimation(modal, root));

        Region btnSpacer = new Region();
        HBox.setHgrow(btnSpacer, Priority.ALWAYS);

        StateButton saveBtn = new StateButton("Add New Project", StateButton.ButtonType.PRIMARY);
        saveBtn.setPrefWidth(250);

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
                        String desc = descField.getText();

                        // Strip out '$' or commas if user typed them
                        String budgetStr = budgetField.getText().replaceAll("[$,]", "");
                        BigDecimal budget = budgetStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(budgetStr);

                        LocalDate start = startDateField.getValue() != null ? startDateField.getValue() : LocalDate.now();
                        LocalDate end = endDateField.getValue() != null ? endDateField.getValue() : LocalDate.now().plusMonths(3);

                        Project newProj = new Project(SessionManager.getInstance().getCurrentCompanyId(), name, desc, budget, start, end, true);
                        try {
                            ProjectValidator.validate(newProj);
                        }catch (ProjValExept pe) {
                            saveBtn.setLoading(false);

                            String errorStyle = "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                                    "-fx-border-color: " + Themes.TEXT_ERROR + ";" +
                                    "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 10;" +
                                    "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";";

                            String normalStyle = "-fx-background-color: " + Themes.BG_FIELD_LARGE + ";" +
                                    "-fx-border-color: " + Themes.BORDER_LARGE + ";" +
                                    "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 10;" +
                                    "-fx-text-fill: " + Themes.TEXT_PRIMARY + ";";

                            nameField.setStyle(normalStyle);
                            budgetField.setStyle(normalStyle);
                            startDateField.setStyle(normalStyle);
                            endDateField.setStyle(normalStyle);

                            switch (pe.getCode()) {
                                case NAME_EMPTY -> nameField.setStyle(errorStyle);

                                case BUDGET_NEGATIVE,
                                     SPEND_EXCEEDS_BUDGET -> budgetField.setStyle(errorStyle);

                                case DATE_INVALID_RANGE -> {
                                    startDateField.setStyle(errorStyle);
                                    endDateField.setStyle(errorStyle);
                                }

                                case PROJECT_NULL -> {
                                    // вообще не должно случаться тут
                                }
                            }

                            ToastManager.showError(owner, pe.getMessage());
                            return;
                        }
                        onSuccess.accept(newProj);
                        closeWithAnimation(modal, root);
                    } catch (NumberFormatException ex) {
                        saveBtn.setLoading(false);
                        budgetField.setStyle(budgetField.getStyle() + "-fx-border-color: " + Themes.TEXT_ERROR + ";");
                        ToastManager.showError(owner, "Please enter a valid numeric budget.");
                    }
                });
            }).start();
        });

        buttonBox.getChildren().addAll(cancelBtn, btnSpacer, saveBtn);

        root.getChildren().addAll(header, form, buttonBox);

        Scene scene = new Scene(modalContainer);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        // Animation
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
        label.setStyle("-fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 11px;");
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