package ru.itwizardry.micro.order.services;

import feign.FeignException;
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
import ru.itwizardry.micro.order.dto.OrderDto;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.mappers.OrderMapper;
import ru.itwizardry.micro.order.repositories.OrderRepository;
import ru.itwizardry.micro.product.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final JwtService jwtService;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto createOrder(OrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        ProductDto product;
        try {
            product = productClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найдёт");
        }

        Order order = Order.builder()
                .productId(product.getId())
                .quantity(request.getQuantity())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .userId(null)
                .build();

        order = orderRepository.save(order);
        return orderMapper.toDto(order, username, product.getName());
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = getCurrentUserId();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден."));

        if (!order.getUserId().equals(userId) && !isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет доступа к заказу.");
        }

        String productName = null;

        try {
            ProductDto product = productClient.getProductById(order.getProductId());
            productName = product.getName();
        } catch (FeignException.NotFound ignored) {
            productName = null;
        }
        return orderMapper.toDto(order, username, productName);
    }


    public List<OrderDto> getOrdersForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = getCurrentUserId();

        List<Order> orders;

        if (isAdmin(authentication)) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        return orderMapper.toDtoList(orders, username);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует аутентификация");
        }

        Claims claims = (Claims) authentication.getCredentials();

        try {
            return jwtService.extractUserId(claims);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный формат userId в токене");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}