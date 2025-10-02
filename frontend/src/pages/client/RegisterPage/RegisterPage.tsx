import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../../api'
import Card from '../../../components/ui/Card'
import './styles.scss'

const RegisterPage: React.FC = () => {
    const navigate = useNavigate()
    const [formData, setFormData] = useState({
        fullName: '',
        phone: '',
        email: '',
        organization: '',
        password: ''
    })
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: value
        }))
        setError(null)
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setError(null)

        try {
            const response = await authApi.registerCustomer(formData)
            localStorage.setItem('authToken', response.token)
            localStorage.setItem('userType', 'customer')
            navigate('/client/requests')
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка регистрации')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="register-page">
            <div className="register-container">
                <Card>
                    <div className="register-header">
                        <h2>Регистрация клиента</h2>
                        <p>Заполните форму для получения доступа</p>
                    </div>

                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="register-form">
                        <div className="form-group">
                            <label htmlFor="fullName">ФИО *</label>
                            <input
                                type="text"
                                id="fullName"
                                name="fullName"
                                value={formData.fullName}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                placeholder="Иванов Иван Иванович"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="organization">Название организации *</label>
                            <input
                                type="text"
                                id="organization"
                                name="organization"
                                value={formData.organization}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                placeholder="ООО Рога и Копыта"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="email">Email *</label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                placeholder="ivan@example.com"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Пароль *</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                minLength={6}
                                placeholder="Минимум 6 символов"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="phone">Телефон *</label>
                            <input
                                type="tel"
                                id="phone"
                                name="phone"
                                value={formData.phone}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                placeholder="+7 (999) 123-45-67"
                            />
                        </div>

                        <button
                            type="submit"
                            className="register-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Отправка...' : 'Зарегистрироваться'}
                        </button>

                        <div className="back-link">
                            <Link to="/">← Назад к главной</Link>
                            <span> | </span>
                            <Link to="/client/login">Уже есть аккаунт? Войти</Link>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    )
}

export default RegisterPage