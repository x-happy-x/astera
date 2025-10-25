import React from 'react'
import { Printer, X } from 'lucide-react'
import type { ConfigurationCandidateDto, HeatingRequestDto } from '../../api/types'
import './PrintView.scss'

interface PrintViewProps {
    candidate: ConfigurationCandidateDto
    request?: HeatingRequestDto
    onPrint: () => void
    onClose: () => void
}

const PrintView: React.FC<PrintViewProps> = ({
    candidate,
    request,
    onPrint,
    onClose
}) => {
    const getCategoryLabel = (category: string) => {
        const labels: Record<string, string> = {
            boiler: 'Котел',
            burner: 'Горелка',
            pump: 'Насос',
            valve: 'Клапан',
            flowmeter: 'Расходомер',
            automation: 'Автоматика'
        }
        return labels[category] || category
    }

    const handlePrint = () => {
        window.print()
        onPrint()
    }

    return (
        <div className="print-view-container">
            <div className="print-actions no-print">
                <button className="btn btn-primary" onClick={handlePrint}>
                    <Printer size={18} />
                    Печать
                </button>
                <button className="btn btn-secondary" onClick={onClose}>
                    <X size={18} />
                    Закрыть
                </button>
            </div>

            <div className="print-content">
                <div className="print-header">
                    <div className="company-logo">
                        <h1>ASTERA</h1>
                        <p>Системы отопления</p>
                    </div>
                    <div className="document-title">
                        <h2>Коммерческое предложение</h2>
                        <p className="document-number">Форма №4</p>
                        <p className="document-date">
                            Дата: {new Date().toLocaleDateString('ru-RU')}
                        </p>
                    </div>
                </div>

                {request && (
                    <div className="client-info">
                        <h3>Параметры заявки</h3>
                        <div className="info-grid">
                            <div className="info-item">
                                <span className="label">Мощность:</span>
                                <span className="value">{request.powerKw} кВт</span>
                            </div>
                            <div className="info-item">
                                <span className="label">Температура подачи:</span>
                                <span className="value">{request.tIn}°C</span>
                            </div>
                            <div className="info-item">
                                <span className="label">Температура обратки:</span>
                                <span className="value">{request.tOut}°C</span>
                            </div>
                            <div className="info-item">
                                <span className="label">Топливо:</span>
                                <span className="value">{request.fuelType}</span>
                            </div>
                        </div>
                        {request.notes && (
                            <div className="notes">
                                <span className="label">Примечание:</span>
                                <span className="value">{request.notes}</span>
                            </div>
                        )}
                    </div>
                )}

                <div className="configuration-summary">
                    <h3>Сводная информация</h3>
                    <div className="summary-grid">
                        <div className="summary-item">
                            <span className="label">DN:</span>
                            <span className="value">{candidate.dnSize}</span>
                        </div>
                        <div className="summary-item">
                            <span className="label">Тип подключения:</span>
                            <span className="value">{candidate.connectionKey}</span>
                        </div>
                        <div className="summary-item">
                            <span className="label">Срок поставки:</span>
                            <span className="value">{candidate.maxDeliveryDays} дней</span>
                        </div>
                    </div>
                </div>

                <div className="components-table">
                    <h3>Состав оборудования</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>№</th>
                                <th>Категория</th>
                                <th>Бренд</th>
                                <th>Модель</th>
                                <th>Характеристики</th>
                                <th>Кол-во</th>
                                <th>Цена за ед.</th>
                                <th>Срок, дн.</th>
                                <th>Сумма</th>
                            </tr>
                        </thead>
                        <tbody>
                            {candidate.components.map((component, index) => (
                                <tr key={index}>
                                    <td>{index + 1}</td>
                                    <td>{getCategoryLabel(component.category)}</td>
                                    <td>{component.brand}</td>
                                    <td>{component.model}</td>
                                    <td>
                                        {component.dnSize && `DN${component.dnSize}`}
                                        {component.connectionKey && `, ${component.connectionKey}`}
                                    </td>
                                    <td>{component.qty}</td>
                                    <td>{component.unitPrice.toLocaleString()} ₽</td>
                                    <td>{component.deliveryDays}</td>
                                    <td>{component.subtotal.toLocaleString()} ₽</td>
                                </tr>
                            ))}
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colSpan={8} className="total-label">
                                    <strong>Итого:</strong>
                                </td>
                                <td className="total-value">
                                    <strong>
                                        {candidate.totalPrice.toLocaleString()} {candidate.currency || '₽'}
                                    </strong>
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <div className="print-footer">
                    <p>Коммерческое предложение действительно в течение 30 дней с даты формирования.</p>
                    <p>Окончательная стоимость может отличаться в зависимости от курса валют и наличия на складе.</p>
                </div>
            </div>
        </div>
    )
}

export default PrintView
