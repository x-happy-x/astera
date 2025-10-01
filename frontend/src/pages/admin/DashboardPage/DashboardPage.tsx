import React from 'react'
import Card from '../../../components/ui/Card'
import './styles.scss'

const DashboardPage: React.FC = () => {
    return (
        <div className="dashboard-page">
            <div className="stats-grid">
                <Card className="stat-card primary">
                    <div className="stat-label">Всего клиентов</div>
                    <div className="stat-value">-</div>
                </Card>

                <Card className="stat-card danger">
                    <div className="stat-label">Активные заявки</div>
                    <div className="stat-value">-</div>
                </Card>

                <Card className="stat-card success">
                    <div className="stat-label">Единиц оборудования</div>
                    <div className="stat-value">-</div>
                </Card>
            </div>

            <Card title="Добро пожаловать!">
                <p className="welcome-text">
                    Здесь вы можете управлять клиентами, оборудованием и заявками.
                    Используйте боковое меню для навигации по разделам системы.
                </p>
            </Card>
        </div>
    )
}

export default DashboardPage