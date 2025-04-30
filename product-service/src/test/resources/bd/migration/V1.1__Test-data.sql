CREATE TABLE IF NOT EXISTS products
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Аналог BIGSERIAL в H2
    name        VARCHAR(255)   NOT NULL,                         -- TEXT -> VARCHAR для совместимости с @Column
    description TEXT,
    price       DECIMAL(19, 2) NOT NULL,
    quantity    INTEGER        NOT NULL,
    created_at  TIMESTAMP,
    CONSTRAINT uk_product_name UNIQUE (name)
);

-- Для тестов можно добавить комментарии (H2 поддерживает):
COMMENT ON TABLE products IS 'Тестовая таблица продуктов';
COMMENT ON COLUMN products.price IS 'Цена с точностью до 2 знаков после запятой';