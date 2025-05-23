package ru.itwizardry.micro.common.jwt;

public enum ApplicationRole {
    ROLE_ADMIN,
    ROLE_USER;

    public static ApplicationRole fromClaim(String roleClaim) {
        try {
            return valueOf(roleClaim.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + roleClaim);
        }
    }

    public String getAuthority() {
        return this.name();
    }
}
