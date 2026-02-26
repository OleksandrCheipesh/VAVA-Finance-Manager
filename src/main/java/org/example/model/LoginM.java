package org.example.model;

public class LoginM {
    private String login;
    private String email;
    private String password;
    // private DataBase users;

    //verifies if password is in the database
    private boolean verifierOfPassword(String password) {
        /*
            if (!this.user.contains(password) {
                return false;
            }
         */
        return true;
    }

    //verifies if user is in database
    private boolean verifierOfUser(String user) {
      /*
        if (!this.user.contains(user) {
            return false;
        }
        */
        return true;
    }



    public void validateUser(String user, String password) throws Exception {
        if (!verifierOfPassword(password) || !verifierOfUser(user)) {
            throw new Exception("The user or password is incorrect!");
        }
    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
