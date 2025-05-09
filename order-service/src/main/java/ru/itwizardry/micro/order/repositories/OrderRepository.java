package ru.itwizardry.micro.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itwizardry.micro.order.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
