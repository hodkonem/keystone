package ru.itwizardry.micro.order.services;

import feign.FeignException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.order.client.ProductClient;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.repositories.OrderRepository;
import ru.itwizardry.micro.product.entities.Product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private JwtService jwtService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, productClient, jwtService);
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        Claims mockedClaims = mock(Claims.class);
        Long expectedUserId = 123L;

        when(jwtService.validateAndExtractClaims(anyString())).thenReturn(mockedClaims);
        when(jwtService.extractUserId(mockedClaims)).thenReturn(expectedUserId);

        Product mockedProduct = new Product();
        mockedProduct.setId(1L);
        mockedProduct.setQuantity(10);

        when(productClient.getProductById(1L)).thenReturn(mockedProduct);

        Order saveOrder = new Order();
        saveOrder.setId(1L);

        when(orderRepository.save(any(Order.class))).thenReturn(saveOrder);

        OrderService orderService = new OrderService(orderRepository, productClient, jwtService);

        OrderRequest orderRequest = new OrderRequest(1L, 2);
        Order result = orderService.createOrder(orderRequest);
        assertNotNull(result);

        verify(orderRepository).save(any(Order.class));
        Order savedOrder = orderCaptor.getValue();
        assertNotNull(result);
        assertNotNull(savedOrder.getCreatedAt());
        assertEquals(1L, result.getId());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotEnough() {
        Claims mockedClaims = mock(Claims.class);
        Long expectedUserId = 123L;

        when(jwtService.validateAndExtractClaims(anyString())).thenReturn(mockedClaims);
        when(jwtService.extractUserId(mockedClaims)).thenReturn(expectedUserId);

        Product mockedProduct = new Product();
        mockedProduct.setId(1L);
        mockedProduct.setQuantity(1);

        when(productClient.getProductById(1L)).thenReturn(mockedProduct);

        OrderRequest orderRequest = new OrderRequest(1L, 5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(orderRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {

        OrderRequest orderRequest = new OrderRequest(1L, 5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(orderRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void createOrder_ShouldThrowException_WhenJwtInvalid() {
        when(jwtService.validateAndExtractClaims(anyString())).thenThrow(new JwtException("Invalid token"));

        OrderRequest orderRequest = new OrderRequest(1L, 5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(orderRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductServiceReturnsNotFound() {
        Claims mockedClaims = mock(Claims.class);
        Long expectedUserId = 123L;

        when(jwtService.validateAndExtractClaims(anyString())).thenReturn(mockedClaims);
        when(jwtService.extractUserId(mockedClaims)).thenReturn(expectedUserId);

        when(productClient.getProductById(1L)).thenThrow(new FeignException.NotFound("Not Found", null, null, null));

        OrderRequest orderRequest = new OrderRequest(1L, 2);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(orderRequest));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}