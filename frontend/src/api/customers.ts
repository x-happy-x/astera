import { apiClient } from './client'
import type { CustomerDto, CustomerCreateDto, CustomerUpdateDto, CustomerPageDto } from './types'

const BASE_PATH = '/admin/customers'

/**
 * API для работы с клиентами (админка)
 */
export const customersApi = {
    /**
     * Получить список клиентов с пагинацией
     */
    list: async (page = 0, size = 20): Promise<CustomerPageDto> => {
        const response = await apiClient.get<CustomerPageDto>(BASE_PATH, {
            params: { page, size }
        })
        return response.data
    },

    /**
     * Получить клиента по ID
     */
    get: async (id: string): Promise<CustomerDto> => {
        const response = await apiClient.get<CustomerDto>(`${BASE_PATH}/${id}`)
        return response.data
    },

    /**
     * Создать нового клиента
     */
    create: async (data: CustomerCreateDto): Promise<CustomerDto> => {
        const response = await apiClient.post<CustomerDto>(BASE_PATH, data)
        return response.data
    },

    /**
     * Обновить данные клиента
     */
    update: async (id: string, data: CustomerUpdateDto): Promise<CustomerDto> => {
        const response = await apiClient.put<CustomerDto>(`${BASE_PATH}/${id}`, data)
        return response.data
    },

    /**
     * Удалить клиента
     */
    delete: async (id: string): Promise<void> => {
        await apiClient.delete(`${BASE_PATH}/${id}`)
    }
}
