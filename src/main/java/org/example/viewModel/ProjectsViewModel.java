package org.example.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.SessionManager;
import org.example.model.database.entity.Employee;
import org.example.model.database.entity.Project;
import org.example.model.database.service.EmployeeService;
import org.example.model.database.service.ProjectService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class ProjectsViewModel {
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final FilteredList<Project> filteredProjects = new FilteredList<>(projects, p -> true);
    private final StringProperty message = new SimpleStringProperty("");
    private ProjectService db = new ProjectService();
    private final StringProperty activeProject = new SimpleStringProperty("");
    //TO DO: Total free budget Maybe some entity to get it
    private final StringProperty budget = new SimpleStringProperty("$2.4M");
    public StringProperty activeProjectProperty() { return activeProject; }
    public StringProperty budgetProperty() { return budget; }


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
        }
        finally {
            activeProject.set(projects.stream()
                    .filter(Project::isActive)
                    .count() + " Units");

        }
    }

    public void addProject(Project p) {
        try {
            projects.add(0, p); // Add to beginning of list
            db.addProject(p);
            message.set("Success: Project '" + p.getName() + "' created successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to create project.");
        }
        finally {
            activeProject.set((int) projects.stream()
                    .filter(Project::isActive)
                    .count() + " Units");

        }
    }
    public void deleteProject(Project p) {
        try {
            projects.removeIf(project -> project.getId() == p.getId());
            db.deleteProject(p.getId());
            message.set("Success: Project '" + p.getName() + "' delete successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to delete project.");
        }
        finally {
            activeProject.set(projects.stream()
                    .filter(Project::isActive)
                    .count() + " Units");

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