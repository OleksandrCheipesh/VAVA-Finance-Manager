package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.model.database.entity.Project;
import org.example.logging.AppLog;
import org.example.model.database.entity.Transaction;
import org.example.view.templates.*;
import org.example.viewModel.TransactionsViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.effect.GaussianBlur;
import org.example.model.database.entity.Position;


public class TransactionsView extends BaseView {

    private final TransactionsViewModel viewModel = new TransactionsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private AppTable<Transaction> table;
    private StateButton addBtn;
    private HBox topBar;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // Top Bar
        topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: white; -fx-padding: 0 40; -fx-border-color: #E5E7EB; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Label title = new Label(I18n.t("Transactions"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Transactions")));

        Label subtitle = new Label(I18n.t("Monitor your income, expenses, and financial records."));
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        I18n.language.addListener((obs, o, v) -> subtitle.setText(I18n.t("Monitor your income, expenses, and financial records.")));

        titleBox.getChildren().addAll(title, subtitle);

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

        Label tabAll = new Label(I18n.t("All"));
        Label tabSales = new Label(I18n.t("Sales"));
        Label tabPurchases = new Label(I18n.t("Purchase"));
        I18n.language.addListener((obs, o, v) -> { tabAll.setText(I18n.t("All")); tabSales.setText(I18n.t("Sales")); tabPurchases.setText(I18n.t("Purchase")); });

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

