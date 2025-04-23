package ru.itwizardry.micro.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.StringJoiner;

public class LoginRequest {
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 2, max = 45, message = "Имя пользователя должно быть не менее 2 символов и не более 45 символов")
    private String username;

    @NotEmpty
    @Size(min = 8, message = "Пароль должен состоять хотя бы из 8 символов")
    private String password;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LoginRequest.class.getSimpleName() + "{", "}")
                .add("username='" + username + "'")
                .toString();
    }
}