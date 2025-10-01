import React from 'react'
import Card from '../../../components/ui/Card'
import './styles.scss'

const ManagersPage: React.FC = () => {
    return (
        <div className="managers-page">
            <Card title="Управление менеджерами">
                <p className="page-description">
                    Здесь можно добавлять и управлять менеджерами (только для администраторов).
                </p>
            </Card>
        </div>
    )
}

export default ManagersPage