import { apiClient } from './client'
import type {
    HeatingRequestCreateDto,
    HeatingRequestDto,
    HeatingRequestUpdateDto,
    HeatingRequestListParams,
    HeatingRequestStatus,
    Page
} from './types'

const BASE_PATH = '/heating-requests'

/**
 * API для работы с заявками на отопление
 */
export const heatingRequestsApi = {
    /**
     * Создать новую заявку
     */
    create: async (data: HeatingRequestCreateDto): Promise<HeatingRequestDto> => {
        const response = await apiClient.post<HeatingRequestDto>(BASE_PATH, data)
        return response.data
    },

    /**
     * Получить заявку по ID
     */
    get: async (id: string): Promise<HeatingRequestDto> => {
        const response = await apiClient.get<HeatingRequestDto>(`${BASE_PATH}/${id}`)
        return response.data
    },

    /**
     * Получить список заявок с фильтрацией и пагинацией
     */
    list: async (params?: HeatingRequestListParams): Promise<Page<HeatingRequestDto>> => {
        const response = await apiClient.get<Page<HeatingRequestDto>>(BASE_PATH, {
            params: {
                customerId: params?.customerId,
                status: params?.status,
                fuelType: params?.fuelType,
                page: params?.page ?? 0,
                size: params?.size ?? 20
            }
        })
        return response.data
    },

    /**
     * Обновить параметры заявки
     */
    update: async (id: string, data: HeatingRequestUpdateDto): Promise<HeatingRequestDto> => {
        const response = await apiClient.patch<HeatingRequestDto>(`${BASE_PATH}/${id}`, data)
        return response.data
    },

    /**
     * Изменить статус заявки
     */
    setStatus: async (id: string, status: HeatingRequestStatus): Promise<HeatingRequestDto> => {
        const response = await apiClient.patch<HeatingRequestDto>(
            `${BASE_PATH}/${id}/status`,
            { status }
        )
        return response.data
    },

    /**
     * Удалить заявку
     */
    delete: async (id: string): Promise<void> => {
        await apiClient.delete(`${BASE_PATH}/${id}`)
    }
}
