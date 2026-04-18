package org.example.viewModel;

public class UserUI {
    private String name;
    private String surname;
    private String email;
    private String role;
    private String passwordHash;

    public UserUI(String name, String surname, String email, String role, String passwordHash) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
}