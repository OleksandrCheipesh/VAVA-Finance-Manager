package org.example.model.models;

import org.example.SessionManager;
import org.example.model.database.entity.Account;
import org.example.model.database.entity.Company;
import org.example.model.database.entity.User;
import org.example.model.database.service.AccountService;
import org.example.model.database.service.CompanyService;
import org.example.model.database.service.UserService;

import java.math.BigDecimal;
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
        Company savedCompany = companyService.addCompany(company);
        User currentUser = SessionManager.getInstance().getCurrentUser();
        currentUser.setCompanyId(savedCompany.getId());
        userService.updateUser(currentUser);

        AccountService accountService = new AccountService();
        Account mainAccount = new Account(savedCompany.getId(), "Main Account", BigDecimal.ZERO, company.getCurrency());
        Account savedAccount = accountService.addAccount(mainAccount);
        SessionManager.getInstance().setCurrentAccountId(savedAccount.getId());
    }
}