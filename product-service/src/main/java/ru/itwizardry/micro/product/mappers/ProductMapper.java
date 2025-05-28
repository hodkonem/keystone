package ru.itwizardry.micro.product.mappers;

import org.springframework.stereotype.Component;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.entities.Product;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) return null;
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();


    }

    public Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

    public Product updateEntity(Product product, ProductDto dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return product;
    }
}
