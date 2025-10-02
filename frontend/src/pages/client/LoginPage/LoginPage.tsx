import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi } from '../../../api'
import Card from '../../../components/ui/Card'
import './styles.scss'

const LoginPage: React.FC = () => {
    const navigate = useNavigate()
    const [formData, setFormData] = useState({
        email: '',
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
            const response = await authApi.loginCustomer(formData)
            localStorage.setItem('authToken', response.token)
            localStorage.setItem('userId', response.id)
            localStorage.setItem('userType', 'customer')
            navigate('/client/requests')
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка входа')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="login-page">
            <div className="login-container">
                <Card>
                    <div className="login-header">
                        <h2>Вход для клиентов</h2>
                        <p>Введите ваши учетные данные</p>
                    </div>

                    {error && (
                        <div className="error-message">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="login-form">
                        <div className="form-group">
                            <label htmlFor="email">Email</label>
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
                            <label htmlFor="password">Пароль</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                disabled={isLoading}
                                placeholder="Ваш пароль"
                            />
                        </div>

                        <button
                            type="submit"
                            className="login-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Вход...' : 'Войти'}
                        </button>

                        <div className="links">
                            <Link to="/">← Назад к главной</Link>
                            <span> | </span>
                            <Link to="/register">Нет аккаунта? Зарегистрироваться</Link>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    )
}

export default LoginPage
