package org.example.model.models;

import org.example.SessionManager;
import org.example.logging.AppLog;
import org.example.model.database.entity.Company;
import org.example.model.database.entity.User;
import org.example.model.database.service.CompanyService;
import org.example.model.database.service.UserService;

import java.sql.SQLException;

public class CreateCompanyModel {
    private final Company company = new Company();
    private final CompanyService companyService = new CompanyService();
    private final UserService userService = new UserService();

    public CreateCompanyModel(String companyName, String country, String currency) {
        this.company.setCountry(country);
        this.company.setCurrency(currency);
        this.company.setName(companyName);
    }

    public void createCompany() throws SQLException {
        var logger = AppLog.getLogger(CreateCompanyModel.class);
        Company savedCompany = companyService.addCompany(company);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        currentUser.setCompanyId(savedCompany.getId());
        userService.updateUser(currentUser);

        logger.info("Company created: id={} name={}", savedCompany.getId(), savedCompany.getName());
        logger.info("Company switched / selected in session: id={} name={}", savedCompany.getId(), savedCompany.getName());
        AppLog.pushSession(SessionManager.getInstance().getStatus());
    }
}