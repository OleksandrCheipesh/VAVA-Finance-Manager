package org.example.view.templates;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.viewModel.ReportsViewModel;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.example.model.database.entity.Project;
import javafx.collections.ListChangeListener;

public class ReportModals {

    // --- 1. Project Profit Distribution ---
    public static void showProfitDistribution(Stage owner, ReportsViewModel vm) {
        ProjectPieChart chart = new ProjectPieChart(vm.getProjectSummaries(), 400);
        chart.setMinHeight(320);
        chart.setPrefHeight(320);

        VBox extraFilters = new VBox(15);

        VBox threshBox = new VBox(5);
        Label threshLbl = new Label(I18n.t("MIN. PROFIT THRESHOLD"));
        threshLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        TextField thresholdField = UIFactory.inputField(CurrencyFormatter.symbol() + " 0.00");
        thresholdField.setMinHeight(44);
        thresholdField.setPrefHeight(44);
        thresholdField.setMaxHeight(44);
        thresholdField.setMaxWidth(Double.MAX_VALUE);

        threshBox.getChildren().addAll(threshLbl, thresholdField);

        HBox presets = new HBox(8);
        Label presetLbl = new Label(I18n.t("Quick preset:"));
        presetLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Button p10  = createPresetBtn(CurrencyFormatter.symbol() + "10k",  "10000",  thresholdField);
        Button p50  = createPresetBtn(CurrencyFormatter.symbol() + "50k",  "50000",  thresholdField);
        Button p100 = createPresetBtn(CurrencyFormatter.symbol() + "100k", "100000", thresholdField);
        presets.getChildren().addAll(presetLbl, p10, p50, p100);
        presets.setAlignment(Pos.CENTER_LEFT);

        extraFilters.getChildren().addAll(threshBox, presets);

        showBaseModal(owner, I18n.t("Project Profit Distribution"), chart, extraFilters, vm,
                () -> {
                    String raw = thresholdField.getText().replaceAll("[$,\\s]", "");
                    if (!raw.isEmpty()) {
                        try {
                            BigDecimal threshold = new BigDecimal(raw);
                            var filtered = vm.getProjectSummaries().stream()
                                    .filter(p -> p.getNetProfit().compareTo(threshold) >= 0)
                                    .collect(Collectors.toList());
                            chart.rebuildFrom(filtered);
                        } catch (NumberFormatException ignored) {}
                    } else {
                        chart.rebuildFrom(vm.getProjectSummaries());
                    }
                });
    }

    // --- 2. Income VS Expenses ---
    public static void showIncomeVsExpenses(Stage owner, ReportsViewModel vm) {
        IncomeExpenseBarChart chart = new IncomeExpenseBarChart(vm.getMonthlySummaries());
        chart.setMinHeight(320);
        chart.setPrefHeight(320);

        VBox extraFilters = new VBox(15);

        VBox projBox = new VBox(5);
        Label projLbl = new Label(I18n.t("PROJECT SELECTION"));
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> projectCombo = UIFactory.inputComboBox(I18n.t("All Projects"));
        projectCombo.getItems().add(I18n.t("All Projects"));
        projectCombo.getItems().addAll(vm.getAvailableProjects().stream().map(Project::getName).collect(Collectors.toList()));
        projectCombo.setValue(I18n.t("All Projects"));

        vm.getAvailableProjects().addListener((ListChangeListener<Project>) c -> {
            java.util.List<String> names = vm.getAvailableProjects().stream().map(Project::getName).collect(Collectors.toList());
            javafx.application.Platform.runLater(() -> {
                String current = projectCombo.getValue();
                projectCombo.getItems().setAll(I18n.t("All Projects"));
                projectCombo.getItems().addAll(names);
                if (projectCombo.getItems().contains(current)) projectCombo.setValue(current);
                else projectCombo.setValue(I18n.t("All Projects"));
            });
        });
        projectCombo.setMinHeight(44); projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44); projectCombo.setMaxWidth(Double.MAX_VALUE);

        projectCombo.setOnAction(e -> {
            String selected = projectCombo.getValue();
            vm.setSelectedProject(I18n.t("All Projects").equals(selected) ? null : selected);
        });

        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox();
        groupAndToggles.setAlignment(Pos.TOP_LEFT);

