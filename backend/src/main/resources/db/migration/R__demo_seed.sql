-- R__data_seed.sql

-- Пользователи
INSERT INTO users (id, email, full_name, role, password_hash, is_active)
VALUES ('00000000-0000-0000-0000-000000000000',
        'admin@astera.ru',
        'Administrator',
        'admin',
        '{bcrypt}$2a$10$FdXcIKrOg5LHy60maL/QluNBOz1tEPcBLKnaUjWn8j07A0FQWe6N6', -- admin_1234
        TRUE),
       ('00000000-0000-0000-0001-000000000000',
        'moderator1@astera.ru',
        'Менеджер Теплообмен',
        'manager',
        '{bcrypt}$2a$10$FdXcIKrOg5LHy60maL/QluNBOz1tEPcBLKnaUjWn8j07A0FQWe6N6', -- admin_1234
        TRUE),
       ('00000000-0000-0000-0001-000000000001',
        'moderator2@astera.ru',
        'Менеджер Теплообмен',
        'manager',
        '{bcrypt}$2a$10$FdXcIKrOg5LHy60maL/QluNBOz1tEPcBLKnaUjWn8j07A0FQWe6N6', -- admin_1234
        TRUE),
       ('00000000-0000-0000-0002-000000000000',
        'ivan@mail.ru',
        'Иванов Петя Сидорович',
        'customer',
        '{bcrypt}$2a$10$FdXcIKrOg5LHy60maL/QluNBOz1tEPcBLKnaUjWn8j07A0FQWe6N6', -- admin_1234
        TRUE)
ON CONFLICT (email) DO UPDATE
    SET full_name     = EXCLUDED.full_name,
        role          = EXCLUDED.role,
        password_hash = EXCLUDED.password_hash,
        is_active     = EXCLUDED.is_active;

INSERT INTO users (id, email, full_name, role, is_active)
VALUES ('33333333-3333-3333-3333-333333333333', 'customer@example.com', 'Иван Петров', 'customer', TRUE)
ON CONFLICT (email) DO UPDATE SET full_name = EXCLUDED.full_name,
                                  role      = EXCLUDED.role,
                                  is_active = EXCLUDED.is_active;

INSERT INTO customer_profiles (user_id, phone, organization)
VALUES ('00000000-0000-0000-0002-000000000000', '+7-900-000-00-00', 'ООО "ДГТУ"')
ON CONFLICT (user_id) DO UPDATE
    SET phone        = EXCLUDED.phone,
        organization = EXCLUDED.organization;

-- Котлы
INSERT INTO equipment (id, category, brand, model, active,
                       power_min_kw, power_max_kw, dn_size, connection_key,
                       price, delivery_days)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'boiler', 'ARCUS', 'KB-500', TRUE, 300, 700, 80, 'DN80_GAS_STD', 780000,
        14),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'boiler', 'NEVA', 'NV-800', TRUE, 500, 900, 100, 'DN100_GAS_STD',
        1120000, 21)
ON CONFLICT (brand, model) DO UPDATE
    SET active         = EXCLUDED.active,
        power_min_kw   = EXCLUDED.power_min_kw,
        power_max_kw   = EXCLUDED.power_max_kw,
        dn_size        = EXCLUDED.dn_size,
        connection_key = EXCLUDED.connection_key,
        price          = EXCLUDED.price,
        delivery_days  = EXCLUDED.delivery_days;

-- Горелки (газ)
INSERT INTO equipment (id, category, brand, model, active,
                       power_min_kw, power_max_kw, fuel_type, connection_key,
                       price, delivery_days)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'burner', 'KONORD', 'G-500', TRUE, 300, 700, 'gas', 'DN80_GAS_STD',
        260000, 10),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'burner', 'ROSS', 'RG-900', TRUE, 600, 1000, 'gas', 'DN100_GAS_STD',
        390000, 12)
ON CONFLICT (brand, model) DO UPDATE
    SET active         = EXCLUDED.active,
        power_min_kw   = EXCLUDED.power_min_kw,
        power_max_kw   = EXCLUDED.power_max_kw,
        fuel_type      = EXCLUDED.fuel_type,
        connection_key = EXCLUDED.connection_key,
        price          = EXCLUDED.price,
        delivery_days  = EXCLUDED.delivery_days;

-- Насосы (подбираются по расходу)
INSERT INTO equipment (id, category, brand, model, active,
                       flow_min_m3h, flow_max_m3h, price, delivery_days)
VALUES ('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'pump', 'WILO', 'P-65/160', TRUE, 10.0, 45.0, 120000, 7),
       ('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'pump', 'CNP', 'NIS50-200', TRUE, 12.0, 60.0, 88000, 14)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        flow_min_m3h  = EXCLUDED.flow_min_m3h,
        flow_max_m3h  = EXCLUDED.flow_max_m3h,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- Арматура (по DN)
INSERT INTO equipment (id, category, brand, model, active,
                       dn_size, price, delivery_days)
VALUES ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'valve', 'LD', 'LD-DN80', TRUE, 80, 28000, 5),
       ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'valve', 'FAF', 'FAF-DN100', TRUE, 100, 36000, 6)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        dn_size       = EXCLUDED.dn_size,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- Расходомеры (по DN)
INSERT INTO equipment (id, category, brand, model, active,
                       dn_size, price, delivery_days)
VALUES ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee1', 'flowmeter', 'Piterflow', 'PF-DN80', TRUE, 80, 54000, 10),
       ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeee2', 'flowmeter', 'Vzlet', 'VZ-DN100', TRUE, 100, 62000, 12)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        dn_size       = EXCLUDED.dn_size,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- Автоматика
INSERT INTO equipment (id, category, brand, model, active, price, delivery_days)
VALUES ('ffffffff-ffff-ffff-ffff-fffffffffff1', 'automation', 'Siemens', 'AUT-1', TRUE, 85000, 7)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;
