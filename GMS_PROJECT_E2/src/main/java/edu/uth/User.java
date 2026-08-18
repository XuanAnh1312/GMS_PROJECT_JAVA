package edu.uth;

public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String email;
    private Role role;

    public User(String userId, String username, String password, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public boolean login() {
        return this.username != null && !this.username.isEmpty();
    }

    public boolean login(String username, String password) {
        return this.username.equalsIgnoreCase(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println("User " + username + " logged out.");
    }

    public Role getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "userId: " + userId + "\n" +
               "username: " + username + "\n" +
               "email: " + email + "\n" +
               "role: " + (role != null ? role.getRoleName() : "N/A");
    }
}
