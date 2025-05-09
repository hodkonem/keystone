package ru.itwizardry.micro.order.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest, @RequestHeader("Authirization") String token) {
        Order order = orderService.createOrder(orderRequest, token);
        return ResponseEntity.status(201).body(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestHeader("Authorization") String token) {
        List<Order> orders = orderService.getOrdersForUser(token);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Order order = orderService.getOrderById(id, token);
        return ResponseEntity.ok(order);
    }
}