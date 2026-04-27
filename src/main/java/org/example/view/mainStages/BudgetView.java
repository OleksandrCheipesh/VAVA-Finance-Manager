package org.example.view.mainStages;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.AccountCategory;
import org.example.view.templates.*;
import org.example.viewModel.BudgetViewModel;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.effect.GaussianBlur;

public class BudgetView extends BaseView {

    private final BudgetViewModel viewModel = new BudgetViewModel();

    private BorderPane root;
    private VBox contentArea;
    private FlowPane accountsGrid;
    private StateButton addAccountBtnMid;
    private TextField searchField;

    private HBox topBar;
    private Label c1Value;
    private Label c2Value;
    private Label c3Value;
    private Label c3Sub;

    private final double GRID_GAP = 25.0;

    private static String formatCategory(org.example.model.database.entity.AccountCategory cat) {
        if (cat == null) return "Other";
        return switch (cat) {
            case BANK_ACCOUNT -> "Bank Account";
            case CASH -> "Cash";
            case CREDIT_LINE -> "Credit Line";
            case SAVINGS -> "Savings";
            case INVESTMENT -> "Investment";
            case OTHER -> "Other";
        };
    }

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        topBar = new HBox(20);

        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);

        Label title = new Label("Budget");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");

        Label subtitle = new Label("Manage and track your company spending.");
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");

        titleBox.getChildren().addAll(title, subtitle);

        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        topBar.getChildren().addAll(titleBox, spacer1);

        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        DoubleBinding cardWidthBinding = contentArea.widthProperty()
                .subtract(80)
                .subtract(GRID_GAP * 2)
                .divide(3)
                .subtract(1);

        HBox summaryContainer = new HBox(GRID_GAP);

        VBox card1 = createBaseCard(false, cardWidthBinding);

        Label c1Title = new Label("TOTAL ASSETS");
        c1Title.setStyle("-fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal totalAssets = viewModel.getTotalAssets();
        c1Value = new Label(String.format(Locale.US, "$%,.2f", totalAssets));
        c1Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

        card1.getChildren().addAll(c1Title, c1Value);

        VBox card2 = createBaseCard(false, cardWidthBinding);

        Label c2Title = new Label("TOTAL LIABILITIES");
        c2Title.setStyle("-fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal totalLiabilities = viewModel.getTotalLiabilities();
        c2Value = new Label(String.format(Locale.US, "$%,.2f", totalLiabilities.abs()));
        c2Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        card2.getChildren().addAll(c2Title, c2Value);

        VBox card3 = createBaseCard(true, cardWidthBinding);

        Label c3Title = new Label("NET POSITION");
        c3Title.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal netPosition = viewModel.getNetPosition();
        c3Value = new Label(String.format(Locale.US, "$%,.2f", netPosition));
        c3Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + (netPosition.compareTo(BigDecimal.ZERO) >= 0 ? "white" : "#FECACA") + ";");

        c3Sub = new Label(netPosition.compareTo(BigDecimal.ZERO) >= 0 ? "Available for allocation" : "Deficit");
        c3Sub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (netPosition.compareTo(BigDecimal.ZERO) >= 0 ? Themes.PRIMARY : "#FCA5A5") + ";");
        card3.getChildren().addAll(c3Title, c3Value, c3Sub);

        summaryContainer.getChildren().addAll(card1, card2, card3);

        HBox midHeader = new HBox(15);

        midHeader.setAlignment(Pos.BOTTOM_LEFT);

        VBox.setMargin(midHeader, new Insets(10, 0, 0, 0));

        VBox midTitleBox = new VBox(5);

        Label midTitle = new Label("Accounts Overview");
        midTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label midSubtitle = new Label("Manage your operational and reserve funds");
        midSubtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        midTitleBox.getChildren().addAll(midTitle, midSubtitle);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox searchContainer = new HBox(10);

        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPrefWidth(250);
        searchContainer.setMinHeight(44);
        searchContainer.setPrefHeight(44);
        searchContainer.setMaxHeight(44);
        searchContainer.setStyle("-fx-background-color: white; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 12;");

        SVGPath searchIcon = new SVGPath();
        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        searchField = new TextField();
        searchField.setPromptText("Search accounts...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchContainer.getChildren().addAll(searchIcon, searchField);

        addAccountBtnMid = new StateButton("+ Add New Account", StateButton.ButtonType.PRIMARY);
        addAccountBtnMid.setMinHeight(44);
        addAccountBtnMid.setPrefHeight(44);
        addAccountBtnMid.setMaxHeight(44);

        addAccountBtnMid.setOnMousePressed(e -> {
            addAccountBtnMid.setScaleX(0.95);
            addAccountBtnMid.setScaleY(0.95);
        });

        addAccountBtnMid.setOnMouseReleased(e -> {
            addAccountBtnMid.setScaleX(1.0);
            addAccountBtnMid.setScaleY(1.0);
        });

        midHeader.getChildren().addAll(midTitleBox, spacer2, searchContainer, addAccountBtnMid);

        accountsGrid = new FlowPane(GRID_GAP, GRID_GAP);
        accountsGrid.setAlignment(Pos.TOP_LEFT);

        updateGrid(cardWidthBinding);

        contentArea.getChildren().addAll(summaryContainer, midHeader, accountsGrid);
        mainContainer.getChildren().addAll(topBar, contentArea);

        root.setCenter(mainContainer);
        scene = new Scene(root);
        stage.setTitle("Admin - Budget");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        if (!viewModel.hasAccessProperty().get()) {
            GaussianBlur blur = new GaussianBlur(10);
            contentArea.setEffect(blur);

            Label lock = new Label("🔒");
            lock.setStyle("-fx-font-size: 48px;");

            Label msg = new Label("Access Denied");
            msg.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

            VBox box = new VBox(10, lock, msg);
            box.setAlignment(Pos.CENTER);

            StackPane overlay = new StackPane();
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.getChildren().add(box);

            StackPane wrapper = new StackPane(contentArea, overlay);
            VBox.setVgrow(wrapper, Priority.ALWAYS);
            root.setCenter(new VBox(topBar, wrapper));
            return;
        }

        DoubleBinding cardWidthBinding = contentArea.widthProperty()
                .subtract(80)
                .subtract(GRID_GAP * 2)
                .divide(3)
                .subtract(1);

        viewModel.getFilteredAccounts().addListener((ListChangeListener<Account>) c -> {
            updateGrid(cardWidthBinding);
            refreshSummaryCards();
        });

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            String regex = "(?i).*" + newText + ".*";
            viewModel.filterAccounts(a -> a.getAccountName().matches(regex));
        });

        addAccountBtnMid.setOnAction(e -> {
            AddBudgetDialog.show(stage, newAccount -> {
                viewModel.addAccount(newAccount);
                refreshDashboardState(cardWidthBinding);
            });
        });
    }

    private void refreshDashboardState(DoubleBinding cardWidthBinding) {
        refreshSummaryCards();
        updateGrid(cardWidthBinding);
    }

    private void refreshSummaryCards() {
        BigDecimal totalAssets = viewModel.getTotalAssets();
        BigDecimal totalLiabilities = viewModel.getTotalLiabilities();
        BigDecimal netPosition = viewModel.getNetPosition();

        c1Value.setText(String.format(Locale.US, "$%,.2f", totalAssets));
        c2Value.setText(String.format(Locale.US, "$%,.2f", totalLiabilities.abs()));
        c3Value.setText(String.format(Locale.US, "$%,.2f", netPosition));
        c3Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + (netPosition.compareTo(BigDecimal.ZERO) >= 0 ? "white" : "#FECACA") + ";");
        c3Sub.setText(netPosition.compareTo(BigDecimal.ZERO) >= 0 ? "Available for allocation" : "Deficit");
        c3Sub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + (netPosition.compareTo(BigDecimal.ZERO) >= 0 ? Themes.PRIMARY : "#FCA5A5") + ";");
    }

    private void updateGrid(DoubleBinding widthBinding) {
        accountsGrid.getChildren().clear();

        if (viewModel.getFilteredAccounts().isEmpty()) {
            VBox emptyState = new VBox(15);

            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(80, 0, 80, 0));

            emptyState.prefWidthProperty().bind(accountsGrid.widthProperty());

            StackPane iconCircle = new StackPane();

            iconCircle.setMinSize(80, 80);
            iconCircle.setMaxSize(80, 80);
            iconCircle.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + "88; -fx-background-radius: 40;");

            SVGPath emptyIcon = new SVGPath();

            emptyIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
            emptyIcon.setFill(Color.web(Themes.TEXT_MUTED));
            emptyIcon.setScaleX(2.0);
            emptyIcon.setScaleY(2.0);

            iconCircle.getChildren().add(emptyIcon);

            Label emptyTitle = new Label("No accounts found");
            emptyTitle.setStyle("-fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-size: 20px; -fx-font-weight: 900;");

            Label emptySubtitle = new Label("We couldn't find any accounts matching your criteria.\nTry adjusting your search or add a new account.");
            emptySubtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px; -fx-text-alignment: center;");
            emptySubtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            emptyState.getChildren().addAll(iconCircle, emptyTitle, emptySubtitle);
            accountsGrid.getChildren().add(emptyState);
        } else {
            for (Account account : viewModel.getFilteredAccounts()) {
                accountsGrid.getChildren().add(buildAccountCard(account, widthBinding));
            }
        }
    }

    private VBox buildAccountCard(Account account, DoubleBinding widthBinding) {
        VBox card = new VBox(18);

        card.prefWidthProperty().bind(widthBinding);
        card.minWidthProperty().bind(widthBinding);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                        "-fx-border-radius: 16; -fx-background-radius: 16;" +
                        "-fx-padding: 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);"
        );

        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();

        iconBox.setPrefSize(42, 42);
        iconBox.setStyle("-fx-background-color: " + Themes.PRIMARY + "22; -fx-background-radius: 10;");

        SVGPath icon = new SVGPath();

        icon.setContent("M20 7h-4V5c0-1.1-.9-2-2-2h-4c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v11c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2zM10 5h4v2h-4V5z");
        icon.setFill(Color.web(Themes.PRIMARY));
        icon.setScaleX(0.7); icon.setScaleY(0.7);
        iconBox.getChildren().add(icon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button menuBtn = new Button("⋮");
        menuBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 22px; -fx-text-fill: #CBD5E1; -fx-cursor: hand; -fx-padding: 0;");
        menuBtn.setMouseTransparent(true);

        cardHeader.getChildren().addAll(iconBox, spacer, menuBtn);

        VBox texts = new VBox(4);

        Label nameLabel = new Label(account.getAccountName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        String categoryDisplay = formatCategory(account.getCategory()).toUpperCase();
        String currency = account.getCurrency() != null ? account.getCurrency() : "USD";
        Label categoryLabel = new Label(categoryDisplay + " • " + currency);
        categoryLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-letter-spacing: 1px;");
        texts.getChildren().addAll(nameLabel, categoryLabel);

        AccountCategory category = account.getCategory();
        boolean isSavingsOrInvestment = category == AccountCategory.SAVINGS || category == AccountCategory.INVESTMENT;
        boolean isCreditLine = category == AccountCategory.CREDIT_LINE;
        boolean hasUsableLimit = account.getLimitAmount() != null && account.getLimitAmount() > 0;

        BigDecimal rawBalance = account.getCurrentBalance() != null ? account.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal displayBalance = rawBalance;

        double balance = displayBalance.doubleValue();
        String balanceText = isCreditLine
                ? String.format(Locale.US, "%s %,.2f", currency, Math.abs(balance))
                : String.format(Locale.US, "%s %,.2f", currency, balance);
        String balanceColor = isCreditLine
                ? (balance < 0 ? Themes.TEXT_ERROR : Themes.DARK_GREEN)
                : Themes.DARK_GREEN;
        Label balanceLabel = new Label(balanceText);
        balanceLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: " + balanceColor + ";");

        VBox progressSection = null;

        if ((isSavingsOrInvestment || isCreditLine) && hasUsableLimit) {
            double utilization;
            String progressLabelText;

            if (isCreditLine) {
                utilization = rawBalance.negate().max(BigDecimal.ZERO)
                        .divide(BigDecimal.valueOf(account.getLimitAmount()), 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100.0))
                        .doubleValue();
                progressLabelText = "CREDIT USED";
            } else {
                utilization = Math.min((rawBalance.doubleValue() / account.getLimitAmount()) * 100.0, 100.0);
                progressLabelText = "GOAL PROGRESS";
            }
            utilization = Math.min(utilization, 100.0);

            progressSection = new VBox(8);
            HBox progressText = new HBox();

            Label progressLabel = new Label(progressLabelText);
            progressLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_GRAY + ";");

            Region pSpacer = new Region();
            HBox.setHgrow(pSpacer, Priority.ALWAYS);

            Label percentLabel = new Label((int) utilization + "%");
            percentLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 900; -fx-text-fill: " + Themes.PRIMARY + ";");
            progressText.getChildren().addAll(progressLabel, pSpacer, percentLabel);

            StackPane barContainer = new StackPane();
            barContainer.setAlignment(Pos.CENTER_LEFT);

            Region barBg = new Region();
            barBg.setMinHeight(6); barBg.setMaxHeight(6);
            barBg.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + "; -fx-background-radius: 10;");

            Region barFill = new Region();
            barFill.setMinHeight(6); barFill.setMaxHeight(6);
            final double finalUtil = utilization;
            barFill.prefWidthProperty().bind(barBg.widthProperty().multiply(finalUtil / 100.0));
            barFill.maxWidthProperty().bind(barBg.widthProperty().multiply(finalUtil / 100.0));
            barFill.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 10;");

            barContainer.getChildren().addAll(barBg, barFill);
            progressSection.getChildren().addAll(progressText, barContainer);
        }

        card.getChildren().addAll(cardHeader, texts, balanceLabel);
        if (progressSection != null) {
            card.getChildren().add(progressSection);
        }

        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(e -> {
            BudgetDetailDialog.show(
                    stage,
                    account,
                    viewModel,
                    () -> ToastManager.showError(stage, "Edit functionality coming in the next task."),
                    () -> {
                        viewModel.deleteAccount(account);
                        refreshDashboardState(widthBinding);
                    }
            );
        });

        return card;
    }

    private VBox createBaseCard(boolean isDark, DoubleBinding widthBinding) {
        VBox card = new VBox(15);

        card.prefWidthProperty().bind(widthBinding);
        card.minWidthProperty().bind(widthBinding);

        String bgColor = isDark ? Themes.DARK_TEXT : "white";
        String border = isDark ? "" : "-fx-border-color: " + Themes.BORDER_LIGHT + "; ";

        card.setStyle(
                "-fx-background-color: " + bgColor + ";" + border +
                        "-fx-border-radius: 16; -fx-background-radius: 16;" +
                        "-fx-padding: 30;" +
                        (isDark ? "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.3), 15, 0, 0, 8);" : "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 10, 0, 0, 4);")
        );

        return card;
    }
}