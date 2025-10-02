# API Client Documentation

Централизованный API-клиент для взаимодействия с бэкендом.

## Структура

```
api/
├── client.ts          # Настройка axios с interceptors
├── types.ts           # Общие типы и enum
├── auth.ts            # API авторизации
├── heatingRequests.ts # API заявок на отопление
├── selection.ts       # API подбора конфигураций
├── candidates.ts      # API кандидатов
├── index.ts           # Централизованный экспорт
└── README.md          # Документация
```

## Использование

### 1. Создание заявки (Форма №2)

```typescript
import { heatingRequestsApi, FuelType } from '@/api'

const createRequest = async () => {
    try {
        const request = await heatingRequestsApi.create({
            powerKw: 500,
            temperatureIn: 70,
            temperatureOut: 90,
            fuelType: FuelType.NATURAL_GAS,
            notes: 'Примечание'
        })

        console.log('Создана заявка:', request.id)
        return request.id
    } catch (error) {
        console.error('Ошибка создания:', error)
    }
}
```

### 2. Превью конфигураций без сохранения

```typescript
import { selectionApi } from '@/api'

const previewCandidates = async (requestId: string) => {
    try {
        const candidates = await selectionApi.previewCandidates(requestId, {
            topN: 5,
            includeAutomation: true
        })

        console.log('Найдено вариантов:', candidates.length)
        candidates.forEach(c => {
            console.log(`Цена: ${c.totalPrice}, Срок: ${c.maxDeliveryDays} дней`)
        })
    } catch (error) {
        console.error('Ошибка подбора:', error)
    }
}
```

### 3. Сохранение вариантов

```typescript
import { selectionApi, candidatesApi } from '@/api'

const saveAndLoadCandidates = async (requestId: string) => {
    try {
        // Генерируем и сохраняем
        await selectionApi.generateCandidates(requestId, {
            topN: 5,
            includeAutomation: true
        })

        // Загружаем сохранённые с компонентами
        const saved = await candidatesApi.getByRequest(requestId, true)

        console.log('Сохранено вариантов:', saved.length)
        return saved
    } catch (error) {
        console.error('Ошибка сохранения:', error)
    }
}
```

### 4. Список заявок (История)

```typescript
import { heatingRequestsApi, HeatingRequestStatus, FuelType } from '@/api'

const loadRequests = async () => {
    try {
        const page = await heatingRequestsApi.list({
            status: HeatingRequestStatus.CANDIDATES_READY,
            fuelType: FuelType.NATURAL_GAS,
            page: 0,
            size: 20
        })

        console.log(`Загружено ${page.content.length} из ${page.totalElements}`)
        return page.content
    } catch (error) {
        console.error('Ошибка загрузки списка:', error)
    }
}
```

### 5. Обновление параметров заявки

```typescript
import { heatingRequestsApi } from '@/api'

const updateRequest = async (id: string) => {
    try {
        const updated = await heatingRequestsApi.update(id, {
            powerKw: 600,
            notes: 'Обновлённое примечание'
        })

        console.log('Обновлена заявка:', updated.id)
    } catch (error) {
        console.error('Ошибка обновления:', error)
    }
}
```

### 6. Работа с кандидатами

```typescript
import { candidatesApi } from '@/api'

// Получить одного кандидата
const getCandidate = async (candidateId: string) => {
    const candidate = await candidatesApi.get(candidateId, true)
    console.log('Компонентов:', candidate.components.length)
}

// Получить компоненты отдельно
const getComponents = async (candidateId: string) => {
    const components = await candidatesApi.getComponents(candidateId)
    components.forEach(c => {
        console.log(`${c.brandName} ${c.modelName}: ${c.price} руб.`)
    })
}

// Удалить кандидата
const deleteCandidate = async (candidateId: string) => {
    await candidatesApi.delete(candidateId)
    console.log('Кандидат удалён')
}
```

### 7. Авторизация

```typescript
import { authApi } from '@/api'

// Вход клиента
const loginCustomer = async () => {
    const response = await authApi.loginCustomer({
        email: 'client@example.com',
        password: 'password'
    })

    localStorage.setItem('authToken', response.token)
    console.log('Вход выполнен:', response.companyName)
}

// Регистрация клиента
const registerCustomer = async () => {
    const response = await authApi.registerCustomer({
        email: 'new@example.com',
        password: 'password',
        companyName: 'ООО Рога и Копыта',
        contactPerson: 'Иванов Иван',
        phone: '+7 999 123-45-67',
        address: 'Москва'
    })

    localStorage.setItem('authToken', response.token)
}

// Вход менеджера/админа
const loginManager = async () => {
    const response = await authApi.login({
        email: 'admin@example.com',
        password: 'password'
    })

    localStorage.setItem('authToken', response.token)
}
```

## Обработка ошибок

API-клиент автоматически обрабатывает ошибки:

- **401 Unauthorized** → автоматический редирект на `/login` + очистка токена
- **Другие ошибки** → выбрасывается Error с сообщением от сервера

```typescript
try {
    const request = await heatingRequestsApi.create(data)
} catch (error) {
    // error.message содержит текст ошибки от сервера
    toast.error(error.message)
}
```

## Типы

Все типы экспортируются из `@/api`:

```typescript
import type {
    HeatingRequestDto,
    ConfigurationCandidateDto,
    ConfigurationComponentDto,
    FuelType,
    HeatingRequestStatus,
    Page
} from '@/api'
```

## Enum значения

### FuelType
- `NATURAL_GAS` - природный газ
- `DIESEL` - дизель
- `ELECTRICITY` - электричество
- `PELLETS` - пеллеты

### HeatingRequestStatus
- `DRAFT` - черновик
- `IN_PROGRESS` - в работе
- `CANDIDATES_READY` - варианты готовы
- `COMPLETED` - завершено
- `CANCELLED` - отменено

## Особенности

1. **Автоматическая авторизация**: токен добавляется к каждому запросу из `localStorage.authToken`
2. **Централизованная обработка ошибок**: все 401 ошибки перенаправляют на логин
3. **TypeScript**: полная типизация всех запросов и ответов
4. **Axios interceptors**: единая точка для модификации запросов/ответов
