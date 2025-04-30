CREATE TABLE IF NOT EXISTS products
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT           NOT NULL UNIQUE,
    description TEXT,
    price       DECIMAL(19, 2) NOT NULL CHECK (price > 0),
    quantity    INT            NOT NULL CHECK (quantity >= 0),
    created_at  TIMESTAMP
);