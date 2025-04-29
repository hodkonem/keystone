package ru.itwizardry.micro.product.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.product.entities.Product;
import ru.itwizardry.micro.product.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Macbook")
                .description("Test Description")
                .price(BigDecimal.valueOf(1028.50))
                .quantity(10)
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(testProduct));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals(testProduct, products.get(0));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductById(1L);

        assertEquals(testProduct, foundProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_WhenProductNotExist_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void createProduct_SaveAndReturnProduct() {
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        Product createProduct = productService.createProduct(testProduct);

        assertEquals(testProduct, createProduct);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_WhenProductExist_UpdateAndReturnProduct() {
        Product updatedDetails = Product.builder()
                .name("Updated name")
                .description("Updated Description")
                .price(BigDecimal.valueOf(100.60))
                .quantity(10)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        Product updatedProduct = productService.updateProduct(1L, updatedDetails);

        assertEquals("Updated name", updatedProduct.getName());
        assertEquals("Updated Description", updatedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(100.60), updatedProduct.getPrice());
        assertEquals(10, updatedProduct.getQuantity());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }
}