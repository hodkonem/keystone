package ru.itwizardry.micro.order.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.order.client.ProductClient;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final JwtService jwtService;

    public Order createOrder(OrderRequest orderRequest) {
        Long userId = getCurrentUserId();

        var product = productClient.getProductById(orderRequest.getProductId());

        if (product == null || product.getQuantity() < orderRequest.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Недостаточно товара на складе или товар не найден.");
        }

        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .build();

        return orderRepository.save(order);
    }

    public Order getOrderByIdForCurrentUser(Long id) {
        Long userId = getCurrentUserId();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден."));

        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к заказу.");
        }

        return order;
    }

    public List<Order> getOrdersForCurrentUser() {
        Long userId = getCurrentUserId();
        return orderRepository.findByUserId(userId);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует аутентификация");
        }
        Claims claims = (Claims) authentication.getCredentials();
        try {
            return Long.valueOf(jwtService.extractUsername(claims));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный формат userId в токене");
        }
    }
}