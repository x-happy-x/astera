import { apiClient } from './client'
import type { EquipmentDto, EquipmentCreateDto, EquipmentUpdateDto, EquipmentPageDto } from './types'

const BASE_PATH = '/admin/equipment'

/**
 * API для работы с оборудованием (админка)
 */
export const equipmentApi = {
    /**
     * Получить список оборудования с пагинацией
     */
    list: async (page = 0, size = 20): Promise<EquipmentPageDto> => {
        const response = await apiClient.get<EquipmentPageDto>(BASE_PATH, {
            params: { page, size }
        })
        return response.data
    },

    /**
     * Получить оборудование по ID
     */
    get: async (id: string): Promise<EquipmentDto> => {
        const response = await apiClient.get<EquipmentDto>(`${BASE_PATH}/${id}`)
        return response.data
    },

    /**
     * Создать новое оборудование
     */
    create: async (data: EquipmentCreateDto): Promise<EquipmentDto> => {
        const response = await apiClient.post<EquipmentDto>(BASE_PATH, data)
        return response.data
    },

    /**
     * Обновить данные оборудования
     */
    update: async (id: string, data: EquipmentUpdateDto): Promise<EquipmentDto> => {
        const response = await apiClient.put<EquipmentDto>(`${BASE_PATH}/${id}`, data)
        return response.data
    },

    /**
     * Удалить оборудование
     */
    delete: async (id: string): Promise<void> => {
        await apiClient.delete(`${BASE_PATH}/${id}`)
    }
}
