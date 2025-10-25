import React, { useState } from 'react';
import { authApi } from '../../api';

type Props = {
    title: string;
    subtitle?: string;
    onSuccess: () => void;
};

const LoginForm: React.FC<Props> = ({ title, subtitle, onSuccess }) => {
    const [formData, setFormData] = useState({ email: '', password: '' });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError(null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            const res = await authApi.loginCustomer(formData);
            localStorage.setItem('authToken', res.token);
            localStorage.setItem('userId', res.id);
            localStorage.setItem('userType', 'customer');
            onSuccess();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Ошибка входа. Проверьте данные.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="form">
            <header className="form__header">
                <h2 className="form__title">{title}</h2>
                {subtitle && <p className="form__subtitle">{subtitle}</p>}
            </header>

            {error && <div className="form__error">{error}</div>}

            <form onSubmit={handleSubmit} className="form__body">
                <label className="field">
                    <span className="field__label">E-mail</span>
                    <input
                        className="field__input"
                        type="email"
                        name="email"
                        placeholder="ivan@example.com"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        autoComplete="email"
                    />
                </label>

                <label className="field">
                    <span className="field__label">Пароль</span>
                    <input
                        className="field__input"
                        type="password"
                        name="password"
                        placeholder="Ваш пароль"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        autoComplete="current-password"
                    />
                </label>

                <button type="submit" className="form__submit" disabled={isLoading}>
                    {isLoading ? 'Входим…' : 'Войти'}
                </button>
            </form>
        </div>
    );
};

export default LoginForm;
