package ru.itwizardry.micro.order.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.services.OrderService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.TestConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OrderService orderService() {
            return Mockito.mock(OrderService.class);
        }
    }

    @Test
    void createOrder_ShouldReturnCreated() throws Exception {
        Order order = Order.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .userId(123L)
                .build();

        Mockito.when(orderService.createOrder(any(OrderRequest.class))).thenReturn(order);

        OrderRequest orderRequest = new OrderRequest(1L, 2);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createOrder_ShouldReturn400_WhenInvalidRequest() throws Exception {
        OrderRequest invalidRequest = new OrderRequest(null, 0);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrders_ShouldReturnListOfOrders() throws Exception {
        Order order = Order.builder().id(1L).build();

        Mockito.when(orderService.getOrdersForCurrentUser()).thenReturn(List.of(order));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOrders_ShouldReturnEmptyList_WhenNoOrders() throws Exception {
        Mockito.when(orderService.getOrdersForCurrentUser()).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        Order order = Order.builder().id(1L).build();

        Mockito.when(orderService.getOrderByIdForCurrentUser(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOrderById_ShouldReturn404_WhenOrderNotFound() throws Exception {
        Mockito.when(orderService.getOrderByIdForCurrentUser(999L))
                .thenThrow(new ResponseStatusException(org.springframework
                        .http.HttpStatus.NOT_FOUND, "Order not found"));

        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }
}