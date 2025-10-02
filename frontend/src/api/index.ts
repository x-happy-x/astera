/**
 * Централизованный экспорт всех API методов
 *
 * Использование:
 * import { heatingRequestsApi, selectionApi, candidatesApi, authApi } from '@/api'
 */

export { apiClient } from './client'
export { heatingRequestsApi } from './heatingRequests'
export { selectionApi } from './selection'
export { candidatesApi } from './candidates'
export { authApi } from './auth'

export { FuelType, HeatingRequestStatus, EquipmentCategory } from './types'

export type {
    HeatingRequestCreateDto,
    HeatingRequestDto,
    HeatingRequestUpdateDto,
    HeatingRequestListParams,
    ConfigurationCandidateDto,
    ConfigurationComponentDto,
    SelectionParams,
    Page
} from './types'

export type {
    LoginDto,
    CustomerRegistrationDto,
    ManagerRegistrationDto,
    CustomerResponseDto,
    AuthResponseDto
} from './auth'
