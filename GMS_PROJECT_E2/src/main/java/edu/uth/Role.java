package edu.uth;

public enum Role {
    ADMIN("Admin"),
    TRAINER("Trainer"),
    MEMBER("Member");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
