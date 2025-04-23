package ru.itwizardry.micro.auth.exceptions;

import io.jsonwebtoken.JwtException;

public class InvalidRoleException extends JwtException {
    public InvalidRoleException(String message) {
        super(message);
    }
}