        VBox groupBox = new VBox(5);
        Label groupLbl = new Label(I18n.t("GROUPING"));
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        HBox groupBtns = createSegmentedControl(I18n.t("Month"), I18n.t("Quarter"),
                selected -> vm.setGrouping(selected));
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        HBox incomeToggle  = createToggle(I18n.t("Show Income"),   true,  (on) -> chart.setSeriesVisible("Income",   on));
        HBox expenseToggle = createToggle(I18n.t("Show Expenses"), false, (on) -> chart.setSeriesVisible("Expenses", on));

        VBox togglesBox = new VBox(12);
        togglesBox.getChildren().addAll(incomeToggle, expenseToggle);
        HBox.setMargin(togglesBox, new Insets(17, 0, 0, 0));

        groupAndToggles.getChildren().addAll(groupBox, centerSpacer, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, I18n.t("Income VS Expenses"), chart, extraFilters, vm, null);
    }

    // --- 3. Net Profit Trend ---
    public static void showNetProfitTrend(Stage owner, ReportsViewModel vm) {
        TrendLineChart chart = new TrendLineChart(vm.getMonthlySummaries());
        chart.setMinHeight(320);
        chart.setPrefHeight(320);

        chart.sceneProperty().addListener((obs, o, newScene) -> {
            if (newScene != null) chart.setZeroLineVisible(true);
        });

        VBox extraFilters = new VBox(15);

        VBox projBox = new VBox(5);
        Label projLbl = new Label(I18n.t("PROJECT SELECTION"));
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> projectCombo = UIFactory.inputComboBox(I18n.t("All Projects"));
        projectCombo.getItems().add(I18n.t("All Projects"));
        projectCombo.getItems().addAll(vm.getAvailableProjects().stream().map(Project::getName).collect(Collectors.toList()));
        projectCombo.setValue(I18n.t("All Projects"));
        vm.getAvailableProjects().addListener((ListChangeListener<Project>) c -> {
            java.util.List<String> names = vm.getAvailableProjects().stream().map(Project::getName).collect(Collectors.toList());
            javafx.application.Platform.runLater(() -> {
                String current = projectCombo.getValue();
                projectCombo.getItems().setAll(I18n.t("All Projects"));
                projectCombo.getItems().addAll(names);
                if (projectCombo.getItems().contains(current)) projectCombo.setValue(current);
                else projectCombo.setValue(I18n.t("All Projects"));
            });
        });
        projectCombo.setMinHeight(44); projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44); projectCombo.setMaxWidth(Double.MAX_VALUE);

        projectCombo.setOnAction(e -> {
            String selected = projectCombo.getValue();
            vm.setSelectedProject(I18n.t("All Projects").equals(selected) ? null : selected);
        });

        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox();
        groupAndToggles.setAlignment(Pos.TOP_LEFT);

        VBox groupBox = new VBox(5);
        Label groupLbl = new Label(I18n.t("GROUPING"));
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        HBox groupBtns = createSegmentedControl(I18n.t("Month"), I18n.t("Quarter"),
                selected -> vm.setGrouping(selected));
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        VBox togglesBox = new VBox(8);
        togglesBox.getChildren().add(createToggle(I18n.t("Show Zero Line"), true, (on) -> chart.setZeroLineVisible(on)));
        HBox.setMargin(togglesBox, new Insets(17, 0, 0, 0));

        groupAndToggles.getChildren().addAll(groupBox, centerSpacer, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, I18n.t("Net Profit Trend"), chart, extraFilters, vm, null);
    }

    // --- SHARED MODAL LOGIC ---
    private static void showBaseModal(Stage owner,
                                      String titleText,
                                      Node chartNode,
                                      VBox extraFilters,
                                      ReportsViewModel vm,
                                      Runnable onApplyExtra) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        javafx.scene.paint.Paint originalFill = ownerScene.getFill();
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

        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(20);
        root.setPadding(new Insets(35));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(1050);

        // Header
        HBox header = new HBox();
        VBox titleBox = new VBox(2);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subTitle = new Label(I18n.t("Fiscal Year 2024 · Global Portfolio Breakdown"));
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32); closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Body
        HBox body = new HBox(30);

        VBox chartWrapper = new VBox(chartNode);
        chartWrapper.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-padding: 15;");
        chartWrapper.setPrefSize(650, 480);
        HBox.setHgrow(chartWrapper, Priority.ALWAYS);
        VBox.setVgrow(chartNode, Priority.ALWAYS);

        VBox filtersPanel = new VBox(20);
        filtersPanel.setPrefWidth(320);
        filtersPanel.setMinWidth(320);

        HBox fHeader = new HBox();
        Label fTitle = new Label(I18n.t("Filters"));
        fTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);
        Label reset = new Label(I18n.t("RESET ALL"));
        reset.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-cursor: hand;");
        fHeader.getChildren().addAll(fTitle, fSpacer, reset);

        // TIME PERIOD
        VBox timeBox = new VBox(5);
        Label timeLbl = new Label(I18n.t("TIME PERIOD"));
        timeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> timeCombo = UIFactory.inputComboBox(I18n.t("Select Period"));
        timeCombo.getItems().addAll(
                I18n.t("This Month"), I18n.t("Last Month"),
                I18n.t("This Quarter"), I18n.t("This Year"), I18n.t("Custom")
        );
        timeCombo.setValue(vm.getPeriodAsString());
        timeCombo.setMinHeight(44); timeCombo.setPrefHeight(44);
        timeCombo.setMaxHeight(44); timeCombo.setMaxWidth(Double.MAX_VALUE);

        timeCombo.valueProperty().addListener((obs, oldVal, newVal) -> vm.setPeriodFromString(newVal));
        timeBox.getChildren().addAll(timeLbl, timeCombo);

        // CUSTOM DATE RANGE
        HBox customDates = new HBox(10);

        VBox fromBox = new VBox(5);
        Label fromLbl = new Label(I18n.t("FROM"));
        fromLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker fromDP = UIFactory.inputDatePicker("mm/dd/yyyy");
        fromDP.setMinHeight(44); fromDP.setPrefHeight(44);
        fromDP.setMaxHeight(44); fromDP.setMaxWidth(Double.MAX_VALUE);
        fromDP.valueProperty().bindBidirectional(vm.customFromProperty());
        fromBox.getChildren().addAll(fromLbl, fromDP);
        HBox.setHgrow(fromBox, Priority.ALWAYS);

        VBox toBox = new VBox(5);
        Label toLbl = new Label(I18n.t("TO"));
        toLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker toDP = UIFactory.inputDatePicker("mm/dd/yyyy");
        toDP.setMinHeight(44); toDP.setPrefHeight(44);
        toDP.setMaxHeight(44); toDP.setMaxWidth(Double.MAX_VALUE);
        toDP.valueProperty().bindBidirectional(vm.customToProperty());
        toBox.getChildren().addAll(toLbl, toDP);
        HBox.setHgrow(toBox, Priority.ALWAYS);

        customDates.getChildren().addAll(fromBox, toBox);

        boolean isCustom = I18n.t("Custom").equals(timeCombo.getValue());
        customDates.setVisible(isCustom);
        customDates.setManaged(isCustom);
        timeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean custom = I18n.t("Custom").equals(newVal);
            customDates.setVisible(custom);
            customDates.setManaged(custom);
        });

        // PROJECT STATUS
        VBox statusBox = new VBox(8);
        Label statusLbl = new Label(I18n.t("PROJECT STATUS"));
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        RadioButton r1 = new RadioButton(I18n.t("All Projects"));  r1.setSelected(true); r1.getStyleClass().add("custom-radio");
        RadioButton r2 = new RadioButton(I18n.t("Active only"));   r2.getStyleClass().add("custom-radio");
        RadioButton r3 = new RadioButton(I18n.t("Inactive only")); r3.getStyleClass().add("custom-radio");

        ToggleGroup tg = new ToggleGroup();
        r1.setToggleGroup(tg); r2.setToggleGroup(tg); r3.setToggleGroup(tg);

        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == r2) vm.setSelectedStatus("Active");
            else if (newT == r3) vm.setSelectedStatus("Inactive");
            else vm.setSelectedStatus(null);
        });

        reset.setOnMouseClicked(e -> {
            vm.setPeriodFromString("This Year");
            vm.customFromProperty().set(null);
            vm.customToProperty().set(null);
            vm.setSelectedProject(null);
            vm.setSelectedStatus(null);
            tg.selectToggle(r1);
        });

        statusBox.getChildren().addAll(statusLbl, r1, r2, r3);

        // Action buttons
        HBox actions = new HBox(10);
        Button applyBtn = new Button(I18n.t("Apply Filters"));
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");
        applyBtn.setMinHeight(44); applyBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(applyBtn, Priority.ALWAYS);

        Button exportBtn = new Button(I18n.t("Export PDF"));
        exportBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");
        exportBtn.setMinHeight(44); exportBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(exportBtn, Priority.ALWAYS);

        addClickEffect(applyBtn);
        addClickEffect(exportBtn);

        applyBtn.setOnAction(e -> {
            vm.recompute();
            if (onApplyExtra != null) onApplyExtra.run();
        });

        actions.getChildren().addAll(applyBtn, exportBtn);

        filtersPanel.getChildren().addAll(fHeader, timeBox, customDates, statusBox, extraFilters, actions);
        body.getChildren().addAll(chartWrapper, filtersPanel);

        // Footer
        HBox footer = new HBox();
        Label syncLabel = new Label(I18n.t("LAST SYNCHRONIZED: 12 MINUTES AGO"));
        syncLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        footer.getChildren().add(syncLabel);

        root.getChildren().addAll(header, body, footer);
        container.getChildren().add(root);

        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);
        try { scene.getStylesheets().add(ReportModals.class.getResource("/styles/global.css").toExternalForm()); } catch (Exception e) {}
        modal.setScene(scene);

        container.setOpacity(0);
        container.setTranslateY(30);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setToY(0);
        new ParallelTransition(fadeIn, slideUp).play();
        modal.show();
    }

    // --- Quick Filters ---
    public static void showQuickFilters(Stage owner, ReportsViewModel vm) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        Scene ownerScene = owner.getScene();
        javafx.scene.paint.Paint originalFill = ownerScene.getFill();
        ownerScene.setFill(Color.web(Themes.TEXT_DARK));

        javafx.scene.Node bgRoot = ownerScene.getRoot();
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.3);
        GaussianBlur blur = new GaussianBlur(15);
        blur.setInput(darken);
        bgRoot.setEffect(blur);

        modal.setOnHidden(e -> {
            bgRoot.setEffect(null);
            ownerScene.setFill(originalFill);
        });

        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(25);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(380);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleIcon = new Label();
        titleIcon.setGraphic(IconFactory.getIcon("sliders-vertical", 18));
        titleIcon.setPadding(new Insets(0, 10, 0, 0));
        Label title = new Label(I18n.t("Quick Filters"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("X");
        closeBtn.setMinSize(32, 32); closeBtn.setMaxSize(32, 32);
        closeBtn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;");
        closeBtn.setOnAction(e -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleIcon, title, spacer, closeBtn);

        HBox presetBtns = createSegmentedControl(I18n.t("This Month"), I18n.t("Last 6 Months"));

        // Date Range
        HBox dateBox = new HBox(15);
        VBox startBox = new VBox(5);
        Label startLbl = new Label(I18n.t("START DATE"));
        startLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker startDP = UIFactory.inputDatePicker("Jan 01, 2024");
        startDP.setMinHeight(44); startDP.setPrefHeight(44);
        startDP.setMaxHeight(44); startDP.setMaxWidth(Double.MAX_VALUE);
        startDP.valueProperty().bindBidirectional(vm.customFromProperty());
        startBox.getChildren().addAll(startLbl, startDP);
        HBox.setHgrow(startBox, Priority.ALWAYS);

        VBox endBox = new VBox(5);
        Label endLbl = new Label(I18n.t("END DATE"));
        endLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker endDP = UIFactory.inputDatePicker("Mar 31, 2024");
        endDP.setMinHeight(44); endDP.setPrefHeight(44);
        endDP.setMaxHeight(44); endDP.setMaxWidth(Double.MAX_VALUE);
        endDP.valueProperty().bindBidirectional(vm.customToProperty());
        endBox.getChildren().addAll(endLbl, endDP);
        HBox.setHgrow(endBox, Priority.ALWAYS);

        dateBox.getChildren().addAll(startBox, endBox);

        // Projects list
        VBox projBox = new VBox(10);
        Label projLbl = new Label(I18n.t("PROJECTS"));
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ToggleGroup tg = new ToggleGroup();
        java.util.function.Consumer<java.util.List<Project>> rebuildRadios = (projects) -> {
            projBox.getChildren().clear();
            projBox.getChildren().add(projLbl);
            if (projects.isEmpty()) {
                Label placeholder = new Label(I18n.t("No projects"));
                placeholder.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + ";");
                projBox.getChildren().add(placeholder);
                return;
            }
            for (Project p : projects) {
                RadioButton rb = new RadioButton(p.getName());
                rb.getStyleClass().add("custom-radio");
                rb.setToggleGroup(tg);
                projBox.getChildren().add(rb);
                if (vm.selectedProjectProperty().get() != null && vm.selectedProjectProperty().get().getId() == p.getId()) rb.setSelected(true);
            }
        };

        rebuildRadios.accept(vm.getAvailableProjects());
        vm.getAvailableProjects().addListener((ListChangeListener<Project>) c -> rebuildRadios.accept(vm.getAvailableProjects()));

        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT instanceof RadioButton rb) {
                vm.setSelectedProject(rb.getText());
            }
        });

        // Footer
        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button resetBtn = new Button(I18n.t("⟲ Reset"));
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-font-weight: bold;");
        resetBtn.setOnAction(e -> {
            vm.setPeriodFromString("This Year");
            vm.customFromProperty().set(null);
            vm.customToProperty().set(null);
            vm.setSelectedProject(null);
            vm.setSelectedStatus(null);
            tg.selectToggle(null);
        });

        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        Button applyBtn = new Button(I18n.t("Apply Filters"));
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        applyBtn.setOnAction(e -> {
            vm.recompute();
            closeWithAnimation(modal, container);
        });
        addClickEffect(applyBtn);

        actions.getChildren().addAll(resetBtn, actionSpacer, applyBtn);
        root.getChildren().addAll(header, presetBtns, dateBox, projBox, actions);
        container.getChildren().add(root);

        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);
        try { scene.getStylesheets().add(ReportModals.class.getResource("/styles/global.css").toExternalForm()); } catch (Exception ignored) {}
        modal.setScene(scene);

        container.setOpacity(0);
        container.setTranslateY(20);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setToY(0);
        new ParallelTransition(fadeIn, slideUp).play();
        modal.show();
    }

    // ----------------------------------------------------------------
    // Per-tab filter modals (dispatched from the Filters button)
    // ----------------------------------------------------------------
    public static void showTabFilters(Stage owner, ReportsViewModel vm, int tabIndex) {
        switch (tabIndex) {
            case 0 -> showTabFilterModal(owner, vm, "P&L Filters",       null,                       null);
            case 1 -> showTabFilterModal(owner, vm, "Income Filters",    buildMinAmountBox("MIN. INCOME AMOUNT"), vm.incomeMinAmountProperty());
            case 2 -> showTabFilterModal(owner, vm, "Expense Filters",   buildMinAmountBox("MIN. EXPENSE AMOUNT"), vm.expenseMinAmountProperty());
            case 3 -> showHRFilterModal(owner, vm);
            case 4 -> showMonthlyFilterModal(owner, vm);
        }
    }

    private static TextField buildMinAmountBox(String label) {
        return UIFactory.inputField(label);
    }

    private static void showTabFilterModal(Stage owner, ReportsViewModel vm,
                                           String title,
                                           TextField minAmountField,
                                           javafx.beans.property.ObjectProperty<java.math.BigDecimal> minAmountProp) {
        Stage modal = buildModalStage(owner);
        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(20);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(380);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);
        Button closeBtn = makeCloseBtn(() -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleLbl, hSpacer, closeBtn);

        // Time period
        VBox timeBox = new VBox(5);
        Label timeLbl = new Label(I18n.t("TIME PERIOD"));
        timeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        ComboBox<String> timeCombo = UIFactory.inputComboBox(I18n.t("Select Period"));
        timeCombo.getItems().addAll(I18n.t("This Month"), I18n.t("Last Month"),
                I18n.t("This Quarter"), I18n.t("This Year"), I18n.t("Custom"));
        timeCombo.setValue(vm.getPeriodAsString());
        timeCombo.setMinHeight(44); timeCombo.setPrefHeight(44);
        timeCombo.setMaxHeight(44); timeCombo.setMaxWidth(Double.MAX_VALUE);
        timeBox.getChildren().addAll(timeLbl, timeCombo);

        // Project
        VBox projBox = buildProjectPicker(vm);

        // Optional min amount
        VBox minSection = null;
        if (minAmountField != null) {
            minSection = new VBox(5);
            Label lbl = new Label("MIN. AMOUNT");
            lbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
            minAmountField.setMinHeight(44); minAmountField.setPrefHeight(44);
            minAmountField.setMaxWidth(Double.MAX_VALUE);
            if (minAmountProp.get() != null) minAmountField.setText(minAmountProp.get().toPlainString());
            minSection.getChildren().addAll(lbl, minAmountField);
        }

        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(5, 0, 0, 0));
        Button resetBtn = new Button(I18n.t("⟲ Reset"));
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-font-weight: bold;");
        Region actSpacer = new Region(); HBox.setHgrow(actSpacer, Priority.ALWAYS);
        Button applyBtn = new Button(I18n.t("Apply Filters"));
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        addClickEffect(applyBtn);

        TextField finalMinField = minAmountField;
        javafx.beans.property.ObjectProperty<java.math.BigDecimal> finalProp = minAmountProp;
        applyBtn.setOnAction(e -> {
            vm.setPeriodFromString(timeCombo.getValue());
            if (finalMinField != null && finalProp != null) {
                String raw = finalMinField.getText().replaceAll("[$,\\s]", "");
                try {
                    finalProp.set(raw.isEmpty() ? null : new java.math.BigDecimal(raw));
                } catch (NumberFormatException ignored) {}
            }
            closeWithAnimation(modal, container);
        });

        resetBtn.setOnAction(e -> {
            vm.setPeriodFromString("This Year");
            if (finalMinField != null) finalMinField.clear();
            if (finalProp != null) finalProp.set(null);
        });

        actions.getChildren().addAll(resetBtn, actSpacer, applyBtn);
        root.getChildren().addAll(header, timeBox, projBox);
        if (minSection != null) root.getChildren().add(minSection);
        root.getChildren().add(actions);

        container.getChildren().add(root);
        showModalWithAnimation(owner, modal, container);
    }

    private static void showHRFilterModal(Stage owner, ReportsViewModel vm) {
        Stage modal = buildModalStage(owner);
        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(20);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(380);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("HR Filters");
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);
        Button closeBtn = makeCloseBtn(() -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleLbl, hSpacer, closeBtn);

        VBox projBox = buildProjectPicker(vm);

        // Employee status
        VBox statusBox = new VBox(8);
        Label statusLbl = new Label("EMPLOYEE STATUS");
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        ToggleGroup tg = new ToggleGroup();
        RadioButton rAll        = new RadioButton("All");         rAll.setToggleGroup(tg); rAll.getStyleClass().add("custom-radio");
        RadioButton rActive     = new RadioButton("ACTIVE");      rActive.setToggleGroup(tg); rActive.getStyleClass().add("custom-radio");
        RadioButton rInactive   = new RadioButton("INACTIVE");    rInactive.setToggleGroup(tg); rInactive.getStyleClass().add("custom-radio");
        RadioButton rContractor = new RadioButton("CONTRACTOR");  rContractor.setToggleGroup(tg); rContractor.getStyleClass().add("custom-radio");

        String current = vm.employeeStatusProperty().get();
        if ("ACTIVE".equalsIgnoreCase(current))          rActive.setSelected(true);
        else if ("INACTIVE".equalsIgnoreCase(current))   rInactive.setSelected(true);
        else if ("CONTRACTOR".equalsIgnoreCase(current)) rContractor.setSelected(true);
        else                                              rAll.setSelected(true);

        statusBox.getChildren().addAll(statusLbl, rAll, rActive, rInactive, rContractor);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(5, 0, 0, 0));
        Button resetBtn = new Button(I18n.t("⟲ Reset"));
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-font-weight: bold;");
        Region actSpacer = new Region(); HBox.setHgrow(actSpacer, Priority.ALWAYS);
        Button applyBtn = new Button(I18n.t("Apply Filters"));
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        addClickEffect(applyBtn);

        applyBtn.setOnAction(e -> {
            Toggle sel = tg.getSelectedToggle();
            if (sel == rActive)          vm.employeeStatusProperty().set("ACTIVE");
            else if (sel == rInactive)   vm.employeeStatusProperty().set("INACTIVE");
            else if (sel == rContractor) vm.employeeStatusProperty().set("CONTRACTOR");
            else                         vm.employeeStatusProperty().set(null);
            closeWithAnimation(modal, container);
        });
        resetBtn.setOnAction(e -> {
            tg.selectToggle(rAll);
            vm.employeeStatusProperty().set(null);
        });

        actions.getChildren().addAll(resetBtn, actSpacer, applyBtn);
        root.getChildren().addAll(header, projBox, statusBox, actions);
        container.getChildren().add(root);
        showModalWithAnimation(owner, modal, container);
    }

    private static void showMonthlyFilterModal(Stage owner, ReportsViewModel vm) {
        Stage modal = buildModalStage(owner);
        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(20);
        root.setPadding(new Insets(25, 30, 25, 30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(380);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("Monthly Filters");
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region hSpacer = new Region(); HBox.setHgrow(hSpacer, Priority.ALWAYS);
        Button closeBtn = makeCloseBtn(() -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleLbl, hSpacer, closeBtn);

        // Time period
        VBox timeBox = new VBox(5);
        Label timeLbl = new Label(I18n.t("TIME PERIOD"));
        timeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        ComboBox<String> timeCombo = UIFactory.inputComboBox(I18n.t("Select Period"));
        timeCombo.getItems().addAll(I18n.t("This Month"), I18n.t("Last Month"),
                I18n.t("This Quarter"), I18n.t("This Year"), I18n.t("Custom"));
        timeCombo.setValue(vm.getPeriodAsString());
        timeCombo.setMinHeight(44); timeCombo.setPrefHeight(44);
        timeCombo.setMaxHeight(44); timeCombo.setMaxWidth(Double.MAX_VALUE);
        timeBox.getChildren().addAll(timeLbl, timeCombo);

        VBox projBox = buildProjectPicker(vm);

        // Toggles
        HBox profitToggle = createToggle("Show only profitable months", vm.showOnlyProfitableProperty().get(),
                on -> vm.showOnlyProfitableProperty().set(on));
        HBox lossToggle   = createToggle("Show only loss months",        vm.showOnlyLossProperty().get(),
                on -> vm.showOnlyLossProperty().set(on));

        VBox togglesBox = new VBox(10, profitToggle, lossToggle);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(5, 0, 0, 0));
        Button resetBtn = new Button(I18n.t("⟲ Reset"));
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-font-weight: bold;");
        Region actSpacer = new Region(); HBox.setHgrow(actSpacer, Priority.ALWAYS);
        Button applyBtn = new Button(I18n.t("Apply Filters"));
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        addClickEffect(applyBtn);

        applyBtn.setOnAction(e -> {
            vm.setPeriodFromString(timeCombo.getValue());
            closeWithAnimation(modal, container);
        });
        resetBtn.setOnAction(e -> {
            vm.setPeriodFromString("This Year");
            vm.showOnlyProfitableProperty().set(false);
            vm.showOnlyLossProperty().set(false);
        });

        actions.getChildren().addAll(resetBtn, actSpacer, applyBtn);
        root.getChildren().addAll(header, timeBox, projBox, togglesBox, actions);
        container.getChildren().add(root);
        showModalWithAnimation(owner, modal, container);
    }

    private static VBox buildProjectPicker(ReportsViewModel vm) {
        VBox projBox = new VBox(5);
        Label projLbl = new Label(I18n.t("PROJECT"));
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        ComboBox<String> projectCombo = UIFactory.inputComboBox(I18n.t("All Projects"));
        projectCombo.getItems().add(I18n.t("All Projects"));
        projectCombo.getItems().addAll(vm.getAvailableProjects().stream()
                .map(org.example.model.database.entity.Project::getName).collect(java.util.stream.Collectors.toList()));
        projectCombo.setValue(vm.selectedProjectProperty().get() != null
                ? vm.selectedProjectProperty().get().getName() : I18n.t("All Projects"));
        projectCombo.setMinHeight(44); projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44); projectCombo.setMaxWidth(Double.MAX_VALUE);
        projectCombo.setOnAction(e -> {
            String sel = projectCombo.getValue();
            vm.setSelectedProject(I18n.t("All Projects").equals(sel) ? null : sel);
        });
        projBox.getChildren().addAll(projLbl, projectCombo);
        return projBox;
    }

    private static Stage buildModalStage(Stage owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modal.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        javafx.scene.paint.Paint originalFill = owner.getScene().getFill();
        javafx.scene.Node bgRoot = owner.getScene().getRoot();
        javafx.scene.effect.ColorAdjust darken = new javafx.scene.effect.ColorAdjust();
        darken.setBrightness(-0.3);
        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
        blur.setInput(darken);
        bgRoot.setEffect(blur);
        owner.getScene().setFill(Color.web(Themes.TEXT_DARK));
        modal.setOnHidden(e -> { bgRoot.setEffect(null); owner.getScene().setFill(originalFill); });
        return modal;
    }

    private static void showModalWithAnimation(Stage owner, Stage modal, StackPane container) {
        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);
        try { scene.getStylesheets().add(ReportModals.class.getResource("/styles/global.css").toExternalForm()); } catch (Exception ignored) {}
        modal.setScene(scene);
        container.setOpacity(0);
        container.setTranslateY(20);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setToValue(1);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setToY(0);
        new ParallelTransition(fadeIn, slideUp).play();
        modal.show();
    }

    private static Button makeCloseBtn(Runnable onClose) {
        Button btn = new Button("X");
        btn.setMinSize(32, 32); btn.setMaxSize(32, 32);
        btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 0;");
        btn.setOnAction(e -> onClose.run());
        return btn;
    }

    // --- Helpers ---
    private static void closeWithAnimation(Stage modal, Node animatedNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), animatedNode);
        fadeOut.setToValue(0);
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(200), animatedNode);
        slideDown.setToY(30);
        ParallelTransition exitAnimation = new ParallelTransition(fadeOut, slideDown);
        exitAnimation.setOnFinished(e -> modal.close());
        exitAnimation.play();
    }

    private static void addClickEffect(Button btn) {
        btn.setOnMousePressed(e  -> { btn.setScaleX(0.95); btn.setScaleY(0.95); });
        btn.setOnMouseReleased(e -> { btn.setScaleX(1.0);  btn.setScaleY(1.0);  });
    }

    private static Button createPresetBtn(String text, String value, TextField target) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 4 10; -fx-cursor: hand; -fx-background-insets: 0;");
        b.setOnAction(e -> target.setText(value));
        addClickEffect(b);
        return b;
    }

    private static HBox createFakeToggle(String label, boolean on) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        l.setMinWidth(Region.USE_PREF_SIZE);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        StackPane toggle = new StackPane();
        toggle.setPrefSize(34, 18);
        toggle.setStyle("-fx-background-color: " + (on ? Themes.PRIMARY : "#E2E8F0") + "; -fx-background-radius: 10; -fx-cursor: hand;");
        Circle c = new Circle(7, Color.WHITE);
        StackPane.setAlignment(c, on ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(c, new Insets(0, 2, 0, 2));
        toggle.getChildren().add(c);
        box.getChildren().addAll(l, spacer, toggle);
        return box;
    }

    private static HBox createToggle(String label, boolean initialOn, java.util.function.Consumer<Boolean> onChange) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        l.setMinWidth(Region.USE_PREF_SIZE);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        final boolean[] state = {initialOn};
        StackPane toggle = new StackPane();
        toggle.setPrefSize(34, 18);
        toggle.setStyle("-fx-background-color: " + (initialOn ? Themes.PRIMARY : "#E2E8F0") + "; -fx-background-radius: 10; -fx-cursor: hand;");
        Circle c = new Circle(7, Color.WHITE);
        StackPane.setAlignment(c, initialOn ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        StackPane.setMargin(c, new Insets(0, 2, 0, 2));
        toggle.getChildren().add(c);

        toggle.setOnMouseClicked(e -> {
            state[0] = !state[0];
            toggle.setStyle("-fx-background-color: " + (state[0] ? Themes.PRIMARY : "#E2E8F0") + "; -fx-background-radius: 10; -fx-cursor: hand;");
            StackPane.setAlignment(c, state[0] ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            onChange.accept(state[0]);
        });

        box.getChildren().addAll(l, spacer, toggle);
        return box;
    }

    private static HBox createSegmentedControl(String opt1, String opt2) {
        return createSegmentedControl(opt1, opt2, null);
    }

    private static HBox createSegmentedControl(String opt1, String opt2,
                                               java.util.function.Consumer<String> onChange) {
        HBox box = new HBox();

        String activeStyle   = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6 0 0 6; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;";
        String inactiveLeft  = "-fx-background-color: #F8FAFC; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 1 1; -fx-background-radius: 6 0 0 6; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;";
        String activeRight   = "-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0 6 6 0; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;";
        String inactiveRight = "-fx-background-color: #F8FAFC; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-border-color: #E2E8F0; -fx-border-width: 1 1 1 0; -fx-background-radius: 0 6 6 0; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;";

        Button b1 = new Button(opt1);
        b1.setMinHeight(38); b1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b1, Priority.ALWAYS);
        b1.setStyle(activeStyle);

        Button b2 = new Button(opt2);
        b2.setMinHeight(38); b2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b2, Priority.ALWAYS);
        b2.setStyle(inactiveRight);

        b1.setOnAction(e -> {
            b1.setStyle(activeStyle);
            b2.setStyle(inactiveRight);
            if (onChange != null) onChange.accept(opt1);
        });
        b2.setOnAction(e -> {
            b1.setStyle(inactiveLeft);
            b2.setStyle(activeRight);
            if (onChange != null) onChange.accept(opt2);
        });

        box.getChildren().addAll(b1, b2);
        return box;
    }
}