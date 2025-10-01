import React from 'react'
import Card from '../../../components/ui/Card'
import './styles.scss'

const RequestsPage: React.FC = () => {
    return (
        <div className="requests-page">
            <Card title="Управление заявками">
                <p className="page-description">
                    Здесь будут отображаться все заявки клиентов.
                </p>
            </Card>
        </div>
    )
}

export default RequestsPage