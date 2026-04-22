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

        // Using UIFactory for styling
        ComboBox<String> projectCombo = UIFactory.inputComboBox("All Projects");
        projectCombo.getItems().addAll("All Projects", "Project Alpha", "Project Beta");
        projectCombo.setValue("All Projects");
        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox(20);

        VBox groupBox = new VBox(5);
        Label groupLbl = new Label("GROUPING");
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        HBox groupBtns = createSegmentedControl("Month", "Quarter");
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        VBox togglesBox = new VBox(8);
        togglesBox.getChildren().addAll(createFakeToggle("Show Income", true), createFakeToggle("Show Expenses", false));

        groupAndToggles.getChildren().addAll(groupBox, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, "Income VS Expenses", extraFilters /*, vm */);
    }

    // --- 3. Net Profit Trend ---
    public static void showNetProfitTrend(Stage owner /*, ReportsViewModel vm */) {
        VBox extraFilters = new VBox(15);

        VBox projBox = new VBox(5);
        Label projLbl = new Label("PROJECT SELECTION");
        projLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        // Using UIFactory for styling
        ComboBox<String> projectCombo = UIFactory.inputComboBox("All Projects");
        projectCombo.getItems().addAll("All Projects", "Project Alpha");
        projectCombo.setValue("All Projects");
        projBox.getChildren().addAll(projLbl, projectCombo);

        HBox groupAndToggles = new HBox(20);

        VBox groupBox = new VBox(5);
        Label groupLbl = new Label("GROUPING");
        groupLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        HBox groupBtns = createSegmentedControl("Month", "Quarter");
        groupBox.getChildren().addAll(groupLbl, groupBtns);

        VBox togglesBox = new VBox(8);
        togglesBox.setAlignment(Pos.CENTER_LEFT);
        togglesBox.getChildren().addAll(createFakeToggle("Show Zero Line", true));

        groupAndToggles.getChildren().addAll(groupBox, togglesBox);
        extraFilters.getChildren().addAll(projBox, groupAndToggles);

        showBaseModal(owner, "Net Profit Trend", extraFilters /*, vm */);
    }


    // --- SHARED MODAL LOGIC ---
    private static void showBaseModal(Stage owner, String titleText, VBox extraFilters /*, ReportsViewModel vm */) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        javafx.scene.Node bgRoot = owner.getScene().getRoot();
        GaussianBlur blur = new GaussianBlur(20);
        ColorAdjust darken = new ColorAdjust();
        darken.setBrightness(-0.3);
        darken.setInput(blur);
        bgRoot.setEffect(darken);
        modal.setOnHidden(e -> bgRoot.setEffect(null));

        StackPane container = new StackPane();
        container.setStyle("-fx-background-color: transparent;");
        container.setPadding(new Insets(40));

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 30, 0, 0, 10);");
        root.setMaxWidth(800);

        // Header
        HBox header = new HBox();
        VBox titleBox = new VBox(2);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subTitle = new Label("Fiscal Year 2024 · Global Portfolio Breakdown");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 13px;");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 20px; -fx-cursor: hand; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        closeBtn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), root);
            ft.setToValue(0);
            ft.setOnFinished(ev -> modal.close());
            ft.play();
        });
        header.getChildren().addAll(titleBox, spacer, closeBtn);

        // Body (Two Columns)
        HBox body = new HBox(30);

        // Left: Chart Placeholder
        Region chartArea = new Region();
        chartArea.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");
        chartArea.setPrefSize(400, 350);
        HBox.setHgrow(chartArea, Priority.ALWAYS);

        // Right: Shared Filters Panel
        VBox filtersPanel = new VBox(20);
        filtersPanel.setPrefWidth(300);

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

        // Using UIFactory for styling
        ComboBox<String> timeCombo = UIFactory.inputComboBox("Select Period");
        timeCombo.getItems().addAll("This Month", "Last Month", "This Quarter", "This Year", "Custom");
        timeCombo.setValue("This Year");
        timeBox.getChildren().addAll(timeLbl, timeCombo);

        HBox customDates = new HBox(10);
        VBox fromBox = new VBox(5); Label fromLbl = new Label("FROM"); fromLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        // Using UIFactory for DatePickers
        DatePicker fromDP = UIFactory.inputDatePicker("mm/dd/yyyy");
        fromBox.getChildren().addAll(fromLbl, fromDP);

        VBox toBox = new VBox(5); Label toLbl = new Label("TO"); toLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        DatePicker toDP = UIFactory.inputDatePicker("mm/dd/yyyy");
        toBox.getChildren().addAll(toLbl, toDP);
        customDates.getChildren().addAll(fromBox, toBox);

        VBox statusBox = new VBox(5);
        Label statusLbl = new Label("PROJECT STATUS");
        statusLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        RadioButton r1 = new RadioButton("All Projects"); r1.setSelected(true);
        RadioButton r2 = new RadioButton("Active only");
        RadioButton r3 = new RadioButton("Inactive only");
        ToggleGroup tg = new ToggleGroup(); r1.setToggleGroup(tg); r2.setToggleGroup(tg); r3.setToggleGroup(tg);
        statusBox.getChildren().addAll(statusLbl, r1, r2, r3);

        // Action Buttons
        HBox actions = new HBox(10);
        Button applyBtn = new Button("Apply Filters");
        applyBtn.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

        Button exportBtn = new Button("Export PDF");
        exportBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");

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

        // Load the CSS so the Calendar popup doesn't look gray!
        try {
            scene.getStylesheets().add(ReportModals.class.getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load global.css for Modal");
        }

        modal.setScene(scene);

        root.setOpacity(0);
        root.setTranslateY(20);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setToValue(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), root);
        slideUp.setToY(0);

        new ParallelTransition(fadeIn, slideUp).play();

        modal.show();
    }

    // --- Helpers ---
    private static Button createPresetBtn(String text, String value, TextField target) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 4 10; -fx-cursor: hand;");
        b.setOnAction(e -> target.setText(value));
        return b;
    }

    private static HBox createFakeToggle(String label, boolean on) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        // Fake toggle switch visually
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
        b1.setStyle("-fx-background-color: " + Themes.PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6 0 0 6; -fx-padding: 6 15; -fx-cursor: hand;");
        Button b2 = new Button(opt2);
        b2.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-border-color: #E2E8F0; -fx-border-width: 1 1 1 0; -fx-background-radius: 0 6 6 0; -fx-padding: 5 15; -fx-cursor: hand;");
        box.getChildren().addAll(b1, b2);
        return box;
    }
}