package ru.itwizardry.micro.common.jwt;

import io.jsonwebtoken.JwtException;

public class InvalidRoleException extends JwtException {
    public InvalidRoleException(String message) {
        super(message);
    }
}