package org.example.view.mainStages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.model.database.entity.Account;
import org.example.view.templates.*;
import org.example.viewModel.AccountsViewModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AccountsView extends BaseView {

    private final AccountsViewModel viewModel = new AccountsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private AppTable<Account> table;
    private StateButton addBtn;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // Top Bar
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40;" +
                " -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(4);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Accounts");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subTitle = new Label("Manage your company bank accounts and cash balances.");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add Account", StateButton.ButtonType.PRIMARY);

        topBar.getChildren().addAll(titleBox, spacer, addBtn);

        // Content Area
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Filter bar (search only)
        HBox filterBar = buildFilterBar();

        // Summary card
        SummaryCard totalCard = new SummaryCard("TOTAL BALANCE", "0.00", "", Themes.TEXT_SUCCESS);
        viewModel.totalBalanceProperty().addListener((obs, o, v) -> totalCard.setValue(v));
        totalCard.setValue(viewModel.totalBalanceProperty().get());

        HBox cardsBox = new HBox(20);
        cardsBox.getChildren().add(totalCard);

        // Table
        table = new AppTable<>("");

        VBox emptyStateBox = new VBox(5);
        emptyStateBox.setAlignment(Pos.CENTER);
        Label emptyTitle = new Label("No Accounts Yet");
        emptyTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label emptySub = new Label("Add your first bank account or cash account");
        emptySub.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        emptyStateBox.getChildren().addAll(emptyTitle, emptySub);
        table.setPlaceholder(emptyStateBox);

        table.setItems(viewModel.getFilteredAccounts());
        VBox.setVgrow(table, Priority.ALWAYS);

        String headerStyle = "-fx-alignment: center-left; -fx-padding: 0 0 0 20;";

        TableColumn<Account, String> nameCol = new TableColumn<>("ACCOUNT NAME");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        nameCol.setStyle(headerStyle);
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle("-fx-font-weight: 800; -fx-font-size: 15px; -fx-text-fill: " + Themes.TEXT_DARK + "; -fx-alignment: center-left;");
                }
            }
        });

        TableColumn<Account, BigDecimal> balanceCol = new TableColumn<>("BALANCE");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("currentBalance"));
        balanceCol.setStyle(headerStyle);
        balanceCol.setCellFactory(col -> new TableCell<>() {
            private final DecimalFormat df = new DecimalFormat(
                    "#,##0.00", new DecimalFormatSymbols(Locale.US));
            @Override protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    Account acc = getTableRow().getItem();
                    String prefix = currencyPrefix(acc != null ? acc.getCurrency() : "");
                    setText(prefix + df.format(item));
                    String color = item.compareTo(BigDecimal.ZERO) >= 0 ? Themes.TEXT_SUCCESS : Themes.TEXT_ERROR;
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: 800; -fx-font-size: 15px; -fx-alignment: center-left;");
                }
            }
        });

        TableColumn<Account, String> currencyCol = new TableColumn<>("CURRENCY");
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
        currencyCol.setStyle(headerStyle);
        currencyCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    Label pill = new Label(item);
                    pill.setStyle("-fx-background-color: " + Themes.PRIMARY + "33; -fx-text-fill: " + Themes.PRIMARY + ";" +
                            " -fx-padding: 5 14; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 13px;");
                    setGraphic(pill);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        TableColumn<Account, OffsetDateTime> createdCol = new TableColumn<>("CREATED");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdCol.setStyle(headerStyle);
        createdCol.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            @Override protected void updateItem(OffsetDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item.format(fmt));
                    setStyle("-fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-alignment: center-left;");
                }
            }
        });

        TableColumn<Account, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else {
                    Label dots = new Label("⋮");
                    dots.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 24px; -fx-font-weight: bold;" +
                            " -fx-cursor: hand; -fx-background-color: transparent;");
                    dots.setOnMouseClicked(e -> {
                        Account acc = getTableView().getItems().get(getIndex());
                        showAccountActions(acc, e);
                    });
                    setGraphic(dots);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        table.getColumns().addAll(nameCol, balanceCol, currencyCol, createdCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.35));
        balanceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        currencyCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        createdCol.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.10));

        contentArea.getChildren().addAll(filterBar, cardsBox, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS files.");
        }

        stage.setTitle("Admin - Accounts");
    }

    @Override
    protected void setStyle() {
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
    }

    @Override
    protected void setLogic() {
        addBtn.setOnAction(e ->
                AddAccountDialog.show(stage, newAccount -> viewModel.addAccount(newAccount)));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    private HBox buildFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-border-color: " + Themes.BORDER_LIGHT +
                "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        String filterStyle = "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6;" +
                " -fx-background-radius: 6; -fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

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
        search.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0;" +
                " -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        search.textProperty().addListener((obs, old, val) -> viewModel.filterBySearch(val));

        StackPane searchPane = new StackPane();
        searchPane.setAlignment(Pos.CENTER_LEFT);
        Label searchPrompt = new Label("Search by name...");
        searchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");
        searchPrompt.setMouseTransparent(true);
        search.textProperty().addListener((obs, old, val) -> searchPrompt.setVisible(val.isEmpty()));
        searchPane.getChildren().addAll(search, searchPrompt);
        HBox.setHgrow(searchPane, Priority.ALWAYS);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);
        searchContainer.getChildren().addAll(searchIcon, searchPane);

        Button clear = new Button("Clear");
        clear.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED +
                "; -fx-font-weight: bold; -fx-cursor: hand;");
        clear.setOnAction(e -> {
            search.clear();
            viewModel.filterBySearch("");
        });

        bar.getChildren().addAll(searchContainer, clear);
        return bar;
    }

    private String currencyPrefix(String currency) {
        if (currency == null) return "";
        return switch (currency.toUpperCase()) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            default    -> currency + " ";
        };
    }

    private void showAccountActions(Account acc, javafx.scene.input.MouseEvent e) {
        ContextMenu menu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setStyle("-fx-text-fill: " + Themes.TEXT_ERROR + ";");
        deleteItem.setOnAction(ev -> viewModel.deleteAccount(acc.getId()));
        menu.getItems().add(deleteItem);
        menu.show(stage, e.getScreenX(), e.getScreenY());
    }
}
