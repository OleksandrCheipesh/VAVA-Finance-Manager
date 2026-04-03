package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Project;
import org.example.model.database.service.ProjectService;

import java.util.List;
import java.util.regex.Pattern;

public class ProjectsViewModel {

    private final ProjectService projectService = new ProjectService();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final FilteredList<Project> filteredProjects = new FilteredList<>(projects, p -> true);
    private final StringProperty message = new SimpleStringProperty("");

    public ProjectsViewModel() {
        loadProjects();
    }

    public void loadProjects() {
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            List<Project> list = projectService.getProjectsByCompanyId(companyId);
            projects.setAll(list);
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
        } catch (Exception e) {
            message.set("Error: Failed to load projects — " + e.getMessage());
        }
    }

    public void addProject(Project p) {
        message.set("");
        try {
            int companyId = SessionManager.getInstance().getCurrentCompanyId();
            p.setCompanyId(companyId);
            Project saved = projectService.addProject(p);
            projects.add(0, saved);
            message.set("Success: Project '" + saved.getName() + "' created successfully!");
        } catch (IllegalStateException e) {
            message.set("Error: No active session. Please log in.");
        } catch (Exception e) {
            message.set("Error: Failed to create project — " + e.getMessage());
        }
    }

    // Real-time Regex Search
    public void filterBySearch(String regex) {
        if (regex == null || regex.trim().isEmpty()) {
            filteredProjects.setPredicate(p -> true);
            return;
        }

        try {
            Pattern pattern = Pattern.compile("(?i).*" + regex + ".*"); // Case-insensitive regex
            filteredProjects.setPredicate(project -> pattern.matcher(project.getName()).matches());
        } catch (Exception e) {
            // If user types an invalid regex halfway through, just fall back to simple contains
            filteredProjects.setPredicate(project -> project.getName().toLowerCase().contains(regex.toLowerCase()));
        }
    }

    public FilteredList<Project> getFilteredProjects() { return filteredProjects; }
    public StringProperty messageProperty() { return message; }
}