        addBtn = new StateButton(I18n.t("+ Add Transaction"), StateButton.ButtonType.PRIMARY);
        I18n.language.addListener((obs, o, v) -> addBtn.setText(I18n.t("+ Add Transaction")));
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));

        addBtn.setOnMousePressed(e -> {
            addBtn.setScaleX(0.95);
            addBtn.setScaleY(0.95);
        });

        addBtn.setOnMouseReleased(e -> {
            addBtn.setScaleX(1.0);
            addBtn.setScaleY(1.0);
        });

        topBar.getChildren().addAll(titleBox, sep, tabs, spacer, addBtn);


        // Content area
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Filter Bar
        HBox filterBar = createCustomFilterBar();

        // Summary Cards
        SummaryCard incomeCard   = new SummaryCard(I18n.t("INCOME"),      "0.00$", "0 sales",     Themes.TEXT_SUCCESS);
        SummaryCard expensesCard = new SummaryCard(I18n.t("EXPENSES"),    "0.00$", "0 purchases", Themes.TEXT_ERROR);
        SummaryCard netCard      = new SummaryCard(I18n.t("NET BALANCE"), "0.00$", "",            Themes.TEXT_SUCCESS);
        SummaryCard largestCard  = new SummaryCard(I18n.t("LARGEST"),     "0.00$", "",            Themes.TEXT_SUCCESS);
        I18n.language.addListener((obs, o, v) -> {
            incomeCard.setTitle(I18n.t("INCOME")); expensesCard.setTitle(I18n.t("EXPENSES"));
            netCard.setTitle(I18n.t("NET BALANCE")); largestCard.setTitle(I18n.t("LARGEST"));
        });

        viewModel.totalIncomeProperty()   .addListener((obs, o, v) -> incomeCard.setValue(v));
        viewModel.incomeSubtextProperty() .addListener((obs, o, v) -> incomeCard.setSubText(v));
        viewModel.totalExpensesProperty() .addListener((obs, o, v) -> expensesCard.setValue(v));
        viewModel.expensesSubtextProperty().addListener((obs, o, v) -> expensesCard.setSubText(v));
        viewModel.netBalanceProperty()    .addListener((obs, o, v) -> netCard.setValue(v));
        viewModel.largestAmountProperty() .addListener((obs, o, v) -> largestCard.setValue(v));

        incomeCard.setValue(viewModel.totalIncomeProperty().get());
        incomeCard.setSubText(viewModel.incomeSubtextProperty().get());

        expensesCard.setValue(viewModel.totalExpensesProperty().get());
        expensesCard.setSubText(viewModel.expensesSubtextProperty().get());

        netCard.setValue(viewModel.netBalanceProperty().get());
        largestCard.setValue(viewModel.largestAmountProperty().get());

        HBox cardsBox = new HBox(20);
        cardsBox.getChildren().addAll(incomeCard, expensesCard, netCard, largestCard);

        // Table and empty state
        table = new AppTable<>("");
        table.setItems(viewModel.getFilteredTransactions());

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox emptyStateBox = new VBox(5);
        emptyStateBox.setAlignment(Pos.CENTER);

        Label emptyTitle = new Label(I18n.t("No Transactions Yet"));
        emptyTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        I18n.language.addListener((obs, o, v) -> emptyTitle.setText(I18n.t("No Transactions Yet")));

        Label emptySub = new Label(I18n.t("Register your first sale or purchase"));
        emptySub.setStyle("-fx-font-size: 18px; -fx-text-fill: #869F9B;");
        I18n.language.addListener((obs, o, v) -> emptySub.setText(I18n.t("Register your first sale or purchase")));
        emptyStateBox.getChildren().addAll(emptyTitle, emptySub);

        table.setPlaceholder(emptyStateBox);

        String headerStyle = "-fx-alignment: center-left; -fx-padding: 0 0 0 20;";

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>(I18n.t("DATE"));
        I18n.language.addListener((obs, o, v) -> dateCol.setText(I18n.t("DATE")));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle(headerStyle);
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("MM/dd/yy")));
                    setStyle("-fx-font-weight: 800; -fx-font-size: 16px; -fx-text-fill: #111827; -fx-alignment: center-left;");
                }
            }
        });

        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>(I18n.t("AMOUNT"));
        I18n.language.addListener((obs, o, v) -> amountCol.setText(I18n.t("AMOUNT")));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setStyle(headerStyle);
        amountCol.setCellFactory(col -> new TableCell<>() {
            private final java.text.DecimalFormat df = new java.text.DecimalFormat(
                    "#,##0.00", new java.text.DecimalFormatSymbols(java.util.Locale.US));
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;

                    Transaction t = getTableView().getItems().get(idx);

                    if (t == null) return;

                    boolean isSale = "SALE".equalsIgnoreCase(t.getType());

                    setText((isSale ? "+$" : "-$") + df.format(item));
                    String color = isSale ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: 800; -fx-font-size: 16px; -fx-alignment: center-left;");
                }
            }
        });

        TableColumn<Transaction, Integer> projectCol = new TableColumn<>(I18n.t("PROJECT"));
        I18n.language.addListener((obs, o, v) -> projectCol.setText(I18n.t("PROJECT")));
        projectCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectCol.setStyle(headerStyle);
        projectCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setAlignment(Pos.CENTER_LEFT);
                } else {
                    String name = viewModel.getProjectName(item);
                    setText(name.isBlank() ? "—" : name);
                    setFont(Font.font("System", FontWeight.NORMAL, 15));
                    setTextFill(Color.web(name.isBlank() ? Themes.TEXT_MUTED : Themes.TEXT_DARK));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, Integer> clientCol = new TableColumn<>(I18n.t("CLIENT"));
        I18n.language.addListener((obs, o, v) -> clientCol.setText(I18n.t("CLIENT")));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        clientCol.setStyle(headerStyle);
        clientCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setAlignment(Pos.CENTER_LEFT);
                } else {
                    String name = viewModel.getClientName(item);
                    setText(name.isBlank() ? "—" : name);
                    setFont(Font.font("System", FontWeight.NORMAL, 15));
                    setTextFill(Color.web(name.isBlank() ? Themes.TEXT_MUTED : Themes.TEXT_DARK));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, String> descCol = new TableColumn<>(I18n.t("DESCRIPTION"));
        I18n.language.addListener((obs, o, v) -> descCol.setText(I18n.t("DESCRIPTION")));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setStyle(headerStyle);
        descCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setFont(Font.getDefault());
                    setTextFill(Color.BLACK);
                    setAlignment(Pos.CENTER_LEFT);
                } else {
                    setText(item);
                    setFont(Font.font("System", FontWeight.NORMAL, 15));
                    setTextFill(Color.web(Themes.TEXT_MUTED));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Transaction, String> typeCol = new TableColumn<>(I18n.t("TYPE"));
        I18n.language.addListener((obs, o, v) -> typeCol.setText(I18n.t("TYPE")));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setStyle(headerStyle);
        typeCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setAlignment(Pos.CENTER_LEFT);
                } else {
                    boolean isSale = "SALE".equalsIgnoreCase(item);

                    Label pill = new Label(isSale ? "Sale" : "Buy");
                    String color = isSale ? Themes.TEXT_SUCCESS : "#F59E0B";

                    pill.setStyle("-fx-background-color: " + color + "33; -fx-text-fill: " + color + "; -fx-padding: 5 14; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 13px;");
                    setGraphic(pill);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        table.getColumns().addAll(dateCol, amountCol, projectCol, clientCol, descCol, typeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.13));
        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.17));
        projectCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        clientCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        descCol.prefWidthProperty().bind(table.widthProperty().multiply(0.22));
        typeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.18));

        table.setOnMouseClicked(e -> {
            Transaction tx = table.getSelectionModel().getSelectedItem();
            table.getSelectionModel().clearSelection();
            if (tx != null) {
                TransactionDetailDialog.show(
                        stage,
                        tx,
                        viewModel.getProjectName(tx.getProjectId()),
                        viewModel.getClientName(tx.getClientId()),
                        () -> EditTransactionDialog.show(stage, tx, viewModel.getAccounts(), viewModel.getProjects(), viewModel.getClients(), updatedTx -> {
                            viewModel.updateTransaction(updatedTx);
                        }),
                        () -> viewModel.deleteTransaction(tx)
                );
            }
        });

        // Merge layout
        contentArea.getChildren().addAll(filterBar, cardsBox, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            var logger = AppLog.getLogger(TransactionsView.class);
            logger.warn("Could not load CSS files for TransactionsView: {}", e.getMessage());
            logger.debug("CSS load exception", e);
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
        if (!viewModel.hasAccessProperty().get()) {
            showAccessDenied();
            return;
        }

        addBtn.setOnAction(e -> {
            AddTransactionDialog.show(stage, viewModel.getAccounts(), viewModel.getProjects(), viewModel.getClients(), newTx -> {
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

    private HBox createCustomFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        String filterStyle = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(filterStyle + " -fx-padding: 0 12;");
        searchContainer.setPrefHeight(40);

        SVGPath searchIcon = new SVGPath();
        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        TextField search = new TextField();
        search.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        search.textProperty().addListener((obs, old, val) -> viewModel.filterBySearch(val));

        StackPane searchPane = new StackPane();
        searchPane.setAlignment(Pos.CENTER_LEFT);
        Label searchPrompt = new Label(I18n.t("Search by name..."));
        searchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");
        searchPrompt.setMouseTransparent(true);
        I18n.language.addListener((obs, o, v) -> searchPrompt.setText(I18n.t("Search by name...")));
        search.textProperty().addListener((obs, old, val) -> searchPrompt.setVisible(val.isEmpty()));

        searchPane.getChildren().addAll(search, searchPrompt);

        HBox.setHgrow(searchPane, Priority.ALWAYS);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);

        searchContainer.getChildren().addAll(searchIcon, searchPane);

        ComboBox<Project> proj = new ComboBox<>();
        proj.setPromptText(I18n.t("Project"));
        I18n.language.addListener((obs, o, v) -> proj.setPromptText(I18n.t("Project")));
        proj.setStyle(filterStyle);
        proj.getStyleClass().add("filter-item");
        proj.setPrefHeight(40);
        proj.setPrefWidth(130);
        proj.setItems(viewModel.getProjects());
        proj.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        proj.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Project" : item.getName());
            }
        });
        proj.valueProperty().addListener((obs, old, val) ->
                viewModel.filterByProject(val == null ? null : val.getId()));

        ComboBox<String> type = new ComboBox<>();
        type.setPromptText(I18n.t("Type"));
        I18n.language.addListener((obs, o, v) -> type.setPromptText(I18n.t("Type")));
        type.setStyle(filterStyle);
        type.getStyleClass().add("filter-item");
        type.setPrefHeight(40);
        type.setPrefWidth(130);
        type.getItems().addAll("ALL", "SALE", "PURCHASE");

        type.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Type" : item);
            }
        });

        type.valueProperty().addListener((obs, old, val) ->
                viewModel.filterByType(val == null ? "ALL" : val));

        DatePicker d1 = new DatePicker();
        d1.setStyle(filterStyle);
        d1.getStyleClass().add("filter-item");
        d1.setPrefHeight(40);
        d1.setPrefWidth(130);
        d1.setConverter(UIFactory.safeDateConverter());

        DatePicker d2 = new DatePicker();
        d2.setStyle(filterStyle);
        d2.getStyleClass().add("filter-item");
        d2.setPrefHeight(40);
        d2.setPrefWidth(130);
        d2.setConverter(UIFactory.safeDateConverter());

        d1.getEditor().focusedProperty().addListener((obs, was, isFocused) -> {
            if (!isFocused) {
                java.time.LocalDate val = d1.getValue();
                d1.getEditor().setText(val != null ? d1.getConverter().toString(val) : "");
            }
        });
        d2.getEditor().focusedProperty().addListener((obs, was, isFocused) -> {
            if (!isFocused) {
                java.time.LocalDate val = d2.getValue();
                d2.getEditor().setText(val != null ? d2.getConverter().toString(val) : "");
            }
        });

        d1.valueProperty().addListener((obs, old, val) ->
                viewModel.filterByDateRange(val, d2.getValue()));
        d2.valueProperty().addListener((obs, old, val) ->
                viewModel.filterByDateRange(d1.getValue(), val));

        StackPane d1Pane = createPromptWrapper(d1, "01/01/01");
        StackPane d2Pane = createPromptWrapper(d2, "01/01/01");

        Button clear = new Button(I18n.t("Clear"));
        clear.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        I18n.language.addListener((obs, o, v) -> clear.setText(I18n.t("Clear")));
        clear.setOnAction(e -> {
            search.clear();
            proj.setValue(null);
            type.setValue(null);

            d1.setValue(null);
            d1.getEditor().clear();

            d2.setValue(null);
            d2.getEditor().clear();

            viewModel.clearFilters();
        });

        bar.getChildren().addAll(searchContainer, proj, type, d1Pane, d2Pane, clear);
        return bar;
    }

    private StackPane createPromptWrapper(Control control, String promptText) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER_LEFT);

        Label prompt = new Label(promptText);
        prompt.setStyle("-fx-text-fill: #9CA3AF; -fx-padding: 0 0 0 12; -fx-font-size: 14px;");
        prompt.setMouseTransparent(true);

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