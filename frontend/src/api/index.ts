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
export { customersApi } from './customers'
export { equipmentApi } from './equipment'
export { usersApi } from './users'

export { FuelType, HeatingRequestStatus, EquipmentCategory, UserRole } from './types'

export type {
    HeatingRequestCreateDto,
    HeatingRequestDto,
    HeatingRequestUpdateDto,
    HeatingRequestListParams,
    ConfigurationCandidateDto,
    ConfigurationComponentDto,
    SelectionParams,
    SelectionDto,
    Page,
    CustomerDto,
    CustomerCreateDto,
    CustomerUpdateDto,
    CustomerPageDto,
    EquipmentDto,
    EquipmentCreateDto,
    EquipmentUpdateDto,
    EquipmentPageDto,
    UserDto,
    UserCreateDto,
    UserUpdateDto,
    UserPageDto
} from './types'

export type {
    LoginDto,
    CustomerRegistrationDto,
    ManagerRegistrationDto,
    CustomerResponseDto,
    AuthResponseDto
} from './auth'
