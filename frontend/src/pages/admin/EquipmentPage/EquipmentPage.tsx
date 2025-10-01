import React from 'react'
import Card from '../../../components/ui/Card'
import './styles.scss'

const EquipmentPage: React.FC = () => {
    return (
        <div className="equipment-page">
            <Card title="Управление оборудованием">
                <p className="page-description">
                    Здесь будет каталог оборудования.
                </p>
            </Card>
        </div>
    )
}

export default EquipmentPage