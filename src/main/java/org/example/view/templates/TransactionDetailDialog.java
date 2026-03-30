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
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.model.database.entity.Transaction;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionDetailDialog {

    public static void show(Stage owner, Transaction transaction, Runnable onEdit, Runnable onDelete) {
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
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 10);"
        );
        root.setPrefWidth(500);

        StackPane shadowWrapper = new StackPane(root);

        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(48, 48);
        iconCircle.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 12;");

        SVGPath receiptIcon = new SVGPath();

        receiptIcon.setContent("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z");
        receiptIcon.setFill(Color.WHITE);
        receiptIcon.setScaleX(0.8); receiptIcon.setScaleY(0.8);

        iconCircle.getChildren().add(receiptIcon);

        VBox titleBox = new VBox(2);

        Label title = new Label("TRANSACTION DETAIL");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        Label subtitle = new Label("ID: #TXN-00" + transaction.getId() + "8412");
        subtitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: #64748B; -fx-cursor: hand; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper));

        header.getChildren().addAll(iconCircle, titleBox, spacer, closeBtn);

        // Main card
        VBox card = new VBox(25);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        // Name
        VBox mainTitleBox = new VBox(10);
        mainTitleBox.setAlignment(Pos.CENTER);

        Label txName = new Label(transaction.getDescription() != null ? transaction.getDescription() : "Transaction");
        txName.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #0F172A;");

        boolean isSale = "SALE".equalsIgnoreCase(transaction.getType());

        Label typePill = new Label(isSale ? "Sale" : "Purchase");

        String pillBg = isSale ? "#D1FAE5" : "#FEF3C7";
        String pillText = isSale ? "#059669" : "#D97706";
        typePill.setStyle("-fx-background-color: " + pillBg + "; -fx-text-fill: " + pillText + "; -fx-padding: 4 16; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");

        mainTitleBox.getChildren().addAll(txName, typePill);

        // Amount
        HBox amountBox = new HBox();
        amountBox.setAlignment(Pos.BOTTOM_LEFT);

        Label amountLabel = new Label("Amount");
        amountLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold; -fx-font-size: 13px;");

        Region amountSpacer = new Region();
        HBox.setHgrow(amountSpacer, Priority.ALWAYS);

        DecimalFormat df = new DecimalFormat("#,##0.00", new java.text.DecimalFormatSymbols(Locale.US));
        Label amountValue = new Label((isSale ? "$" : "$") + df.format(transaction.getAmount()));
        amountValue.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + (isSale ? Themes.DARK_GREEN : "#EF4444") + ";");

        amountBox.getChildren().addAll(amountLabel, amountSpacer, amountValue);

        // Dividing line
        Region divider1 = new Region();
        divider1.setMinHeight(1); divider1.setStyle("-fx-background-color: #F1F5F9;");

        // Grid
        GridPane detailsGrid = new GridPane();

        detailsGrid.setVgap(20); detailsGrid.setHgap(20);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        detailsGrid.getColumnConstraints().addAll(col1, col2);

        detailsGrid.add(createDetailItem("PROJECT", "M20 6h-8l-2-2H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm0 12H4V8h16v10z", "Project Nova"), 0, 0);
        detailsGrid.add(createDetailItem("DATE", "M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z", transaction.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US))), 1, 0);
        detailsGrid.add(createDetailItem("PAYMENT METHOD", "M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z", "Corporate Visa **** 9012"), 0, 1);
        detailsGrid.add(createDetailItem("VENDOR", "M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5", "Azure Cloud Services"), 1, 1);

        // Dividing line 2
        Region divider2 = new Region();
        divider2.setMinHeight(1); divider2.setStyle("-fx-background-color: #F1F5F9;");

        // Attachments
        VBox attachBox = new VBox(10);

        Label attachTitle = new Label("ATTACHMENTS");
        attachTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox attachPill = new HBox(8);

        attachPill.setAlignment(Pos.CENTER_LEFT);
        attachPill.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 8 12; -fx-background-radius: 6; -fx-max-width: -Infinity; -fx-cursor: hand;");

        SVGPath fileIcon = new SVGPath();
        fileIcon.setContent("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8V4h5v5h5v11z");
        fileIcon.setFill(Color.web("#64748B")); fileIcon.setScaleX(0.7); fileIcon.setScaleY(0.7);

        Label fileName = new Label("invoice_oct_24.pdf");
        fileName.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px; -fx-font-weight: bold;");
        attachPill.getChildren().addAll(fileIcon, fileName);

        attachBox.getChildren().addAll(attachTitle, attachPill);

        card.getChildren().addAll(mainTitleBox, amountBox, divider1, detailsGrid, divider2, attachBox);

        // Down Buttons
        HBox actionBox = new HBox(15);

        Button editBtn = new Button("Edit Transaction");

        editBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setMinHeight(45);

        HBox.setHgrow(editBtn, Priority.ALWAYS);

        SVGPath editIcon = new SVGPath();

        editIcon.setContent("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z");
        editIcon.setFill(Color.WHITE); editIcon.setScaleX(0.7); editIcon.setScaleY(0.7);
        editBtn.setGraphic(editIcon);
        editBtn.setOnAction(e -> {
            closeWithAnimation(modal, shadowWrapper);
            if (onEdit != null) onEdit.run();
        });

        Button delBtn = new Button();

        delBtn.setStyle("-fx-background-color: #FECACA; -fx-background-radius: 8; -fx-cursor: hand;");
        delBtn.setMinSize(55, 45); delBtn.setMaxSize(55, 45);

        SVGPath trashIcon = new SVGPath();

        trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        trashIcon.setFill(Color.web("#DC2626")); trashIcon.setScaleX(0.8); trashIcon.setScaleY(0.8);
        delBtn.setGraphic(trashIcon);
        delBtn.setOnAction(e -> {
            closeWithAnimation(modal, shadowWrapper);
            if (onDelete != null) onDelete.run();
        });

        actionBox.getChildren().addAll(editBtn, delBtn);

        root.getChildren().addAll(header, card, actionBox);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);
        try { scene.getStylesheets().add(TransactionDetailDialog.class.getResource("/styles/global.css").toExternalForm()); } catch (Exception e) {}
        modal.setScene(scene);

        // Animations
        shadowWrapper.setOpacity(0);
        shadowWrapper.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), shadowWrapper); fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), shadowWrapper); slideUp.setToY(0);
        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    private static VBox createDetailItem(String titleText, String svgPath, String valueText) {
        VBox box = new VBox(6);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: #64748B; -fx-letter-spacing: 1px;");

        HBox valBox = new HBox(8);

        valBox.setAlignment(Pos.CENTER_LEFT);
        SVGPath icon = new SVGPath();

        icon.setContent(svgPath);
        icon.setFill(Color.web(Themes.PRIMARY));
        icon.setScaleX(0.7); icon.setScaleY(0.7);

        Label value = new Label(valueText);

        value.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        value.setWrapText(true);

        valBox.getChildren().addAll(icon, value);
        box.getChildren().addAll(title, valBox);

        return box;
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode); fadeOut.setToValue(0);

        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode); slideDown.setToY(30);

        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }
}