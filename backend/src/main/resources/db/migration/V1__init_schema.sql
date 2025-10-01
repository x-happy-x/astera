-- V1__init_schema.sql

-- Enum equipment_category (создание идемпотентно через DO-блок)
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'equipment_category') THEN
            CREATE TYPE equipment_category AS ENUM ('boiler','burner','pump','valve','flowmeter','automation');
            COMMENT ON TYPE equipment_category IS 'Категория оборудования: boiler, burner, pump, valve, flowmeter, automation';
        END IF;
    END
$$;

-- users: менеджеры и админы (для админки)
CREATE TABLE IF NOT EXISTS users
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    full_name     TEXT                NOT NULL,
    role          TEXT                NOT NULL CHECK (role IN ('manager', 'admin')),
    password_hash TEXT,
    is_active     BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ         NOT NULL DEFAULT now()
);
COMMENT ON TABLE users IS 'Пользователи системы (менеджеры/админы).';
COMMENT ON COLUMN users.id IS 'Первичный ключ пользователя (UUID).';
COMMENT ON COLUMN users.email IS 'Email (уникальный, регистронезависимый).';
COMMENT ON COLUMN users.full_name IS 'ФИО пользователя.';
COMMENT ON COLUMN users.role IS 'Роль: manager или admin.';
COMMENT ON COLUMN users.password_hash IS 'Хэш пароля (если используется локальная аутентификация).';
COMMENT ON COLUMN users.is_active IS 'Активен ли пользователь.';
COMMENT ON COLUMN users.created_at IS 'Метка времени создания записи.';

-- leads: клиенты/лиды (Форма №1)
CREATE TABLE IF NOT EXISTS leads
(
    id                    UUID PRIMARY KEY,
    full_name             TEXT         NOT NULL,
    phone                 TEXT         NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    organization          TEXT         NOT NULL,
    consent_personal_data BOOLEAN      NOT NULL,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    assigned_manager_id   UUID         NULL REFERENCES users (id),
    CONSTRAINT uq_lead_contact UNIQUE (email, phone)
);
COMMENT ON TABLE leads IS 'Лиды/клиенты, введённые через Форму №1.';
COMMENT ON COLUMN leads.id IS 'Первичный ключ лида (UUID).';
COMMENT ON COLUMN leads.full_name IS 'Имя/ФИО заказчика.';
COMMENT ON COLUMN leads.phone IS 'Телефон заказчика.';
COMMENT ON COLUMN leads.email IS 'Email заказчика.';
COMMENT ON COLUMN leads.organization IS 'Название организации заказчика.';
COMMENT ON COLUMN leads.consent_personal_data IS 'Флаг согласия на обработку ПДн.';
COMMENT ON COLUMN leads.created_at IS 'Метка времени создания лида.';
COMMENT ON COLUMN leads.assigned_manager_id IS 'Назначенный менеджер (users.id).';
COMMENT ON CONSTRAINT uq_lead_contact ON leads IS 'Исключает дубликаты по связке email+phone.';

-- heating_requests: параметры подбора (Форма №2)
CREATE TABLE IF NOT EXISTS heating_requests
(
    id         UUID PRIMARY KEY,
    lead_id    UUID           NOT NULL REFERENCES leads (id),
    power_kw   NUMERIC(10, 2) NOT NULL CHECK (power_kw > 0),
    t_in       NUMERIC(5, 2)  NOT NULL,
    t_out      NUMERIC(5, 2)  NOT NULL,
    fuel_type  TEXT           NOT NULL DEFAULT 'gas' CHECK (fuel_type IN ('gas', 'diesel', 'other')),
    notes      TEXT,
    status     TEXT           NOT NULL DEFAULT 'created' CHECK (status IN ('created', 'proposed', 'selected')),
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_temp_delta CHECK (t_in > t_out)
);
COMMENT ON TABLE heating_requests IS 'Запросы на подбор оборудования (Форма №2).';
COMMENT ON COLUMN heating_requests.id IS 'Первичный ключ запроса (UUID).';
COMMENT ON COLUMN heating_requests.lead_id IS 'Связь с лидом (leads.id).';
COMMENT ON COLUMN heating_requests.power_kw IS 'Расчётная тепловая мощность, кВт (>0).';
COMMENT ON COLUMN heating_requests.t_in IS 'Температура подачи, °C.';
COMMENT ON COLUMN heating_requests.t_out IS 'Температура обратки, °C.';
COMMENT ON COLUMN heating_requests.fuel_type IS 'Тип топлива для горелки: gas/diesel/other.';
COMMENT ON COLUMN heating_requests.notes IS 'Дополнительные пожелания/комментарии.';
COMMENT ON COLUMN heating_requests.status IS 'Статус запроса: created/proposed/selected.';
COMMENT ON COLUMN heating_requests.created_at IS 'Метка времени создания запроса.';
COMMENT ON CONSTRAINT chk_temp_delta ON heating_requests IS 'Проверка: температура подачи выше температуры обратки.';

