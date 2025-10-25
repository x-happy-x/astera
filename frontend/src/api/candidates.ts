import { apiClient } from './client'
import type { ConfigurationCandidateDto } from './types'

/**
 * API для работы с кандидатами конфигураций
 */
export const candidatesApi = {
    /**
     * Превью кандидатов без сохранения
     */
    preview: async (
        requestId: string,
        topN = 3,
        includeAutomation = true
    ): Promise<ConfigurationCandidateDto[]> => {
        const response = await apiClient.post<ConfigurationCandidateDto[]>(
            `/heating-requests/${requestId}/preview-candidates`,
            null,
            {
                params: { topN, includeAutomation }
            }
        )
        return response.data
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
    }
}
