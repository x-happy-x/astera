// Базовые типы
export const FuelType = {
    NATURAL_GAS: 'gas' as const,
    DIESEL: 'diesel' as const,
    OTHER: 'other' as const
}

export type FuelType = typeof FuelType[keyof typeof FuelType]

export const HeatingRequestStatus = {
    DRAFT: 'DRAFT' as const,
    IN_PROGRESS: 'IN_PROGRESS' as const,
    CANDIDATES_READY: 'CANDIDATES_READY' as const,
    COMPLETED: 'COMPLETED' as const,
    CANCELLED: 'CANCELLED' as const
}

export type HeatingRequestStatus = typeof HeatingRequestStatus[keyof typeof HeatingRequestStatus]

// HeatingRequest
export interface HeatingRequestCreateDto {
    customerId?: string
    powerKw: number
    tIn: number
    tOut: number
    fuelType: FuelType
    notes?: string
}

export interface HeatingRequestUpdateDto {
    powerKw?: number
    tIn?: number
    tOut?: number
    fuelType?: FuelType
    notes?: string
}

export interface HeatingRequestDto {
    id: string
    customerId?: string
    powerKw: number
    tIn: number
    tOut: number
    fuelType: FuelType
    notes?: string
    status: HeatingRequestStatus
}

// Equipment Category
export const EquipmentCategory = {
    boiler: 'boiler' as const,
    burner: 'burner' as const,
    pump: 'pump' as const,
    valve: 'valve' as const,
    flowmeter: 'flowmeter' as const,
    automation: 'automation' as const
}

export type EquipmentCategory = typeof EquipmentCategory[keyof typeof EquipmentCategory]

// ConfigurationCandidate
export interface ConfigurationComponentDto {
    equipmentId: string
    category: EquipmentCategory
    brand: string
    model: string
    dnSize?: number
    connectionKey?: string
    deliveryDays: number
    qty: number
    unitPrice: number
    subtotal: number
}

export interface ConfigurationCandidateDto {
    id?: string
    requestId?: string
    totalPrice: number
    currency?: string
    maxDeliveryDays: number
    connectionKey: string
    dnSize: number
    components: ConfigurationComponentDto[]
}

// Pagination
export interface Page<T> {
    content: T[]
    totalElements: number
    totalPages: number
    size: number
    number: number
    first: boolean
    last: boolean
    empty: boolean
}

// Параметры для списка запросов
export interface HeatingRequestListParams {
    customerId?: string
    status?: HeatingRequestStatus
    fuelType?: FuelType
    page?: number
    size?: number
}

// Параметры для подбора конфигураций
export interface SelectionParams {
    topN?: number
    includeAutomation?: boolean
}

// Selection (выбор клиента)
export interface SelectionDto {
    id: string
    requestId: string
    candidateId: string
    pdfPath?: string
    selectedAt: string
}

// Customer (клиент)
export interface CustomerDto {
    id: string
    email: string
    fullName: string
    phone: string
    organization: string
    isActive: boolean
    createdAt: string
}

export interface CustomerCreateDto {
    email: string
    fullName: string
    phone: string
    organization: string
    password?: string
}

export interface CustomerUpdateDto {
    email: string
    fullName: string
    phone: string
    organization: string
    isActive?: boolean
}

export interface CustomerPageDto {
    customers: CustomerDto[]
    totalCustomers: number
    totalPages: number
    currentPage: number
    pageSize: number
}

// Equipment (оборудование)
export interface EquipmentDto {
    id: string
    category: EquipmentCategory
    brand: string
    model: string
    active: boolean
    powerMinKw?: number
    powerMaxKw?: number
    flowMinM3h?: number
    flowMaxM3h?: number
    dnSize?: number
    fuelType?: FuelType
    connectionKey?: string
    price: number
    deliveryDays?: number
}

export interface EquipmentCreateDto {
    category: EquipmentCategory
    brand: string
    model: string
    active?: boolean
    powerMinKw?: number
    powerMaxKw?: number
    flowMinM3h?: number
    flowMaxM3h?: number
    dnSize?: number
    fuelType?: FuelType
    connectionKey?: string
    price: number
    deliveryDays?: number
}

export interface EquipmentUpdateDto {
    category: EquipmentCategory
    brand: string
    model: string
    active?: boolean
    powerMinKw?: number
    powerMaxKw?: number
    flowMinM3h?: number
    flowMaxM3h?: number
    dnSize?: number
    fuelType?: FuelType
    connectionKey?: string
    price: number
    deliveryDays?: number
}

export interface EquipmentPageDto {
    equipment: EquipmentDto[]
    totalEquipment: number
    totalPages: number
    currentPage: number
    pageSize: number
}

// User (пользователь)
export const UserRole = {
    admin: 'admin' as const,
    manager: 'manager' as const,
    customer: 'customer' as const
}

export type UserRole = typeof UserRole[keyof typeof UserRole]

export interface UserDto {
    id: string
    email: string
    fullName: string
    role: UserRole
    isActive: boolean
    createdAt: string
}

export interface UserCreateDto {
    email: string
    fullName: string
    role: UserRole
    password?: string
    isActive?: boolean
}

export interface UserUpdateDto {
    email: string
    fullName: string
    role: UserRole
    password?: string
    isActive?: boolean
}

export interface UserPageDto {
    users: UserDto[]
    totalUsers: number
    totalPages: number
    currentPage: number
    pageSize: number
}
