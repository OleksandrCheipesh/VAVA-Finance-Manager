package org.example.view.mainStages;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.logging.AppLog;
import org.example.model.database.entity.Client;
import org.example.view.templates.*;
import org.example.viewModel.ClientViewModel;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

public class ClientsView extends BaseView {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ClientViewModel viewModel = new ClientViewModel();

    private BorderPane root;
    private VBox contentArea;
    private AppTable<Client> table;
    private StateButton addBtn;

    private FilteredList<Client> filtered;
    private SortedList<Client> sorted;

    private TextField searchField;
    private ComboBox<String> incomeSortCombo;
    private ComboBox<String> dateSortCombo;

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        filtered = new FilteredList<>(viewModel.getClients(), p -> true);
        sorted = new SortedList<>(filtered);

        VBox mainContainer = new VBox();

        // ── Top bar ───────────────────────────────────────────────────────────
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                "-fx-padding: 0 40;" +
                "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                "-fx-border-width: 0 0 1 0;"
        );
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Label title = new Label("Clients");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subtitle = new Label("Manage your clients and their profiles.");
        subtitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");

        titleBox.getChildren().addAll(title, subtitle);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add Client", StateButton.ButtonType.PRIMARY);
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));
        addBtn.setOnMousePressed(e -> { addBtn.setScaleX(0.95); addBtn.setScaleY(0.95); });
        addBtn.setOnMouseReleased(e -> { addBtn.setScaleX(1.0); addBtn.setScaleY(1.0); });

        topBar.getChildren().addAll(titleBox, topSpacer, addBtn);

        // ── Content area ──────────────────────────────────────────────────────
        contentArea = new VBox(25);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        HBox summaryRow = buildSummaryRow();
        HBox filterBar = buildFilterBar();

        table = new AppTable<>("");
        table.setItems(sorted);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox emptyState = new VBox(5);
        emptyState.setAlignment(Pos.CENTER);
        Label emptyTitle = new Label("No Clients Yet");
        emptyTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: 900; -fx-text-fill: #111827;");
        Label emptySub = new Label("Register your first client");
        emptySub.setStyle("-fx-font-size: 18px; -fx-text-fill: #869F9B;");
        emptyState.getChildren().addAll(emptyTitle, emptySub);
        table.setPlaceholder(emptyState);

        buildTableColumns();

        table.setOnMouseClicked(e -> {
            Client selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) openDetailDialog(selected);
        });

        contentArea.getChildren().addAll(summaryRow, filterBar, table);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        } catch (Exception e) {
            AppLog.getLogger(ClientsView.class).warn("Could not load CSS: {}", e.getMessage());
        }

        stage.setTitle("Admin - Clients");
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

        addBtn.setOnAction(e -> AddEditClientDialog.show(stage, null, client -> {
            try {
                viewModel.addClient(client);
                ToastManager.showSuccess(stage, "Client added successfully.");
            } catch (IllegalArgumentException | SQLException ex) {
                ToastManager.showError(stage, ex.getMessage());
            }
        }));
    }

    // ── Filter / sort logic ───────────────────────────────────────────────────

    private void updateFilter() {
        String query = searchField.getText().trim().toLowerCase();
        filtered.setPredicate(client -> {
            if (query.isEmpty()) return true;
            String fullName = (client.getName() + " " + client.getSurname()).toLowerCase();
            String email = client.getEmail() != null ? client.getEmail().toLowerCase() : "";
            String phone = client.getPhone() != null ? client.getPhone().toLowerCase() : "";
            return fullName.contains(query) || email.contains(query) || phone.contains(query);
        });
    }

    private void updateComparator() {
        String iv = incomeSortCombo.getValue();
        String dv = dateSortCombo.getValue();

        Comparator<Client> comp = null;

        if ("Income ↑".equals(iv)) {
            comp = Comparator.comparing(
                    c -> c.getMonthlyIncome() != null ? c.getMonthlyIncome() : BigDecimal.ZERO,
                    Comparator.naturalOrder()
            );
        } else if ("Income ↓".equals(iv)) {
            comp = Comparator.comparing(
                    (Client c) -> c.getMonthlyIncome() != null ? c.getMonthlyIncome() : BigDecimal.ZERO,
                    Comparator.reverseOrder()
            );
        }

        Comparator<Client> dateComp = null;
        if ("Newest first".equals(dv)) {
            dateComp = Comparator.comparing(
                    Client::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())
            );
        } else if ("Oldest first".equals(dv)) {
            dateComp = Comparator.comparing(
                    Client::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())
            );
        }

        if (comp == null) comp = dateComp;
        else if (dateComp != null) comp = comp.thenComparing(dateComp);

        sorted.setComparator(comp);
    }

    // ── Detail dialog ─────────────────────────────────────────────────────────

    private void openDetailDialog(Client client) {
        ClientDetailDialog.show(stage, client,
                () -> AddEditClientDialog.show(stage, client, updated -> {
                    try {
                        viewModel.updateClient(updated);
                        table.refresh();
                        ToastManager.showSuccess(stage, "Client updated successfully.");
                    } catch (IllegalArgumentException | SQLException ex) {
                        ToastManager.showError(stage, ex.getMessage());
                    }
                }),
                () -> {
                    try {
                        viewModel.deleteClient(client.getId());
                        ToastManager.showSuccess(stage, "Client deleted.");
                    } catch (SQLException ex) {
                        ToastManager.showError(stage, ex.getMessage());
                    }
                }
        );
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void buildTableColumns() {
        TableColumn<Client, String> idCol = new TableColumn<>("#");
        idCol.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getId())));
        idCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        idCol.prefWidthProperty().bind(table.widthProperty().multiply(0.06));

        TableColumn<Client, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName() + " " + cd.getValue().getSurname()));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.20));

        TableColumn<Client, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(cd -> new SimpleStringProperty(orDash(cd.getValue().getEmail())));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.22));

        TableColumn<Client, String> phoneCol = new TableColumn<>("PHONE");
        phoneCol.setCellValueFactory(cd -> new SimpleStringProperty(orDash(cd.getValue().getPhone())));
        phoneCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<Client, String> incomeCol = new TableColumn<>("MONTHLY INCOME");
        incomeCol.setCellValueFactory(cd -> {
            BigDecimal income = cd.getValue().getMonthlyIncome();
            String text = income != null ? String.format(Locale.US, "$%,.2f", income.doubleValue()) : "—";
            return new SimpleStringProperty(text);
        });
        incomeCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        incomeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<Client, String> sinceCol = new TableColumn<>("MEMBER SINCE");
        sinceCol.setCellValueFactory(cd -> {
            var dt = cd.getValue().getCreatedAt();
            return new SimpleStringProperty(dt != null ? dt.toLocalDate().format(DATE_FMT) : "—");
        });
        sinceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.13));

        TableColumn<Client, Void> actionCol = new TableColumn<>("ACTION");
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Label dots = new Label("⋮");
                    dots.setPadding(Insets.EMPTY);
                    dots.setStyle(
                            "-fx-text-fill: #9CA3AF;" +
                            "-fx-font-size: 24px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-background-color: transparent;"
                    );
                    dots.setOnMouseClicked(e -> {
                        Client client = getTableView().getItems().get(getIndex());
                        openDetailDialog(client);
                    });
                    setGraphic(dots);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        actionCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, incomeCol, sinceCol, actionCol);
    }

    // ── Summary row ───────────────────────────────────────────────────────────

    private HBox buildSummaryRow() {
        HBox row = new HBox(20);

        Label totalCountVal = new Label();
        Label totalIncomeVal = new Label();
        Label avgIncomeVal = new Label();

        Label totalCountSub = new Label("registered clients");
        Label totalIncomeSub = new Label("combined monthly");
        Label avgIncomeSub = new Label("per client avg.");

        Runnable updateStats = () -> {
            var list = viewModel.getClients();
            int count = list.size();
            BigDecimal total = list.stream()
                    .map(Client::getMonthlyIncome)
                    .filter(i -> i != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = count == 0 ? BigDecimal.ZERO :
                    total.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);

            totalCountVal.setText(String.valueOf(count));
            totalIncomeVal.setText(String.format(Locale.US, "$%,.2f", total.doubleValue()));
            avgIncomeVal.setText(String.format(Locale.US, "$%,.2f", avg.doubleValue()));
        };

        updateStats.run();
        viewModel.getClients().addListener((javafx.collections.ListChangeListener<Client>) c -> updateStats.run());

        row.getChildren().addAll(
                createSummaryCard("TOTAL CLIENTS", totalCountVal, totalCountSub, Themes.TEXT_SUCCESS),
                createSummaryCard("TOTAL MONTHLY INCOME", totalIncomeVal, totalIncomeSub, Themes.TEXT_MUTED),
                createSummaryCard("AVERAGE MONTHLY INCOME", avgIncomeVal, avgIncomeSub, Themes.TEXT_MUTED)
        );
        return row;
    }

    private VBox createSummaryCard(String title, Label valueLabel, Label subText, String subColor) {
        VBox card = new VBox(4);
        card.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 18 20;"
        );
        HBox.setHgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        subText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + subColor + ";");

        card.getChildren().addAll(lblTitle, valueLabel, subText);
        return card;
    }

    // ── Filter bar ────────────────────────────────────────────────────────────

    private HBox buildFilterBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
                "-fx-background-color: " + Themes.BG_CARD + ";" +
                "-fx-border-color: " + Themes.BORDER_LIGHT + ";" +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;"
        );

        String comboStyle =
                "-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 6; -fx-background-radius: 6;" +
                "-fx-font-size: 14px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

        // ── Search ────────────────────────────────────────────────────────────
        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(comboStyle + " -fx-padding: 0 12;");
        searchContainer.setPrefHeight(40);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);

        SVGPath searchIcon = new SVGPath();
        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        searchField = new TextField();
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        StackPane searchPane = new StackPane();
        searchPane.setAlignment(Pos.CENTER_LEFT);
        Label searchPrompt = new Label("Search by name, email, phone...");
        searchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");
        searchPrompt.setMouseTransparent(true);
        searchField.textProperty().addListener((obs, old, val) -> {
            searchPrompt.setVisible(val.isEmpty());
            updateFilter();
        });
        searchPane.getChildren().addAll(searchField, searchPrompt);
        HBox.setHgrow(searchPane, Priority.ALWAYS);

        searchContainer.getChildren().addAll(searchIcon, searchPane);

        // ── Income sort ───────────────────────────────────────────────────────
        incomeSortCombo = new ComboBox<>();
        incomeSortCombo.getItems().addAll("Income (default)", "Income ↑", "Income ↓");
        incomeSortCombo.setValue("Income (default)");
        incomeSortCombo.setStyle(comboStyle);
        incomeSortCombo.setPrefHeight(40);
        incomeSortCombo.setPrefWidth(155);
        incomeSortCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Income (default)" : item);
            }
        });
        incomeSortCombo.valueProperty().addListener((obs, old, val) -> updateComparator());

        // ── Date sort ─────────────────────────────────────────────────────────
        dateSortCombo = new ComboBox<>();
        dateSortCombo.getItems().addAll("Date (default)", "Newest first", "Oldest first");
        dateSortCombo.setValue("Date (default)");
        dateSortCombo.setStyle(comboStyle);
        dateSortCombo.setPrefHeight(40);
        dateSortCombo.setPrefWidth(145);
        dateSortCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Date (default)" : item);
            }
        });
        dateSortCombo.valueProperty().addListener((obs, old, val) -> updateComparator());

        // ── Clear ─────────────────────────────────────────────────────────────
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-weight: bold; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            incomeSortCombo.setValue("Income (default)");
            dateSortCombo.setValue("Date (default)");
        });

        bar.getChildren().addAll(searchContainer, incomeSortCombo, dateSortCombo, clearBtn);
        return bar;
    }

    private static String orDash(String value) {
        return (value != null && !value.isBlank()) ? value : "—";
    }
}