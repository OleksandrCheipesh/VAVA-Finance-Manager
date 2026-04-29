package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;
import org.example.model.models.ProjectsModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProjectsViewModel {
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final FilteredList<Project> filteredProjects = new FilteredList<>(projects, p -> true);
    private final StringProperty message = new SimpleStringProperty("");
    private ProjectService db = new ProjectService();
    private final StringProperty activeProject = new SimpleStringProperty("");
    private final StringProperty budget = new SimpleStringProperty("$2.4M");
    private final BooleanProperty hasAccess = new SimpleBooleanProperty(new ProjectsModel().hasAccess());

    public StringProperty activeProjectProperty() { return activeProject; }
    public StringProperty budgetProperty() { return budget; }
    public BooleanProperty hasAccessProperty() { return hasAccess; }

    public ProjectsViewModel() {
        loadProjects();
    }

    public void loadProjects() {
        var logger = org.example.logging.AppLog.getLogger(ProjectsViewModel.class);

        int companyId = SessionManager.getInstance().getCurrentCompanyId();
        logger.info("Loading projects for companyId={}", companyId);

        try {
            List<Project> dbProjects = db.getProjectsByCompanyId(companyId);
            projects.clear();
            projects.addAll(dbProjects);
            message.set("Success: Projects loaded successfully!");
            logger.info("Loaded {} projects for companyId={}", dbProjects.size(), companyId);
        } catch (Exception e) {
            message.set("Error: Failed to load projects. " + e.getMessage());
            logger.error("Failed to load projects for companyId={}", companyId, e);
        } finally {
            activeProject.set(projects.stream().filter(Project::isActive).count() + " Units");
            budget.set(formatBudget());
        }
    }

    public void addProject(Project p) {
        try {
            projects.add(0, p);
            db.addProject(p);
            message.set("Success: Project '" + p.getName() + "' created successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to create project.");
        } finally {
            activeProject.set((int) projects.stream().filter(Project::isActive).count() + " Units");
            budget.set(formatBudget());
        }
    }

    public void deleteProject(Project p) {
        try {
            projects.removeIf(project -> project.getId() == p.getId());
            db.deleteProject(p.getId());
            message.set("Success: Project '" + p.getName() + "' delete successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to delete project.");
        } finally {
            activeProject.set(projects.stream().filter(Project::isActive).count() + " Units");
            budget.set(formatBudget());
        }
    }

    private String formatBudget() {
        float total = (float) projects.stream()
                .map(Project::getBudgetLimit)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        if (total >= 1_000_000) return String.format("%.1fM", total / 1_000_000);
        if (total >= 1_000) return String.format("%.1fK", total / 1_000);
        return String.format("%.1f $", total);
    }

    public void filterBySearch(String regex) {
        if (regex == null || regex.trim().isEmpty()) {
            filteredProjects.setPredicate(p -> true);
            return;
        }
        try {
            Pattern pattern = Pattern.compile("(?i).*" + regex + ".*");
            filteredProjects.setPredicate(project -> pattern.matcher(project.getName()).matches());
        } catch (Exception e) {
            filteredProjects.setPredicate(project -> project.getName().toLowerCase().contains(regex.toLowerCase()));
        }
    }

    public FilteredList<Project> getFilteredProjects() { return filteredProjects; }
    public StringProperty messageProperty() { return message; }
}