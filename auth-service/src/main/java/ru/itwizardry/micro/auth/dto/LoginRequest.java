package ru.itwizardry.micro.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "password")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 2, max = 45, message = "Имя пользователя должно быть не менее 2 символов и не более 45 символов")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, message = "Пароль должен состоять хотя бы из 8 символов")
    private String password;
}