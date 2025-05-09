package ru.itwizardry.micro.order.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Product id cannot be null")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Status cannot be null")
    @Size(min = 1, max = 20, message = "Size should be between 1 and 20 characters")
    private String status;

    @NotNull(message = "Creation date cannot be null")
    private LocalDateTime createdAt;

    @NotNull(message = "User id is cannot be null")
    private Long userId;
}
