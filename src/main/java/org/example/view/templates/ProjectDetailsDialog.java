package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.SessionManager;
import org.example.model.database.entity.Project;
import org.example.model.validation.ProjValExept;
import org.example.model.validation.ProjectValidator;
import org.example.view.templates.StateButton;
import org.example.viewModel.ProjectsViewModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ProjectDetailsDialog {

    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static void show(Stage owner, Project project, Consumer<Project> onSuccess) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        // --- Background Blur & Darken ---
        Node backgroundRoot = owner.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(30);
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.4);
        darken.setInput(blur);
        backgroundRoot.setEffect(darken);

        modal.setOnHidden(e -> backgroundRoot.setEffect(null));

        // --- Container (Fixes Black Corners) ---
        StackPane modalContainer = new StackPane();
        modalContainer.setStyle("-fx-background-color: transparent;");
        modalContainer.setPadding(new Insets(60));

        // Outer Modal (Off-white background like Figma)
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-radius: 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 40, 0, 0, 15);"
        );
        root.setPrefWidth(550);
        root.setMaxWidth(550);

        modalContainer.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER);

        modalContainer.setOnMouseClicked(e -> {
            if (e.getTarget() == modalContainer) closeWithAnimation(modal, root);
        });

        // --- Header Section ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("⛙"); // Placeholder for Figma network icon
        icon.setStyle("-fx-background-color: #A7F3D0; -fx-text-fill: #047857; -fx-font-size: 18px; -fx-padding: 10 12; -fx-background-radius: 12;");

        VBox headerTitles = new VBox(2);
        Label headerTop = new Label("PROJECT DETAILS");
        headerTop.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label headerSub = new Label("ID: " + project.getName() + " • Strategic");
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        headerTitles.getChildren().addAll(headerTop, headerSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("X");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 16px;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, root));

        header.getChildren().addAll(icon, headerTitles, spacer, closeBtn);

        // --- Inner White Card ---
        VBox innerCard = new VBox(25);
        innerCard.setAlignment(Pos.TOP_CENTER);
        innerCard.setPadding(new Insets(40, 30, 40, 30));
        innerCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        // Title & Pill
        Label projName = new Label(project.getName());
        projName.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label pill = new Label("STRATEGIC INITIATIVE");
        pill.setStyle("-fx-background-color: #E6F7F6; -fx-text-fill: " + Themes.PRIMARY + "; -fx-font-size: 10px; -fx-font-weight: 900; -fx-padding: 6 15; -fx-background-radius: 16;");

        Label desc = new Label(project.getDescription());
        desc.setWrapText(true);
        desc.setAlignment(Pos.CENTER);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-text-alignment: center; -fx-padding: 10 20;");

        // Stats Row (Budget, Spend, Remaining)
        HBox statsRow = new HBox(0);
        statsRow.setAlignment(Pos.CENTER);

        BigDecimal remaining = project.getBudgetLimit().subtract(project.getCurrentSpend());

        VBox col1 = createStatColumn("BUDGET LIMIT", currencyFormat.format(project.getBudgetLimit()), Themes.TEXT_DARK);
        VBox col2 = createStatColumn("CURRENT SPEND", currencyFormat.format(project.getCurrentSpend()), Themes.PRIMARY);
        VBox col3 = createStatColumn("REMAINING", currencyFormat.format(remaining), Themes.TEXT_DARK);

        col1.prefWidthProperty().bind(innerCard.widthProperty().divide(3));
        col2.prefWidthProperty().bind(innerCard.widthProperty().divide(3));
        col3.prefWidthProperty().bind(innerCard.widthProperty().divide(3));
        statsRow.getChildren().addAll(col1, col2, col3);

        // Progress Bar Section
        VBox progressBox = new VBox(10);
        progressBox.setPadding(new Insets(15, 0, 0, 0));

        HBox pHeader = new HBox();
        int percent = (int)(project.getSpendPercentage() * 100);

        VBox pTitles = new VBox(0);
        Label pTop = new Label("BUDGET");
        pTop.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label pValue = new Label(percent + "%");
        pValue.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        pTitles.getChildren().addAll(pTop, pValue);

        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);

        Label pStatus = new Label("On Frontend");
        pStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-alignment: bottom-right;");

        pHeader.getChildren().addAll(pTitles, pSpacer, pStatus);
        pHeader.setAlignment(Pos.BOTTOM_CENTER);

        // Custom drawn bar
        HBox barTrack = new HBox();
        barTrack.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 6; -fx-pref-height: 8px;");
        Region barFill = new Region();
        barFill.setStyle("-fx-background-color: #047857; -fx-background-radius: 6;"); // Darker green for Figma match
        barFill.prefWidthProperty().bind(barTrack.widthProperty().multiply(Math.min(project.getSpendPercentage(), 1.0)));
        barTrack.getChildren().add(barFill);

        progressBox.getChildren().addAll(pHeader, barTrack);

        // Dates Section
        HBox datesRow = new HBox();
        datesRow.setPadding(new Insets(20, 0, 0, 0));

        VBox startBox = new VBox(5);
        Label sLbl = new Label("START DATE");
        sLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label sVal = new Label(project.getStartDate().format(dateFormatter));
        sVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        startBox.getChildren().addAll(sLbl, sVal);
        startBox.setAlignment(Pos.CENTER_LEFT);

        Region dSpacer1 = new Region(); HBox.setHgrow(dSpacer1, Priority.ALWAYS);
        Line divider = new Line(0, 0, 0, 30);
        divider.setStroke(Color.web("#E2E8F0"));
        Region dSpacer2 = new Region(); HBox.setHgrow(dSpacer2, Priority.ALWAYS);

        VBox endBox = new VBox(5);
        Label eLbl = new Label("END DATE");
        eLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label eVal = new Label(project.getEndDate().format(dateFormatter));
        eVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        endBox.getChildren().addAll(eLbl, eVal);
        endBox.setAlignment(Pos.CENTER_RIGHT);

        datesRow.getChildren().addAll(startBox, dSpacer1, divider, dSpacer2, endBox);

        innerCard.getChildren().addAll(projName, pill, desc, statsRow, progressBox, datesRow);

        // --- Bottom Buttons ---
        HBox bottomButtons = new HBox(15);

        StateButton editBtn = new StateButton("✏ Edit Budget", StateButton.ButtonType.PRIMARY);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 12; -fx-cursor: hand;");
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setOnAction(e -> System.out.println("Edit Budget clicked"));

