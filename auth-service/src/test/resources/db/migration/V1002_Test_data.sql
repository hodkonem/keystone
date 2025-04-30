-- Очистка перед вставкой (важно для тестов)
DELETE FROM user_roles;
DELETE FROM users;

-- Вставка тестовых данных
INSERT INTO users (id, username, password)
VALUES (1, 'admin', '$2a$10$xVCHqIAYwGZ8VinfGGrZHOBG1lJ5jO7Q2u9PYYh5X.FjezV6d4Q7K')
ON CONFLICT (id) DO UPDATE
    SET username = EXCLUDED.username,
        password = EXCLUDED.password;

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER')
ON CONFLICT (user_id, role) DO NOTHING;