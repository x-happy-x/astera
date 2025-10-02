# Frontend Client Setup - Готово ✅

Создан полноценный клиентский интерфейс с API-интеграцией и управлением заявками.

## 📁 Созданные файлы

### API Client (`frontend/src/api/`)
- ✅ `client.ts` - Axios с interceptors (авторизация, 401 redirect)
- ✅ `types.ts` - TypeScript типы и enum
- ✅ `auth.ts` - API авторизации и регистрации
- ✅ `heatingRequests.ts` - CRUD операции с заявками
- ✅ `selection.ts` - Подбор конфигураций (preview/generate)
- ✅ `candidates.ts` - Работа с кандидатами
- ✅ `index.ts` - Централизованный экспорт
- ✅ `README.md` - Документация с примерами

### Client Pages (`frontend/src/pages/client/`)
- ✅ `LoginPage/` - Авторизация клиента
- ✅ `RegisterPage/` - Регистрация с полями (обновлена)
- ✅ `RequestsPage/` - Список заявок с фильтрами
- ✅ `RequestFormPage/` - Создание/редактирование заявки
- ✅ `LandingPage/` - Главная страница (обновлена)

### Routing
- ✅ Обновлён `ClientRoutes.tsx` с новыми маршрутами

## 🎯 Реализованный функционал

### 1. Регистрация и авторизация
```
/register          - Регистрация клиента
/client/login      - Вход клиента
```

**Поля регистрации:**
- Контактное лицо
- Название компании
- Email
- Пароль (min 6 символов)
- Телефон
- Адрес (опционально)

### 2. Список заявок (`/client/requests`)
- ✅ Отображение всех заявок клиента
- ✅ Фильтры по статусу и типу топлива
- ✅ Карточки с информацией:
  - Мощность (кВт)
  - Температуры подачи/обратки
  - Тип топлива
  - Статус (с цветными бейджами)
  - Дата создания
- ✅ Кнопки "Открыть" и "Редактировать"
- ✅ Кнопка создания новой заявки
- ✅ Кнопка выхода

### 3. Форма заявки (`/client/requests/new`, `/client/requests/:id/edit`)
- ✅ Поля:
  - Мощность (кВт) *
  - Температура подачи (°C) *
  - Температура обратки (°C) *
  - Тип топлива *
  - Примечание
- ✅ Валидация:
  - Мощность > 0
  - Температура подачи < температуры обратки
- ✅ Работает в режимах create/edit
- ✅ Автозагрузка данных в режиме редактирования

## 🔧 API Integration

### Автоматическая авторизация
```typescript
// Токен автоматически добавляется к каждому запросу
Authorization: Bearer <token>
```

### Обработка ошибок
- `401` → Автоматический redirect на `/login`
- Остальные ошибки → Отображение сообщения

### Примеры использования

#### Создание заявки
```typescript
import { heatingRequestsApi, FuelType } from '@/api'

const request = await heatingRequestsApi.create({
    powerKw: 500,
    temperatureIn: 70,
    temperatureOut: 90,
    fuelType: FuelType.NATURAL_GAS,
    notes: 'Примечание'
})
```

#### Получение списка
```typescript
import { heatingRequestsApi, HeatingRequestStatus } from '@/api'

const page = await heatingRequestsApi.list({
    status: HeatingRequestStatus.CANDIDATES_READY,
    fuelType: FuelType.NATURAL_GAS,
    page: 0,
    size: 20
})
```

## 🎨 UI Features

### Статусы заявок (с цветными бейджами)
- 🔵 **Черновик** (DRAFT) - синий
- 🟠 **В работе** (IN_PROGRESS) - оранжевый
- 🟢 **Варианты готовы** (CANDIDATES_READY) - зелёный
- 🟣 **Завершено** (COMPLETED) - фиолетовый
- 🔴 **Отменено** (CANCELLED) - красный

### Типы топлива
- Природный газ (NATURAL_GAS)
- Дизель (DIESEL)
- Электричество (ELECTRICITY)
- Пеллеты (PELLETS)

## 📍 Маршруты

### Публичные
- `/` - Landing page
- `/register` - Регистрация
- `/client/login` - Вход клиента

### Защищённые (требуют авторизации)
- `/client/requests` - Список заявок
- `/client/requests/new` - Создание заявки
- `/client/requests/:id/edit` - Редактирование заявки
- `/client/requests/:id` - Детали заявки (TODO)

## 🚀 Следующие шаги

Для полной интеграции с бизнес-процессом подбора (из вашего описания) осталось добавить:

### 1. Страница деталей заявки (`/client/requests/:id`)
- Отображение параметров заявки
- **Кнопка "Подобрать"** → `selectionApi.previewCandidates()`
- Отображение вариантов конфигураций
- **Кнопка "Сохранить варианты"** → `selectionApi.generateCandidates()`
- Выбор варианта и печать (Форма №4)

### 2. Компоненты
```typescript
<CandidatesPreview requestId={id} />
  - Карточки вариантов (цена, срок, DN, connectionKey)
  - Раскрывающийся состав (компоненты)
  - Кнопки "Подобрать" и "Сохранить"

<CandidateCard candidate={...} />
  - Отображение одного варианта
  - Кнопка "Выбрать"

<PrintView candidate={selected} />
  - Формат для печати (Форма №4)
  - Логотип, реквизиты, состав, цены
  - window.print()
```

## 💡 Особенности реализации

### localStorage
```typescript
authToken    // JWT токен
userType     // 'customer' | 'admin' | 'manager'
```

### TypeScript
✅ Полная типизация всех API запросов/ответов
✅ Enum для статусов и типов топлива
✅ Валидация на уровне типов

### Responsive Design
✅ Grid layout для карточек заявок
✅ Адаптивные формы
✅ Mobile-friendly

## 📖 Документация
Полная документация API с примерами: `frontend/src/api/README.md`
