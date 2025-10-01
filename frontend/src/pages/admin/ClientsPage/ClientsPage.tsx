import React from 'react'
import Card from '../../../components/ui/Card'
import './styles.scss'

const ClientsPage: React.FC = () => {
    return (
        <div className="clients-page">
            <Card title="Управление клиентами">
                <p className="page-description">
                    Здесь будет список всех зарегистрированных клиентов.
                </p>
            </Card>
        </div>
    )
}

export default ClientsPage