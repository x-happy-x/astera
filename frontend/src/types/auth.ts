export interface User {
    email: string
    fullName: string
    role: 'admin' | 'manager'
    token: string
}

export interface AuthState {
    user: User | null
    isLoading: boolean
    error: string | null
}

export type AuthAction =
    | { type: 'LOGIN_START' }
    | { type: 'LOGIN_SUCCESS'; payload: User }
    | { type: 'LOGIN_FAILURE'; payload: string }
    | { type: 'LOGOUT' }
    | { type: 'CLEAR_ERROR' }

export interface AuthContextType {
    state: AuthState
    login: (email: string, password: string) => Promise<void>
    logout: () => void
    clearError: () => void
}