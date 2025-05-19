package ru.itwizardry.micro.product.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.entities.Product;
import ru.itwizardry.micro.product.exceptions.ResourceNotFoundException;
import ru.itwizardry.micro.product.mappers.ProductMapper;
import ru.itwizardry.micro.product.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductMapper productMapper;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productMapper = new ProductMapper();
        productService = new ProductService(productRepository, productMapper);
    }

    @Test
    void createProduct_ShouldReturnSavedDto() {
        ProductDto dto = new ProductDto("Iphone", "New smartPhone", new BigDecimal("999.99"), 10);
        Product entity = productMapper.toEntity(dto);
        entity.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(entity);

        ProductDto result = productService.createProduct(dto);

        assertThat(result.getName()).isEqualTo("Iphone");
        assertThat(result.getStock()).isEqualTo(10);
    }

    @Test
    void getProductById_WhenFound_ShouldReturnDto() {
        Product product = Product.builder()
                .id(1L)
                .name("TV")
                .description("OLED screen")
                .price(new BigDecimal("1999.00"))
                .stock(3)
                .build();
    }

    @Test
    void getProductById_WhenNotFound_ShouldThrow404() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Продукт не найден");
    }

    @Test
    void getAllProducts_ShouldReturnMappedList() {
        List<Product> products = List.of(
                Product.builder().name("A").description("...").price(BigDecimal.ONE).stock(1).build(),
                Product.builder().name("B").description("...").price(BigDecimal.TEN).stock(2).build()
        );

        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> result = productService.getAllProducts();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("A");
        assertThat(result.get(1).getName()).isEqualTo("B");
    }

}