package ru.itwizardry.micro.order.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String username;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String status;
    private LocalDateTime createAt;
}
