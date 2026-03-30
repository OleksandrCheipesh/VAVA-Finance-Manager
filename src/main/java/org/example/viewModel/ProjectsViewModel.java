package org.example.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.model.database.entity.Project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ProjectsViewModel {

    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final FilteredList<Project> filteredProjects = new FilteredList<>(projects, p -> true);
    private final StringProperty message = new SimpleStringProperty("");

    public ProjectsViewModel() {
        loadProjects();
    }

    public void loadProjects() {
        projects.clear();

        // 1. Create using the REAL database constructor
        Project p1 = new Project(1, "Legacy Core Refactor", "Systematic migration of monolithic COBOL services to modern microservices.", new BigDecimal("540000"), LocalDate.of(2024, 5, 20), LocalDate.of(2024, 11, 12), true);
        p1.setId(1);
        p1.setCurrentSpend(new BigDecimal("383400")); // Inject the spend amount

        Project p2 = new Project(1, "Internal Growth", "Scale internal operations to accommodate the 40% growth in staff projected for Q4.", new BigDecimal("250000"), LocalDate.of(2024, 1, 12), LocalDate.of(2024, 12, 20), true);
        p2.setId(2);
        p2.setCurrentSpend(new BigDecimal("182500"));

        Project p3 = new Project(1, "Mobile App V2", "Redesign and deploy the new native mobile application for iOS and Android.", new BigDecimal("150000"), LocalDate.of(2024, 3, 1), LocalDate.of(2024, 8, 30), true);
        p3.setId(3);
        p3.setCurrentSpend(new BigDecimal("140000"));

        projects.addAll(p1, p2, p3);
    }

    public void addProject(Project p) {
        try {
            projects.add(0, p); // Add to beginning of list
            message.set("Success: Project '" + p.getName() + "' created successfully!");
        } catch (Exception e) {
            message.set("Error: Failed to create project.");
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