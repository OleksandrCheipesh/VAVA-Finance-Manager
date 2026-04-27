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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
// import org.example.viewModel.ReportsViewModel;

public class ReportModals {

    // --- 1. Project Profit Distribution ---
    public static void showProfitDistribution(Stage owner /*, ReportsViewModel vm */) {
        VBox extraFilters = new VBox(15);

        // Threshold Input
        VBox threshBox = new VBox(5);
        Label threshLbl = new Label("MIN. PROFIT THRESHOLD");
        threshLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        TextField thresholdField = UIFactory.inputField("$ 0.00");

        thresholdField.setMinHeight(44);
        thresholdField.setPrefHeight(44);
        thresholdField.setMaxHeight(44);
        thresholdField.setMaxWidth(Double.MAX_VALUE);

        threshBox.getChildren().addAll(threshLbl, thresholdField);

        // Preset Buttons
        HBox presets = new HBox(8);

        Label presetLbl = new Label("Quick preset:");
        presetLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        Button p10 = createPresetBtn("$10k", "10000", thresholdField);
        Button p50 = createPresetBtn("$50k", "50000", thresholdField);
        Button p100 = createPresetBtn("$100k", "100000", thresholdField);

        presets.getChildren().addAll(presetLbl, p10, p50, p100);
        presets.setAlignment(Pos.CENTER_LEFT);

        extraFilters.getChildren().addAll(threshBox, presets);

        showBaseModal(owner, "Project Profit Distribution", extraFilters /*, vm */);
    }

    // --- 2. Income VS Expenses ---
    public static void showIncomeVsExpenses(Stage owner /*, ReportsViewModel vm */) {
        VBox extraFilters = new VBox(15);

        VBox projBox = new VBox(5);
        Label projLbl = new Label("PROJECT SELECTION");
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> projectCombo = UIFactory.inputComboBox("All Projects");

        projectCombo.getItems().addAll("All Projects", "Project Alpha", "Project Beta");
        projectCombo.setValue("All Projects");
        projectCombo.setMinHeight(44);
        projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44);
        projectCombo.setMaxWidth(Double.MAX_VALUE);

        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox();
        groupAndToggles.setAlignment(Pos.TOP_LEFT);

        VBox groupBox = new VBox(5);

        Label groupLbl = new Label("GROUPING");
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        HBox groupBtns = createSegmentedControl("Month", "Quarter");
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        VBox togglesBox = new VBox(12);
        togglesBox.getChildren().addAll(createFakeToggle("Show Income", true), createFakeToggle("Show Expenses", false));
        HBox.setMargin(togglesBox, new Insets(17, 0, 0, 0));

