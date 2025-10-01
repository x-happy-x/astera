-- V2__admin_user.sql
INSERT INTO users (id, email, full_name, role, password_hash, is_active, created_at)
VALUES ('00000000-0000-0000-0000-000000000001',
        'admin@astera.ru',
        'Administrator',
        'admin',
        '{bcrypt}$2a$10$FdXcIKrOg5LHy60maL/QluNBOz1tEPcBLKnaUjWn8j07A0FQWe6N6', -- admin_1234
        TRUE,
        NOW());