package ru.itwizardry.micro.order.mappers;

import org.springframework.stereotype.Component;
import ru.itwizardry.micro.order.dto.OrderDto;
import ru.itwizardry.micro.order.entities.Order;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order, String username, String productName) {
        if (order == null) return null;
        return OrderDto.builder()
                .id(order.getId())
                .username(username)
                .productId(order.getProductId())
                .productName(productName)
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createAt(order.getCreatedAt())
                .build();
    }

    public OrderDto toDto(Order order, String username) {
        return toDto(order, username, null);
    }

    public List<OrderDto> toDtoList(List<Order> orders, String username) {
        if (orders == null) return List.of();
        return orders.stream()
                .map(order -> toDto(order, username))
                .collect(Collectors.toList());
    }
}
