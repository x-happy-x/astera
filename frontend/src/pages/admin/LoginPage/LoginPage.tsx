import React, { useState } from 'react';
import { Navigate, Link } from 'react-router-dom';
import { useAuth } from '../../../hooks/useAuth';
import './styles.scss';

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const { state, login } = useAuth();

    if (state.user) {
        return <Navigate to="/admin/requests" replace />;
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await login(email, password);
        } catch (error) {
            console.error('Login failed:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="admin-login-page">
            <div className="admin-login-container">
                <header className="admin-login-header">
                    <div className="admin-login-badge">АСТЕРА</div>
                    <h1 className="admin-login-title">Вход для менеджеров</h1>
                    <p className="admin-login-subtitle">Административная панель</p>
                </header>

                <div className="admin-login-card">
                    {state.error && (
                        <div className="admin-error-message">
                            {state.error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="admin-login-form">
                        <div className="admin-form-field">
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

                        <div className="admin-form-field">
                            <label htmlFor="password">Пароль</label>
                            <input
                                type="password"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                disabled={isLoading}
                                placeholder="Введите пароль"
                            />
                        </div>

                        <button
                            type="submit"
                            className="admin-submit-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Вход...' : 'Войти'}
                        </button>
                    </form>

                    <div className="admin-login-footer">
                        <Link to="/" className="back-link">
                            ← Вернуться на главную
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;