//        Button deleteBtn = new Button("🗑");
        StateButton deleteBtn = new StateButton("🗑", StateButton.ButtonType.DANGER);

        deleteBtn.setOnAction(e -> {
            deleteBtn.setLoading(true);

            new Thread(() -> {
                try {
                    javafx.application.Platform.runLater(() -> {
                        onSuccess.accept(project);
                    });

                    Thread.sleep(300);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        deleteBtn.setLoading(false);
                        ToastManager.showError(owner, "Failed to delete project.");
                    });
                    return;
                }

                javafx.application.Platform.runLater(() -> {
                    deleteBtn.setLoading(false);
                    ToastManager.showSuccess(owner, "Project deleted successfully!");
                    closeWithAnimation(modal, root);
                });
            }).start();
        });

        deleteBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #EF4444; -fx-background-radius: 12; -fx-font-size: 18px; -fx-padding: 8 18; -fx-cursor: hand;");

        bottomButtons.getChildren().addAll(editBtn, deleteBtn);

        // Assemble Modal
        root.getChildren().addAll(header, innerCard, bottomButtons);

        Scene scene = new Scene(modalContainer);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        // Animations
        root.setOpacity(0);
        root.setTranslateY(30);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), root);
        slideUp.setToY(0);
        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    private static VBox createStatColumn(String title, String value, String valueColor) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label lblVal = new Label(value);
        lblVal.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + valueColor + ";");
        box.getChildren().addAll(lblTitle, lblVal);
        return box;
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