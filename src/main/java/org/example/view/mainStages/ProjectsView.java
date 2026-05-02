package org.example.view.mainStages;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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
    private HBox topBar;

    private final DecimalFormat currencyFormat = new DecimalFormat("$#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    @Override
    protected void setContent() {
        root = new BorderPane();
        buildSidebar(root);

        VBox mainContainer = new VBox();

        // --- 1. Top Bar ---
        topBar = new HBox(20);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setStyle("-fx-background-color: " + Themes.BG_CARD + "; -fx-padding: 0 40; -fx-border-color: " + Themes.BORDER_LIGHT + "; -fx-border-width: 0 0 1 0;");
        topBar.setMinHeight(85);

        VBox titleBox = new VBox(2);
        titleBox.setAlignment(Pos.BOTTOM_LEFT);
        Label title = new Label(I18n.t("Projects"));
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        I18n.language.addListener((obs, o, v) -> title.setText(I18n.t("Projects")));
        Label subTitle = new Label(I18n.t("Monitoring capital allocation and architectural milestones."));
        subTitle.setStyle("-fx-text-fill: " + Themes.TEXT_MUTED + "; -fx-font-size: 14px;");
        I18n.language.addListener((obs, o, v) -> subTitle.setText(I18n.t("Monitoring capital allocation and architectural milestones.")));
        titleBox.getChildren().addAll(title, subTitle);

        HBox.setMargin(titleBox, new Insets(0, 0, 15, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new StateButton(I18n.t("+ Add New Project"), StateButton.ButtonType.PRIMARY);
        I18n.language.addListener((obs, o, v) -> addBtn.setText(I18n.t("+ Add New Project")));
        HBox.setMargin(addBtn, new Insets(0, 0, 20, 0));

        topBar.getChildren().addAll(titleBox, spacer, addBtn);

        // --- 2. Content Area ---
        contentArea = new VBox(25);
        contentArea.setPadding(new Insets(30, 40, 40, 40));
        contentArea.setStyle("-fx-background-color: " + Themes.BG_DASHBOARD + ";");

        // Header Row
        HBox contentHeader = new HBox(20);
        contentHeader.setAlignment(Pos.CENTER_LEFT);

        Label activeTitle = new Label(I18n.t("Active Projects"));
        activeTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        I18n.language.addListener((obs, o, v) -> activeTitle.setText(I18n.t("Active Projects")));

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 15;");
        searchContainer.setMinHeight(40);
        searchContainer.setPrefHeight(40);
        searchContainer.setMaxHeight(40);
        searchContainer.setPrefWidth(250);

        SVGPath searchIcon = new SVGPath();
        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.web(Themes.TEXT_MUTED));
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        TextField searchBar = new TextField();
        searchBar.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-font-size: 14px; -fx-text-fill: " + Themes.TEXT_DARK + ";");

        StackPane searchPane = new StackPane();
        searchPane.setAlignment(Pos.CENTER_LEFT);
        Label searchPrompt = new Label(I18n.t("Search by name..."));
        searchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");
        searchPrompt.setMouseTransparent(true);
        I18n.language.addListener((obs, o, v) -> searchPrompt.setText(I18n.t("Search by name...")));

        searchBar.textProperty().addListener((obs, old, val) -> {
            searchPrompt.setVisible(val.isEmpty());
            viewModel.filterBySearch(val);
        });

        searchPane.getChildren().addAll(searchBar, searchPrompt);
        HBox.setHgrow(searchPane, Priority.ALWAYS);
        searchContainer.getChildren().addAll(searchIcon, searchPane);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox summaryBox = new HBox(15);
        summaryBox.getChildren().addAll(
                createHeaderWidget("TOTAL BUDGET", viewModel.budgetProperty(), "wallet"),
                createHeaderWidget("ACTIVE SPRINT", viewModel.activeProjectProperty(), "calendar")
        );
        I18n.language.addListener((obs, o, v) -> summaryBox.getChildren().setAll(
                createHeaderWidget("TOTAL BUDGET", viewModel.budgetProperty(), "wallet"),
                createHeaderWidget("ACTIVE SPRINT", viewModel.activeProjectProperty(), "calendar")
        ));

        contentHeader.getChildren().addAll(activeTitle, searchContainer, headerSpacer, summaryBox);

        // --- 3. Card Grid ---
        cardGrid = new FlowPane(25, 25);
        cardGrid.setAlignment(Pos.TOP_LEFT);

        viewModel.getFilteredProjects().addListener((ListChangeListener<Project>) c -> refreshCards());
        refreshCards();

        ScrollPane outerScroll = new ScrollPane(contentArea);
        outerScroll.setFitToWidth(true);
        outerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        outerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        outerScroll.setStyle("-fx-background: " + Themes.BG_DASHBOARD + "; -fx-background-color: " + Themes.BG_DASHBOARD + "; -fx-border-width: 0;");
        VBox.setVgrow(outerScroll, Priority.ALWAYS);

        contentArea.getChildren().addAll(contentHeader, cardGrid);
        mainContainer.getChildren().addAll(topBar, outerScroll);
        root.setCenter(mainContainer);

        scene = new Scene(root);
        stage.setTitle("Admin - Projects");
    }

    @Override
    protected void setStyle() {}

    @Override
    protected void setLogic() {
        if (!viewModel.hasAccessProperty().get()) {
            showAccessDenied();
            return;
        }

        addBtn.setOnAction(e -> AddProjectDialog.show(stage, null, newProj -> viewModel.addProject(newProj)));

        addBtn.setOnMousePressed(e -> {
            addBtn.setScaleX(0.95);
            addBtn.setScaleY(0.95);
        });

        addBtn.setOnMouseReleased(e -> {
            addBtn.setScaleX(1.0);
            addBtn.setScaleY(1.0);
        });

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
        cardGrid.getChildren().add(createAddNewCard());
    }

    private VBox createProjectCard(Project p) {
        VBox card = new VBox(15);
        card.setPrefSize(320, 300);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-cursor: hand;");
        card.setOnMouseClicked(e -> ProjectDetailsDialog.show(stage, p,
                updatedProject -> viewModel.updateProject(updatedProject),
                deletedProject -> viewModel.deleteProject(deletedProject)
        ));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: #E6F7F6; -fx-padding: 8; -fx-background-radius: 8;");

        ImageView baseFolder = IconFactory.getIcon("folder", 16);
        baseFolder.setFitWidth(16);
        baseFolder.setFitHeight(16);

        Region coloredFolder = new Region();
        coloredFolder.setStyle("-fx-background-color: " + Themes.PRIMARY + ";");
        coloredFolder.setMinSize(16, 16);
        coloredFolder.setMaxSize(16, 16);
        coloredFolder.setClip(baseFolder);

        iconBox.getChildren().add(coloredFolder);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusPill = new Label("MONITORING");
        statusPill.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: " + Themes.PRIMARY + "; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 10px; -fx-font-weight: 900;");

        topRow.getChildren().addAll(iconBox, spacer, statusPill);

        VBox titleBox = new VBox(8);
        Label name = new Label(p.getName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        desc.setPrefHeight(45);
        titleBox.getChildren().addAll(name, desc);

        HBox detailsBox = new HBox();
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        VBox timelineBox = new VBox(5);
        Label timeLabel = new Label("TIMELINE");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label timeValue = new Label(p.getStartDate().format(dateFormatter) + " - " + p.getEndDate().format(dateFormatter));
        timeValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        timelineBox.getChildren().addAll(timeLabel, timeValue);

        Region detailSpacer = new Region();
        HBox.setHgrow(detailSpacer, Priority.ALWAYS);

        VBox capBox = new VBox(5);
        capBox.setAlignment(Pos.CENTER_LEFT);
        Label capLabel = new Label("BUDGET CAP");
        capLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label capValue = new Label(currencyFormat.format(p.getBudgetLimit()));
        capValue.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + Themes.PRIMARY + ";");
        capBox.getChildren().addAll(capLabel, capValue);

        detailsBox.getChildren().addAll(timelineBox, detailSpacer, capBox);

        double percentage = p.getSpendPercentage();
        int displayPercent = (int)(percentage * 100);
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

        HBox barTrack = new HBox();
        barTrack.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 4; -fx-pref-height: 6px;");
        Region barFill = new Region();
        barFill.setStyle("-fx-background-color: " + barColor + "; -fx-background-radius: 4;");
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
        card.setOnMouseClicked(e -> AddProjectDialog.show(stage, null, newProj -> viewModel.addProject(newProj)));

        return card;
    }

    private HBox createHeaderWidget(String titleKey, StringProperty valueProperty, String iconName) {
        HBox widget = new HBox(15);
        widget.setAlignment(Pos.CENTER_LEFT);
        widget.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 12 20;");

        StackPane iconBox = new StackPane();
        iconBox.setMinSize(36, 36);
        iconBox.setMaxSize(36, 36);
        iconBox.setStyle("-fx-background-color: #E6F7F6; -fx-background-radius: 8;");
        ImageView iconView = IconFactory.getIcon(iconName, 18);
        iconBox.getChildren().add(iconView);

        VBox textBox = new VBox(2);
        Label lblTitle = new Label(I18n.t(titleKey));
        lblTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_MUTED + ";");
        Label lblValue = new Label();
        lblValue.textProperty().bind(valueProperty);
        lblValue.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: " + Themes.TEXT_DARK + ";");
        textBox.getChildren().addAll(lblTitle, lblValue);

        widget.getChildren().addAll(iconBox, textBox);
        return widget;
    }
}