        groupAndToggles.getChildren().addAll(groupBox, centerSpacer, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, "Income VS Expenses", extraFilters /*, vm */);
    }

    // --- 3. Net Profit Trend ---
    public static void showNetProfitTrend(Stage owner /*, ReportsViewModel vm */) {
        VBox extraFilters = new VBox(15);

        VBox projBox = new VBox(5);

        Label projLbl = new Label("PROJECT SELECTION");
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> projectCombo = UIFactory.inputComboBox("All Projects");

        projectCombo.getItems().addAll("All Projects", "Project Alpha");
        projectCombo.setValue("All Projects");
        projectCombo.setMinHeight(44);
        projectCombo.setPrefHeight(44);
        projectCombo.setMaxHeight(44);
        projectCombo.setMaxWidth(Double.MAX_VALUE);

        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox();
        groupAndToggles.setAlignment(Pos.TOP_LEFT);

        VBox groupBox = new VBox(5);

        Label groupLbl = new Label("GROUPING");
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        HBox groupBtns = createSegmentedControl("Month", "Quarter");
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        Region centerSpacer = new Region();
        HBox.setHgrow(centerSpacer, Priority.ALWAYS);

        VBox togglesBox = new VBox(8);
        togglesBox.getChildren().addAll(createFakeToggle("Show Zero Line", true));
        HBox.setMargin(togglesBox, new Insets(17, 0, 0, 0));

        groupAndToggles.getChildren().addAll(groupBox, centerSpacer, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, "Net Profit Trend", extraFilters /*, vm */);
    }


    // --- SHARED MODAL LOGIC ---
    private static void showBaseModal(Stage owner, String titleText, VBox extraFilters /*, ReportsViewModel vm */) {
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

        Label subTitle = new Label("Fiscal Year 2024 · Global Portfolio Breakdown");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

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

        closeBtn.setOnAction(e -> closeWithAnimation(modal, container));

        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Body (Two Columns)
        HBox body = new HBox(30);

        // Left: Chart Placeholder
        Region chartArea = new Region();
        chartArea.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");
        chartArea.setPrefSize(650, 480);

        HBox.setHgrow(chartArea, Priority.ALWAYS);

        // Right: Shared Filters Panel
        VBox filtersPanel = new VBox(20);
        filtersPanel.setPrefWidth(320);
        filtersPanel.setMinWidth(320);

        HBox fHeader = new HBox();

        Label fTitle = new Label("Filters");
        fTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Region fSpacer = new Region(); HBox.setHgrow(fSpacer, Priority.ALWAYS);

        Label reset = new Label("RESET ALL");
        reset.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-cursor: hand;");
        fHeader.getChildren().addAll(fTitle, fSpacer, reset);

        VBox timeBox = new VBox(5);

        Label timeLbl = new Label("TIME PERIOD");
        timeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        ComboBox<String> timeCombo = UIFactory.inputComboBox("Select Period");

        timeCombo.getItems().addAll("This Month", "Last Month", "This Quarter", "This Year", "Custom");
        timeCombo.setValue("This Year");
        timeCombo.setMinHeight(44);
        timeCombo.setPrefHeight(44);
        timeCombo.setMaxHeight(44);
        timeCombo.setMaxWidth(Double.MAX_VALUE);

        timeBox.getChildren().addAll(timeLbl, timeCombo);

        HBox customDates = new HBox(10);

        VBox fromBox = new VBox(5); Label fromLbl = new Label("FROM"); fromLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        DatePicker fromDP = UIFactory.inputDatePicker("mm/dd/yyyy");
        fromDP.setMinHeight(44);
        fromDP.setPrefHeight(44);
        fromDP.setMaxHeight(44);
        fromDP.setMaxWidth(Double.MAX_VALUE);

        fromBox.getChildren().addAll(fromLbl, fromDP);
        HBox.setHgrow(fromBox, Priority.ALWAYS);

        VBox toBox = new VBox(5); Label toLbl = new Label("TO"); toLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        DatePicker toDP = UIFactory.inputDatePicker("mm/dd/yyyy");

        toDP.setMinHeight(44);
        toDP.setPrefHeight(44);
        toDP.setMaxHeight(44);
        toDP.setMaxWidth(Double.MAX_VALUE);

        toBox.getChildren().addAll(toLbl, toDP);

        HBox.setHgrow(toBox, Priority.ALWAYS);
        customDates.getChildren().addAll(fromBox, toBox);

        VBox statusBox = new VBox(8);

        Label statusLbl = new Label("PROJECT STATUS");
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        RadioButton r1 = new RadioButton("All Projects");
        r1.setSelected(true);
        r1.getStyleClass().add("custom-radio");

        RadioButton r2 = new RadioButton("Active only");
        r2.getStyleClass().add("custom-radio");

        RadioButton r3 = new RadioButton("Inactive only");
        r3.getStyleClass().add("custom-radio");

        ToggleGroup tg = new ToggleGroup(); r1.setToggleGroup(tg); r2.setToggleGroup(tg); r3.setToggleGroup(tg);
        statusBox.getChildren().addAll(statusLbl, r1, r2, r3);

        // Action Buttons
        HBox actions = new HBox(10);

        Button applyBtn = new Button("Apply Filters");
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");
        applyBtn.setMinHeight(44);
        applyBtn.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(applyBtn, Priority.ALWAYS);

        Button exportBtn = new Button("Export PDF");

        exportBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-background-insets: 0;");
        exportBtn.setMinHeight(44);
        exportBtn.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(exportBtn, Priority.ALWAYS);

        addClickEffect(applyBtn);
        addClickEffect(exportBtn);

        actions.getChildren().addAll(applyBtn, exportBtn);

        filtersPanel.getChildren().addAll(fHeader, timeBox, customDates, statusBox, extraFilters, actions);
        body.getChildren().addAll(chartArea, filtersPanel);

        // Footer
        HBox footer = new HBox();

        Label syncLabel = new Label("LAST SYNCHRONIZED: 12 MINUTES AGO");
        syncLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        footer.getChildren().add(syncLabel);

        root.getChildren().addAll(header, body, footer);
        container.getChildren().add(root);

        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);

        try {
            scene.getStylesheets().add(ReportModals.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {}

        modal.setScene(scene);

        container.setOpacity(0);
        container.setTranslateY(30);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), container);
        slideUp.setToY(0);

        ParallelTransition entranceAnimation = new ParallelTransition(fadeIn, slideUp);

        modal.show();
        entranceAnimation.play();
    }


    // --- Quick Filters
    public static void showQuickFilters(Stage owner /*, ReportsViewModel vm */) {
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

        // 1. Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleIcon = new Label();
        titleIcon.setGraphic(IconFactory.getIcon("sliders-vertical", 18));
        titleIcon.setPadding(new Insets(0, 10, 0, 0));

        Label title = new Label("Quick Filters");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

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

        closeBtn.setOnAction(e -> closeWithAnimation(modal, container));
        header.getChildren().addAll(titleIcon, title, spacer, closeBtn);

        // 2. Preset Toggles
        HBox presetBtns = createSegmentedControl("This Month", "Last 6 Months");

        // 3. Date Range
        HBox dateBox = new HBox(15);

        VBox startBox = new VBox(5);
        Label startLbl = new Label("START DATE");
        startLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker startDP = UIFactory.inputDatePicker("Jan 01, 2024");
        startDP.setMinHeight(44);
        startDP.setPrefHeight(44);
        startDP.setMaxHeight(44);
        startDP.setMaxWidth(Double.MAX_VALUE);
        startBox.getChildren().addAll(startLbl, startDP);
        HBox.setHgrow(startBox, Priority.ALWAYS);

        VBox endBox = new VBox(5);
        Label endLbl = new Label("END DATE");
        endLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker endDP = UIFactory.inputDatePicker("Mar 31, 2024");
        endDP.setMinHeight(44);
        endDP.setPrefHeight(44);
        endDP.setMaxHeight(44);
        endDP.setMaxWidth(Double.MAX_VALUE);
        endBox.getChildren().addAll(endLbl, endDP);
        HBox.setHgrow(endBox, Priority.ALWAYS);

        dateBox.getChildren().addAll(startBox, endBox);

        // 4. Projects List (OPRAVENÉ RÁDIO BUTTONY)
        VBox projBox = new VBox(10);
        Label projLbl = new Label("PROJECTS");
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        RadioButton p1 = new RadioButton("Project Alpha");
        p1.setSelected(true);
        p1.getStyleClass().add("custom-radio"); // Pridaný štýl

        RadioButton p2 = new RadioButton("Project Beta");
        p2.getStyleClass().add("custom-radio"); // Pridaný štýl

        RadioButton p3 = new RadioButton("Corporate Rebrand");
        p3.getStyleClass().add("custom-radio"); // Pridaný štýl

        // Zoskupenie, aby fungovali ako prepínače
        ToggleGroup tg = new ToggleGroup();
        p1.setToggleGroup(tg);
        p2.setToggleGroup(tg);
        p3.setToggleGroup(tg);

        projBox.getChildren().addAll(projLbl, p1, p2, p3);

        // 5. Footer Actions
        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button resetBtn = new Button("⟲ Reset");
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-cursor: hand; -fx-font-weight: bold;");

        Region actionSpacer = new Region(); HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        Button applyBtn = new Button("Apply Filters");
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;");
        addClickEffect(applyBtn);

        actions.getChildren().addAll(resetBtn, actionSpacer, applyBtn);

        // Assemble
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
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
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

    private static HBox createSegmentedControl(String opt1, String opt2) {
        HBox box = new HBox();

        Button b1 = new Button(opt1);
        b1.setMinHeight(38);
        b1.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(b1, Priority.ALWAYS);
        b1.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6 0 0 6; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;");

        Button b2 = new Button(opt2);
        b2.setMinHeight(38);
        b2.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(b2, Priority.ALWAYS);
        b2.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-border-color: #E2E8F0; -fx-border-width: 1 1 1 0; -fx-background-radius: 0 6 6 0; -fx-padding: 0 8; -fx-cursor: hand; -fx-background-insets: 0;");

        box.getChildren().addAll(b1, b2);

        return box;
    }
}