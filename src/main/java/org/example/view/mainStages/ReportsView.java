package org.example.view.mainStages;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.example.model.database.entity.Employee;
import org.example.model.reports.*;
import org.example.view.templates.*;
import org.example.viewModel.ReportsViewModel;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportsView extends BaseView {

    private final ReportsViewModel viewModel = new ReportsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private StackPane panelHost;

    private int activeTab = 4;
    private final Node[] panelCache = new Node[5];
    private final List<Double> momValues = new ArrayList<>();

    private Label tab1, tab2, tab3, tab4, tab5;
    private Button filterBtn, exportBtn;

    private static final String ACTIVE_TAB   = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY +
            "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 10 15 10; -fx-cursor: hand;";
    private static final String INACTIVE_TAB = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED +
            "; -fx-padding: 0 10 15 10; -fx-cursor: hand;";

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        Label title = new Label(I18n.t("Reports"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Reports")));
        Label subTitle = new Label(I18n.t("Financial overview · FY 2024"));
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        I18n.language.addListener((obs, o, v) -> subTitle.setText(I18n.t("Financial overview · FY 2024")));
        titleBox.getChildren().addAll(title, subTitle);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        topBar.getChildren().addAll(titleBox, topSpacer);

        contentArea = new VBox(25);
        contentArea.setPadding(new Insets(30, 40, 40, 40));

        HBox cardsRow = new HBox(25);
        VBox card1 = buildCard("PROJECT PROFIT DISTRIBUTION", viewModel.totalProfitProperty(), null, true,
                new ProjectPieChart(viewModel.getProjectSummaries(), 200));
        card1.setOnMouseClicked(e -> ReportModals.showProfitDistribution(stage, viewModel));
        VBox card2 = buildCard("INCOME VS EXPENSES", viewModel.grossIncomeProperty(), viewModel.netExpenseProperty(), false,
                new IncomeExpenseBarChart(viewModel.getMonthlySummaries()));
        card2.setOnMouseClicked(e -> ReportModals.showIncomeVsExpenses(stage, viewModel));
        VBox card3 = buildCard("NET PROFIT TREND", viewModel.netProfitTrendProperty(), "Cumulative Growth", true,
                new TrendLineChart(viewModel.getMonthlySummaries()));
        card3.setOnMouseClicked(e -> ReportModals.showNetProfitTrend(stage, viewModel));
        HBox.setHgrow(card1, Priority.ALWAYS); HBox.setHgrow(card2, Priority.ALWAYS); HBox.setHgrow(card3, Priority.ALWAYS);
        card1.setMaxWidth(Double.MAX_VALUE); card2.setMaxWidth(Double.MAX_VALUE); card3.setMaxWidth(Double.MAX_VALUE);
        cardsRow.getChildren().addAll(card1, card2, card3);

        VBox tableSection = new VBox();
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16;");
        tableSection.setMinHeight(500);

        HBox tableHeader = buildTableTabs();

        panelHost = new StackPane();
        panelHost.setMinHeight(420);
        VBox.setVgrow(panelHost, Priority.ALWAYS);

        HBox pagination = buildPagination();
        tableSection.getChildren().addAll(tableHeader, panelHost, pagination);

        contentArea.getChildren().addAll(cardsRow, tableSection);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: " + Themes.BG_DASHBOARD +
                "; -fx-background-color: " + Themes.BG_DASHBOARD + "; -fx-border-width: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(topBar, scrollPane);
        root.setCenter(mainContainer);

        scene = new Scene(root);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS files.");
        }
        stage.setTitle("Admin - Reports");
    }

    @Override
    protected void setStyle() {
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        if (!viewModel.hasAccessProperty().get()) {
            showAccessDenied();
            return;
        }

        exportBtn.setOnAction(e -> exportCSV());
        filterBtn.setOnAction(e -> ReportModals.showTabFilters(stage, viewModel, activeTab));
        setActiveTab(activeTab);
    }

    private void setActiveTab(int idx) {
        activeTab = idx;
        Label[] tabs = {tab1, tab2, tab3, tab4, tab5};
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setStyle(i == idx ? ACTIVE_TAB : INACTIVE_TAB);
        }
        showPanel(getPanel(idx));
    }

    private void showPanel(Node panel) {
        panelHost.getChildren().setAll(panel);
    }

    private Node getPanel(int idx) {
        if (panelCache[idx] == null) {
            panelCache[idx] = switch (idx) {
                case 0 -> buildPLSummaryPanel();
                case 1 -> buildIncomePanel();
                case 2 -> buildExpensePanel();
                case 3 -> buildHRPanel();
                case 4 -> buildMonthlyTablePanel();
                default -> new Label("Unknown panel");
            };
        }
        return panelCache[idx];
    }

    private Node buildPLSummaryPanel() {
        Label revVal   = new Label(CurrencyFormatter.symbol() + "0");
        Label costsVal = new Label(CurrencyFormatter.symbol() + "0");
        Label grossVal = new Label(CurrencyFormatter.symbol() + "0");
        Label netVal   = new Label(CurrencyFormatter.symbol() + "0");

        Label ledgerRevVal    = new Label(CurrencyFormatter.symbol() + "0");
        Label ledgerCostsVal  = new Label(CurrencyFormatter.symbol() + "0");
        Label ledgerGrossVal  = new Label(CurrencyFormatter.symbol() + "0");
        Label ledgerOpexVal   = new Label(CurrencyFormatter.symbol() + "0");
        Label ledgerNetVal    = new Label(CurrencyFormatter.symbol() + "0");
        Label ledgerMarginVal = new Label("0.0%");

        Runnable refresh = () -> {
            BigDecimal rev    = viewModel.getProjectSummaries().stream()
                    .map(ProjectSummaryDTO::getTotalIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal costs  = viewModel.getProjectSummaries().stream()
                    .map(ProjectSummaryDTO::getTotalExpense).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal gross  = rev.subtract(costs);
            BigDecimal net    = gross;
            double margin     = rev.compareTo(BigDecimal.ZERO) == 0 ? 0
                    : net.divide(rev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();

            revVal.setText(CurrencyFormatter.format(rev));
            costsVal.setText(CurrencyFormatter.format(costs));
            grossVal.setText(CurrencyFormatter.format(gross));
            netVal.setText(CurrencyFormatter.format(net));

            ledgerRevVal.setText(CurrencyFormatter.format(rev));
            ledgerCostsVal.setText(CurrencyFormatter.format(costs));
            ledgerGrossVal.setText(CurrencyFormatter.format(gross));
            ledgerOpexVal.setText(CurrencyFormatter.format(costs));
            ledgerNetVal.setText(CurrencyFormatter.format(net));
            ledgerMarginVal.setText(String.format("%.1f%%", margin));

            String grossColor  = gross.compareTo(BigDecimal.ZERO) >= 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
            String netColor    = net.compareTo(BigDecimal.ZERO)   >= 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
            String marginColor = margin >= 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
            ledgerGrossVal.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + grossColor + ";");
            ledgerNetVal.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + netColor + ";");
            ledgerMarginVal.setStyle("-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + marginColor + ";");
        };

        refresh.run();
        viewModel.getProjectSummaries().addListener((ListChangeListener<ProjectSummaryDTO>) c -> refresh.run());

        HBox cards = new HBox(16);
        cards.setPadding(new Insets(20, 20, 0, 20));
        cards.getChildren().addAll(
                createStatCard("TOTAL REVENUE",    revVal,   Themes.PRIMARY),
                createStatCard("TOTAL COSTS",      costsVal, Themes.TEXT_ERROR),
                createStatCard("GROSS PROFIT",     grossVal, Themes.TEXT_SUCCESS),
                createStatCard("NET PROFIT",        netVal,   Themes.TEXT_SUCCESS)
        );
        for (Node c : cards.getChildren()) HBox.setHgrow(c, Priority.ALWAYS);

        GridPane ledger = new GridPane();
        ledger.setHgap(0); ledger.setVgap(0);
        ledger.setPadding(new Insets(16, 20, 20, 20));
        ColumnConstraints cc1 = new ColumnConstraints(); cc1.setHgrow(Priority.ALWAYS);
        ColumnConstraints cc2 = new ColumnConstraints(); cc2.setMinWidth(160); cc2.setHalignment(javafx.geometry.HPos.RIGHT);
        ledger.getColumnConstraints().addAll(cc1, cc2);

        String[][] rows = {
                {"Revenue", null}, {"Cost of Goods", null}, {"Gross Profit", null},
                {"Operating Expenses", null}, {"Net Profit", null}, {"Margin %", null}
        };
        Label[] valueLabels = {ledgerRevVal, ledgerCostsVal, ledgerGrossVal, ledgerOpexVal, ledgerNetVal, ledgerMarginVal};
        String[] defaultStyle = {
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";",
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";",
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";",
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";",
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";",
                "-fx-font-size: 16px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_SUCCESS + ";"
        };

        for (int i = 0; i < rows.length; i++) {
            boolean odd = (i % 2 == 0);
            String bg = odd ? "-fx-background-color: #F8FAFC;" : "-fx-background-color: white;";

            HBox rowLeft = new HBox();
            rowLeft.setStyle(bg + "-fx-padding: 12 16;");
            rowLeft.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label(rows[i][0]);
            lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            rowLeft.getChildren().add(lbl);

            HBox rowRight = new HBox();
            rowRight.setStyle(bg + "-fx-padding: 12 16;");
            rowRight.setAlignment(Pos.CENTER_RIGHT);
            valueLabels[i].setStyle(defaultStyle[i]);
            rowRight.getChildren().add(valueLabels[i]);

            ledger.add(rowLeft, 0, i);
            ledger.add(rowRight, 1, i);
        }

        VBox panel = new VBox(0);
        panel.getChildren().addAll(cards, ledger);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private Node buildIncomePanel() {
        TableView<IncomeBreakdownDTO> table = new TableView<>();
        table.setItems(viewModel.getIncomeBreakdown());
        table.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<IncomeBreakdownDTO, String> srcCol = new TableColumn<>("SOURCE");
        srcCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getProjectName()));

        TableColumn<IncomeBreakdownDTO, String> amtCol = new TableColumn<>("AMOUNT");
        amtCol.setPrefWidth(130); amtCol.setMaxWidth(130);
        amtCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(CurrencyFormatter.format(d.getValue().getAmount())));
        amtCol.setCellFactory(col -> centerAlignCell());
        amtCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<IncomeBreakdownDTO, String> pctCol = new TableColumn<>("% OF TOTAL");
        pctCol.setPrefWidth(120); pctCol.setMaxWidth(120);
        pctCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(String.format("%.1f%%", d.getValue().getPercentage())));
        pctCol.setCellFactory(col -> centerAlignCell());
        pctCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<IncomeBreakdownDTO, IncomeBreakdownDTO> barCol = new TableColumn<>("");
        barCol.setPrefWidth(160); barCol.setMaxWidth(160);
        barCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        barCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(IncomeBreakdownDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                setGraphic(buildMiniBar(item.getPercentage()));
            }
        });

        table.getColumns().addAll(srcCol, amtCol, pctCol, barCol);

        VBox panel = new VBox(0);
        panel.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private Node buildExpensePanel() {
        TableView<ExpenseCategoryDTO> table = new TableView<>();
        table.setItems(viewModel.getExpenseCategories());
        table.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ExpenseCategoryDTO, String> catCol = new TableColumn<>("CATEGORY");
        catCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getCategory()));

        TableColumn<ExpenseCategoryDTO, String> amtCol = new TableColumn<>("AMOUNT");
        amtCol.setPrefWidth(130); amtCol.setMaxWidth(130);
        amtCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(CurrencyFormatter.format(d.getValue().getAmount())));
        amtCol.setCellFactory(col -> centerAlignCell());
        amtCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ExpenseCategoryDTO, String> pctCol = new TableColumn<>("% OF TOTAL");
        pctCol.setPrefWidth(120); pctCol.setMaxWidth(120);
        pctCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(String.format("%.1f%%", d.getValue().getPercentage())));
        pctCol.setCellFactory(col -> centerAlignCell());
        pctCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<ExpenseCategoryDTO, ExpenseCategoryDTO> barCol = new TableColumn<>("");
        barCol.setPrefWidth(160); barCol.setMaxWidth(160);
        barCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        barCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(ExpenseCategoryDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                setGraphic(buildMiniBar(item.getPercentage()));
            }
        });

        table.getColumns().addAll(catCol, amtCol, pctCol, barCol);

        VBox panel = new VBox(0);
        panel.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private Node buildHRPanel() {
        TableView<Employee> table = new TableView<>();
        table.setItems(viewModel.getFilteredEmployees());
        table.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Employee, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getName() + " " + d.getValue().getSurname()));

        TableColumn<Employee, String> posCol = new TableColumn<>("POSITION");
        posCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPosition()));

        TableColumn<Employee, String> deptCol = new TableColumn<>("DEPARTMENT");
        deptCol.setCellValueFactory(d -> {
            String dept = d.getValue().getDepartment();
            return new ReadOnlyObjectWrapper<>((dept == null || dept.isBlank()) ? "—" : dept);
        });

        TableColumn<Employee, String> salaryCol = new TableColumn<>("MONTHLY SALARY");
        salaryCol.setPrefWidth(150);
        salaryCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(
                d.getValue().getSalary() != null ? CurrencyFormatter.format(d.getValue().getSalary()) : "—"));
        salaryCol.setCellFactory(col -> centerAlignCell());
        salaryCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Employee, String> statusCol = new TableColumn<>("STATUS");
        statusCol.setPrefWidth(110);
        statusCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); return; }
                boolean active = "ACTIVE".equalsIgnoreCase(item);
                Label pill = new Label(item);
                pill.setStyle(
                        "-fx-background-color: " + (active ? "#DCFCE7" : "#F3F4F6") + ";" +
                                "-fx-text-fill: " + (active ? "#16A34A" : Themes.TEXT_MUTED) + ";" +
                                "-fx-font-size: 11px; -fx-font-weight: bold;" +
                                "-fx-background-radius: 12; -fx-padding: 3 8;");
                HBox box = new HBox(pill);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
                setText(null);
            }
        });

        table.getColumns().addAll(nameCol, posCol, deptCol, salaryCol, statusCol);

        Label payrollLbl = new Label("TOTAL PAYROLL");
        payrollLbl.setStyle("-fx-font-weight: 900; -fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label payrollVal = new Label(CurrencyFormatter.symbol() + "0");
        payrollVal.setStyle("-fx-font-weight: 900; -fx-font-size: 16px; -fx-text-fill: " + Themes.PRIMARY + ";");

        Runnable updateTotal = () -> {
            BigDecimal total = viewModel.getEmployees().stream()
                    .filter(e -> e.getSalary() != null)
                    .map(Employee::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            payrollVal.setText(CurrencyFormatter.format(total));
        };
        updateTotal.run();
        viewModel.getEmployees().addListener((ListChangeListener<Employee>) c -> updateTotal.run());

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(12, 20, 12, 20));
        footer.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        footer.getChildren().addAll(payrollLbl, footerSpacer, payrollVal);

        VBox panel = new VBox(0);
        panel.getChildren().addAll(table, footer);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private Node buildMonthlyTablePanel() {
        TableView<MonthlySnapshotDTO> table = new TableView<>();
        table.setItems(viewModel.getMonthlySummaries());
        table.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        DateTimeFormatter periodFmt = DateTimeFormatter.ofPattern("MMM yyyy");

        TableColumn<MonthlySnapshotDTO, String> monthCol = new TableColumn<>("MONTH");
        monthCol.setPrefWidth(120);
        monthCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPeriod().format(periodFmt)));

        TableColumn<MonthlySnapshotDTO, String> incCol = new TableColumn<>("INCOME");
        incCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(CurrencyFormatter.format(d.getValue().getIncome())));
        incCol.setCellFactory(col -> centerAlignCell());
        incCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<MonthlySnapshotDTO, String> expCol = new TableColumn<>("EXPENSES");
        expCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(CurrencyFormatter.format(d.getValue().getExpense())));
        expCol.setCellFactory(col -> centerAlignCell());
        expCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<MonthlySnapshotDTO, BigDecimal> netCol = new TableColumn<>("NET PROFIT");
        netCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getNetProfit()));
        netCol.setCellFactory(col -> centerAlignDecimalCell(true));
        netCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<MonthlySnapshotDTO, MonthlySnapshotDTO> momCol = new TableColumn<>("MOM CHANGE");
        momCol.setPrefWidth(120);
        momCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        momCol.setStyle("-fx-alignment: CENTER;");
        momCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(MonthlySnapshotDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-alignment: CENTER; -fx-padding: 0;"); return; }
                int idx = getTableRow() != null ? getTableRow().getIndex() : -1;
                if (idx <= 0 || idx >= momValues.size() || momValues.get(idx) == null) {
                    setText("—");
                    setStyle("-fx-alignment: CENTER; -fx-padding: 0; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
                } else {
                    double pct = momValues.get(idx);
                    setText(String.format("%+.1f%%", pct));
                    setStyle("-fx-alignment: CENTER; -fx-padding: 0; -fx-font-weight: bold; -fx-text-fill: " + (pct > 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR) + ";");
                }
            }
        });

        table.getColumns().addAll(monthCol, incCol, expCol, netCol, momCol);

        Runnable rebuildMom = () -> {
            momValues.clear();
            List<MonthlySnapshotDTO> data = viewModel.getMonthlySummaries();
            for (int i = 0; i < data.size(); i++) {
                if (i == 0) { momValues.add(null); continue; }
                BigDecimal prev = data.get(i - 1).getNetProfit();
                BigDecimal curr = data.get(i).getNetProfit();
                if (prev.compareTo(BigDecimal.ZERO) != 0) {
                    double pct = curr.subtract(prev)
                            .divide(prev.abs(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue();
                    momValues.add(pct);
                } else {
                    momValues.add(null);
                }
            }
            table.refresh();
        };

        rebuildMom.run();
        viewModel.getMonthlySummaries().addListener((ListChangeListener<MonthlySnapshotDTO>) c -> rebuildMom.run());

        VBox panel = new VBox(0);
        panel.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private <T> TableCell<T, String> centerAlignCell() {
        return new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-alignment: CENTER; -fx-padding: 0;");
            }
        };
    }

    private <T> TableCell<T, BigDecimal> centerAlignDecimalCell(boolean colored) {
        return new TableCell<>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-alignment: CENTER; -fx-padding: 0;"); return; }
                setText(CurrencyFormatter.format(item));
                String extraStyle = "";
                if (colored) {
                    int cmp = item.compareTo(BigDecimal.ZERO);
                    String color = cmp > 0 ? Themes.TEXT_SUCCESS : (cmp < 0 ? Themes.TEXT_ERROR : Themes.TEXT_MUTED);
                    extraStyle = "-fx-font-weight: bold; -fx-text-fill: " + color + "; ";
                }
                setStyle("-fx-alignment: CENTER; -fx-padding: 0; " + extraStyle);
            }
        };
    }

    private StackPane buildMiniBar(double pct) {
        double clampedPct = Math.max(0, Math.min(pct, 100));
        Region track = new Region();
        track.setPrefSize(110, 8); track.setMaxSize(110, 8);
        track.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 4;");
        Region fill = new Region();
        fill.setPrefSize(clampedPct * 1.1, 8); fill.setMaxSize(clampedPct * 1.1, 8);
        fill.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-background-radius: 4;");
        StackPane bar = new StackPane(track, fill);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefSize(110, 8); bar.setMaxSize(110, 8);
        return bar;
    }

    private VBox createStatCard(String labelText, Label valueLabel, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");
        card.setMaxWidth(Double.MAX_VALUE);
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        valueLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + accentColor + ";");
        Region accent = new Region();
        accent.setPrefHeight(3);
        accent.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 2;");
        card.getChildren().addAll(lbl, valueLabel, accent);
        return card;
    }

    private HBox buildTableTabs() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.BOTTOM_LEFT);
        header.setPadding(new Insets(0, 20, 0, 20));
        header.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        header.setMinHeight(60);

        tab1 = new Label("P&L Summary");   tab1.setStyle(INACTIVE_TAB);
        tab2 = new Label("Income");         tab2.setStyle(INACTIVE_TAB);
        tab3 = new Label("Expenses");       tab3.setStyle(INACTIVE_TAB);
        tab4 = new Label("HR & Payroll");   tab4.setStyle(INACTIVE_TAB);
        tab5 = new Label("Monthly Table");  tab5.setStyle(ACTIVE_TAB);

        tab1.setOnMouseClicked(e -> setActiveTab(0));
        tab2.setOnMouseClicked(e -> setActiveTab(1));
        tab3.setOnMouseClicked(e -> setActiveTab(2));
        tab4.setOnMouseClicked(e -> setActiveTab(3));
        tab5.setOnMouseClicked(e -> setActiveTab(4));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        filterBtn = new Button("Filters");
        filterBtn.setGraphic(IconFactory.getIcon("funnel", 14));
        filterBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #111827; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0; -fx-padding: 8 16;");

        exportBtn = new Button("Export CSV");
        exportBtn.setGraphic(IconFactory.getWhiteIcon("receipt", 14));
        exportBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0; -fx-padding: 8 16;");

        HBox.setMargin(exportBtn, new Insets(0, 0, 10, 0));
        HBox.setMargin(filterBtn, new Insets(0, 0, 10, 0));
        addClickEffect(filterBtn);
        addClickEffect(exportBtn);

        header.getChildren().addAll(tab1, tab2, tab3, tab4, tab5, spacer, filterBtn, exportBtn);
        return header;
    }

    private VBox buildCard(String titleKey, Object mainVal, Object subVal, boolean singleStat, Node chartContent) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.02), 10, 0, 0, 4);");

        HBox header = new HBox();
        Label titleLbl = new Label(I18n.t(titleKey));
        titleLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-letter-spacing: 1px;");
        I18n.language.addListener((obs, o, v) -> titleLbl.setText(I18n.t(titleKey)));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label expandIcon = new Label();
        expandIcon.setGraphic(IconFactory.getIcon("chart-column-big", 16));
        expandIcon.setOpacity(0.5);
        header.getChildren().addAll(titleLbl, spacer, expandIcon);

        if (chartContent instanceof Region r) { r.setMinHeight(160); r.setPrefHeight(160); r.setMaxHeight(160); }
        VBox.setVgrow(chartContent, Priority.ALWAYS);

        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_LEFT);

        if (singleStat) {
            footer.setAlignment(Pos.CENTER);
            VBox statBox = new VBox(2);
            statBox.setAlignment(Pos.CENTER);
            Label val = new Label();
            if (mainVal instanceof javafx.beans.value.ObservableValue<?> strProp)
                val.textProperty().bind((javafx.beans.value.ObservableValue<? extends String>) strProp);
            else if (mainVal != null) val.setText(mainVal.toString());
            val.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: #111827;");
            Label sub = new Label();
            if (subVal instanceof javafx.beans.value.ObservableValue<?> strProp)
                sub.textProperty().bind((javafx.beans.value.ObservableValue<? extends String>) strProp);
            else if (subVal != null) sub.setText(subVal.toString());
            else sub.setText("TOTAL");
            sub.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            statBox.getChildren().addAll(val, sub);
            footer.getChildren().add(statBox);
        } else {
            VBox stat1 = new VBox(2);
            Label lbl1 = new Label(I18n.t("GROSS INCOME"));
            lbl1.setStyle("-fx-font-size: 9px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            I18n.language.addListener((obs, o, v) -> lbl1.setText(I18n.t("GROSS INCOME")));
            Label val1 = new Label();
            if (mainVal instanceof javafx.beans.value.ObservableValue<?> strProp)
                val1.textProperty().bind((javafx.beans.value.ObservableValue<? extends String>) strProp);
            else if (mainVal != null) val1.setText(mainVal.toString());
            val1.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + Themes.PRIMARY + ";");
            stat1.getChildren().addAll(lbl1, val1);

            Region footerSpacer = new Region();
            HBox.setHgrow(footerSpacer, Priority.ALWAYS);

            VBox stat2 = new VBox(2);
            stat2.setAlignment(Pos.CENTER_RIGHT);
            Label lbl2 = new Label(I18n.t("NET EXPENSE"));
            lbl2.setStyle("-fx-font-size: 9px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            I18n.language.addListener((obs, o, v) -> lbl2.setText(I18n.t("NET EXPENSE")));
            Label val2 = new Label();
            if (subVal instanceof javafx.beans.value.ObservableValue<?> strProp)
                val2.textProperty().bind((javafx.beans.value.ObservableValue<? extends String>) strProp);
            else if (subVal != null) val2.setText(subVal.toString());
            val2.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #111827;");
            stat2.getChildren().addAll(lbl2, val2);
            footer.getChildren().addAll(stat1, footerSpacer, stat2);
        }

        card.getChildren().addAll(header, chartContent, footer);
        return card;
    }

    private HBox buildPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_RIGHT);
        pagination.setPadding(new Insets(20));
        pagination.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        Label info = new Label("Showing all records");
        info.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        pagination.getChildren().addAll(info, spacer);
        return pagination;
    }

    private void exportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Monthly Report");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("monthly_report.csv");
        java.io.File file = fc.showSaveDialog(stage);
        if (file == null) return;

        DateTimeFormatter fmtPeriod = DateTimeFormatter.ofPattern("MMM yyyy");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Month,Income,Expenses,Net Profit");
            for (MonthlySnapshotDTO row : viewModel.getMonthlySummaries()) {
                pw.printf("\"%s\",%s,%s,%s%n",
                        row.getPeriod().format(fmtPeriod),
                        row.getIncome().toPlainString(),
                        row.getExpense().toPlainString(),
                        row.getNetProfit().toPlainString());
            }
            ToastManager.showSuccess(stage, "CSV exported successfully");
        } catch (Exception ex) {
            ToastManager.showError(stage, "Export failed: " + ex.getMessage());
        }
    }

    private void addClickEffect(Button btn) {
        btn.setOnMousePressed(e  -> { btn.setScaleX(0.95); btn.setScaleY(0.95); });
        btn.setOnMouseReleased(e -> { btn.setScaleX(1.0);  btn.setScaleY(1.0);  });
    }
}