-- Удаляем старый enum тип, если существует
DROP TYPE IF EXISTS user_role;

-- Создаём таблицы с текстовым хранением ролей
CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT NOT NULL UNIQUE CHECK (length(username) <= 255),
    password TEXT NOT NULL CHECK (length(password) <= 255)
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL,
    role    TEXT   NOT NULL CHECK (role IN ('ROLE_ADMIN', 'ROLE_USER')),
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);