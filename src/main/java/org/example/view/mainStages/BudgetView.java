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
import org.example.view.templates.*;
import org.example.viewModel.BudgetViewModel;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;

public class BudgetView extends BaseView {

    private final BudgetViewModel viewModel = new BudgetViewModel();

    private BorderPane root;
    private VBox contentArea;
    private FlowPane accountsGrid;
    private StateButton addAccountBtnMid;
    private TextField searchField;

    private Label c1Value;
    private Label c1Pill;
    private Label c2Value;
    private Region barFillCard2;
    private Label c2Sub;
    private Label c3Value;

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

        // Top bar
        HBox topBar = new HBox(20);

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

        // Content part
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        DoubleBinding cardWidthBinding = contentArea.widthProperty()
                .subtract(80)
                .subtract(GRID_GAP * 2)
                .divide(3)
                .subtract(1);

        // Top summary cards
        HBox summaryContainer = new HBox(GRID_GAP);

        // Card 1: Total Combined Balance (from transactions)
        VBox card1 = createBaseCard(false, cardWidthBinding);

        Label c1Title = new Label("TOTAL COMBINED BALANCE");
        c1Title.setStyle("-fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal totalBalance = viewModel.getTotalBalance();
        c1Value = new Label(String.format(Locale.US, "$%,.2f", totalBalance));
        c1Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

        c1Pill = new Label();
        c1Pill.setVisible(false);
        c1Pill.setManaged(false);

        updateBalancePill();

        card1.getChildren().addAll(c1Title, c1Value, c1Pill);

        // Card 2: Monthly Budget Limit (from account limits)
        VBox card2 = createBaseCard(false, cardWidthBinding);

        Label c2Title = new Label("MONTHLY BUDGET LIMIT");
        c2Title.setStyle("-fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal totalLimit = viewModel.getTotalLimit();
        c2Value = new Label(String.format(Locale.US, "$%,.2f", totalLimit));
        c2Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        HBox progressBox = new HBox(15);
        progressBox.setAlignment(Pos.CENTER_LEFT);

        StackPane barContainer = new StackPane();
        barContainer.setAlignment(Pos.CENTER_LEFT);

        Region barBg = new Region();
        barBg.setMinSize(120, 8); barBg.setMaxSize(120, 8);
        barBg.setStyle("-fx-background-color: " + Themes.BORDER_LIGHT + "; -fx-background-radius: 10;");

        barFillCard2 = new Region();
        barFillCard2.setMinSize(0, 8); barFillCard2.setMaxSize(0, 8);
        barFillCard2.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 10;");

        barContainer.getChildren().addAll(barBg, barFillCard2);

        c2Sub = new Label("0% Used");
        c2Sub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_GRAY + ";");

        progressBox.getChildren().addAll(barContainer, c2Sub);
        card2.getChildren().addAll(c2Title, c2Value, progressBox);

        updateLimitCard(totalLimit);

        // Card 3: Remaining Budget
        VBox card3 = createBaseCard(true, cardWidthBinding);

        Label c3Title = new Label("REMAINING BUDGET");
        c3Title.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        BigDecimal currentMonthTotal = viewModel.getCurrentMonthTotal();
        BigDecimal remaining = totalLimit.subtract(currentMonthTotal);
        c3Value = new Label(String.format(Locale.US, "$%,.2f", remaining));
        c3Value.setStyle("-fx-font-size: 34px; -fx-font-weight: 900; -fx-text-fill: white;");

        Label c3Sub = new Label("Available for allocation");
        c3Sub.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + ";");
        card3.getChildren().addAll(c3Title, c3Value, c3Sub);

        summaryContainer.getChildren().addAll(card1, card2, card3);

        // Middle header and Search bar
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
            AddBudgetDialog.show(stage, newAccount -> viewModel.addAccount(newAccount));
        });
    }

    private void refreshSummaryCards() {
        BigDecimal totalBalance = viewModel.getTotalBalance();
        BigDecimal totalLimit = viewModel.getTotalLimit();
        BigDecimal currentMonthTotal = viewModel.getCurrentMonthTotal();
        BigDecimal remaining = totalLimit.subtract(currentMonthTotal);

        c1Value.setText(String.format(Locale.US, "$%,.2f", totalBalance));
        updateBalancePill();

        c2Value.setText(String.format(Locale.US, "$%,.2f", totalLimit));
        updateLimitCard(totalLimit);

        c3Value.setText(String.format(Locale.US, "$%,.2f", remaining));
    }

    private void updateBalancePill() {
        Optional<BigDecimal> prevMonth = viewModel.getPrevMonthTotal();
        if (prevMonth.isEmpty()) {
            c1Pill.setVisible(false);
            c1Pill.setManaged(false);
            return;
        }

        BigDecimal current = viewModel.getTotalBalance();
        BigDecimal prev = prevMonth.get();
        BigDecimal change = current.subtract(prev);

        boolean positive = change.compareTo(BigDecimal.ZERO) >= 0;
        String sign = positive ? "+" : "";
        String bgColor = positive ? "#D1FAE5" : "#FEE2E2";
        String textColor = positive ? "#059669" : "#DC2626";
        String arrow = positive ? "📈" : "📉";

        c1Pill.setText(arrow + " " + sign + String.format(Locale.US, "$%,.2f", change) + " from last month");
        c1Pill.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-padding: 6 12; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: bold;");
        c1Pill.setVisible(true);
        c1Pill.setManaged(true);
    }

    private void updateLimitCard(BigDecimal totalLimit) {
        BigDecimal currentMonthTotal = viewModel.getCurrentMonthTotal();
        double limitD = totalLimit.doubleValue();
        double monthD = currentMonthTotal.doubleValue();

        double pct = (limitD > 0) ? Math.min((monthD / limitD) * 100.0, 100.0) : 0.0;
        double fillWidth = (pct / 100.0) * 120.0;

        barFillCard2.setMinWidth(fillWidth);
        barFillCard2.setMaxWidth(fillWidth);
        c2Sub.setText((int) pct + "% Used");
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

        cardHeader.getChildren().addAll(iconBox, spacer, menuBtn);

        VBox texts = new VBox(4);

        Label nameLabel = new Label(account.getAccountName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        String categoryDisplay = formatCategory(account.getCategory()).toUpperCase();
        String currency = account.getCurrency() != null ? account.getCurrency() : "USD";
        Label categoryLabel = new Label(categoryDisplay + " • " + currency);
        categoryLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_GRAY + "; -fx-letter-spacing: 1px;");
        texts.getChildren().addAll(nameLabel, categoryLabel);

        double balance = account.getCurrentBalance() != null ? account.getCurrentBalance().doubleValue() : 0.0;
        Label balanceLabel = new Label(String.format(Locale.US, "%s %,.2f", currency, balance));
        balanceLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_GREEN + ";");

        double utilization = 0.0;
        if (account.getLimitAmount() != null && account.getLimitAmount() > 0) {
            utilization = Math.min((balance / account.getLimitAmount()) * 100.0, 100.0);
        }

        VBox progressSection = new VBox(8);

        HBox progressText = new HBox();

        Label progressLabel = new Label(utilization < 50 ? "USAGE LIMIT" : "BUDGET UTILIZATION");
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

        card.getChildren().addAll(cardHeader, texts, balanceLabel, progressSection);

        card.setCursor(Cursor.HAND);
        card.setOnMouseClicked(e -> {
            if (e.getTarget() != menuBtn) {
                BudgetDetailDialog.show(
                        stage,
                        account,
                        () -> ToastManager.showError(stage, "Edit functionality coming in the next task."),
                        () -> viewModel.deleteAccount(account)
                );
            }
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
