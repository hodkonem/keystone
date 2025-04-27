INSERT INTO users (username, password, email, phone, address)
VALUES
    ('Donald Smith', 'magnit12345', 'donald.smith@example.com', '+1234567890',
     '1600 Pennsylvania Avenue NW, Washington, DC 20500, USA'),

    ('Emma Johnson', 'securepass789', 'emma.johnson@example.com', '+1987654321',
     '221B Baker Street, London, UK'),

    ('Alex Wong', 'dragon2024', 'alex.wong@example.com', '+8613812345678',
     '1 Chongwenmenwai St, Dongcheng, Beijing, China');

INSERT INTO user_roles (user_id, role)
VALUES
    ((SELECT id FROM users WHERE username = 'Donald Smith'), 'ROLE_ADMIN'),
    ((SELECT id FROM users WHERE username = 'Emma Johnson'), 'ROLE_USER'),
    ((SELECT id FROM users WHERE username = 'Alex Wong'), 'ROLE_USER');