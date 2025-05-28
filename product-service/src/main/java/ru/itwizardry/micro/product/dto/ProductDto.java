package ru.itwizardry.micro.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private Long id;

    @NotNull(message = "Название продукта обязательно")
    @Size(max = 100, message = "Имя должно быть до 100 символов")
    private String name;

    @Size(max = 500, message = "Описание должно быть до 500 символов")
    private String description;

    @Positive(message = "Цена товара должна быть более 0")
    private BigDecimal price;

    @Min(value = 0, message = "Количество товаров в наличии должно быть от 0 и более")
    private int stock;
}
