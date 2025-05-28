package ru.itwizardry.micro.order.services;

import ru.itwizardry.micro.order.dto.OrderDto;
import ru.itwizardry.micro.order.dto.OrderRequest;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderRequest orderRequest);

    List<OrderDto> getOrdersForCurrentUser();

    OrderDto getOrderById(Long id);
}