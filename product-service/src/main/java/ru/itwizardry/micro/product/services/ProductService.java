package ru.itwizardry.micro.product.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.entities.Product;
import ru.itwizardry.micro.product.exceptions.ResourceNotFoundException;
import ru.itwizardry.micro.product.mappers.ProductMapper;
import ru.itwizardry.micro.product.repositories.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден с id: " + id));
        return productMapper.toDto(product);
    }

    public ProductDto createProduct(ProductDto dto) {
        Product product = productMapper.toEntity(dto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден с id: " + id));
        productMapper.updateEntity(product, dto);
        Product updated = productRepository.save(product);
        return productMapper.toDto(updated);
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}