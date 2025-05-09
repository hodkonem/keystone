package ru.itwizardry.micro.order.services;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.order.client.ProductClient;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private JwtService jwtService;

    public Order createOrder(OrderRequest orderRequest, String token) {

        Claims claims = jwtService.validateAndExtractClaims(token);

        String username = jwtService.extractUsername(claims);

        var product = productClient.getProductById(orderRequest.getProductId());

        if (product == null || product.getQuantity() < orderRequest.getQuantity()) {
            throw new RuntimeException("Недостаточно товара на складе или товар не найден.");
        }

        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUserId(Long.valueOf(username));

        return orderRepository.save(order);
    }

    public Order getOrderById(Long id, String token) {
        String username = jwtService.extractUsername(jwtService.validateAndExtractClaims(token));
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Заказ не найден"));
        if (!username.equals(order.getUserId().toString())) {
            throw new RuntimeException("Нельзя получить заказ другого пользователя.");
        }
        return order;
    }

    public List<Order> getOrdersForUser(String token) {
        String username = jwtService.extractUsername(jwtService.validateAndExtractClaims(token));
        return orderRepository.findByUserId(Long.valueOf(username));
    }
}
