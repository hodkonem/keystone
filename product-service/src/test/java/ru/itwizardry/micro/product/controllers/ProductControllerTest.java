package ru.itwizardry.micro.product.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.itwizardry.micro.product.entities.Product;
import ru.itwizardry.micro.product.services.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Test
    @WithMockUser
    void getAllProducts_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

    }

    @Test
    @WithMockUser
    void getAllProducts_ShouldReturnProducts() throws Exception {
        Product testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100.5))
                .quantity(10)
                .build();


        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(100.5))
                .andExpect(jsonPath("$[0].quantity").value(10));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @WithMockUser
    void getProductById_ShouldReturnProduct() throws Exception {
        Product testProduct = Product.builder()
                .id(1L)
                .name("Test")
                .build();

        when(productService.getProductById(1L)).thenReturn(testProduct);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    @WithMockUser
    void getProductById_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithValidData_ShouldReturnCreated() throws Exception {
        Product testProduct = Product.builder().name("New").price(BigDecimal.TEN).build();
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\",\"price\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_WithInvalidData_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_WhenValid_ShouldReturnUpdated() throws Exception {
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated product")
                .build();
        when(productService.updateProduct(eq(1l), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated product\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_WhenNotExist_ShouldThrowsReturn404() throws Exception {
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());
    }


    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }
}