package ru.itwizardry.micro.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itwizardry.micro.auth.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    String username(String username);
}