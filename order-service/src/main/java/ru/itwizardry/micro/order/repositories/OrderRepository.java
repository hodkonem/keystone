package ru.itwizardry.micro.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itwizardry.micro.order.entities.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long aLong);
}

