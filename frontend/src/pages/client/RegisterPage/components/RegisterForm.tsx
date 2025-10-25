import React, { useState } from 'react';
import { authApi } from '../../../../api';

type Props = {
    title: string;
    subtitle?: string;
    onSuccess: () => void;
};

const RegisterForm: React.FC<Props> = ({ title, subtitle, onSuccess }) => {
    const [formData, setFormData] = useState({
        fullName: '',
        phone: '',
        email: '',
        organization: '',
        password: ''
    });
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
            const response = await authApi.registerCustomer(formData);
            localStorage.setItem('authToken', response.token);
            localStorage.setItem('userId', response.id);
            localStorage.setItem('userType', 'customer');
            onSuccess();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Ошибка регистрации. Попробуйте снова.');
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
                    <span className="field__label">ФИО</span>
                    <input
                        className="field__input"
                        type="text"
                        name="fullName"
                        placeholder="Иванов Иван Иванович"
                        value={formData.fullName}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        autoComplete="name"
                    />
                </label>

                <label className="field">
                    <span className="field__label">Организация</span>
                    <input
                        className="field__input"
                        type="text"
                        name="organization"
                        placeholder="ООО Рога и Копыта"
                        value={formData.organization}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        autoComplete="organization"
                    />
                </label>

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
                    <span className="field__label">Телефон</span>
                    <input
                        className="field__input"
                        type="tel"
                        name="phone"
                        placeholder="+7 (999) 123-45-67"
                        value={formData.phone}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        autoComplete="tel"
                    />
                </label>

                <label className="field">
                    <span className="field__label">Пароль</span>
                    <input
                        className="field__input"
                        type="password"
                        name="password"
                        placeholder="Минимум 6 символов"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        disabled={isLoading}
                        minLength={6}
                        autoComplete="new-password"
                    />
                </label>

                <button type="submit" className="form__submit" disabled={isLoading}>
                    {isLoading ? 'Регистрация…' : 'Создать аккаунт'}
                </button>
            </form>
        </div>
    );
};

export default RegisterForm;
