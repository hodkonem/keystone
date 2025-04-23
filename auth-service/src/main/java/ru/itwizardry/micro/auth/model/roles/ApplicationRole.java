package ru.itwizardry.micro.auth.model.roles;

import ru.itwizardry.micro.auth.exceptions.InvalidRoleException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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