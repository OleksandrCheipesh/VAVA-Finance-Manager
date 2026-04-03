package org.example.model.validation;

import org.example.model.database.entity.Employee;
import org.example.model.database.entity.Project;

import java.math.BigDecimal;

public class ProjectValidator {
    public static void validate(Project project) {
        if (project == null) {
            throw new ProjValExept(
                    "Project is null",
                    ProjValExept.ProjErrorCode.PROJECT_NULL
            );
        }

        if (project.getName() == null || project.getName().isBlank()) {
            throw new ProjValExept(
                    "Project name is required",
                    ProjValExept.ProjErrorCode.NAME_EMPTY
            );
        }

        if (project.getBudgetLimit() != null &&
                project.getBudgetLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProjValExept(
                    "Budget cannot be negative",
                    ProjValExept.ProjErrorCode.BUDGET_NEGATIVE
            );
        }

        if (project.getStartDate() != null &&
                project.getEndDate() != null &&
                project.getStartDate().isAfter(project.getEndDate())) {
            throw new ProjValExept(
                    "Start date cannot be after end date",
                    ProjValExept.ProjErrorCode.DATE_INVALID_RANGE
            );
        }

        if (project.getBudgetLimit() != null &&
                project.getCurrentSpend().compareTo(project.getBudgetLimit()) > 0) {
            throw new ProjValExept(
                    "Current spend exceeds budget",
                    ProjValExept.ProjErrorCode.SPEND_EXCEEDS_BUDGET
            );
        }
    }
}
