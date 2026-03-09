package org.example.model.fileExport;

import org.example.model.database.entity.Employee;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tester {
    static void main() {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(
                1,
                "John",
                "Smith",
                "john.smith@company.com",
                30,
                new BigDecimal("3500.50"),
                "Developer",
                OffsetDateTime.now()
        ));

        list.add(new Employee(
                1,
                "Anna",
                "Brown",
                "anna.brown@company.com",
                27,
                new BigDecimal("3200.00"),
                "QA Engineer",
                OffsetDateTime.now()
        ));

        list.add(new Employee(
                2,
                "Michael",
                "Johnson",
                "michael.johnson@company.com",
                40,
                new BigDecimal("5000.00"),
                "Team Lead",
                OffsetDateTime.now()
        ));

        list.add(new Employee(
                2,
                "Emily",
                "Davis",
                "emily.davis@company.com",
                25,
                new BigDecimal("2900.75"),
                "UI Designer",
                OffsetDateTime.now()
        ));

        list.add(new Employee(
                3,
                "Robert",
                "Wilson",
                "robert.wilson@company.com",
                35,
                new BigDecimal("4200.00"),
                "Project Manager",
                OffsetDateTime.now()
        ));
        EmplExelReader.exportToExcel(list, "D:\\FIIT\\Vava\\tast.xlsx");
        list = EmplExelReader.importFromExcel("D:\\FIIT\\Vava\\tast.xlsx", 1);
        EmplExelReader.exportToExcel(list, "D:\\FIIT\\Vava\\tast.xlsx");
    }
}
