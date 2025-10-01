import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import Card from '../../../components/ui/Card'
import './styles.scss'

const RegisterPage: React.FC = () => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        phone: '',
        company: ''
    })
    const [isLoading, setIsLoading] = useState(false)

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: value
        }))
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)

        try {
            // TODO: реализовать отправку данных
            console.log('Регистрация:', formData)
            alert('Спасибо за регистрацию! Мы свяжемся с вами.')
        } catch (error) {
            console.error('Ошибка регистрации:', error)
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

                    <form onSubmit={handleSubmit} className="register-form">
                        <div className="form-group">
                            <label htmlFor="fullName">Полное имя *</label>
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

                        <div className="form-group">
                            <label htmlFor="company">Компания</label>
                            <input
                                type="text"
                                id="company"
                                name="company"
                                value={formData.company}
                                onChange={handleChange}
                                disabled={isLoading}
                                placeholder="Название компании"
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
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    )
}

export default RegisterPage