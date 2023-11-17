package kz.spring.demo.model;

public enum Permission {

    PERMISSION_READ ("developers:read"),
    PERMISSION_WRITE("developers:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return this.permission;
    }
}
