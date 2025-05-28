package ru.itwizardry.micro.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.itwizardry.micro.product.config.JwtTestSecurityConfig;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.exceptions.ResourceNotFoundException;
import ru.itwizardry.micro.product.services.ProductService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(JwtTestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturnCreated() throws Exception {
        ProductDto input = ProductDto.builder()
                .name("New")
                .description("desc")
                .price(new BigDecimal("99.99"))
                .stock(10)
                .build();

        when(productService.createProduct(any(ProductDto.class))).thenReturn(input);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_withInvalidDto_shouldReturn400() throws Exception {
        ProductDto invalid = ProductDto.builder()
                .name("name")
                .description("desc")
                .price(new BigDecimal("-1.0"))
                .stock(-1)
                .build();

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_withRoleUser_shouldReturn403() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("name")
                .description("desc")
                .price(new BigDecimal("10.00"))
                .stock(1)
                .build();

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden());
    }

    @Test
    void createProduct_withoutAuth_shouldReturn401() throws Exception {
        SecurityContextHolder.clearContext();

        ProductDto dto = ProductDto.builder()
                .name("Demo")
                .description("Demo")
                .price(new BigDecimal("10.00"))
                .stock(1)
                .build();

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isUnauthorized());
    }

    @Test
    void getProductById_whenNotFound_shouldReturn404() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_whenNotFound_shouldReturn404() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("Update")
                .description("Missing")
                .price(new BigDecimal("10.00"))
                .stock(1)
                .build();

        when(productService.updateProduct(eq(99L), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound()).andExpect(content().string("Product not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_withInvalidDto_shouldReturn400() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("")
                .description("")
                .price(new BigDecimal("-5.00"))
                .stock(-2)
                .build();

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateProduct_withUser_shouldReturn403() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("New")
                .description("desc")
                .price(new BigDecimal("1.00"))
                .stock(1)
                .build();

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteProduct_withUser_shouldReturn403() throws Exception {
        Long productId = 1L;

        given(productService.deleteProduct(productId)).willReturn(true);

        mockMvc.perform(delete("/products/{id}", productId)).andExpect(status().isForbidden());
    }
}