-- V3__more_equipment.sql
-- More catalogue options for dynamic selection.
-- Idempotent: conflict key = (brand, model).
-- Требуется, чтобы V1/V2 миграции уже применены (есть таблица equipment и тип equipment_category).

-- === Boilers ================================================================
INSERT INTO equipment (id, category, brand, model, active,
                       power_min_kw, power_max_kw, dn_size, connection_key,
                       price, delivery_days)
VALUES ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a1', 'boiler', 'ARCUS', 'KB-650', TRUE, 500, 800, 80, 'DN80_GAS_STD', 920000,
        16),
       ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a2', 'boiler', 'ROSSEN', 'RS-650', TRUE, 450, 750, 80, 'DN80_GAS_STD',
        860000, 18),
       ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a3', 'boiler', 'NEVA', 'NV-600', TRUE, 400, 700, 100, 'DN100_GAS_STD',
        980000, 20),
       ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a4', 'boiler', 'THERMO', 'TH-1000', TRUE, 800, 1200, 125, 'DN125_GAS_STD',
        1450000, 28),
       ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a5', 'boiler', 'ARCUS', 'KB-350', TRUE, 250, 450, 65, 'DN65_GAS_STD', 660000,
        14),
       ('a3a3a3a3-a3a3-4a3a-a3a3-a3a3a3a3a3a6', 'boiler', 'NEVA', 'NV-700D', TRUE, 500, 800, 80, 'DN80_DIESEL_STD',
        1020000, 22)
ON CONFLICT (brand, model) DO UPDATE
    SET active         = EXCLUDED.active,
        power_min_kw   = EXCLUDED.power_min_kw,
        power_max_kw   = EXCLUDED.power_max_kw,
        dn_size        = EXCLUDED.dn_size,
        connection_key = EXCLUDED.connection_key,
        price          = EXCLUDED.price,
        delivery_days  = EXCLUDED.delivery_days;

-- === Burners ================================================================
INSERT INTO equipment (id, category, brand, model, active,
                       power_min_kw, power_max_kw, fuel_type, connection_key,
                       price, delivery_days)
VALUES ('b4b4b4b4-b4b4-4b4b-b4b4-b4b4b4b4b4b1', 'burner', 'BALTUR', 'BTG-650', TRUE, 400, 700, 'gas', 'DN80_GAS_STD',
        330000, 10),
       ('b4b4b4b4-b4b4-4b4b-b4b4-b4b4b4b4b4b2', 'burner', 'ECOFLAM', 'GPN-400', TRUE, 250, 450, 'gas', 'DN65_GAS_STD',
        240000, 9),
       ('b4b4b4b4-b4b4-4b4b-b4b4-b4b4b4b4b4b3', 'burner', 'KONORD', 'G-800', TRUE, 650, 900, 'gas', 'DN100_GAS_STD',
        380000, 12),
       ('b4b4b4b4-b4b4-4b4b-b4b4-b4b4b4b4b4b4', 'burner', 'ROSS', 'RG-1200', TRUE, 900, 1300, 'gas', 'DN125_GAS_STD',
        520000, 14),
       ('b4b4b4b4-b4b4-4b4b-b4b4-b4b4b4b4b4b5', 'burner', 'DIESELCO', 'D-700', TRUE, 500, 800, 'diesel',
        'DN80_DIESEL_STD', 410000, 15)
ON CONFLICT (brand, model) DO UPDATE
    SET active         = EXCLUDED.active,
        power_min_kw   = EXCLUDED.power_min_kw,
        power_max_kw   = EXCLUDED.power_max_kw,
        fuel_type      = EXCLUDED.fuel_type,
        connection_key = EXCLUDED.connection_key,
        price          = EXCLUDED.price,
        delivery_days  = EXCLUDED.delivery_days;

-- === Pumps ==================================================================
INSERT INTO equipment (id, category, brand, model, active,
                       flow_min_m3h, flow_max_m3h, price, delivery_days)
VALUES ('c5c5c5c5-c5c5-4c5c-c5c5-c5c5c5c5c5c1', 'pump', 'WILO', 'P-80/200', TRUE, 20.0, 80.0, 195000, 10),
       ('c5c5c5c5-c5c5-4c5c-c5c5-c5c5c5c5c5c2', 'pump', 'GRUNDFOS', 'TP-65/180', TRUE, 12.0, 55.0, 165000, 9),
       ('c5c5c5c5-c5c5-4c5c-c5c5-c5c5c5c5c5c3', 'pump', 'CNP', 'NIS80-250', TRUE, 25.0, 100.0, 142000, 16)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        flow_min_m3h  = EXCLUDED.flow_min_m3h,
        flow_max_m3h  = EXCLUDED.flow_max_m3h,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- === Valves =================================================================
INSERT INTO equipment (id, category, brand, model, active,
                       dn_size, price, delivery_days)
VALUES ('d6d6d6d6-d6d6-4d6d-d6d6-d6d6d6d6d6d1', 'valve', 'LD', 'LD-DN65', TRUE, 65, 24000, 5),
       ('d6d6d6d6-d6d6-4d6d-d6d6-d6d6d6d6d6d2', 'valve', 'FAF', 'FAF-DN80', TRUE, 80, 31000, 6),
       ('d6d6d6d6-d6d6-4d6d-d6d6-d6d6d6d6d6d3', 'valve', 'BROEN', 'BROEN-DN125', TRUE, 125, 52000, 8)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        dn_size       = EXCLUDED.dn_size,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- === Flowmeters =============================================================
INSERT INTO equipment (id, category, brand, model, active,
                       dn_size, price, delivery_days)
VALUES ('e7e7e7e7-e7e7-4e7e-e7e7-e7e7e7e7e7e1', 'flowmeter', 'Piterflow', 'PF-DN65', TRUE, 65, 47000, 10),
       ('e7e7e7e7-e7e7-4e7e-e7e7-e7e7e7e7e7e2', 'flowmeter', 'Vzlet', 'VZ-DN80', TRUE, 80, 56000, 11),
       ('e7e7e7e7-e7e7-4e7e-e7e7-e7e7e7e7e7e3', 'flowmeter', 'ELSTER', 'EL-DN125', TRUE, 125, 87000, 14)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        dn_size       = EXCLUDED.dn_size,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;

-- === Automation =============================================================
INSERT INTO equipment (id, category, brand, model, active, price, delivery_days)
VALUES ('f8f8f8f8-f8f8-4f8f-f8f8-f8f8f8f8f8f1', 'automation', 'Siemens', 'AUT-2', TRUE, 92000, 7),
       ('f8f8f8f8-f8f8-4f8f-f8f8-f8f8f8f8f8f2', 'automation', 'Oven', 'TRM-210', TRUE, 45000, 5)
ON CONFLICT (brand, model) DO UPDATE
    SET active        = EXCLUDED.active,
        price         = EXCLUDED.price,
        delivery_days = EXCLUDED.delivery_days;
