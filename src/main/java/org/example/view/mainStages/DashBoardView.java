package org.example.view.mainStages;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.view.templates.*;
import org.example.viewModel.DashboardViewModel;
import org.example.viewModel.ReportsViewModel;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashBoardView extends BaseView {

    private VBox contentArea;

    // UI elements for binding
    private Label revenueValueLabel;
    private Label revenueBadgeLabel;
    private Label activeProjectsValueLabel;
    private VBox transactionsListContainer;

    private final ReportsViewModel reportsViewModel = new ReportsViewModel();

    @Override
    protected void setContent() {
        BorderPane root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // Header
        HBox topBar = new HBox();

        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Label title = new Label(I18n.t("Dashboard"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Dashboard")));

        Label subtitle = new Label(I18n.t("Overview of your key performance indicators."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        I18n.language.addListener((obs, o, v) -> subtitle.setText(I18n.t("Overview of your key performance indicators.")));

        titleBox.getChildren().addAll(title, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleBox, topSpacer);

        // Content
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        HBox mainContentSplit = new HBox(25);
        VBox.setVgrow(mainContentSplit, Priority.ALWAYS);

        VBox leftColumn = new VBox(25);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // KPI cards
        HBox kpiContainer = new HBox(25);

        VBox revenueCard = createBaseCard();
        HBox.setHgrow(revenueCard, Priority.ALWAYS);

        HBox revHeader = new HBox();
        revHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane revIconBox = createIcon("fas-dollar-sign", Themes.PRIMARY, Themes.PRIMARY + "22");
        Region revSpacer = new Region();

        HBox.setHgrow(revSpacer, Priority.ALWAYS);

        revenueBadgeLabel = new Label();
        revenueBadgeLabel.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: " + Themes.TEXT_SUCCESS + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        revHeader.getChildren().addAll(revIconBox, revSpacer, revenueBadgeLabel);

        Label revTitle = new Label(I18n.t("TOTAL REVENUE"));
        revTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        I18n.language.addListener((obs, o, v) -> revTitle.setText(I18n.t("TOTAL REVENUE")));
        VBox.setMargin(revTitle, new Insets(20, 0, 5, 0));

        revenueValueLabel = new Label();
        revenueValueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";");

        revenueCard.getChildren().addAll(revHeader, revTitle, revenueValueLabel);

        // Active Projects card
        VBox projectsCard = createBaseCard();
        HBox.setHgrow(projectsCard, Priority.ALWAYS);

        HBox projHeader = new HBox();
        projHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane projIconBox = createIcon("fas-briefcase", Themes.PURPLE_ICON, Themes.PURPLE_ICON + "22");

        Region projSpacer = new Region();
        HBox.setHgrow(projSpacer, Priority.ALWAYS);

        Label projBadge = new Label(I18n.t("Active"));
        projBadge.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #4B5563; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        I18n.language.addListener((obs, o, v) -> projBadge.setText(I18n.t("Active")));
        projHeader.getChildren().addAll(projIconBox, projSpacer, projBadge);

        Label projTitle = new Label(I18n.t("ACTIVE PROJECTS"));
        projTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        I18n.language.addListener((obs, o, v) -> projTitle.setText(I18n.t("ACTIVE PROJECTS")));
        VBox.setMargin(projTitle, new Insets(20, 0, 5, 0));

        activeProjectsValueLabel = new Label();
        activeProjectsValueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        projectsCard.getChildren().addAll(projHeader, projTitle, activeProjectsValueLabel);

        kpiContainer.getChildren().addAll(revenueCard, projectsCard);

        // Trend bar and Bar graphs
        VBox revenueChartCard = createBaseCard();
        VBox.setVgrow(revenueChartCard, Priority.ALWAYS);

        HBox chartHeader = new HBox();
        chartHeader.setAlignment(Pos.CENTER_LEFT);

        VBox chartTitleBox = new VBox(4);

        Label chartTitle = new Label(I18n.t("Revenue vs Projections"));
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        I18n.language.addListener((obs, o, v) -> chartTitle.setText(I18n.t("Revenue vs Projections")));

        Label chartSubtitle = new Label(I18n.t("Quarterly financial performance analysis"));
        chartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        I18n.language.addListener((obs, o, v) -> chartSubtitle.setText(I18n.t("Quarterly financial performance analysis")));
        chartTitleBox.getChildren().addAll(chartTitle, chartSubtitle);

        Region chartSpacer = new Region();
        HBox.setHgrow(chartSpacer, Priority.ALWAYS);

        HBox toggleBox = new HBox(2);
        toggleBox.setAlignment(Pos.CENTER);
        toggleBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-padding: 4;");
        toggleBox.setMaxHeight(Region.USE_PREF_SIZE);

        Button dailyBtn = createToggleButton("Daily", false);
        Button weeklyBtn = createToggleButton("Weekly", true);
        Button monthlyBtn = createToggleButton("Monthly", false);
        toggleBox.getChildren().addAll(dailyBtn, weeklyBtn, monthlyBtn);

        chartHeader.getChildren().addAll(chartTitleBox, chartSpacer, toggleBox);

        TrendLineChart trendChart = new TrendLineChart(reportsViewModel.getMonthlySummaries());
        VBox.setVgrow(trendChart, Priority.ALWAYS);
        // IncomeExpenseBarChart intentionally removed — belongs on Reports screen only

        VBox chartContainer = new VBox(15, trendChart);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);

        revenueChartCard.getChildren().addAll(chartHeader, chartContainer);

        leftColumn.getChildren().addAll(kpiContainer, revenueChartCard);

        VBox rightColumn = new VBox(25);
        rightColumn.setPrefWidth(380);
        rightColumn.setMinWidth(380);

        // PieChart
        VBox expenseCard = createBaseCard();
        expenseCard.setMinHeight(250);

        Label expenseTitle = new Label(I18n.t("Top Projects"));
        expenseTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        I18n.language.addListener((obs, o, v) -> expenseTitle.setText(I18n.t("Top Projects")));

        ProjectPieChart pieChart = new ProjectPieChart(reportsViewModel.getProjectSummaries(), 250);
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        expenseCard.getChildren().addAll(expenseTitle, pieChart);

        // Recent Transactions
        VBox transactionsCard = createBaseCard();
        VBox.setVgrow(transactionsCard, Priority.ALWAYS);

        Label transTitle = new Label(I18n.t("Recent Transactions"));
        transTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");
        I18n.language.addListener((obs, o, v) -> transTitle.setText(I18n.t("Recent Transactions")));

        VBox.setMargin(transTitle, new Insets(0, 0, 20, 0));

        transactionsListContainer = new VBox(15);

        Button viewAllBtn = new Button(I18n.t("View All"));
        I18n.language.addListener((obs, o, v) -> viewAllBtn.setText(I18n.t("View All")));
        viewAllBtn.setMaxWidth(Double.MAX_VALUE);
        viewAllBtn.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: " + Themes.DARK_TEXT + "; -fx-font-weight: 900; -fx-padding: 12; -fx-background-radius: 8; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-insets: 0;");

        viewAllBtn.setOnMousePressed(e -> {
            viewAllBtn.setScaleX(0.98);
            viewAllBtn.setScaleY(0.98);
        });
        viewAllBtn.setOnMouseReleased(e -> {
            viewAllBtn.setScaleX(1.0);
            viewAllBtn.setScaleY(1.0);
        });
        viewAllBtn.setOnAction(e -> {
            navigateTo(new TransactionsView());
        });

        VBox.setMargin(viewAllBtn, new Insets(20, 0, 0, 0));

        transactionsCard.getChildren().addAll(transTitle, transactionsListContainer, viewAllBtn);

        rightColumn.getChildren().addAll(expenseCard, transactionsCard);

        // Composition of the entire Grid
        mainContentSplit.getChildren().addAll(leftColumn, rightColumn);
        contentArea.getChildren().add(mainContentSplit);

        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/charts.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: CSS files not found.");
        }

        stage.setTitle("Admin - Dashboard");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 30, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        DashboardViewModel.reload();
        revenueValueLabel.textProperty().bind(DashboardViewModel.totalRevenueProperty());
        revenueBadgeLabel.textProperty().bind(DashboardViewModel.revenueGrowthProperty());
        activeProjectsValueLabel.textProperty().bind(DashboardViewModel.activeProjectsCountProperty().asString());

        DashboardViewModel.revenueGrowthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("-")) {
                revenueBadgeLabel.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: " + Themes.TEXT_ERROR + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else {
                revenueBadgeLabel.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: " + Themes.TEXT_SUCCESS + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            }
        });

        updateTransactionsList();

        DashboardViewModel.getRecentTransactions().addListener((ListChangeListener<DashboardViewModel.TransactionDto>) c -> {
            updateTransactionsList();
        });
    }

    private void updateTransactionsList() {
        transactionsListContainer.getChildren().clear();

        for (DashboardViewModel.TransactionDto tx : DashboardViewModel.getRecentTransactions()) {
            HBox txRow = new HBox(15);
            txRow.setAlignment(Pos.CENTER_LEFT);

            VBox dateBlock = new VBox(-2);

            dateBlock.setAlignment(Pos.CENTER);
            dateBlock.setMinSize(50, 56);
            dateBlock.setMaxSize(50, 56);
            dateBlock.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 12;");

            Label monthLbl = new Label(tx.getDate().format(DateTimeFormatter.ofPattern("MMM")).toUpperCase());
            monthLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.PRIMARY + ";");

            Label dayLbl = new Label(String.valueOf(tx.getDate().getDayOfMonth()));
            dayLbl.setStyle("-fx-font-size: 17px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

            dateBlock.getChildren().addAll(monthLbl, dayLbl);

            VBox textsBlock = new VBox(2);
            textsBlock.setAlignment(Pos.CENTER_LEFT);

            Label descLbl = new Label(tx.getDescription());
            descLbl.setStyle("-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

            Label clientLbl = new Label(tx.getClientName());
            clientLbl.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: 600;");

            textsBlock.getChildren().addAll(descLbl, clientLbl);

            Region txSpacer = new Region();
            HBox.setHgrow(txSpacer, Priority.ALWAYS);

            String amountColor = tx.getAmount() >= 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
            String prefix = tx.getAmount() >= 0 ? "+ " : "- ";

            Label amountLbl = new Label(prefix + String.format(Locale.US, "$%,.2f", Math.abs(tx.getAmount())));
            amountLbl.setStyle("-fx-font-size: 14.5px; -fx-font-weight: 900; -fx-text-fill: " + amountColor + ";");

            amountLbl.setMinWidth(Region.USE_PREF_SIZE);

            txRow.getChildren().addAll(dateBlock, textsBlock, txSpacer, amountLbl);
            transactionsListContainer.getChildren().add(txRow);
        }
    }

    private VBox createBaseCard() {
        VBox card = new VBox();

        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-border-color: " + Themes.BORDER_LIGHT + "; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 16; " +
                "-fx-padding: 30; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        return card;
    }

    private StackPane createIcon(String iconCode, String colorHex, String bgColorHex) {
        StackPane pane = new StackPane();
        pane.setMinSize(40, 40);
        pane.setMaxSize(40, 40);
        pane.setStyle("-fx-background-color: " + bgColorHex + "; -fx-background-radius: 12;");

        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(colorHex));

        pane.getChildren().add(icon);

        return pane;
    }

    private Button createToggleButton(String text, boolean isActive) {
        Button btn = new Button(text);

        if (isActive) {
            btn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: 900; -fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-insets: 0;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: 900; -fx-background-radius: 8; -fx-padding: 8 16; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-insets: 0;");
        }

        return btn;
    }
}