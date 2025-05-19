package ru.itwizardry.micro.order.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.common.jwt.JwtService;
import ru.itwizardry.micro.common.testutils.FeignTestUtils;
import ru.itwizardry.micro.common.testutils.JwtTestUtils;
import ru.itwizardry.micro.common.testutils.SecurityContextTestUtils;
import ru.itwizardry.micro.order.client.ProductClient;
import ru.itwizardry.micro.order.dto.OrderRequest;
import ru.itwizardry.micro.order.entities.Order;
import ru.itwizardry.micro.order.repositories.OrderRepository;
import ru.itwizardry.micro.product.entities.Product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContextTestUtils.setAuthenticationWithClaims("123L", JwtTestUtils.mockClaimsWithUserId(123L));
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        Product mockedProduct = Product.builder().id(1L).quantity(10).build();
        when(productClient.getProductById(1L)).thenReturn(mockedProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(Order.builder().id(1L).build());

        OrderRequest orderRequest = new OrderRequest(1L, 2);
        Order result = orderService.createOrder(orderRequest);

        assertThat(result).isNotNull();
        verify(orderRepository).save(orderCaptor.capture());

        Order captured = orderCaptor.getValue();
        assertThat(captured.getProductId()).isEqualTo(1L);
        assertThat(captured.getQuantity()).isEqualTo(2);
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotEnough() {
        when(productClient.getProductById(1L)).thenReturn(Product.builder().id(1L).quantity(1).build());

        OrderRequest orderRequest = new OrderRequest(1L, 5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(orderRequest));

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductServiceReturnsNotFound() {
        when(productClient.getProductById(1L))
                .thenThrow(FeignTestUtils.createNotFoundException("Product Not Found"));

        OrderRequest orderRequest = new OrderRequest(1L, 5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(orderRequest));

        assertThat(exception.getStatusCode().value()).isEqualTo(400);
    }
}