package ru.itwizardry.micro.product.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.itwizardry.micro.product.config.JwtServiceConfig;
import ru.itwizardry.micro.product.config.ProductSecurityConfig;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.exceptions.ResourceNotFoundException;
import ru.itwizardry.micro.product.services.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({ProductSecurityConfig.class, JwtServiceConfig.class})
@TestPropertySource(properties = {
        "jwt.secret=12345678901234567890123456789012",
        "jwt.expiration=PT24H"
})
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
        ProductDto input = new ProductDto("New", "Created product", new BigDecimal("99.99"), 10);

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
        ProductDto invalid = new ProductDto("", "", new BigDecimal("-1.0"), -1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_withRoleUser_shouldReturn403() throws Exception {
        ProductDto dto = new ProductDto("Demo", "Demo", new BigDecimal("10.00"), 1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_withoutAuth_shouldReturn401() throws Exception {
        SecurityContextHolder.clearContext();

        ProductDto dto = new ProductDto("Demo", "Demo", new BigDecimal("10.00"), 1);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
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
        ProductDto dto = new ProductDto("Update", "Missing", new BigDecimal("10.00"), 1);
        when(productService.updateProduct(eq(99L), any(ProductDto.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_withInvalidDto_shouldReturn400() throws Exception {
        ProductDto dto = new ProductDto("", "", new BigDecimal("-5.00"), -2);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateProduct_withUser_shouldReturn403() throws Exception {
        ProductDto dto = new ProductDto("New", "desc", new BigDecimal("1.0"), 1);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteProduct_withUser_shouldReturn403() throws Exception {
        Long productId = 1L;

        given(productService.deleteProduct(productId)).willReturn(true);

        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isForbidden());
    }

}