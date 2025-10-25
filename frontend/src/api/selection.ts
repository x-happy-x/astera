import { apiClient } from './client'
import type { ConfigurationCandidateDto, SelectionParams, SelectionDto } from './types'

const BASE_PATH = '/heating-requests'

/**
 * API для подбора конфигураций
 */
export const selectionApi = {

    /**
     * Генерация и сохранение конфигураций
     * Заменяет все существующие кандидаты для заявки на новые
     */
    generateCandidates: async (
        requestId: string,
        params?: SelectionParams
    ): Promise<ConfigurationCandidateDto[]> => {
        const response = await apiClient.post<ConfigurationCandidateDto[]>(
            `${BASE_PATH}/${requestId}/generate-candidates`,
            null,
            {
                params: {
                    topN: params?.topN ?? 5,
                    includeAutomation: params?.includeAutomation ?? true
                }
            }
        )
        return response.data
    },

    /**
     * Зафиксировать выбор кандидата
     */
    select: async (
        requestId: string,
        candidateId: string,
        pdfPath?: string
    ): Promise<SelectionDto> => {
        const response = await apiClient.post<SelectionDto>(
            `${BASE_PATH}/${requestId}/selection`,
            {
                candidateId,
                pdfPath
            }
        )
        return response.data
    },

    /**
     * Получить выбор по заявке
     */
    getByRequest: async (requestId: string): Promise<SelectionDto> => {
        const response = await apiClient.get<SelectionDto>(
            `${BASE_PATH}/${requestId}/selection`
        )
        return response.data
    },
}
