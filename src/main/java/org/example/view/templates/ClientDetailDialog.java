package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.logging.AppLog;
import org.example.model.database.entity.Client;
import org.example.model.database.entity.Transaction;
import org.example.model.database.service.TransactionService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClientDetailDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static void show(Stage owner, Client client, Runnable onEdit, Runnable onDelete) {
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
        root.setPadding(new Insets(35, 40, 35, 40));
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 25, 0, 0, 10);"
        );
        root.setPrefWidth(520);

        StackPane shadowWrapper = new StackPane(root);
        shadowWrapper.setStyle("-fx-background-color: transparent;");
        shadowWrapper.setPadding(new Insets(30));

        // ── Close button row ──────────────────────────────────────────────────
        HBox topNav = new HBox();
        topNav.setAlignment(Pos.CENTER_RIGHT);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32);
        closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle(
                "-fx-background-color: transparent; -fx-cursor: hand;" +
                        "-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold;" +
                        "-fx-font-size: 16px; -fx-padding: 0;"
        );
        closeBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, null));
        HBox.setMargin(closeBtn, new Insets(0, -10, 0, 0));
        topNav.getChildren().add(closeBtn);

        // ── Client header ─────────────────────────────────────────────────────
        HBox clientHeader = new HBox(18);
        clientHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarCircle = new StackPane();
        avatarCircle.setPrefSize(64, 64);
        avatarCircle.setMinSize(64, 64);
        avatarCircle.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 32;");

        String initials = initials(client.getName(), client.getSurname());
        Label initialsLabel = new Label(initials);
        initialsLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: white;");
        avatarCircle.getChildren().add(initialsLabel);

        VBox nameBox = new VBox(4);
        Label nameLabel = new Label(client.getName() + " " + client.getSurname());
        nameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        String memberSince = client.getCreatedAt() != null
                ? I18n.t("PREMIUM CLIENT • MEMBER SINCE ") + client.getCreatedAt().getYear()
                : I18n.t("CLIENT");
        Label memberLabel = new Label(memberSince);
        memberLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 0.5px;");
        nameBox.getChildren().addAll(nameLabel, memberLabel);

        clientHeader.getChildren().addAll(avatarCircle, nameBox);

        // ── Separator ─────────────────────────────────────────────────────────
        Region sep1 = new Region();
        sep1.setMinHeight(1);
        sep1.setStyle("-fx-background-color: #E2E8F0;");

        // ── Contact + Financial grid ──────────────────────────────────────────
        HBox infoRow = new HBox(16);
        infoRow.setAlignment(Pos.TOP_LEFT);

        VBox contactCard = new VBox(12);
        contactCard.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-padding: 18 20;");
        HBox.setHgrow(contactCard, Priority.ALWAYS);

        Label contactTitle = new Label(I18n.t("CONTACT DETAILS"));
        contactTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");

        VBox emailRow = new VBox(2);
        Label emailLbl = new Label(I18n.t("Email Address"));
        emailLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label emailVal = new Label(orDash(client.getEmail()));
        emailVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + "; -fx-wrap-text: true;");
        emailRow.getChildren().addAll(emailLbl, emailVal);

        VBox phoneRow = new VBox(2);
        Label phoneLbl = new Label(I18n.t("Phone Number"));
        phoneLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label phoneVal = new Label(orDash(client.getPhone()));
        phoneVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        phoneRow.getChildren().addAll(phoneLbl, phoneVal);

        contactCard.getChildren().addAll(contactTitle, emailRow, phoneRow);

        VBox financialCard = new VBox(10);
        financialCard.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 12; -fx-padding: 18 20;");
        financialCard.setPrefWidth(180);

        Label financialTitle = new Label(I18n.t("FINANCIAL STATUS"));
        financialTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: rgba(255,255,255,0.75); -fx-letter-spacing: 1px;");

        Label incomeLbl = new Label(I18n.t("Monthly Income"));
        incomeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.75);");

        String incomeText = client.getMonthlyIncome() != null
                ? CurrencyFormatter.format(client.getMonthlyIncome())
                : "—";
        Label incomeVal = new Label(incomeText);
        incomeVal.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: white; -fx-wrap-text: true;");

        financialCard.getChildren().addAll(financialTitle, incomeLbl, incomeVal);

        infoRow.getChildren().addAll(contactCard, financialCard);

        // ── Recent Transactions ───────────────────────────────────────────────
        Region sep2 = new Region();
        sep2.setMinHeight(1);
        sep2.setStyle("-fx-background-color: #E2E8F0;");

        HBox txHeader = new HBox();
        txHeader.setAlignment(Pos.CENTER_LEFT);
        Label txTitle = new Label(I18n.t("RECENT TRANSACTIONS"));
        txTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        txHeader.getChildren().add(txTitle);

        VBox txSection = new VBox(8);

        List<Transaction> transactions = Collections.emptyList();
        try {
            transactions = new TransactionService().getTransactionsByClientId(client.getId());
        } catch (Exception e) {
            AppLog.getLogger(ClientDetailDialog.class).warn("Could not load transactions for clientId={}", client.getId(), e);
        }

        if (transactions.isEmpty()) {
            Label empty = new Label(I18n.t("No transactions found."));
            empty.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
            txSection.getChildren().add(empty);
        } else {
            List<Transaction> recent = transactions.stream().limit(5).toList();
            for (Transaction tx : recent) {
                txSection.getChildren().add(buildTransactionRow(tx));
            }
        }

        // ── Footer buttons ────────────────────────────────────────────────────
        HBox footer = new HBox(15);
        footer.setPadding(new Insets(10, 0, 0, 0));

        Button editBtn = new Button(I18n.t("Edit Client"));
        editBtn.setGraphic(IconFactory.getWhiteIcon("square-pen", 18));
        editBtn.setMinHeight(52);
        editBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(editBtn, Priority.ALWAYS);
        editBtn.setStyle(
                "-fx-background-color: " + Themes.PRIMARY + ";" +
                        "-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 900;" +
                        "-fx-background-radius: 12; -fx-cursor: hand; -fx-graphic-text-gap: 10;"
        );
        editBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, onEdit));
        editBtn.setOnMousePressed(e -> { editBtn.setScaleX(0.98); editBtn.setScaleY(0.98); });
        editBtn.setOnMouseReleased(e -> { editBtn.setScaleX(1.0); editBtn.setScaleY(1.0); });

        Button deleteBtn = new Button();
        deleteBtn.setGraphic(IconFactory.getIcon("trash", 22));
        deleteBtn.setMinSize(52, 52);
        deleteBtn.setMaxSize(52, 52);
        String normalDel = "-fx-background-color: #FEE2E2; -fx-background-radius: 12; -fx-cursor: hand;";
        String hoverDel  = "-fx-background-color: #FECACA; -fx-background-radius: 12; -fx-cursor: hand;";
        deleteBtn.setStyle(normalDel);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(hoverDel));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(normalDel));
        deleteBtn.setOnMousePressed(e -> { deleteBtn.setScaleX(0.95); deleteBtn.setScaleY(0.95); });
        deleteBtn.setOnMouseReleased(e -> { deleteBtn.setScaleX(1.0); deleteBtn.setScaleY(1.0); });
        deleteBtn.setOnAction(e -> closeWithAnimation(modal, shadowWrapper, onDelete));

        footer.getChildren().addAll(editBtn, deleteBtn);

        root.getChildren().addAll(topNav, clientHeader, sep1, infoRow, sep2, txHeader, txSection, footer);

        Scene scene = new Scene(shadowWrapper);
        scene.setFill(null);

        try {
            scene.getStylesheets().add(ClientDetailDialog.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            AppLog.getLogger(ClientDetailDialog.class).warn("Could not load global.css: {}", e.getMessage());
        }

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

    private static HBox buildTransactionRow(Transaction tx) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 0; -fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1 0;");

        boolean isSale = "SALE".equalsIgnoreCase(tx.getType());
        String amountColor = isSale ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
        String amountPrefix = isSale ? "+" : "-";

        StackPane dot = new StackPane();
        dot.setPrefSize(36, 36);
        dot.setMinSize(36, 36);
        String dotBg = isSale ? "#D1FAE5" : "#FEE2E2";
        dot.setStyle("-fx-background-color: " + dotBg + "; -fx-background-radius: 18;");
        Label arrow = new Label(isSale ? "↑" : "↓");
        arrow.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + amountColor + ";");
        dot.getChildren().add(arrow);

        VBox descBox = new VBox(2);
        HBox.setHgrow(descBox, Priority.ALWAYS);
        Label descLabel = new Label(tx.getDescription());
        descLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        Label dateLabel = new Label(tx.getDate() != null ? tx.getDate().format(DATE_FMT) : "");
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        descBox.getChildren().addAll(descLabel, dateLabel);

        String amountText = amountPrefix + CurrencyFormatter.symbol() + String.format(Locale.US, "%,.2f", tx.getAmount().doubleValue());
        Label amountLabel = new Label(amountText);
        amountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 900; -fx-text-fill: " + amountColor + ";");

        row.getChildren().addAll(dot, descBox, amountLabel);
        return row;
    }

    private static String initials(String name, String surname) {
        String n = (name != null && !name.isBlank()) ? String.valueOf(name.trim().charAt(0)).toUpperCase() : "";
        String s = (surname != null && !surname.isBlank()) ? String.valueOf(surname.trim().charAt(0)).toUpperCase() : "";
        return n + s;
    }

    private static String orDash(String value) {
        return (value != null && !value.isBlank()) ? value : "—";
    }

    private static void closeWithAnimation(Stage modal, Node animatedNode, Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);
        ParallelTransition exit = new ParallelTransition(fadeOut, slideDown);
        exit.setOnFinished(e -> {
            modal.close();
            if (onFinished != null) onFinished.run();
        });
        exit.play();
    }
}