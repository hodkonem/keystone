package ru.itwizardry.micro.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itwizardry.micro.product.dto.ProductDto;
import ru.itwizardry.micro.product.entities.Product;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/product/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}
