CREATE TABLE IF NOT EXISTS users
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    username
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL
    );


CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Тестовый администратор
INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$xVCHqIAYwGZ8VinfGGrZHOBG1lJ5jO7Q2u9PYYh5X.FjezV6d4Q7K');

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER');