INSERT INTO products (name, description, price, quantity, created_at)
VALUES
    ('Тестовый продукт 1', 'Описание 1', 100.50, 10, NOW()),
    ('Тестовый продукт 2', 'Описание 2', 200.00, 5, NOW())
ON CONFLICT (name) DO NOTHING;