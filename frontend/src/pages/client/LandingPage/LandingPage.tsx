import React from 'react'
import { Link } from 'react-router-dom'
import Card from '../../../components/ui/Card'
import './styles.scss'

const LandingPage: React.FC = () => {
    return (
        <div className="landing-page">
            <div className="landing-container">
                <div className="landing-header">
                    <h1>Добро пожаловать в Astera MVP</h1>
                    <p className="landing-subtitle">
                        Платформа для управления оборудованием и заявками
                    </p>
                </div>

                <div className="landing-content">
                    <Card className="welcome-card">
                        <h2>Что мы предлагаем</h2>
                        <p>
                            Наша платформа поможет вам эффективно управлять оборудованием,
                            подавать заявки на обслуживание и отслеживать их статус.
                        </p>
                        
                        <div className="action-buttons">
                            <Link to="/register" className="btn btn-primary">
                                Зарегистрироваться
                            </Link>
                            <Link to="/client/login" className="btn btn-secondary">
                                Вход для клиентов
                            </Link>
                            <Link to="/admin/login" className="btn btn-secondary">
                                Вход для администраторов
                            </Link>
                        </div>
                    </Card>
                </div>
            </div>
        </div>
    )
}

export default LandingPage