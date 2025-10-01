# API Документация

## Аутентификация

### POST /api/auth/login
Вход в систему для менеджеров и администраторов

**Тело запроса:**
```json
{
    "email": "admin@example.com",
    "password": "admin"
}
```

**Ответ:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "admin@example.com",
    "fullName": "Администратор",
    "role": "admin"
}
```

## Регистрация клиентов

### POST /api/leads/register
Регистрация новых клиентов (публичный доступ)

**Тело запроса:**
```json
{
    "fullName": "Иван Петров",
    "phone": "+7(999)123-45-67",
    "email": "client@example.com",
    "organization": "ООО Пример",
    "consentPersonalData": true
}
```

## Административные функции

### POST /api/admin/managers/register
Регистрация новых менеджеров (только для администраторов)

**Заголовки:**
```
Authorization: Bearer <admin_token>
```

**Тело запроса:**
```json
{
    "email": "manager@example.com",
    "fullName": "Менеджер Иванов",
    "password": "password123"
}
```

## Учетные данные по умолчанию

**Администратор:**
- Email: `admin@example.com`
- Пароль: `admin`

## Права доступа

- **Публичные endpoints:** `/api/auth/login`, `/api/leads/register`
- **Только для администраторов:** `/api/admin/**`
- **Для аутентифицированных пользователей:** остальные endpoints

## Коды ошибок

- `400` - Ошибки валидации
- `401` - Неверные учетные данные или отсутствует токен
- `403` - Недостаточно прав доступа
- `409` - Конфликт (пользователь/клиент уже существует)
- `500` - Внутренняя ошибка сервера