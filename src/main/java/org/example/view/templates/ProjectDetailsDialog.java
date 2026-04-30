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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Project;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ProjectDetailsDialog {

    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static void show(Stage owner, Project project, Consumer<Project> onUpdate, Consumer<Project> onDelete) {
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
        root.setPadding(new Insets(35, 45, 35, 45));
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-radius: 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 40, 0, 0, 15);"
        );
        root.setPrefWidth(550);
        root.setMaxWidth(550);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(60));

        shadowWrapper.setOnMouseClicked(e -> {
            if (e.getTarget() == shadowWrapper) closeWithAnimation(modal, shadowWrapper);
        });

        // Header Section
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("⛙");
        icon.setStyle("-fx-background-color: " + Themes.PRIMARY + "33; -fx-text-fill: " + Themes.PRIMARY + "; -fx-font-size: 18px; -fx-padding: 10 12; -fx-background-radius: 12;");

        VBox headerTitles = new VBox(2);

        Label headerTop = new Label(I18n.t("PROJECT DETAILS"));
        headerTop.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        Label headerSub = new Label("ID: " + project.getName() + " • " + I18n.t("Strategic"));
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        headerTitles.getChildren().addAll(headerTop, headerSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

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
        HBox.setMargin(closeBtn, new Insets(0, -15, 0, 0));
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        header.getChildren().addAll(icon, headerTitles, spacer, closeBtn);

        // Inner Content Area
        VBox innerCard = new VBox(25);
        innerCard.setAlignment(Pos.TOP_CENTER);
        innerCard.setPadding(new Insets(20, 0, 20, 0));
        innerCard.setStyle("-fx-background-color: transparent;");

        // Title & Pill
        Label projName = new Label(project.getName());
        projName.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label pill = new Label(I18n.t("STRATEGIC INITIATIVE"));
        pill.setStyle("-fx-background-color: " + Themes.PRIMARY + "33; -fx-text-fill: " + Themes.PRIMARY + "; -fx-font-size: 10px; -fx-font-weight: 900; -fx-padding: 6 15; -fx-background-radius: 16;");

        Label desc = new Label(project.getDescription());
        desc.setWrapText(true);
        desc.setAlignment(Pos.CENTER);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-text-alignment: center; -fx-padding: 10 0;");

        // Stats Row
        HBox statsRow = new HBox(0);
        statsRow.setAlignment(Pos.CENTER);

        BigDecimal remaining = project.getBudgetLimit().subtract(project.getCurrentSpend());

        VBox col1 = createStatColumn(I18n.t("BUDGET LIMIT"),    currencyFormat.format(project.getBudgetLimit()),    Themes.TEXT_DARK);
        VBox col2 = createStatColumn(I18n.t("CURRENT SPEND"),   currencyFormat.format(project.getCurrentSpend()),   Themes.PRIMARY);
        VBox col3 = createStatColumn(I18n.t("REMAINING"),       currencyFormat.format(remaining),                   Themes.TEXT_DARK);

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

        Label pTop = new Label(I18n.t("BUDGET"));
        pTop.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        Label pValue = new Label(percent + "%");
        pValue.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        pTitles.getChildren().addAll(pTop, pValue);

        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);

        Label pStatus = new Label(I18n.t("On Frontend"));
        pStatus.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-alignment: bottom-right;");

        pHeader.getChildren().addAll(pTitles, pSpacer, pStatus);
        pHeader.setAlignment(Pos.BOTTOM_CENTER);

        HBox barTrack = new HBox();
        barTrack.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 6; -fx-pref-height: 8px;");

        Region barFill = new Region();
        barFill.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 6;");
        barFill.prefWidthProperty().bind(barTrack.widthProperty().multiply(Math.min(project.getSpendPercentage(), 1.0)));
        barTrack.getChildren().add(barFill);

        progressBox.getChildren().addAll(pHeader, barTrack);

        // Dates Section
        HBox datesRow = new HBox();
        datesRow.setPadding(new Insets(20, 0, 0, 0));

        VBox startBox = new VBox(5);
        Label sLbl = new Label(I18n.t("START DATE"));
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
        Label eLbl = new Label(I18n.t("END DATE"));
        eLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label eVal = new Label(project.getEndDate().format(dateFormatter));
        eVal.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        endBox.getChildren().addAll(eLbl, eVal);
        endBox.setAlignment(Pos.CENTER_RIGHT);

        datesRow.getChildren().addAll(startBox, dSpacer1, divider, dSpacer2, endBox);

        innerCard.getChildren().addAll(projName, pill, desc, statsRow, progressBox, datesRow);

        // Bottom Buttons
        HBox bottomButtons = new HBox(15);
        bottomButtons.setPadding(new Insets(10, 0, 0, 0));

        Button editBtn = new Button(I18n.t("Edit Project"));
        editBtn.setGraphic(IconFactory.getWhiteIcon("square-pen", 18));
        editBtn.setMinHeight(52);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setStyle(
                "-fx-background-color: " + Themes.PRIMARY + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: 900; " +
                        "-fx-background-radius: 12; " +
                        "-fx-cursor: hand;" +
                        "-fx-icon-text-gap: 10px;"
        );
        editBtn.setOnAction(e ->
                closeWithAnimation(modal, shadowWrapper, () ->
                        AddProjectDialog.show(owner, project, updatedProject -> onUpdate.accept(updatedProject))
                )
        );
        editBtn.setOnMousePressed(e -> { editBtn.setScaleX(0.98); editBtn.setScaleY(0.98); });
        editBtn.setOnMouseReleased(e -> { editBtn.setScaleX(1.0); editBtn.setScaleY(1.0); });

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(IconFactory.getIcon("trash", 24));
        deleteBtn.setMinSize(52, 52);

        String normalDeleteStyle = "-fx-background-color: #FEE2E2; -fx-background-radius: 12; -fx-cursor: hand;";
        String hoverDeleteStyle  = "-fx-background-color: #FECACA; -fx-background-radius: 12; -fx-cursor: hand;";
        deleteBtn.setStyle(normalDeleteStyle);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(hoverDeleteStyle));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(normalDeleteStyle));
        deleteBtn.setOnAction(e -> {
            closeWithAnimation(modal, shadowWrapper);
            onDelete.accept(project);
        });
        deleteBtn.setOnMousePressed(e -> { deleteBtn.setScaleX(0.95); deleteBtn.setScaleY(0.95); });
        deleteBtn.setOnMouseReleased(e -> { deleteBtn.setScaleX(1.0); deleteBtn.setScaleY(1.0); });

        bottomButtons.getChildren().addAll(editBtn, deleteBtn);

        root.getChildren().addAll(header, innerCard, bottomButtons);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper);
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
        closeWithAnimation(modal, animatedNode, null);
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode, Runnable onClosed) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> {
            modal.close();
            if (onClosed != null) onClosed.run();
        });
        exitAnimation.play();
    }
}