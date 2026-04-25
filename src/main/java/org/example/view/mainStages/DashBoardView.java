package org.example.view.mainStages;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.view.templates.*;
import org.example.viewModel.DashboardViewModel;
import org.example.viewModel.ReportsViewModel;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashBoardView extends BaseView {

    private VBox contentArea;

    // UI elements for binding
    private Label revenueValueLabel;
    private Label revenueBadgeLabel;
    private Label activeProjectsValueLabel;
    private VBox transactionsListContainer;

    // Report ViewModel instance to get dummy data for charts
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

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        Label subtitle = new Label("Overview of your key performance indicators.");
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");

        titleBox.getChildren().addAll(title, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        StateButton configBtn = new StateButton("Configurate", StateButton.ButtonType.PRIMARY);
        HBox.setMargin(configBtn, new Insets(0, 0, 20, 0));

        configBtn.setOnMousePressed(e -> {
            configBtn.setScaleX(0.95);
            configBtn.setScaleY(0.95);
        });

        configBtn.setOnMouseReleased(e -> {
            configBtn.setScaleX(1.0);
            configBtn.setScaleY(1.0);
        });

        topBar.getChildren().addAll(titleBox, topSpacer, configBtn);

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
        revHeader.setAlignment(Pos.TOP_LEFT);

        StackPane revIconBox = createIconBox("M4 4h16v12H4z M12 14a4 4 0 100-8 4 4 0 000 8z M12 10V6 M12 14v-4", Themes.PRIMARY);
        Region revSpacer = new Region();

        HBox.setHgrow(revSpacer, Priority.ALWAYS);

        revenueBadgeLabel = new Label();
        revenueBadgeLabel.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: " + Themes.TEXT_SUCCESS + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        revHeader.getChildren().addAll(revIconBox, revSpacer, revenueBadgeLabel);

        Label revTitle = new Label("TOTAL REVENUE");
        revTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        VBox.setMargin(revTitle, new Insets(20, 0, 5, 0));

        revenueValueLabel = new Label();
        revenueValueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";");

        revenueCard.getChildren().addAll(revHeader, revTitle, revenueValueLabel);

        // Active Projects card
        VBox projectsCard = createBaseCard();
        HBox.setHgrow(projectsCard, Priority.ALWAYS);

        HBox projHeader = new HBox();

        projHeader.setAlignment(Pos.TOP_LEFT);
        StackPane projIconBox = createIconBox("M12 2a1 1 0 011 1v2c2.8 1.4 4.8 4.3 4.8 7.6 0 2.2-.9 4.2-2.3 5.7L12 22l-3.5-3.7C7.1 16.8 6.2 14.8 6.2 12.6 6.2 9.3 8.2 6.4 11 5V3a1 1 0 011-1z M12 12a1 1 0 110-2 1 1 0 010 2z M9.5 22h5", Themes.PURPLE_ICON);

        Region projSpacer = new Region();
        HBox.setHgrow(projSpacer, Priority.ALWAYS);

        Label projBadge = new Label("Active");
        projBadge.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #4B5563; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        projHeader.getChildren().addAll(projIconBox, projSpacer, projBadge);

        Label projTitle = new Label("ACTIVE PROJECTS");
        projTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
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

        Label chartTitle = new Label("Revenue vs Projections");
        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        Label chartSubtitle = new Label("Quarterly financial performance analysis");
        chartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
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
        IncomeExpenseBarChart barChart = new IncomeExpenseBarChart(reportsViewModel.getMonthlySummaries());

        VBox chartContainer = new VBox(15, trendChart, barChart);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);

        revenueChartCard.getChildren().addAll(chartHeader, chartContainer);

        leftColumn.getChildren().addAll(kpiContainer, revenueChartCard);

        VBox rightColumn = new VBox(25);
        rightColumn.setPrefWidth(380);
        rightColumn.setMinWidth(380);

        // PieChart
        VBox expenseCard = createBaseCard();
        expenseCard.setMinHeight(250);

        Label expenseTitle = new Label("Top Projects");
        expenseTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        ProjectPieChart pieChart = new ProjectPieChart(reportsViewModel.getProjectSummaries());
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        expenseCard.getChildren().addAll(expenseTitle, pieChart);

        // Recent Transactions
        VBox transactionsCard = createBaseCard();
        VBox.setVgrow(transactionsCard, Priority.ALWAYS);

        Label transTitle = new Label("Recent Transactions");
        transTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.DARK_TEXT + ";");

        VBox.setMargin(transTitle, new Insets(0, 0, 20, 0));

        transactionsListContainer = new VBox(15);

        Button viewAllBtn = new Button("View All");
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
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-padding: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 10, 0, 0, 4);");

        return card;
    }

    private StackPane createIconBox(String pathData, String colorHex) {
        StackPane pane = new StackPane();

        pane.setMinSize(40, 40);
        pane.setMaxSize(40, 40);
        pane.setStyle("-fx-background-color: " + colorHex + "22; -fx-background-radius: 10;");

        SVGPath svg = new SVGPath();

        svg.setContent(pathData);
        svg.setFill(Color.TRANSPARENT);
        svg.setStroke(Color.web(colorHex));
        svg.setStrokeWidth(2.0);

        pane.getChildren().add(svg);

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