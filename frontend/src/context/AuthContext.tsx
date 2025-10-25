import React, { createContext, useReducer } from 'react'
import type { ReactNode } from 'react'
import type { AuthState, AuthAction, AuthContextType } from '../types/auth'

const initialState: AuthState = {
    user: null,
    isLoading: true,
    error: null
}

const authReducer = (state: AuthState, action: AuthAction): AuthState => {
    switch (action.type) {
        case 'LOGIN_START':
            return {
                ...state,
                isLoading: true,
                error: null
            }
        case 'LOGIN_SUCCESS':
            return {
                ...state,
                user: action.payload,
                isLoading: false,
                error: null
            }
        case 'LOGIN_FAILURE':
            return {
                ...state,
                user: null,
                isLoading: false,
                error: action.payload
            }
        case 'LOGOUT':
            return {
                ...state,
                user: null,
                isLoading: false,
                error: null
            }
        case 'CLEAR_ERROR':
            return {
                ...state,
                error: null
            }
        default:
            return state
    }
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | undefined>(undefined)

interface AuthProviderProps {
    children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [state, dispatch] = useReducer(authReducer, initialState)

    const login = async (email: string, password: string) => {
        dispatch({ type: 'LOGIN_START' })
        
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            })

            if (!response.ok) {
                const errorData = await response.json()
                throw new Error(errorData.message || 'Ошибка авторизации')
            }

            const userData = await response.json()
            localStorage.setItem('authToken', userData.token)
            dispatch({ type: 'LOGIN_SUCCESS', payload: userData })
        } catch (error) {
            dispatch({ 
                type: 'LOGIN_FAILURE', 
                payload: error instanceof Error ? error.message : 'Неизвестная ошибка' 
            })
            throw error
        }
    }

    const logout = () => {
        localStorage.removeItem('authToken')
        dispatch({ type: 'LOGOUT' })
    }

    const clearError = () => {
        dispatch({ type: 'CLEAR_ERROR' })
    }

    React.useEffect(() => {
        const token = localStorage.getItem('authToken')
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]))
                if (payload.exp > Date.now() / 1000) {
                    dispatch({
                        type: 'LOGIN_SUCCESS',
                        payload: {
                            token,
                            email: payload.email,
                            fullName: payload.fullName,
                            role: payload.role
                        }
                    })
                } else {
                    localStorage.removeItem('authToken')
                    dispatch({ type: 'LOGOUT' })
                }
            } catch {
                localStorage.removeItem('authToken')
                dispatch({ type: 'LOGOUT' })
            }
        } else {
            dispatch({ type: 'LOGOUT' })
        }
    }, [])

    return (
        <AuthContext.Provider value={{ state, login, logout, clearError }}>
            {children}
        </AuthContext.Provider>
    )
}