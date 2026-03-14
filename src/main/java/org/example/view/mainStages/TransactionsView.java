package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.model.database.entity.Transaction;
import org.example.view.templates.*;
import org.example.viewModel.TransactionsViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

public class TransactionsView extends BaseView {

    private final TransactionsViewModel viewModel = new TransactionsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private AppTable<Transaction> table;
    private StateButton addBtn;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // Top Bar
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        Label title = new Label("Transaction");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        HBox.setMargin(title, new Insets(0, 0, 25, 0));

        // Visual divider
        Region sep = new Region();
        sep.setPrefSize(2, 30);
        sep.setMaxSize(2, 30);
        sep.setStyle("-fx-background-color: #E5E7EB;");
        HBox.setMargin(sep, new Insets(0, 10, 25, 10));

        // Tabs
        HBox tabs = new HBox(30);
        tabs.setAlignment(Pos.BOTTOM_LEFT);

        String activeTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + "; -fx-border-color: " + Themes.PRIMARY + "; -fx-border-width: 0 0 3 0; -fx-padding: 0 0 20 0; -fx-cursor: hand;";
        String inactiveTab = "-fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-padding: 0 0 20 0; -fx-cursor: hand;";

        Label tabAll = new Label("All");
        Label tabSales = new Label("Sales");
        Label tabPurchases = new Label("Purchase");

        // Default state
        tabAll.setStyle(activeTab);
        tabSales.setStyle(inactiveTab);
        tabPurchases.setStyle(inactiveTab);

        // Tab click events linked to ViewModel
        tabAll.setOnMouseClicked(e -> {
            tabAll.setStyle(activeTab);
            tabSales.setStyle(inactiveTab);
            tabPurchases.setStyle(inactiveTab);
            viewModel.filterByType("ALL");
        });

        tabSales.setOnMouseClicked(e -> {
            tabSales.setStyle(activeTab);
            tabAll.setStyle(inactiveTab);
            tabPurchases.setStyle(inactiveTab);
            viewModel.filterByType("SALE");
        });

        tabPurchases.setOnMouseClicked(e -> {
            tabPurchases.setStyle(activeTab);
            tabAll.setStyle(inactiveTab);
            tabSales.setStyle(inactiveTab);
            viewModel.filterByType("PURCHASE");
        });

