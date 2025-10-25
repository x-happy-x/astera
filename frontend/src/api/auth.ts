import { apiClient } from './client'

// Типы для авторизации
export interface LoginDto {
    email: string
    password: string
}

export interface CustomerRegistrationDto {
    fullName: string
    phone: string
    email: string
    organization: string
    password: string
}

export interface ManagerRegistrationDto {
    email: string
    fullName: string
    password: string
}

export interface CustomerResponseDto {
    id: string
    token: string
    email: string
    fullName: string
    role: 'CUSTOMER' | 'MANAGER' | 'ADMIN'
    phone: string
    organization: string
}

export interface AuthResponseDto {
    id: string
    token: string
    email: string
    fullName: string
    role: 'CUSTOMER' | 'MANAGER' | 'ADMIN'
}

const BASE_PATH = '/auth'

/**
 * API для авторизации и регистрации
 */
export const authApi = {
    /**
     * Регистрация клиента
     */
    registerCustomer: async (data: CustomerRegistrationDto): Promise<CustomerResponseDto> => {
        const response = await apiClient.post<CustomerResponseDto>(
            `${BASE_PATH}/customer/register`,
            data
        )
        return response.data
    },

    /**
     * Вход клиента
     */
    loginCustomer: async (data: LoginDto): Promise<CustomerResponseDto> => {
        const response = await apiClient.post<CustomerResponseDto>(
            `${BASE_PATH}/customer/login`,
            data
        )
        return response.data
    },

    /**
     * Вход менеджера/админа
     */
    login: async (data: LoginDto): Promise<AuthResponseDto> => {
        const response = await apiClient.post<AuthResponseDto>(`${BASE_PATH}/login`, data)
        return response.data
    }
}
