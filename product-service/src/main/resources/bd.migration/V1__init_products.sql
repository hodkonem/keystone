CREATE TABLE IF NOT EXISTS products
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    price FLOAT NOT NULL,
    quantity INT NOT NULL
);