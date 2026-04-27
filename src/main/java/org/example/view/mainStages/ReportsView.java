package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.view.templates.*;
import javafx.scene.effect.GaussianBlur;
import org.example.viewModel.ReportsViewModel;

// Import your actual ViewModel and DTOs here
// import org.example.viewModel.ReportsViewModel;
// import org.example.model.reports.MonthlySnapshotDTO;

public class ReportsView extends BaseView {

    private final ReportsViewModel viewModel = new ReportsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private TableView<Object> table; // Replace Object with MonthlySnapshotDTO
    private HBox topBar;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");

        // Top Header
        HBox topBar = new HBox(20);

        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);

        Label title = new Label("Reports");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");

        Label subTitle = new Label("Financial overview · FY 2024");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subTitle);

        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        topBar.getChildren().addAll(titleBox, topSpacer);

        // Content Area
        contentArea = new VBox(25);
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Top Section: 3 Cards
        HBox cardsRow = new HBox(25);

        VBox card1 = buildCard("PROJECT PROFIT DISTRIBUTION", "$1.4M", "TOTAL", true);
        card1.setOnMouseClicked(e -> ReportModals.showProfitDistribution(stage /*, viewModel */));

        VBox card2 = buildCard("INCOME VS EXPENSES", "$42,500", "$18,200", false);
        card2.setOnMouseClicked(e -> ReportModals.showIncomeVsExpenses(stage /*, viewModel */));

        VBox card3 = buildCard("NET PROFIT TREND", "$842,000", "Cumulative Growth", true);
        card3.setOnMouseClicked(e -> ReportModals.showNetProfitTrend(stage /*, viewModel */));

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        card1.setMaxWidth(Double.MAX_VALUE);
        card2.setMaxWidth(Double.MAX_VALUE);
        card3.setMaxWidth(Double.MAX_VALUE);

        cardsRow.getChildren().addAll(card1, card2, card3);

        // Bottom Section: Tabs & Table
        VBox tableSection = new VBox();
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16;");
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        HBox tableHeader = buildTableTabs();

        table = new TableView<>();
        table.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        VBox.setVgrow(table, Priority.ALWAYS);
        setupTableColumns();
        // table.setItems(viewModel.getMonthlySnapshots());

        HBox pagination = buildPagination();

        tableSection.getChildren().addAll(tableHeader, table, pagination);
        contentArea.getChildren().addAll(cardsRow, tableSection);

        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        // Load the CSS so the TableView headers aren't gray and 3D!
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS files. Ensure they exist in src/main/resources/styles/");
        }

        stage.setTitle("Admin - Reports");
    }

    @Override
    protected void setStyle() {
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {}

    // Card Builder
    private VBox buildCard(String titleText, String mainVal, String subVal, boolean singleStat) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        HBox header = new HBox();
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        // Expand icon
        Label expandIcon = new Label();

        expandIcon.setGraphic(IconFactory.getIcon("layout-dashboard", 16));
        expandIcon.setOpacity(0.5);
        header.getChildren().addAll(title, spacer, expandIcon);

        // Placeholder Chart Region
        Region chartPlaceholder = new Region();
        // Increase this from 150 to 220 to scale the whole card up!
        chartPlaceholder.setMinHeight(220);
        chartPlaceholder.setPrefHeight(220);
        chartPlaceholder.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8;");

        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_LEFT);

        if (singleStat) {
            footer.setAlignment(Pos.CENTER);
            VBox statBox = new VBox(2);
            statBox.setAlignment(Pos.CENTER);

            Label val = new Label(mainVal);
            val.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #111827;");

            Label sub = new Label(subVal);
            sub.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            statBox.getChildren().addAll(val, sub);
            footer.getChildren().add(statBox);
        } else {
            VBox stat1 = new VBox(2);
            Label lbl1 = new Label("GROSS INCOME"); lbl1.setStyle("-fx-font-size: 9px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            Label val1 = new Label(mainVal); val1.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + Themes.PRIMARY + ";");
            stat1.getChildren().addAll(lbl1, val1);

            Region footerSpacer = new Region();
            HBox.setHgrow(footerSpacer, Priority.ALWAYS);

            VBox stat2 = new VBox(2);
            stat2.setAlignment(Pos.CENTER_RIGHT);

            Label lbl2 = new Label("NET EXPENSE"); lbl2.setStyle("-fx-font-size: 9px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            Label val2 = new Label(subVal); val2.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #111827;");
            stat2.getChildren().addAll(lbl2, val2);

            footer.getChildren().addAll(stat1, footerSpacer, stat2);
        }

        card.getChildren().addAll(header, chartPlaceholder, footer);
        return card;
    }

    // Tabs & Filters
    private HBox buildTableTabs() {
        HBox header = new HBox(15);

        header.setAlignment(Pos.BOTTOM_LEFT);
        header.setPadding(new Insets(0, 20, 0, 20));
        header.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        header.setMinHeight(60);

        String activeStyle = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 10 15 10; -fx-cursor: hand;";
        String inactiveStyle = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-padding: 0 10 15 10; -fx-cursor: hand;";

        Label tab1 = new Label("P&L Summary"); tab1.setStyle(inactiveStyle);
        Label tab2 = new Label("Income"); tab2.setStyle(inactiveStyle);
        Label tab3 = new Label("Expenses"); tab3.setStyle(inactiveStyle);
        Label tab4 = new Label("HR & Payroll"); tab4.setStyle(inactiveStyle);
        Label tab5 = new Label("Monthly Table"); tab5.setStyle(activeStyle);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button filterBtn = new Button("Filters");
        filterBtn.setGraphic(IconFactory.getIcon("layout-dashboard", 14));
        filterBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0; -fx-padding: 8 16;");
        filterBtn.setOnAction(e -> ReportModals.showQuickFilters(stage /*, viewModel */));

        Button exportBtn = new Button("Export CSV");
        exportBtn.setGraphic(IconFactory.getWhiteIcon("receipt", 14));
        exportBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0; -fx-padding: 8 16;");

        HBox.setMargin(exportBtn, new Insets(0, 0, 10, 0));
        HBox.setMargin(filterBtn, new Insets(0, 0, 10, 0));

        addClickEffect(filterBtn);
        addClickEffect(exportBtn);

        header.getChildren().addAll(tab1, tab2, tab3, tab4, tab5, spacer, filterBtn, exportBtn);

        return header;
    }

    private void setupTableColumns() {
        TableColumn<Object, String> idCol = new TableColumn<>("#");
        TableColumn<Object, String> entityCol = new TableColumn<>("ENTITY NAME");
        TableColumn<Object, String> classCol = new TableColumn<>("CLASSIFICATION");
        TableColumn<Object, String> dateCol = new TableColumn<>("DATE POSTED");
        TableColumn<Object, String> amountCol = new TableColumn<>("AMOUNT");
        TableColumn<Object, String> statusCol = new TableColumn<>("STATUS");
        TableColumn<Object, String> actionCol = new TableColumn<>("ACTION");

        table.getColumns().addAll(idCol, entityCol, classCol, dateCol, amountCol, statusCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox buildPagination() {
        HBox pagination = new HBox(10);

        pagination.setAlignment(Pos.CENTER_RIGHT);
        pagination.setPadding(new Insets(20));
        pagination.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");

        Label info = new Label("Showing 1 to 4 of 128 transactions");
        info.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button prev = new Button("<"); prev.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-cursor: hand;");
        Button page1 = new Button("1"); page1.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-background-radius: 6;");
        Button page2 = new Button("2"); page2.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-cursor: hand;");
        Button next = new Button(">"); next.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-cursor: hand;");

        pagination.getChildren().addAll(info, spacer, prev, page1, page2, next);

        return pagination;
    }

    // Helper Methods
    private void addClickEffect(Button btn) {
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }
}