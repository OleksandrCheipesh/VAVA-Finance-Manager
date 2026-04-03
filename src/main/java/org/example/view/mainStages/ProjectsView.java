package org.example.view.mainStages;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.example.model.database.entity.Project;
import org.example.view.templates.*;
import org.example.viewModel.ProjectsViewModel;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class ProjectsView extends BaseView {

    private final ProjectsViewModel viewModel = new ProjectsViewModel();
    private BorderPane root;
    private VBox contentArea;
    private FlowPane cardGrid;
    private StateButton addBtn;

    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // --- 1. Top Bar ---
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Projects");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label subTitle = new Label("Monitoring capital allocation and architectural milestones.");
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton("+ Add New Project", StateButton.ButtonType.PRIMARY);

        topBar.getChildren().addAll(titleBox, spacer, addBtn);

        // --- 2. Content Area ---
        contentArea = new VBox(25);
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Header Row (Title + Search + Summary Widgets)
        HBox contentHeader = new HBox(20);
        contentHeader.setAlignment(Pos.CENTER_LEFT);

        Label activeTitle = new Label("Active Projects");
        activeTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        // Leader requested Regex Search Bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("\uD83D\uDD0D Search by name...");
        searchBar.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 15; -fx-font-size: 14px;");
        searchBar.setPrefWidth(250);
        searchBar.textProperty().addListener((obs, old, val) -> viewModel.filterBySearch(val));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Summary Widgets
        HBox summaryBox = new HBox(15);

//        Мне нужно здесь подключить данные из view model
        summaryBox.getChildren().addAll(

                createHeaderWidget("TOTAL BUDGET", viewModel.budgetProperty(), "💼"),
                createHeaderWidget("ACTIVE SPRINT", viewModel.activeProjectProperty(), "⏱")
        );

        contentHeader.getChildren().addAll(activeTitle, searchBar, headerSpacer, summaryBox);

        // --- 3. Card Grid ---
        cardGrid = new FlowPane(25, 25);
        cardGrid.setAlignment(Pos.TOP_LEFT);

        // Listen for data changes to redraw cards
        viewModel.getFilteredProjects().addListener((ListChangeListener<Project>) c -> refreshCards());
        refreshCards(); // Initial draw

        ScrollPane scroll = new ScrollPane(cardGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox.setVgrow(scroll, Priority.ALWAYS);

        contentArea.getChildren().addAll(contentHeader, scroll);
        mainContainer.getChildren().addAll(topBar, contentArea);
        root.setCenter(mainContainer);

        scene = new Scene(root);
        stage.setTitle("Admin - Projects");
    }

    @Override
    protected void setStyle() {}

    @Override
    protected void setLogic() {
        addBtn.setOnAction(e -> AddProjectDialog.show(stage, newProj -> viewModel.addProject(newProj)));

        viewModel.messageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("Success:")) {
                ToastManager.showSuccess(stage, newVal.replace("Success: ", ""));
            } else if (newVal != null && newVal.startsWith("Error:")) {
                ToastManager.showError(stage, newVal.replace("Error: ", ""));
            }
        });
    }

    private void refreshCards() {
        cardGrid.getChildren().clear();
        for (Project p : viewModel.getFilteredProjects()) {
            cardGrid.getChildren().add(createProjectCard(p));
        }

        // Always append the "Add New Project" dashed card at the end
        cardGrid.getChildren().add(createAddNewCard());
    }

    // --- Card Builders ---

    private VBox createProjectCard(Project p) {
        VBox card = new VBox(15);
        card.setPrefSize(320, 300);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-cursor: hand;");
        card.setOnMouseClicked(e -> ProjectDetailsDialog.show(stage, p, deletedProject -> {
            viewModel.deleteProject(deletedProject);
        }));

        // Top Row (Icon + Pill)
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("▶"); // Placeholder for an icon
        icon.setStyle("-fx-background-color: #E6F7F6; -fx-text-fill: " + Themes.PRIMARY + "; -fx-padding: 8 12; -fx-background-radius: 8; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusPill = new Label("MONITORING");
        statusPill.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.PRIMARY + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: 900;");

        topRow.getChildren().addAll(icon, spacer, statusPill);

        // Titles
        VBox titleBox = new VBox(8);
        Label name = new Label(p.getName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        desc.setPrefHeight(45); // Force consistent height
        titleBox.getChildren().addAll(name, desc);

        // Details (Timeline & Budget)
        HBox detailsBox = new HBox(20);

        VBox timelineBox = new VBox(5);
        Label timeLabel = new Label("TIMELINE");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label timeValue = new Label(p.getStartDate().format(dateFormatter) + " - " + p.getEndDate().format(dateFormatter));
        timeValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        timelineBox.getChildren().addAll(timeLabel, timeValue);

        VBox capBox = new VBox(5);
        Label capLabel = new Label("BUDGET CAP");
        capLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label capValue = new Label(currencyFormat.format(p.getBudgetLimit()));
        capValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + ";");
        capBox.getChildren().addAll(capLabel, capValue);

        detailsBox.getChildren().addAll(timelineBox, capBox);

        // Progress Bar
        double percentage = p.getSpendPercentage();
        int displayPercent = (int)(percentage * 100);

        // Progress color logic based on leader requirements
        String barColor = percentage >= 0.9 ? Themes.TEXT_ERROR : (percentage >= 0.75 ? "#F59E0B" : Themes.PRIMARY);

        VBox progressBox = new VBox(8);
        HBox progressHeader = new HBox();
        Label pLabel = new Label("Spending Progress");
        pLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Region pSpacer = new Region();
        HBox.setHgrow(pSpacer, Priority.ALWAYS);
        Label pValue = new Label(displayPercent + "% Used");
        pValue.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + barColor + ";");
        progressHeader.getChildren().addAll(pLabel, pSpacer, pValue);

        // Custom drawn progress bar using HBox and Region for exact Figma styling
        HBox barTrack = new HBox();
        barTrack.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 4; -fx-pref-height: 6px;");
        Region barFill = new Region();
        barFill.setStyle("-fx-background-color: " + barColor + "; -fx-background-radius: 4;");
        // Bind width to percentage of parent
        barFill.prefWidthProperty().bind(barTrack.widthProperty().multiply(Math.min(percentage, 1.0)));
        barTrack.getChildren().add(barFill);

        progressBox.getChildren().addAll(progressHeader, barTrack);

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        card.getChildren().addAll(topRow, titleBox, detailsBox, bottomSpacer, progressBox);
        return card;
    }

    private VBox createAddNewCard() {
        VBox card = new VBox(15);
        card.setPrefSize(320, 300);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: transparent; -fx-background-radius: 16; -fx-border-color: #CBD5E1; -fx-border-style: dashed; -fx-border-radius: 16; -fx-border-width: 2; -fx-cursor: hand;");

        Label icon = new Label("+");
        icon.setStyle("-fx-font-size: 36px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");

        Label title = new Label("Add New Project");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        Label subTitle = new Label("Define objectives and allocate\narchitectural capital");
        subTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-text-alignment: center;");

        card.getChildren().addAll(icon, title, subTitle);

        // Clicking the dashed card does the exact same thing as the top right button!
        card.setOnMouseClicked(e -> AddProjectDialog.show(stage, newProj -> viewModel.addProject(newProj)));

        return card;
    }

    private HBox createHeaderWidget(String title, StringProperty valueProperty, String iconTxt) {
        HBox widget = new HBox(15);
        widget.setAlignment(Pos.CENTER_LEFT);
        widget.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        Label icon = new Label(iconTxt);
        icon.setStyle("-fx-background-color: #E6F7F6; -fx-text-fill: " + Themes.PRIMARY + "; -fx-padding: 8; -fx-background-radius: 8; -fx-font-size: 16px;");

        VBox textBox = new VBox(2);
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label lblValue = new Label();
        lblValue.textProperty().bind(valueProperty);
        lblValue.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        textBox.getChildren().addAll(lblTitle, lblValue);

        widget.getChildren().addAll(icon, textBox);
        return widget;
    }
}