-- equipment: каталог (единая таблица)
CREATE TABLE IF NOT EXISTS equipment
(
    id             UUID PRIMARY KEY,
    category       equipment_category NOT NULL,
    brand          TEXT               NOT NULL,
    model          TEXT               NOT NULL,
    active         BOOLEAN            NOT NULL DEFAULT TRUE,

    power_min_kw   NUMERIC(10, 2),
    power_max_kw   NUMERIC(10, 2),
    flow_min_m3h   NUMERIC(10, 3),
    flow_max_m3h   NUMERIC(10, 3),
    dn_size        INT,
    fuel_type      TEXT,
    connection_key TEXT,

    price          NUMERIC(14, 2)     NOT NULL,
    delivery_days  INT,

    CONSTRAINT uq_equipment_brand_model UNIQUE (brand, model),
    CONSTRAINT chk_power_range CHECK (
        (power_min_kw IS NULL AND power_max_kw IS NULL)
            OR (power_min_kw IS NOT NULL AND power_max_kw IS NOT NULL AND power_min_kw <= power_max_kw)
        ),
    CONSTRAINT chk_flow_range CHECK (
        (flow_min_m3h IS NULL AND flow_max_m3h IS NULL)
            OR (flow_min_m3h IS NOT NULL AND flow_max_m3h IS NOT NULL AND flow_min_m3h <= flow_max_m3h)
        )
);
COMMENT ON TABLE equipment IS 'Каталог оборудования: котлы, горелки, насосы, арматура, расходомеры, автоматика.';
COMMENT ON COLUMN equipment.id IS 'Первичный ключ оборудования (UUID).';
COMMENT ON COLUMN equipment.category IS 'Категория оборудования (ENUM equipment_category).';
COMMENT ON COLUMN equipment.brand IS 'Бренд/производитель.';
COMMENT ON COLUMN equipment.model IS 'Модель/артикул.';
COMMENT ON COLUMN equipment.active IS 'Активность позиции в каталоге.';
COMMENT ON COLUMN equipment.power_min_kw IS 'Минимальная мощность применимости (кВт).';
COMMENT ON COLUMN equipment.power_max_kw IS 'Максимальная мощность применимости (кВт).';
COMMENT ON COLUMN equipment.flow_min_m3h IS 'Минимальный расход (м³/ч).';
COMMENT ON COLUMN equipment.flow_max_m3h IS 'Максимальный расход (м³/ч).';
COMMENT ON COLUMN equipment.dn_size IS 'Номинальный диаметр (DN).';
COMMENT ON COLUMN equipment.fuel_type IS 'Тип топлива (для горелок).';
COMMENT ON COLUMN equipment.connection_key IS 'Ключ совместимости (фланец/посадочное место), совпадает у совместимых пар.';
COMMENT ON COLUMN equipment.price IS 'Ориентировочная цена (валюта условно RUB).';
COMMENT ON COLUMN equipment.delivery_days IS 'Срок поставки, дней.';
COMMENT ON CONSTRAINT uq_equipment_brand_model ON equipment IS 'Уникальность по комбинации бренд+модель.';
COMMENT ON CONSTRAINT chk_power_range ON equipment IS 'Проверка корректности диапазона мощности.';
COMMENT ON CONSTRAINT chk_flow_range ON equipment IS 'Проверка корректности диапазона расхода.';

