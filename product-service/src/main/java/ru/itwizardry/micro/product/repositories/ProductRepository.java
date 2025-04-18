package ru.itwizardry.micro.product.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itwizardry.micro.product.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
