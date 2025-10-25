import { apiClient } from './client'
import type { UserDto, UserCreateDto, UserUpdateDto, UserPageDto, UserRole } from './types'

const BASE_PATH = '/admin/users'

/**
 * API для работы с пользователями (админка)
 */
export const usersApi = {
    /**
     * Получить список пользователей с пагинацией и фильтрацией по ролям
     */
    list: async (page = 0, size = 20, roles?: UserRole[]): Promise<UserPageDto> => {
        const response = await apiClient.get<UserPageDto>(BASE_PATH, {
            params: {
                page,
                size,
                ...(roles && roles.length > 0 ? { roles: roles.join(',') } : {})
            }
        })
        return response.data
    },

    /**
     * Получить пользователя по ID
     */
    get: async (id: string): Promise<UserDto> => {
        const response = await apiClient.get<UserDto>(`${BASE_PATH}/${id}`)
        return response.data
    },

    /**
     * Создать нового пользователя
     */
    create: async (data: UserCreateDto): Promise<UserDto> => {
        const response = await apiClient.post<UserDto>(BASE_PATH, data)
        return response.data
    },

    /**
     * Обновить данные пользователя
     */
    update: async (id: string, data: UserUpdateDto): Promise<UserDto> => {
        const response = await apiClient.put<UserDto>(`${BASE_PATH}/${id}`, data)
        return response.data
    },

    /**
     * Удалить пользователя
     */
    delete: async (id: string): Promise<void> => {
        await apiClient.delete(`${BASE_PATH}/${id}`)
    }
}