-- config_candidates: сохранённые предложения (Форма №3)
CREATE TABLE IF NOT EXISTS config_candidates
(
    id          UUID PRIMARY KEY,
    request_id  UUID           NOT NULL REFERENCES heating_requests (id) ON DELETE CASCADE,
    total_price NUMERIC(14, 2) NOT NULL,
    currency    TEXT           NOT NULL DEFAULT 'RUB',
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);
COMMENT ON TABLE config_candidates IS 'Сохранённые предложенные конфигурации (итоговые цены) для Формы №3.';
COMMENT ON COLUMN config_candidates.id IS 'Первичный ключ кандидата (UUID).';
COMMENT ON COLUMN config_candidates.request_id IS 'Запрос, под который сформирован кандидат (heating_requests.id).';
COMMENT ON COLUMN config_candidates.total_price IS 'Итоговая цена конфигурации.';
COMMENT ON COLUMN config_candidates.currency IS 'Валюта цены (по умолчанию RUB).';
COMMENT ON COLUMN config_candidates.created_at IS 'Время формирования кандидата.';

-- config_components: состав конфигурации
CREATE TABLE IF NOT EXISTS config_components
(
    candidate_id UUID               NOT NULL REFERENCES config_candidates (id) ON DELETE CASCADE,
    category     equipment_category NOT NULL,
    equipment_id UUID               NOT NULL REFERENCES equipment (id),
    qty          NUMERIC(10, 3)     NOT NULL DEFAULT 1,
    unit_price   NUMERIC(14, 2)     NOT NULL,
    subtotal     NUMERIC(14, 2)     NOT NULL,
    PRIMARY KEY (candidate_id, equipment_id)
);
COMMENT ON TABLE config_components IS 'Состав сохранённой конфигурации: компоненты и цены.';
COMMENT ON COLUMN config_components.candidate_id IS 'Ссылка на кандидата (config_candidates.id).';
COMMENT ON COLUMN config_components.category IS 'Категория компонента.';
COMMENT ON COLUMN config_components.equipment_id IS 'Выбранная позиция из каталога (equipment.id).';
COMMENT ON COLUMN config_components.qty IS 'Количество единиц.';
COMMENT ON COLUMN config_components.unit_price IS 'Цена за единицу на момент формирования.';
COMMENT ON COLUMN config_components.subtotal IS 'Сумма по компоненту (qty * unit_price).';

-- selections: выбор клиента (Форма №4)
CREATE TABLE IF NOT EXISTS selections
(
    id           UUID PRIMARY KEY,
    request_id   UUID        NOT NULL REFERENCES heating_requests (id) ON DELETE CASCADE,
    candidate_id UUID        NOT NULL REFERENCES config_candidates (id),
    selected_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    pdf_path     TEXT
);
COMMENT ON TABLE selections IS 'Выбор клиента и ссылка на сгенерированный PDF (Форма №4).';
COMMENT ON COLUMN selections.id IS 'Первичный ключ выбора (UUID).';
COMMENT ON COLUMN selections.request_id IS 'Запрос, для которого сделан выбор (heating_requests.id).';
COMMENT ON COLUMN selections.candidate_id IS 'Выбранная конфигурация (config_candidates.id).';
COMMENT ON COLUMN selections.selected_at IS 'Время фиксации выбора.';
COMMENT ON COLUMN selections.pdf_path IS 'Путь к сгенерированному PDF-документу.';

-- Индексы
CREATE INDEX IF NOT EXISTS users_email_idx ON users (email);
CREATE INDEX IF NOT EXISTS leads_email_idx ON leads (email);
CREATE INDEX IF NOT EXISTS leads_phone_idx ON leads (phone);

CREATE INDEX IF NOT EXISTS heating_requests_lead_idx ON heating_requests (lead_id);
CREATE INDEX IF NOT EXISTS heating_requests_status_idx ON heating_requests (status);

CREATE INDEX IF NOT EXISTS equipment_category_idx ON equipment (category);
CREATE INDEX IF NOT EXISTS equipment_active_idx ON equipment (active) WHERE active;
CREATE INDEX IF NOT EXISTS equipment_conn_key_idx ON equipment (connection_key);
CREATE INDEX IF NOT EXISTS equipment_power_range_idx ON equipment (power_min_kw, power_max_kw);
CREATE INDEX IF NOT EXISTS equipment_flow_range_idx ON equipment (flow_min_m3h, flow_max_m3h);
CREATE INDEX IF NOT EXISTS equipment_dn_idx ON equipment (dn_size);

CREATE INDEX IF NOT EXISTS config_candidates_request_idx ON config_candidates (request_id);
CREATE INDEX IF NOT EXISTS config_components_candidate_idx ON config_components (candidate_id);
CREATE INDEX IF NOT EXISTS selections_request_idx ON selections (request_id);
