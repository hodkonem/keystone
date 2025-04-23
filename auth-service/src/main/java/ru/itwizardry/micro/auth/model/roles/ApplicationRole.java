package ru.itwizardry.micro.auth.model.roles;

public enum ApplicationRole {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String authority;

    ApplicationRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public static ApplicationRole fromSting(String role) {
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        for (ApplicationRole r : values()) {
            if (r.authority.equals(normalizedRole)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}