        tabs.getChildren().addAll(tabAll, tabSales, tabPurchases);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add Transaction", StateButton.ButtonType.PRIMARY);
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));

        topBar.getChildren().addAll(title, sep, tabs, spacer, addBtn);


        // Content area
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Filter Bar
        HBox filterBar = createCustomFilterBar();

        // Summary Cards
        HBox cardsBox = new HBox(20);
        cardsBox.getChildren().addAll(
                new SummaryCard("INCOME", "15 000", "1 sales", Themes.TEXT_SUCCESS),
                new SummaryCard("EXPENSES", "15 000", "1 purchases", Themes.TEXT_ERROR),
                new SummaryCard("NET BALANCE", "15 000", "", Themes.TEXT_SUCCESS),
                new SummaryCard("LARGEST", "15 000", "", Themes.TEXT_SUCCESS)
        );

        // Table and empty state
        table = new AppTable<>("");
        table.setItems(viewModel.getFilteredTransactions());
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox emptyStateBox = new VBox(5);
        emptyStateBox.setAlignment(Pos.CENTER);
        Label emptyTitle = new Label("No Transactions Yet");
        emptyTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        Label emptySub = new Label("Register your first sale or purchase");
        emptySub.setStyle("-fx-font-size: 18px; -fx-text-fill: #869F9B;");
        emptyStateBox.getChildren().addAll(emptyTitle, emptySub);
        table.setPlaceholder(emptyStateBox);

        String headerStyle = "-fx-alignment: center-left; -fx-padding: 0 0 0 20;";

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle(headerStyle);

        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(item.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                    setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
                    setTextFill(Color.web("#111827"));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("AMOUNT");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle(headerStyle);

        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    boolean isSale = "SALE".equalsIgnoreCase(t.getType());
                    DecimalFormat df = new DecimalFormat("#,##0.00");

                    setText((isSale ? "+$" : "-$") + df.format(item));
                    setTextFill(Color.web(isSale ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR));
                    setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, Integer> projectCol = new TableColumn<>("PROJECT");
        projectCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectCol.setStyle(headerStyle);

        projectCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText("Design");
                    setFont(Font.font("System", FontWeight.NORMAL, 15));
                    setTextFill(Color.web(Themes.TEXT_MUTED));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, String> descCol = new TableColumn<>("DESCRIPTION");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setStyle(headerStyle);

        descCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(item);
                    setFont(Font.font("System", FontWeight.NORMAL, 15));
                    setTextFill(Color.web(Themes.TEXT_MUTED));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, String> typeCol = new TableColumn<>("TYPE");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle(headerStyle);

        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    boolean isSale = "SALE".equalsIgnoreCase(item);
                    Label pill = new Label(isSale ? "Sale" : "Buy");
                    String color = isSale ? Themes.TEXT_SUCCESS : "#F59E0B";

                    pill.setStyle("-fx-background-color: " + color + "33; -fx-text-fill: " + color + "; -fx-padding: 5 14; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 13px;");
                    setGraphic(pill);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setStyle("-fx-alignment: center;");

        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    Label dots = new Label("⋮");
                    dots.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand;");
                    setGraphic(dots);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        table.getColumns().addAll(dateCol, amountCol, projectCol, descCol, typeCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));
        projectCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        descCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
        typeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.12));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.08));

        // Merge layout
        contentArea.getChildren().addAll(filterBar, cardsBox, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS files. Ensure they exist in src/main/resources/");
            e.printStackTrace();
        }

        stage.setTitle("Admin - Transactions");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        addBtn.setOnAction(e -> {
            AddTransactionDialog.show(stage, newTx -> {
                viewModel.addTransaction(newTx);
            });
        });

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    // Custom filter bar
    private HBox createCustomFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        String filterStyle = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        TextField search = new TextField();
        search.setStyle(filterStyle + " -fx-padding: 8 12;");
        search.setPrefHeight(40);
        search.textProperty().addListener((obs, old, val) -> viewModel.filterBySearch(val));

        // Wrap search field
        StackPane searchPane = createPromptWrapper(search, "\uD83D\uDD0D Search by name...");
        HBox.setHgrow(searchPane, Priority.ALWAYS);

        // ComboBox handles prompt text correctly on its own
        ComboBox<String> proj = new ComboBox<>();
        proj.setPromptText("Project");
        proj.setStyle(filterStyle);
        proj.getStyleClass().add("filter-item");
        proj.setPrefHeight(40);
        proj.setPrefWidth(130);

        ComboBox<String> type = new ComboBox<>();
        type.setPromptText("Type");
        type.setStyle(filterStyle);
        type.getStyleClass().add("filter-item");
        type.setPrefHeight(40);
        type.setPrefWidth(130);

        DatePicker d1 = new DatePicker();
        d1.setStyle(filterStyle);
        d1.getStyleClass().add("filter-item");
        d1.setPrefHeight(40);
        d1.setPrefWidth(130);
        // Wrap DatePicker 1
        StackPane d1Pane = createPromptWrapper(d1, "01/01/01");

        DatePicker d2 = new DatePicker();
        d2.setStyle(filterStyle);
        d2.getStyleClass().add("filter-item");
        d2.setPrefHeight(40);
        d2.setPrefWidth(130);
        // Wrap DatePicker 2
        StackPane d2Pane = createPromptWrapper(d2, "01/01/01");

        Button clear = new Button("Clear");
        clear.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clear.setOnAction(e -> {
            // Clear text and values
            search.clear();
            proj.setValue(null);
            type.setValue(null);

            d1.setValue(null);
            d1.getEditor().clear(); // Clears raw typed text

            d2.setValue(null);
            d2.getEditor().clear(); // Clears raw typed text
        });

        // Add the wrappers instead of the raw inputs
        bar.getChildren().addAll(searchPane, proj, type, d1Pane, d2Pane, clear);
        return bar;
    }

    private StackPane createPromptWrapper(Control control, String promptText) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);

        Label prompt = new Label(promptText);
        prompt.setStyle("-fx-text-fill: #9CA3AF; -fx-padding: 0 0 0 12; -fx-font-size: 14px;");
        prompt.setMouseTransparent(true); // Clicks pass through to the input

        // Listen to actual text changes to hide the custom label
        if (control instanceof TextField) {
            TextField tf = (TextField) control;

            tf.textProperty().addListener((obs, old, val) -> prompt.setVisible(val.isEmpty()));
        } else if (control instanceof DatePicker) {
            DatePicker dp = (DatePicker) control;

            dp.getEditor().textProperty().addListener((obs, old, val) -> prompt.setVisible(val.isEmpty()));
        }

        stack.getChildren().addAll(control, prompt);
        return stack;
    }
}