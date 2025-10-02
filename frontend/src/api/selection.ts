import { apiClient } from './client'
import type { ConfigurationCandidateDto, SelectionParams } from './types'

const BASE_PATH = '/heating-requests'

/**
 * API для подбора конфигураций
 */
export const selectionApi = {
    /**
     * Превью конфигураций без сохранения
     * Возвращает top-N вариантов для заявки
     */
    previewCandidates: async (
        requestId: string,
        params?: SelectionParams
    ): Promise<ConfigurationCandidateDto[]> => {
        const response = await apiClient.post<ConfigurationCandidateDto[]>(
            `${BASE_PATH}/${requestId}/preview-candidates`,
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
    }
}
