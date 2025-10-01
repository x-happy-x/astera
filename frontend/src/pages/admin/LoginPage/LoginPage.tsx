import React, { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../../../hooks/useAuth'
import Card from '../../../components/ui/Card'
import './styles.scss'

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [isLoading, setIsLoading] = useState(false)
    const { state, login } = useAuth()

    if (state.user) {
        return <Navigate to="/admin/dashboard" replace />
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)

        try {
            await login(email, password)
        } catch (error) {
            console.error('Login failed:', error)
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="login-page">
            <div className="login-container">
                <Card>
                    <div className="login-header">
                        <h2>Вход в систему</h2>
                        <p>Административная панель</p>
                    </div>

                    <form onSubmit={handleSubmit} className="login-form">
                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                disabled={isLoading}
                                placeholder="admin@example.com"
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="password">Пароль</label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                disabled={isLoading}
                            />
                        </div>

                        {state.error && (
                            <div className="error-message">
                                {state.error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="login-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Вход...' : 'Войти'}
                        </button>
                    </form>
                </Card>
            </div>
        </div>
    )
}

export default LoginPage