import { apiClient } from './client'
import type { ConfigurationCandidateDto, ConfigurationComponentDto } from './types'

/**
 * API для работы с кандидатами конфигураций
 */
export const candidatesApi = {
    /**
     * Получить всех кандидатов для заявки
     */
    getByRequest: async (
        requestId: string,
        withComponents = true
    ): Promise<ConfigurationCandidateDto[]> => {
        const response = await apiClient.get<ConfigurationCandidateDto[]>(
            `/heating-requests/${requestId}/candidates`,
            {
                params: { withComponents }
            }
        )
        return response.data
    },

    /**
     * Заменить всех кандидатов для заявки
     */
    replaceForRequest: async (
        requestId: string,
        candidates: ConfigurationCandidateDto[]
    ): Promise<void> => {
        await apiClient.put(`/heating-requests/${requestId}/candidates`, candidates)
    },

    /**
     * Получить одного кандидата по ID
     */
    get: async (
        candidateId: string,
        withComponents = true
    ): Promise<ConfigurationCandidateDto> => {
        const response = await apiClient.get<ConfigurationCandidateDto>(
            `/candidates/${candidateId}`,
            {
                params: { withComponents }
            }
        )
        return response.data
    },

    /**
     * Удалить кандидата
     */
    delete: async (candidateId: string): Promise<void> => {
        await apiClient.delete(`/candidates/${candidateId}`)
    },

    /**
     * Получить компоненты кандидата
     */
    getComponents: async (candidateId: string): Promise<ConfigurationComponentDto[]> => {
        const response = await apiClient.get<ConfigurationComponentDto[]>(
            `/candidates/${candidateId}/components`
        )
        return response.data
    }
}
