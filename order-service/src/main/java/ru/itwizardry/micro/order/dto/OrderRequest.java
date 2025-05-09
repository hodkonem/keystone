package ru.itwizardry.micro.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "id товара обязательно")
    @Positive(message = "Id товара должен быть положительным числом")
    private Long productId;

    @NotNull(message = "Нужно выбрать количество товара")
    @Min(value = 1, message = "Количество товара должно быть хотя бы 1")
    private Integer quantity;
}
