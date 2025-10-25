import axios, { AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'

const BASE_URL = '/api'

class ApiClient {
    private readonly client: AxiosInstance

    constructor() {
        this.client = axios.create({
            baseURL: BASE_URL,
            headers: {
                'Content-Type': 'application/json'
            }
        })

        this.setupInterceptors()
    }

    private setupInterceptors() {
        // Request interceptor - добавляем токен
        this.client.interceptors.request.use(
            (config: InternalAxiosRequestConfig) => {
                const token = localStorage.getItem('authToken')
                if (token && config.headers) {
                    config.headers.Authorization = `Bearer ${token}`
                }

                return config
            },
            (error) => Promise.reject(error)
        )

        // Response interceptor - обработка ошибок
        this.client.interceptors.response.use(
            (response) => response,
            (error: AxiosError<{ message?: string }>) => {
                // 401 - редирект на логин только если это не сам запрос авторизации
                if (error.response?.status === 401) {
                    const url = error.config?.url || ''
                    const isLoginRequest = url.includes('/login') || url.includes('/register')

                    if (!isLoginRequest) {
                        localStorage.removeItem('authToken')
                        window.location.href = '/login'
                    }
                }

                // Формируем понятное сообщение об ошибке
                const errorMessage = error.response?.data?.message
                    || error.message
                    || 'Произошла ошибка'

                // Можно добавить toast-уведомления здесь
                console.error('API Error:', errorMessage)

                return Promise.reject(new Error(errorMessage))
            }
        )
    }

    getClient(): AxiosInstance {
        return this.client
    }
}

export const apiClient = new ApiClient().getClient()
